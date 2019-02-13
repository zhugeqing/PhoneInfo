package com.example.administrator.phoneinfo;

import android.app.Application;
import android.location.LocationListener;
import android.location.LocationManager;

import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.Polygon;
import com.baidu.mapapi.model.LatLng;

import org.apache.poi.hssf.usermodel.HSSFAnchor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/2.
 */

public  class  MyApplication  extends  Application{

    public UserAuthInfo USER_AUTH_INFO=new UserAuthInfo();

    public static int COLLECT_PEROID=3000;   //1000毫秒，信号采样周期
    public static double SECTOR_MAP_SHOW_R=600; //600米，在MapViewActivity的百度地图上显示当前经纬度周边1000米内的小区扇区图层
    public static double MR_MAP_SHOW_R=500; //300米，在MapViewActivity的百度地图上显示当前经纬度周边300米内的MR栅格图层
    public static double SECTOR_R=60; //100米,基站图层中小区扇区的半径
    public static double USER_LAYER_MAP_SHOW_R=1000; //600米，在MapViewActivity的百度地图上显示当前经纬度周边1000米内的小区扇区图层
    public String CellInfoFileName_LTE="wyxf.xls";
    public String CellInfoUrl_LTE="http://120.199.120.85:50080/wzgjgl_mobile/UploadFiles_201701/cellquery/wyxf.xlsx";
    public String CellInfoUrl_GSM="http://120.199.120.85:50080/wzgjgl_mobile/UploadFiles_201701/cellquery/wyxf_GSM.xlsx";
    public String CellInfoUrl_FDD="http://120.199.120.85:50080/wzgjgl_mobile/UploadFiles_201701/cellquery/wyxf_FDD.xlsx";
    public String CellInfoUrl_NB="http://120.199.120.85:50080/wzgjgl_mobile/UploadFiles_201701/cellquery/wyxf_NB.xlsx";

    public Boolean flagCellInfoLteExist=true;

    public int numsGpsSatllite=0;
    public double newGpsLong=0;
    public double newGpsLat=0;
    public double sectorShowCenLong=0;
    public double sectorShowCenLat=0;
    public double MRShowCenLong=0;
    public double MRShowCenLat=0;
    public double userLayerShowCenLong=0;
    public double userLayerShowCenLat=0;
    public double dragMapCenLong=0;
    public double dragMapCenLat=0;
    public String logFileSavePath="/storage/emulated/0/Android/data/com.example.administrator.phoneinfo/";
    public HashMap<Integer,HashValueCellName> hash_cellName = new HashMap<>();
    public HashMap<String,String> hash_NCellName = new HashMap<>();

    public Iterator iter_CellName=hash_cellName.entrySet().iterator();

    public List<CellGeneralInfo> cellInfoList;
    public List<CellGeneralInfo> cellInfoList_map;
    public MyPhoneInfo myPhoneInfo1;
    public List<LogRecord> logCellInfoList;
    public List<LogRecord> savedCellInfoList;
    public boolean isSavingFile=false;
    public boolean isPlayingLog=false;
    public String link_CellId="";
    public String link_CellIdGsm="";
    public static String mrFtpServerIp="112.16.41.98";
    public static int mrFtpServerPort=21;
    public static String mrFtpUser="wangyournp";
    public static String mrFtpPassword="rnp$123123";
    public String cellInfoFtpDir="/wzmcc_cellinfo/APP_DEFAULT/cellinfo/";
    public String mrFilesFtpDir="/wzmcc_cellinfo/APP_DEFAULT/MR_all/";
    public String logFilesFtpDir="/wzmcc_cellinfo/APP_DEFAULT/log/";
    public String layerFilesFtpDir="/wzmcc_cellinfo/APP_DEFAULT/layer_files/";
    public String ftpSelectlogFileName="";
    public String ftpSelectLayerFileName="";
    public boolean flagMrQuaThrehasChanged=false;
    public String preMrArea="DEFAULT";
    public double mrPoorQuaPercentThre10=0.1,mrPoorQuaPercentThre30=0.3,mrPoorQuaPercentThre60=0.6; //分别对应10%，30%，60%门限
    public int mrPoorCountsThre10=0;
    public int mrPoorCountsThre30=0;
    public int mrPoorCountsThre60=0;

    public int mrQuanThre1=10000;
    public int mrQuanThre2=20000;
    public int mrQuanThre3=40000;
    public int mrQuanThre4=80000;
    public int mrQuanThre5=150000;
    //<10000 深绿色,10000-20000 淡绿色，20000-40000 浅蓝色，40000-80000黄色,80000-150000红色,>=150000 黑色

    public List<String> userLayerNames=new ArrayList<>();
    public double searchResultLon;
    public double searchResultLat;
    public List<List<LatLng>> userSelectPolygonList=new ArrayList<>();
    public List<Polygon> userSelectPolygonShowList=new ArrayList<>();
    public String userSelectPolyLayerName="";
    public List<MyCoordinatePolygon> kmlPolygonList=new ArrayList<>();

    public LocationListener locationListener_app=null;
    public LocationManager locationManager_app=null;
    public String gpsProvider_app="";

    public int screenWidth;
    public int screenHeigh;

    public boolean flagTopWindowShow=false;
    public String waterText="";

    public boolean flagUserLoginSucc=false;

    public static int readProgress;

    public int levelGreen=-80;
    public int levelLightGreen=-90;
    public int levelBlue=-100;
    public int levelYellow=-110;
    public int levelRed=-120;
    public int levelBlack=-140;

    @Override
    public void onCreate() {
        myPhoneInfo1=new MyPhoneInfo();
        cellInfoList=new ArrayList<>();
        logCellInfoList= new ArrayList<>();
        savedCellInfoList= new ArrayList<>();

        super.onCreate();
    }

    public class MyPhoneInfo {
        public String deviceId;
        public String deviceSoftwareVersion;
        public String Imsi;
        public String Imei;
        public String line1Number;
        public String serialNumber;
        public String operaterName;
        public String operaterId;
        public int mnc;
        public int mcc;
        public int datastate;
        public int cellcount;
        public int phoneDatastate;
        public String phoneModel;
        public int timecount;
        public double phoneGPSlong;
        public double phoneGPSlat;
        public String ratType="";
        public String sysTime="";
        public String locType;
        public int satelliteNums;
        public double termSpeed;
        public double termAltitude;
        public float termDirection=-1;
        public String termAddress;
        public int phoneDirection=0;
        public int phoneDowntilt=0;
        public int phoneRotation=0;
    }

    public class LogRecord{
        public String logSysTime;
        public int logType;
        public String logCellName;
        public int logCId;
        public int logLac;
        public int logTac;
        public int logPsc;
        public int logPci;
        public int logSignalStrength;
        public String logRsrq;
        public double logGPSlong;
        public double logGPSlat;
        public double secMidGPSlong;   //手机与小区连线的小区经纬度
        public double secMidGPSlat;    //手机与小区连线的小区经纬度
        public String logRatType;
        public int logSINR=99;
    }

    public class HashValueCellName{
        public String hsValue_cellName;
        public double hsValue_dirLong;
        public double hsValue_dirLat;
        public int hsValue_PCI;
    }

    public void getcellGeneralInfo() {}
    public void setMyString() {}
}