package com.example.administrator.phoneinfo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.platform.comapi.map.D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */

public class CellSectorLayer {
    //private double long_lefttop,lat_lefttop,long_righttop,lat_righttop,long_leftbottom,lat_leftbottom,long_rightbottom,lat_rightbottom;
    //public List<CellSector> sectorList = new ArrayList<>();

    public List<CellSector> cellSectorCollect(double longCenter,double latCenter,SQLiteDatabase myDb) {
        double SECTR_R_DEFAULT=MyApplication.SECTOR_R;
        int SECTR_R_X_TIMER=0;
        List<String> tpSameSectorList=new ArrayList<>();
        List<Integer> tpSECTR_R_X_TIMER_List=new ArrayList<>();
        String strSameSector="";
        String preSiteName="";

        Cursor cursor1 = null;
            try{
            double long_lefttop, lat_lefttop, long_righttop, lat_righttop, long_leftbottom, lat_leftbottom, long_rightbottom, lat_rightbottom;
            List<CellSector> sectorList = new ArrayList<>();
            String sqlstr = "";
            long_lefttop = longCenter - MyApplication.SECTOR_MAP_SHOW_R / 1000 / 100 * 1.1;
            long_righttop = longCenter + MyApplication.SECTOR_MAP_SHOW_R / 1000 / 100 * 1.1;
            lat_righttop = latCenter + MyApplication.SECTOR_MAP_SHOW_R / 1000 / 100;
            lat_rightbottom = latCenter - MyApplication.SECTOR_MAP_SHOW_R / 1000 / 100 - 0.002;
            sqlstr = "select * from " + CreateCellInfoDBActivity.TABLENAME1 + " where 经度>" + long_lefttop + " and 经度<" + long_righttop + " and 纬度>" + lat_rightbottom + " and 纬度<" + lat_righttop + " order by 小区名";
            cursor1 = myDb.rawQuery(sqlstr, null);

            while (cursor1.moveToNext()) {
                CellSector tempSector = new CellSector();
                tempSector.sec_siteName = cursor1.getString(1);//获取第一列的值,第一列的索引从1开始，数据库中基站信息表第一类是id，自动增加的
                tempSector.sec_cellName = cursor1.getString(2);//获取第二列的值
                tempSector.sec_TAC = cursor1.getInt(3);
                tempSector.sec_ENBID = cursor1.getInt(4);
                tempSector.sec_CELLID = cursor1.getInt(5);
                tempSector.sec_PCI = cursor1.getInt(6);
                tempSector.sec_coverType = cursor1.getString(7);
                tempSector.sec_long = cursor1.getDouble(8);
                tempSector.sec_lat = cursor1.getDouble(9);

                tempSector.sec_direction = (double) cursor1.getInt(10);
                tempSector.sec_bandwidth = (double) cursor1.getInt(11);
                if(tempSector.sec_bandwidth<30){
                    tempSector.sec_bandwidth=60;
                }
                tempSector.sec_freqBand = cursor1.getString(12).trim();
                tempSector.sec_freqNumber = cursor1.getString(13);

                if(preSiteName.equals("")||!preSiteName.equals(tempSector.sec_cellName)){
                    MyApplication.SECTOR_R=SECTR_R_DEFAULT;
                    preSiteName=tempSector.sec_cellName;
                    tpSameSectorList.clear();
                    tpSECTR_R_X_TIMER_List.clear();
                    tpSameSectorList.add(tempSector.sec_cellName+tempSector.sec_long+tempSector.sec_lat+tempSector.sec_direction+tempSector.sec_freqBand);
                    tpSECTR_R_X_TIMER_List.add(0);
                }else{
                    strSameSector=tempSector.sec_cellName+tempSector.sec_long+tempSector.sec_lat+tempSector.sec_direction+tempSector.sec_freqBand;
                    int sIndex=tpSameSectorList.indexOf(strSameSector);
                    if(sIndex!=-1){
                        SECTR_R_X_TIMER=tpSECTR_R_X_TIMER_List.get(sIndex)+1;
                        MyApplication.SECTOR_R=SECTR_R_DEFAULT-SECTR_R_X_TIMER*6;
                        tpSECTR_R_X_TIMER_List.set(sIndex,SECTR_R_X_TIMER);
                    }else{
                        tpSameSectorList.add(strSameSector);
                        tpSECTR_R_X_TIMER_List.add(0);
                        MyApplication.SECTOR_R= SECTR_R_DEFAULT;
                    }
                }

                if (tempSector.sec_freqBand.equals("FDD")|| tempSector.sec_freqBand.equals("FDD频段")|| tempSector.sec_freqBand.equals("FDD-1800")) {
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * 6.7 / 6 * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 6.7 / 6 *Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R *6.7 / 6 * Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 12) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R *6.7 / 6 * Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 12) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R *6.7 / 6 * Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 12) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R *6.7 / 6 * Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 12) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFFFF00FF;
                }else if (tempSector.sec_freqBand.equals("FDD-900")) {
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * 7.5 / 6 * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 7.5 / 6 *Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R *7.5 / 6 * Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 25) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R *7.5 / 6 * Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 25) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R *7.5 / 6 * Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 25) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R *7.5 / 6 * Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 25) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFF00FFFF;
                }
                else if (tempSector.sec_freqBand.equals("FDD-NB")) {
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * 7.5 / 6 * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 7.5 / 6 *Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R *7.5 / 6 * Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 60) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R *7.5 / 6 * Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 60) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R *7.5 / 6 * Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 60) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R *7.5 / 6 * Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 60) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFF009900;
                }
                else if (tempSector.sec_freqBand.equals("39") ||tempSector.sec_freqBand.equals("F") ||tempSector.sec_freqBand.equals("F1") || tempSector.sec_freqBand.equals("F2") || tempSector.sec_freqBand.equals("F频段")|| tempSector.sec_freqBand.equals("TDD-F")) {
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R * Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R * Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R * Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R * Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFF00EE00;
                } else if (tempSector.sec_freqBand.equals("38") ||tempSector.sec_freqBand.equals("D") ||tempSector.sec_freqBand.equals("D1") || tempSector.sec_freqBand.equals("D2") || tempSector.sec_freqBand.equals("D3")|| tempSector.sec_freqBand.equals("D频段")|| tempSector.sec_freqBand.equals("TDD-D")) {
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * 3 / 4 * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 3 / 4 * Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R * 3 / 4 * Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 3 / 4 * Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R * 3 / 4 * Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 3 / 4 * Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFFFF6600;
                } else if (tempSector.sec_freqBand.equals("40") ||tempSector.sec_freqBand.equals("E") ||tempSector.sec_freqBand.equals("E1") || tempSector.sec_freqBand.equals("E2") || tempSector.sec_freqBand.equals("E3")|| tempSector.sec_freqBand.equals("E频段")|| tempSector.sec_freqBand.equals("TDD-E")) {
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * 1 / 2 * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 1 / 2 * Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R * 1 / 2 * Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 4) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 1 / 2 * Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 4) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R * 1 / 2 * Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 4) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 1 / 2 * Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 4) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFF3399FF;
                } else {
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * 1 / 2 * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 1 / 2 * Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R * 1 / 2 * Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 1 / 2 * Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R * 1 / 2 * Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 1 / 2 * Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 8) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFFAAAAAA;
                }

                sectorList.add(tempSector);

            }
            MyApplication.SECTOR_R=SECTR_R_DEFAULT;
            return sectorList;
        }catch(Exception e){
                return null;
        }finally {
                if (cursor1 != null) {
                    try {
                        cursor1.close();
                        MyApplication.SECTOR_R=SECTR_R_DEFAULT;
                    } catch (Exception e){                    }
                }
        }
    }



    public class CellSector{
        public String sec_siteName;
        public String sec_cellName;
        public int sec_TAC;
        public int sec_ENBID;
        public int sec_CELLID;
        public int sec_PCI;
        public String sec_coverType;
        public double sec_long;
        public double sec_dirlong;
        public double sec_mindirlong;
        public double sec_maxdirlong;

        public double sec_lat;
        public double sec_dirlat;
        public double sec_mindirlat;
        public double sec_maxdirlat;

        public double sec_direction;
        public double sec_bandwidth;
        public String sec_freqBand;
        public String sec_freqNumber;
        public int sec_color;
    }
}


