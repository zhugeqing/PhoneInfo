package com.example.administrator.phoneinfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */

public class MRGridLayer {
    public List<MRGrid> mrGridCollect(double longCenter,double latCenter,SQLiteDatabase myDb,int mrShowContent,Context context)
    {
        MyApplication myApplication = (MyApplication) context.getApplicationContext();
        try{
            double long_lefttop, lat_lefttop, long_righttop, lat_righttop, long_leftbottom, lat_leftbottom, long_rightbottom, lat_rightbottom;
            List<MRGrid> mrGridList = new ArrayList<>();
            String sqlstr = "";
            long_lefttop = longCenter - MyApplication.MR_MAP_SHOW_R / 1000 / 100 * 1.1;
            long_righttop = longCenter + MyApplication.MR_MAP_SHOW_R / 1000 / 100 * 1.1;
            lat_righttop = latCenter + MyApplication.MR_MAP_SHOW_R / 1000 / 100;
            lat_rightbottom = latCenter - MyApplication.MR_MAP_SHOW_R / 1000 / 100 - 0.002;
            sqlstr = "select * from " + CreateCellInfoDBActivity.TABLENAME_MR + " where 经度>" + long_lefttop + " and 经度<" + long_righttop + " and 纬度>" + lat_rightbottom + " and 纬度<" + lat_righttop + " order by id";
            Cursor cursor1 = myDb.rawQuery(sqlstr, null);
            String columnName[]=cursor1.getColumnNames();
            int ci_area=0,ci_gridnumber=0,ci_lat=0,ci_long=0,ci_quantity=0,ci_uncover=0,ci_poorquantity=0,ci_cover=0,ci_info=0;
            String mrPoorQuanName="";
            String mrQuanName="";
            for(int i=0;i<columnName.length;i++) {
                switch (columnName[i]){
                    case "区域":ci_area=i;break;
                    case "序号":ci_gridnumber=i;break;
                    case "纬度":ci_lat=i;break;
                    case "经度":ci_long=i;break;
                    case "MR总条数":ci_quantity=i;mrQuanName="MR总条数";break;
                    case "弱覆盖占比":ci_uncover=i;break;
                    case "RSRP弱覆盖占比":ci_uncover=i;break;
                    case "弱覆盖MR总条数":ci_poorquantity=i;mrPoorQuanName="弱覆盖MR总条数";break;
                    case "弱覆盖MR数":ci_poorquantity=i;mrPoorQuanName="弱覆盖MR数";break;
                    case "MR覆盖率":ci_cover=i;break;
                    case "其他信息":ci_info=i;break;
                    default:
                        break;
                }
            }
            cursor1.moveToNext();
            if(!cursor1.getString(ci_area).equals(myApplication.preMrArea)){
                myApplication.preMrArea=cursor1.getString(ci_area);
                //myApplication.flagMrQuaThrehasChanged=true;
                if(mrShowContent==2){
                    String sqlstr2 = "select "+mrPoorQuanName+" from " + CreateCellInfoDBActivity.TABLENAME_MR + " where 区域='" + cursor1.getString(ci_area) + "' order by "+mrPoorQuanName+" DESC";
                    Cursor cursor2 = myDb.rawQuery(sqlstr2, null);
                    int rows=cursor2.getCount();
                    cursor2.moveToPosition((int) (rows*myApplication.mrPoorQuaPercentThre10));
                    myApplication.mrPoorCountsThre10=Integer.parseInt(cursor2.getString(0));
                    cursor2.moveToPosition((int) (rows*myApplication.mrPoorQuaPercentThre30));
                    myApplication.mrPoorCountsThre30=Integer.parseInt(cursor2.getString(0));
                    cursor2.moveToPosition((int) (rows*myApplication.mrPoorQuaPercentThre60));
                    myApplication.mrPoorCountsThre60=Integer.parseInt(cursor2.getString(0));
                    cursor2.close();
                }
            }
            cursor1.moveToFirst();
            while (cursor1.moveToNext()) {
                MRGrid tempGrid = new MRGrid();
                tempGrid.mr_area=cursor1.getString(ci_area);
                tempGrid.mr_gridnumber=cursor1.getString(ci_gridnumber);//获取第一列的值,第一列的索引从1开始，数据库中基站信息表第一类是id，自动增加的
                tempGrid.mrgrid_centlat=cursor1.getDouble(ci_lat);//获取第二列的值
                tempGrid.mrgrid_centlong=cursor1.getDouble(ci_long);
                try {
                    tempGrid.mr_quantity = Integer.parseInt(cursor1.getString(ci_quantity));
                }catch (Exception e){
                    tempGrid.mr_quantity=0;
                }
                try {
                    tempGrid.mr_uncoverpercent=Double.parseDouble(cursor1.getString(ci_uncover));
                }catch (Exception e){
                    tempGrid.mr_uncoverpercent=0;
                }
                try {
                    tempGrid.mr_poorquantity=Integer.parseInt(cursor1.getString(ci_poorquantity));
                }catch (Exception e){
                    tempGrid.mr_poorquantity=0;
                }
                try {
                    tempGrid.mr_coverpercent=Double.parseDouble(cursor1.getString(ci_cover));
                }catch (Exception e){
                    tempGrid.mr_coverpercent=0;
                }
                if(ci_info>0){
                    tempGrid.mr_info=cursor1.getString(ci_info);
                }



                tempGrid.mrgrid_lefttoplong=tempGrid.mrgrid_centlong-0.00015;
                tempGrid.mrgrid_leftbottomlong=tempGrid.mrgrid_centlong-0.00015;
                tempGrid.mrgrid_righttoplong=tempGrid.mrgrid_centlong+0.00015;
                tempGrid.mrgrid_rightbottomlong=tempGrid.mrgrid_centlong+0.00015;

                tempGrid.mrgrid_lefttoplat=tempGrid.mrgrid_centlat+0.00015;
                tempGrid.mrgrid_leftbottomlat=tempGrid.mrgrid_centlat-0.00015;
                tempGrid.mrgrid_righttoplat=tempGrid.mrgrid_centlat+0.00015;
                tempGrid.mrgrid_rightbottomlat=tempGrid.mrgrid_centlat-0.00015;

                if(mrShowContent==1){
                    if(tempGrid.mr_coverpercent>=0.96){
                        tempGrid.mr_gridcolor=0x8800EE00;  //绿色
                    }else if(tempGrid.mr_coverpercent<0.96 && tempGrid.mr_coverpercent>=0.9){
                        tempGrid.mr_gridcolor=0x88FFFF00; //黄色
                    }else if(tempGrid.mr_coverpercent<0.9 && tempGrid.mr_coverpercent>=0.8){
                        tempGrid.mr_gridcolor=0xAAFF66FF;  //紫色
                    }else if(tempGrid.mr_coverpercent<0.8){
                        tempGrid.mr_gridcolor=0xDDFF0000;  //红色
                    }else{
                        tempGrid.mr_gridcolor=0xAAADADAD; //灰色
                    }
                }
                else if((mrShowContent==2)){
                    if(tempGrid.mr_poorquantity>=myApplication.mrPoorCountsThre10){
                        tempGrid.mr_gridcolor=0xDDFF0000;  //红色 0xAAFF0000
                    }else if(tempGrid.mr_poorquantity<myApplication.mrPoorCountsThre10 && tempGrid.mr_poorquantity>=myApplication.mrPoorCountsThre30){
                        tempGrid.mr_gridcolor=0xAAFF66FF; //紫色 0xAAFF0099
                    }else if(tempGrid.mr_poorquantity<myApplication.mrPoorCountsThre30 && tempGrid.mr_poorquantity>=myApplication.mrPoorCountsThre60){
                        tempGrid.mr_gridcolor=0x88FFFF00;  //黄色 0xAAFFFF00
                    }else if(tempGrid.mr_poorquantity<myApplication.mrPoorCountsThre60){
                        tempGrid.mr_gridcolor=0x8800EE00;  //绿色 0xAA00EE00
                    }else{
                        tempGrid.mr_gridcolor=0xAAADADAD; //灰色
                    }
                }else if((mrShowContent==3)){
                    if(tempGrid.mr_quantity<myApplication.mrQuanThre1){
                        tempGrid.mr_gridcolor=0xAA00EE00;  //绿色 0xAA00EE00
                    }else if(tempGrid.mr_quantity>=myApplication.mrQuanThre1 && tempGrid.mr_quantity<myApplication.mrQuanThre2){
                        tempGrid.mr_gridcolor=0xDD00ff00; //浅绿色
                    }else if(tempGrid.mr_quantity<myApplication.mrQuanThre3 && tempGrid.mr_quantity>=myApplication.mrQuanThre2){
                        tempGrid.mr_gridcolor=0xCC00BFFF;  //淡蓝色
                    }else if(tempGrid.mr_quantity<myApplication.mrQuanThre4 && tempGrid.mr_quantity>=myApplication.mrQuanThre3){
                        tempGrid.mr_gridcolor=0xDDFFFF00;  //黄色
                    }else if(tempGrid.mr_quantity<myApplication.mrQuanThre5 && tempGrid.mr_quantity>=myApplication.mrQuanThre4){
                        tempGrid.mr_gridcolor=0xAAFF0000;  //红色
                    } else if(tempGrid.mr_quantity>=myApplication.mrQuanThre5){
                        tempGrid.mr_gridcolor=0xAA000000; //黑色
                    }else{
                        tempGrid.mr_gridcolor=0xAAADADAD; //灰色
                    }
                }
                mrGridList.add(tempGrid);

            }
            cursor1.close();
            return mrGridList;
        }catch(Exception e){
            return null;
        }
    }



    public class MRGrid{
        public String mr_gridnumber;
        public int mr_quantity;
        public int mr_poorquantity;
        public double mr_uncoverpercent;
        public double mr_coverpercent;
        public double mrgrid_centlong;
        public double mrgrid_centlat;
        public double mrgrid_lefttoplong;
        public double mrgrid_lefttoplat;
        public double mrgrid_righttoplong;
        public double mrgrid_righttoplat;
        public double mrgrid_leftbottomlong;
        public double mrgrid_leftbottomlat;
        public double mrgrid_rightbottomlong;
        public double mrgrid_rightbottomlat;
        public int mr_gridcolor;
        public String mr_area;
        public String mr_info="";
    }
}



