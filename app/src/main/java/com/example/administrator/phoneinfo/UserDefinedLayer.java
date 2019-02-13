package com.example.administrator.phoneinfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */

public class UserDefinedLayer {
    private String userLayerTableName;
    private String fieldnamelon;
    private String fieldnamelat;
    private List<String> fieldnamesinfo;
    private String layerMarkerStyle;
    private int layerMarkerColor;
    private double layerMarkerSize;
    private int userLayerType;  // xls=1 ;kml=2

    public UserDefinedLayer(String tbname,String fNameLon,String fNameLat,List<String> fNamesinfo,String mkStyle,int mcolor,double msize,int mtype){
        userLayerTableName=tbname;
        fieldnamelon=fNameLon;
        fieldnamelat=fNameLat;
        fieldnamesinfo=fNamesinfo;
        layerMarkerStyle=mkStyle;
        layerMarkerColor=mcolor;
        layerMarkerSize=msize;
        userLayerType=mtype;
    }

    public List<UserLayerMarker> userLayerDataCollect(double longCenter,double latCenter,SQLiteDatabase myDb,Context context) {
        MyApplication myApplication = (MyApplication) context.getApplicationContext();
        try{
            double long_lefttop, lat_lefttop, long_righttop, lat_righttop, long_leftbottom, lat_leftbottom, long_rightbottom, lat_rightbottom;
            List<UserLayerMarker> userMarkerList = new ArrayList<>();
            String sqlstr = "";
            long_lefttop = longCenter - MyApplication.USER_LAYER_MAP_SHOW_R*3/ 1000 / 100 * 1.1;
            long_righttop = longCenter + MyApplication.USER_LAYER_MAP_SHOW_R*3/ 1000 / 100 * 1.1;
            lat_righttop = latCenter + MyApplication.USER_LAYER_MAP_SHOW_R*3/ 1000 / 100;
            lat_rightbottom = latCenter - MyApplication.USER_LAYER_MAP_SHOW_R*3/ 1000 / 100 - 0.002;
            sqlstr = "select * from tb_userlayer_" + userLayerTableName + " where "+fieldnamelon+">" + long_lefttop + " and "+fieldnamelon+"<" + long_righttop + " and "+fieldnamelat+">" + lat_rightbottom + " and "+fieldnamelat+"<" + lat_righttop + " order by id";
            //sqlstr = "select * from tb_userlayer_" + userLayerTableName;
            Cursor cursor1 = myDb.rawQuery(sqlstr, null);
            System.out.println("zgq:"+cursor1.getCount());
            String columnName[]=cursor1.getColumnNames();
            int colindexLon=0,colindexlat=0;
            List<Integer> colindexInfoList=new ArrayList<>();
            for(int i=0;i<columnName.length;i++) {
                if(columnName[i].equals(fieldnamelon)){
                    colindexLon=i;
                }
                if(columnName[i].equals(fieldnamelat)){
                    colindexlat=i;
                }
            }
            for(int j=0;j<fieldnamesinfo.size();j++) {
                for(int s=0;s<columnName.length;s++){
                    if(columnName[s].equals(fieldnamesinfo.get(j))){
                        colindexInfoList.add(s);
                        break;
                    }
                }
            }

            //
            if(userLayerType==2){    //kml类型处理
                cursor1.close();
                String sqlstr2="select * from tb_userlayer_" + userLayerTableName + " where shape_id in"+"(select distinct shape_id from tb_userlayer_" + userLayerTableName + " where "+fieldnamelon+">" + long_lefttop + " and "+fieldnamelon+"<" + long_righttop + " and "+fieldnamelat+">" + lat_rightbottom + " and "+fieldnamelat+"<" + lat_righttop + " ) order by id";
                Cursor cursor2 = myDb.rawQuery(sqlstr2, null);
                System.out.println("zgq:"+cursor2.getCount());

                String preShapeId="";
                UserLayerMarker tempMarker=new UserLayerMarker();
                while (cursor2.moveToNext()) {
                    if(!preShapeId.equals(cursor2.getString(1))){
                        if(!preShapeId.equals("")){
                            userMarkerList.add(tempMarker);
                        }
                        preShapeId=String.copyValueOf(cursor2.getString(1).toCharArray());
                        tempMarker = new UserLayerMarker();
                        tempMarker.mk_centlat = cursor2.getDouble(colindexlat);
                        tempMarker.mk_centlong = cursor2.getDouble(colindexLon);
                        try{
                            if(!cursor2.getString(9).equals("")){
                                String tpcolor="#"+cursor2.getString(9);
                                tempMarker.mk_color= Color.parseColor(tpcolor);
                                //tempMarker.mk_color=Integer.valueOf(tpcolor,16);
                                tempMarker.mk_fillcolor=tempMarker.mk_color;
                            }else{
                                tempMarker.mk_color=0xFFFF0000; //红色
                                tempMarker.mk_fillcolor=0xFFFF0000; //红色
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            tempMarker.mk_color=0xFFFF0000;  //红色
                            tempMarker.mk_fillcolor=0xFFFF0000;  //红色
                        }

                        tempMarker.mk_type_kml=Integer.parseInt(cursor2.getString(2));
                        for (int i = 0; i < colindexInfoList.size(); i++) {
                            tempMarker.mk_infoList.add(cursor2.getString(colindexInfoList.get(i)));
                        }
                    }

                    double tplon=cursor2.getDouble(3);
                    double tplat=cursor2.getDouble(4);
                    LatLng bd_latLng= MapViewActivity.gpstobaiduCoordiConverter(new LatLng(tplat,tplon));
                    if (cursor2.getString(2).equals("1")) {
                        //kml文件中的Polygon类型处理
                        tempMarker.mk_PolyLinePoints_kml.add(bd_latLng);
                    } else if (cursor2.getString(2).equals("2")) {
                        //kml文件中的Lintestring类型处理
                        tempMarker.mk_LinePoints_kml.add(bd_latLng);
                    } else if (cursor2.getString(2).equals("3")) {
                        //kml文件中的Point类型处理
                        tempMarker.mk_Point_kml=bd_latLng;
                    }

                }
                userMarkerList.add(tempMarker);
                cursor2.close();

            }else {                     //普通xls表格类型图层的数据处理

                while (cursor1.moveToNext()) {
                    UserLayerMarker tempMarker = new UserLayerMarker();
                    tempMarker.mk_centlat = cursor1.getDouble(colindexlat);
                    tempMarker.mk_centlong = cursor1.getDouble(colindexLon);
                    for (int i = 0; i < colindexInfoList.size(); i++) {
                        tempMarker.mk_infoList.add(cursor1.getString(colindexInfoList.get(i)));
                    }
                    if (layerMarkerStyle.equals("正方形")) {
                        tempMarker.mk_lefttoplong = tempMarker.mk_centlong - layerMarkerSize / 100000;
                        tempMarker.mk_leftbottomlong = tempMarker.mk_centlong - layerMarkerSize / 100000;
                        tempMarker.mk_righttoplong = tempMarker.mk_centlong + layerMarkerSize / 100000;
                        tempMarker.mk_rightbottomlong = tempMarker.mk_centlong + layerMarkerSize / 100000;

                        tempMarker.mk_lefttoplat = tempMarker.mk_centlat + layerMarkerSize / 100000;
                        tempMarker.mk_leftbottomlat = tempMarker.mk_centlat - layerMarkerSize / 100000;
                        tempMarker.mk_righttoplat = tempMarker.mk_centlat + layerMarkerSize / 100000;
                        tempMarker.mk_rightbottomlat = tempMarker.mk_centlat - layerMarkerSize / 100000;
                    } else if (layerMarkerStyle.equals("梯形")) {
                        tempMarker.mk_lefttoplong = tempMarker.mk_centlong - layerMarkerSize / 100000 / 2;
                        tempMarker.mk_leftbottomlong = tempMarker.mk_centlong - layerMarkerSize / 100000;
                        tempMarker.mk_righttoplong = tempMarker.mk_centlong + layerMarkerSize / 100000 / 2;
                        tempMarker.mk_rightbottomlong = tempMarker.mk_centlong + layerMarkerSize / 100000;

                        tempMarker.mk_lefttoplat = tempMarker.mk_centlat + layerMarkerSize / 100000 / 2 * 3;
                        tempMarker.mk_leftbottomlat = tempMarker.mk_centlat - layerMarkerSize / 100000 / 2 * 3;
                        tempMarker.mk_righttoplat = tempMarker.mk_centlat + layerMarkerSize / 100000 / 2 * 3;
                        tempMarker.mk_rightbottomlat = tempMarker.mk_centlat - layerMarkerSize / 100000 / 2 * 3;
                    }
                    tempMarker.mk_color = layerMarkerColor;
                    userMarkerList.add(tempMarker);

                }
            }
            //
            cursor1.close();
            return userMarkerList;
        }catch(Exception e){
            return null;
        }
    }



    public class UserLayerMarker{
        public int mk_number;
        public double mk_centlong;
        public double mk_centlat;
        public double mk_lefttoplong;
        public double mk_lefttoplat;
        public double mk_righttoplong;
        public double mk_righttoplat;
        public double mk_leftbottomlong;
        public double mk_leftbottomlat;
        public double mk_rightbottomlong;
        public double mk_rightbottomlat;
        public int mk_color;
        public List<String> mk_infoList=new ArrayList<>();

        public List<LatLng> mk_PolyLinePoints_kml=new ArrayList<>();  //单个多边形的点位List
        public List<LatLng> mk_LinePoints_kml=new ArrayList<>();   //单条线的两个点位List
        public LatLng mk_Point_kml;  //单个点
        public int mk_fillcolor;
        public int mk_type_kml; // Polygon= 1  Linestring= 2  Point= 3
    }
}



