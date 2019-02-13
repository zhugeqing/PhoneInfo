package com.example.administrator.phoneinfo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */

public class CellSectorGsmLayer {
    //private double long_lefttop,lat_lefttop,long_righttop,lat_righttop,long_leftbottom,lat_leftbottom,long_rightbottom,lat_rightbottom;
    //public List<CellSector> sectorList = new ArrayList<>();

    public List<CellSectorGsm> cellSectorGsmCollect(double longCenter,double latCenter,SQLiteDatabase myDb) {
        Cursor cursor1 = null;
        try{
            double long_lefttop, long_righttop, lat_righttop, lat_rightbottom;
            List<CellSectorGsm> sectorGsmList = new ArrayList<>();
            String sqlstr = "";
            long_lefttop = longCenter - MyApplication.SECTOR_MAP_SHOW_R / 1000 / 100 * 1.1;
            long_righttop = longCenter + MyApplication.SECTOR_MAP_SHOW_R / 1000 / 100 * 1.1;
            lat_righttop = latCenter + MyApplication.SECTOR_MAP_SHOW_R / 1000 / 100;
            lat_rightbottom = latCenter - MyApplication.SECTOR_MAP_SHOW_R / 1000 / 100 - 0.002;
            sqlstr = "select * from " + CreateCellInfoDBActivity.TABLENAME_GSM + " where 经度>" + long_lefttop + " and 经度<" + long_righttop + " and 纬度>" + lat_rightbottom + " and 纬度<" + lat_righttop + " order by id";
            cursor1 = myDb.rawQuery(sqlstr, null);

            while (cursor1.moveToNext()) {
                CellSectorGsm tempSector = new CellSectorGsm();
                tempSector.sec_siteName = cursor1.getString(1);//获取第一列的值,第一列的索引从1开始，数据库中基站信息表第一类是id，自动增加的
                tempSector.sec_area = cursor1.getString(2);
                tempSector.sec_siteid = cursor1.getString(3);
                tempSector.sec_coverType = cursor1.getString(4);
                tempSector.sec_btsid = cursor1.getString(5);
                tempSector.sec_bsc = cursor1.getString(6);
                tempSector.sec_lac = cursor1.getString(7);
                tempSector.sec_cellName = cursor1.getString(8);
                tempSector.sec_CELLID = cursor1.getInt(9);
                tempSector.sec_long = cursor1.getDouble(10);
                tempSector.sec_lat = cursor1.getDouble(11);
                tempSector.sec_ncc = cursor1.getString(12);
                tempSector.sec_bcc = cursor1.getString(13);
                tempSector.sec_bcch = cursor1.getString(14);
                tempSector.sec_freqBand = cursor1.getString(15);
                tempSector.sec_atennahight = cursor1.getString(16);
                tempSector.sec_direction = (double) cursor1.getInt(17);//如果数据库中的这一个数据恰好是null，而null又不是int，此时并不会抛出NumberFormatException 异常，而是直接返回0！为了避免将null和0混淆，可以考虑用getString()代替。
                tempSector.sec_bandwidth = (double) cursor1.getInt(18);
                tempSector.sec_elecdowntilt=cursor1.getString(19);
                tempSector.sec_mechdowntilt=cursor1.getString(20);

                if(tempSector.sec_bandwidth<20) {
                    tempSector.sec_bandwidth=60;
                }

                if (tempSector.sec_freqBand.contains("900") && tempSector.sec_direction>0) {
                    //GSM 900MH宏站
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * 7 / 6 * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 7 / 6 *Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R *7 / 6 * Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 16) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R *7 / 6 * Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 16) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R *7 / 6 * Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 16) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R *7 / 6 * Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 16) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFFFFFF00;
                }else if(tempSector.sec_freqBand.contains("900") && tempSector.sec_direction<=0) {
                    //GSM 900MH微站与室分
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * 3.5 / 6 * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 3.5 / 6 *Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R *3.5 / 6 * Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 16) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R *3.5 / 6 * Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 16) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R *3.5 / 6 * Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 16) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R *3.5 / 6 * Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 16) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFFFFFF00;
                }else if (tempSector.sec_freqBand.contains("1800") && tempSector.sec_direction>0) {
                    //GSM宏站1800MH
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * 8 / 6 * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 8 / 6 * Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R * 8 / 6* Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 200) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 8 / 6* Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 200) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R * 8 / 6* Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 200) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 8 / 6* Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 200) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFFFF0000;
                }else if (tempSector.sec_freqBand.contains("1800") && tempSector.sec_direction<=0) {
                    //GSM 1800MH微站与室分
                    tempSector.sec_dirlong = tempSector.sec_long + MyApplication.SECTOR_R * 4 / 6 * Math.sin(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_dirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 4 / 6 * Math.cos(tempSector.sec_direction / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlong = tempSector.sec_long + MyApplication.SECTOR_R * 4 / 6* Math.sin((tempSector.sec_direction - tempSector.sec_bandwidth / 200) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_mindirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 4 / 6* Math.cos((tempSector.sec_direction - tempSector.sec_bandwidth / 200) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlong = tempSector.sec_long + MyApplication.SECTOR_R * 4 / 6* Math.sin((tempSector.sec_direction + tempSector.sec_bandwidth / 200) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_maxdirlat = tempSector.sec_lat + MyApplication.SECTOR_R * 4 / 6* Math.cos((tempSector.sec_direction + tempSector.sec_bandwidth / 200) / 360 * 2 * Math.PI) / 100000;
                    tempSector.sec_color = 0xFFFF0000;
                }
                sectorGsmList.add(tempSector);

            }
            return sectorGsmList;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            if (cursor1 != null) {
                try {
                    cursor1.close();
                } catch (Exception e){                    }
            }
        }
    }



    public class CellSectorGsm{
        public String sec_siteName;
        public String sec_area;
        public String sec_siteid;
        public String sec_coverType;
        public String sec_btsid;
        public String sec_bsc;
        public String sec_lac;
        public String sec_cellName;
        public int sec_CELLID;

        public double sec_long;
        public double sec_dirlong;
        public double sec_mindirlong;
        public double sec_maxdirlong;

        public double sec_lat;
        public double sec_dirlat;
        public double sec_mindirlat;
        public double sec_maxdirlat;

        public String sec_ncc;
        public String sec_bcc;
        public String sec_bcch;
        public String sec_freqBand;
        public String sec_atennahight;
        public double sec_direction;
        public double sec_bandwidth;
        public String sec_elecdowntilt;
        public String sec_mechdowntilt;

        public int sec_color;
    }
}
