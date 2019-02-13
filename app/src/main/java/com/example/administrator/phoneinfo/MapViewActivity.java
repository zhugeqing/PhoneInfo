package com.example.administrator.phoneinfo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.view.View;

import com.baidu.mapapi.map.*;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;

import org.json.JSONArray;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.view.View.TEXT_ALIGNMENT_TEXT_END;


public class MapViewActivity extends AppCompatActivity implements View.OnClickListener {

    private MapView mapView;
    private BaiduMap mBaiduMap;
    private boolean flagThreadrun = true;
    private SQLiteDatabase db_sec, db_mr;
    private Overlay preSCellLine;
    private Overlay preArrowIcon;
    private List<Overlay> tempSectorShow = new ArrayList<>();
    private List<Overlay> tempSectorGsmShow = new ArrayList<>();
    private List<Overlay> tempSectorFddShow = new ArrayList<>();
    private List<Overlay> tempSectorNbiotShow = new ArrayList<>();

    private List<Overlay> tempMRShow = new ArrayList<>();
    private String[][] logExcelArray;
    private List<LogPlayEntry> logPlayEntryList = new ArrayList<>();
    private ListView logPlayListView;
    private FruitAdapter logPlayAdapter;

    private double mylatitude = 28.01648;// 纬度
    private double mylongitude = 120.72144;// 经度

    private int screenWidth;
    private int screenHeight;
    private double mapdragdistX = 0;
    private double mapdragdistY = 0;

    private LatLng hmPos = new LatLng(mylatitude, mylongitude);// 坐标
    private MyBaiduSdkReceiver receiver;
    public MyApplication myApplication;
    private MapStatusUpdate mapstatusUpdatePoint;
    private List<CellGeneralInfo> tempCellinfo = new ArrayList<>();

    private Button sCellHideButton;
    private ImageButton changeMapButton;
    private ImageButton searchMapButton;
    private ImageButton menuMoreButton;
    private ImageButton locateMapButton;
    private ImageButton distanceMapButton;
    private ImageButton polylineSelectMapButton;
    private ImageButton circleSelectMapButton;
    private ImageButton specialTestMapButton;
    private ImageButton shareGpsMapButton;
    private ImageButton webTestMapButton;
    private ImageButton sreenOnMapButton;
    private ImageButton textReaderMapButton;
    private Boolean flagSreenOn = false;

    private ImageButton logSaveButton;
    private ImageButton logSaveStopButton;
    private ImageButton locCenterButton;
    private ImageButton clearOverlayButton;
    private ImageButton logPlayButton;
    private ImageButton logPlayFastButton;
    private ImageButton logPlaySlowButton;
    private ImageButton logPlayStopButton;
    private ImageButton logPlayCloseButton;
    private ImageButton logPlayEndButton;
    private ImageButton logPlayStartButton;

    private TableRow sCellTitleBar;
    private TableRow sCellTitleBar2;
    private TableRow sCellTableRow;
    private TableRow nCell1TableRow;
    private TableRow nCell2TableRow;
    private TableRow nCell3TableRow;
    private TableRow nCell4TableRow;
    private TableRow mapContorlTbr;
    private TableRow logSaveTbr;
    private TableRow logSaveStopTbr;
    private TableRow logPlayTbr;
    private TableRow logPlayControlTbr;
    private TableRow colorbarSINRTbr;
    private TableRow colorbarMrTbr;
    private TableRow colorbarMrQuanTbr;
    private LinearLayout mapMenuMoreLayout;
    private LinearLayout nbCellControlLayout;
    private Boolean flagNbCellShow = false;
    private LinearLayout mapLayerControlTb;
    private LinearLayout cellInfoLinearLayout;

    private Boolean flagScellHide = false;
    private TextView sCellLongTextView;
    private TextView sCellLatTextView;
    private TextView sysTimeTextView;
    private TextView sysDateTextView;
    private TextView textViewUpdateMapData;
    private boolean mapMenuShow = false;
    private TextView tvScellTime, tvSCellName, tvSCellCI, tvSCellPCI, tvSCellTAC, tvSCellDBm, tvSCellRsrq, tvSCellSinr;
    private TextView tvN1CellName, tvN1CellCI, tvN1CellPCI, tvN1CellTAC, tvN1CellDBm, tvN1CellRsrq, tvN1CellSinr;
    private TextView tvN2CellName, tvN2CellCI, tvN2CellPCI, tvN2CellTAC, tvN2CellDBm, tvN2CellRsrq, tvN2CellSinr;
    private TextView tvN3CellName, tvN3CellCI, tvN3CellPCI, tvN3CellTAC, tvN3CellDBm, tvN3CellRsrq, tvN3CellSinr;
    private TextView tvN4CellName, tvN4CellCI, tvN4CellPCI, tvN4CellTAC, tvN4CellDBm, tvN4CellRsrq, tvN4CellSinr;
    private RadioGroup radioGroupMapLayer;
    private RadioGroup radioGroupMrLayer;
    private RadioButton radioButtonMapNormal;
    private RadioButton radioButtonMapSat;
    private Button buttonNewLayer;
    private CheckBox checkBoxSector;
    private CheckBox checkBoxMrLayer;
    private CheckBox checkBoxMrInfo;
    private EditText editTextMrRadius;
    private CheckBox checkBoxSinr;
    private CheckBox checkBoxKeepLine;
    private CheckBox checkBoxSectorGsm;
    private CheckBox checkBoxSectorFdd;
    private CheckBox checkBoxSectorNb;

    private boolean flagSectorLayerShow = true;
    private boolean flagSectorGsmLayerShow = true;
    private boolean flagSectorFddLayerShow = false;
    private boolean flagSectorNbiotLayerShow = false;

    private boolean flagMrLayerShow = false;
    private boolean flagMrInfoShow = true;
    private boolean flagSinrLayerShow = false;
    private boolean flagKeepScellLine = false;
    private boolean flagArrowIconShow = true;
    private int flagMrShowContent = 1; //1显示MR覆盖率，2显示MR弱覆盖量,3显示MR数
    private Timer sectorShowTimer;
    private TimerTask sectorShowTask;
    private Timer sectorGsmShowTimer;
    private TimerTask sectorGsmShowTask;


    private Timer mrLayerShowTimer;
    private TimerTask mrLayerShowTask;

    long sectorShowDelay = 500;
    long mrLayerShowDelay = 500;
    long userLayerShowDelay = 500;
    private Timer logPlayTimer;
    private TimerTask logPlayTask;
    private String logFileSaveName = "";

    private InfoWindow infoWindow;

    private int flagfirstLoad = 0;
    private Boolean flagfirstLoadNbCellShow = true;
    private int centerMaptimer = 0;
    private int logPlayRows = 0;
    private int logPlayIndex = 1;
    private long logPlaySpeed = 1000; //默认原始速度，1秒一个打点
    private Boolean flagLogPlayToEnd = false;
    private Boolean flagLogPlaying = false;
    private Boolean flagLogPointSelect = false;
    private Boolean flagLogListViewClick = false;

    private Boolean flagAutoCenterMap = true;
    private Boolean flagSearchLocateMap = false;
    private Boolean flagSectorMapMoved = false;
    private Boolean flagSectorGsmMapMoved = false;
    private Boolean flagSectorFddMapMoved = false;
    private Boolean flagSectorNbiotMapMoved = false;

    private Boolean flagMrLayerMapMoved = false;

    private Boolean flagMrLayerdrawing = false;
    private Boolean flagUserLayerDrawing = false;
    private Boolean flagSectorLteDrawing = false;
    private Boolean flagSectorGsmDrawing = false;

    private ArrayList<Boolean> flagUserLayerMapMovedList = new ArrayList<>();
    private int logIdSelected;
    private CellGeneralInfo logPlaySCellinfo = new CellGeneralInfo();
    private List<String> userLayerNameList = new ArrayList<>();
    //private List<CheckBox> checkBoxListUserLayer=new ArrayList<>();
    //private String[][] userLayersFieldsArray=new String[0][];
    private int numsUserLayer = 0;
    private int indexUserLayer = 0;
    private String nameUserLayerDelete = "";
    private String nameUserLayerEdit = "";
    private List<UserLayer> userLayerList = new ArrayList<>();
    private ArrayAdapter arr_adapter, color_adapter;
    private View viewClicking;
    private Boolean flagUserLayerEdit = false;
    private Boolean flagMapDistance = false;
    private LatLng dotA_Distance;
    private long mapDragStartTime = 0, mapDragEndTime = 0, mapDragTimeIntervel = 0;

    private Boolean flagMapPolySelect = false;
    private List<String> mapPolySelectedLayers = new ArrayList<>();
    private List<CheckBox> checkBoxsPolySelectLayer = new ArrayList<>();
    private List<CheckBox> checkBoxsNbImeiSelect = new ArrayList<>();
    private List<LatLng> mapPolySelectPoints = new ArrayList<>();
    private List<Overlay> mapPolySelectOverlays = new ArrayList<>();
    private LatLng mapPolySelectfirstPoint;
    private ImageButton buttonMapEditOk;
    private ImageButton buttonMapEditCancel;
    private ImageButton buttonMapEditStatic;
    private TableRow tablerowMapEdit;
    private ImageButton buttonPowerSaving;
    private ImageButton buttonOritation;
    private ImageButton buttonWaterCamera;
    private Boolean flagPowerSavingMode = false;
    private Boolean flagWaterCameraOn = false;
    private ImageButton buttonTakePhoto;

    private int preScreenBrightness = 0;
    private String tempImagePath = "";
    private ImageButton buttonOutService;
    private ImageButton buttonOutServiceList;
    private Boolean flagOutServiceOn = false;
    private Boolean flagShareGpsOn = false;

    private TableRow tableRowOutServArea;
    private TableRow tableRowOutServCount;
    private TextView tvOutServCountLuchen;
    private TextView tvOutServCountLongwan;
    private TextView tvOutServCountOuhai;
    private TextView tvOutServCountYueqing;
    private TextView tvOutServCountRuian;
    private TextView tvOutServCountCangnan;
    private TextView tvOutServCountYongjia;
    private TextView tvOutServCountPingyang;
    private TextView tvOutServCountWencheng;
    private TextView tvOutServCountTaishun;
    private TextView tvOutServCountDongtou;

    private TableRow tableRowOutServCellCount;
    private TextView tvOutServCountCellLuchen;
    private TextView tvOutServCountCellLongwan;
    private TextView tvOutServCountCellOuhai;
    private TextView tvOutServCountCellYueqing;
    private TextView tvOutServCountCellRuian;
    private TextView tvOutServCountCellCangnan;
    private TextView tvOutServCountCellYongjia;
    private TextView tvOutServCountCellPingyang;
    private TextView tvOutServCountCellWencheng;
    private TextView tvOutServCountCellTaishun;
    private TextView tvOutServCountCellDongtou;

    private TextView tvLevelGreen;
    private TextView tvLevelLightGreen;
    private TextView tvLevelBlue;
    private TextView tvLevelYellow;
    private TextView tvLevelRed;
    private TextView tvLevelBlack;

    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;

    String earfcnToString(int tpEARFCN) {
        String res = "";
        if (tpEARFCN != -2 && tpEARFCN != -1) {
            res = "\n" + String.valueOf(tpEARFCN);
        }
        return res;
    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            try {

                if (msg == null) {
                    System.out.println("receive....msg is null");
                } else {
                    if (msg.what == 1) {

                        int logSize = myApplication.logCellInfoList.size();
                        if (myApplication.isPlayingLog) {
                            if (flagLogPointSelect) {
                                sCellLongTextView.setText(logExcelArray[logIdSelected][logGPSLongId]);
                                sCellLatTextView.setText(logExcelArray[logIdSelected][logGPSLatId]);
                                String msystime = logExcelArray[logIdSelected][logSysTimeId];
                                //sysTimeTextView.setText(msystime.substring(msystime.indexOf("_")+1));
                                tvScellTime.setText(msystime.substring(msystime.indexOf("_") + 1));


                            } else {
                                sCellLongTextView.setText(logExcelArray[logPlayIndex][logGPSLongId]);
                                sCellLatTextView.setText(logExcelArray[logPlayIndex][logGPSLatId]);
                                String msystime = logExcelArray[logPlayIndex][logSysTimeId];
                                //sysTimeTextView.setText(msystime.substring(msystime.indexOf("_")+1));
                                tvScellTime.setText(msystime.substring(msystime.indexOf("_") + 1));
                            }
                        } else {
                            sCellLongTextView.setText(String.valueOf(myApplication.newGpsLong));
                            sCellLatTextView.setText(String.valueOf(myApplication.newGpsLat));
                            String msystime = myApplication.logCellInfoList.get(logSize - 1).logSysTime;
                            //sysTimeTextView.setText(msystime.substring(msystime.indexOf("_")+1));
                            if (myApplication.myPhoneInfo1.locType.equals("gps")) {
                                if (myApplication.numsGpsSatllite >= 4) {
                                    sysTimeTextView.setTextColor(Color.GREEN);
                                } else {
                                    sysTimeTextView.setTextColor(Color.RED);
                                }
                                sysTimeTextView.setText("GPS(" + myApplication.numsGpsSatllite + ")");
                            } else {
                                sysTimeTextView.setTextColor(Color.RED);
                                sysTimeTextView.setText(myApplication.myPhoneInfo1.locType);
                            }
                            tvScellTime.setText(msystime.substring(msystime.indexOf("_") + 1));
                        }


                        if (!flagScellHide) {
                            if (myApplication.isPlayingLog) {
                                tempCellinfo.clear();
                                tempCellinfo.add(logPlaySCellinfo);
                                logPlayAdapter.notifyDataSetChanged();
                            } else {
                                tempCellinfo = DeepCopy.deepCopy(myApplication.cellInfoList_map);
                                //tempCellinfo = myApplication.cellInfoList;

                            }
                            if (flagfirstLoadNbCellShow && tempCellinfo.size() > 1) {
                                nbCellControlLayout.setVisibility(View.VISIBLE);
                                flagNbCellShow = true;
                                flagfirstLoadNbCellShow = false;
                            }

                            if (tempCellinfo.size() == 1) {
                                tvSCellName.setText(String.valueOf(tempCellinfo.get(0).cellName));
                                tvSCellCI.setText(String.valueOf(tempCellinfo.get(0).CId) + earfcnToString(tempCellinfo.get(0).ERFCN));
                                tvSCellPCI.setText(String.valueOf(tempCellinfo.get(0).pci));
                                tvSCellTAC.setText(String.valueOf(tempCellinfo.get(0).tac));
                                tvSCellDBm.setText(String.valueOf(tempCellinfo.get(0).signalStrength));
                                tvSCellRsrq.setText(String.valueOf(tempCellinfo.get(0).rsrq));
                                tvSCellSinr.setText(String.valueOf(tempCellinfo.get(0).SINR));

                                if (flagNbCellShow == true) {
                                    tvN1CellName.setText("");
                                    tvN1CellCI.setText("");
                                    tvN1CellPCI.setText("");
                                    tvN1CellTAC.setText("");
                                    tvN1CellDBm.setText("");
                                    tvN1CellRsrq.setText("");
                                    tvN1CellSinr.setText("");
                                    tvN2CellName.setText("");
                                    tvN2CellCI.setText("");
                                    tvN2CellPCI.setText("");
                                    tvN2CellTAC.setText("");
                                    tvN2CellDBm.setText("");
                                    tvN2CellRsrq.setText("");
                                    tvN2CellSinr.setText("");
                                    tvN3CellName.setText("");
                                    tvN3CellCI.setText("");
                                    tvN3CellPCI.setText("");
                                    tvN3CellTAC.setText("");
                                    tvN3CellDBm.setText("");
                                    tvN3CellRsrq.setText("");
                                    tvN3CellSinr.setText("");
                                    tvN4CellName.setText("");
                                    tvN4CellCI.setText("");
                                    tvN4CellPCI.setText("");
                                    tvN4CellTAC.setText("");
                                    tvN4CellDBm.setText("");
                                    tvN4CellRsrq.setText("");
                                    tvN4CellSinr.setText("");
                                }

                            }
                            if (tempCellinfo.size() == 2) {
                                tvSCellName.setText(String.valueOf(tempCellinfo.get(0).cellName));
                                //tvSCellCI.setText(String.valueOf(tempCellinfo.get(0).CId));
                                tvSCellCI.setText(String.valueOf(tempCellinfo.get(0).CId) + earfcnToString(tempCellinfo.get(0).ERFCN));
                                tvSCellPCI.setText(String.valueOf(tempCellinfo.get(0).pci));
                                tvSCellTAC.setText(String.valueOf(tempCellinfo.get(0).tac));
                                tvSCellDBm.setText(String.valueOf(tempCellinfo.get(0).signalStrength));
                                tvSCellRsrq.setText(String.valueOf(tempCellinfo.get(0).rsrq));
                                tvSCellSinr.setText(String.valueOf(tempCellinfo.get(0).SINR));
                                tvN1CellName.setText(String.valueOf(tempCellinfo.get(1).cellName));
                                tvN1CellCI.setText(String.valueOf(tempCellinfo.get(1).CId));
                                tvN1CellPCI.setText(String.valueOf(tempCellinfo.get(1).pci));
                                tvN1CellTAC.setText(String.valueOf(tempCellinfo.get(1).tac));
                                tvN1CellDBm.setText(String.valueOf(tempCellinfo.get(1).signalStrength));
                                tvN1CellRsrq.setText(String.valueOf(tempCellinfo.get(1).rsrq));
                                tvN1CellSinr.setText(String.valueOf(tempCellinfo.get(1).SINR));
                                tvN2CellName.setText("");
                                tvN2CellCI.setText("");
                                tvN2CellPCI.setText("");
                                tvN2CellTAC.setText("");
                                tvN2CellDBm.setText("");
                                tvN2CellRsrq.setText("");
                                tvN2CellSinr.setText("");
                                tvN3CellName.setText("");
                                tvN3CellCI.setText("");
                                tvN3CellPCI.setText("");
                                tvN3CellTAC.setText("");
                                tvN3CellDBm.setText("");
                                tvN3CellRsrq.setText("");
                                tvN3CellSinr.setText("");
                                tvN4CellName.setText("");
                                tvN4CellCI.setText("");
                                tvN4CellPCI.setText("");
                                tvN4CellTAC.setText("");
                                tvN4CellDBm.setText("");
                                tvN4CellRsrq.setText("");
                                tvN4CellSinr.setText("");
                            }
                            if (tempCellinfo.size() == 3) {
                                tvSCellName.setText(String.valueOf(tempCellinfo.get(0).cellName));
                                //tvSCellCI.setText(String.valueOf(tempCellinfo.get(0).CId));
                                tvSCellCI.setText(String.valueOf(tempCellinfo.get(0).CId) + earfcnToString(tempCellinfo.get(0).ERFCN));
                                tvSCellPCI.setText(String.valueOf(tempCellinfo.get(0).pci));
                                tvSCellTAC.setText(String.valueOf(tempCellinfo.get(0).tac));
                                tvSCellDBm.setText(String.valueOf(tempCellinfo.get(0).signalStrength));
                                tvSCellRsrq.setText(String.valueOf(tempCellinfo.get(0).rsrq));
                                tvSCellSinr.setText(String.valueOf(tempCellinfo.get(0).SINR));
                                tvN1CellName.setText(String.valueOf(tempCellinfo.get(1).cellName));
                                tvN1CellCI.setText(String.valueOf(tempCellinfo.get(1).CId));
                                tvN1CellPCI.setText(String.valueOf(tempCellinfo.get(1).pci));
                                tvN1CellTAC.setText(String.valueOf(tempCellinfo.get(1).tac));
                                tvN1CellDBm.setText(String.valueOf(tempCellinfo.get(1).signalStrength));
                                tvN1CellRsrq.setText(String.valueOf(tempCellinfo.get(1).rsrq));
                                tvN1CellSinr.setText(String.valueOf(tempCellinfo.get(1).SINR));
                                tvN2CellName.setText(String.valueOf(tempCellinfo.get(2).cellName));
                                tvN2CellCI.setText(String.valueOf(tempCellinfo.get(2).CId));
                                tvN2CellPCI.setText(String.valueOf(tempCellinfo.get(2).pci));
                                tvN2CellTAC.setText(String.valueOf(tempCellinfo.get(2).tac));
                                tvN2CellDBm.setText(String.valueOf(tempCellinfo.get(2).signalStrength));
                                tvN2CellRsrq.setText(String.valueOf(tempCellinfo.get(2).rsrq));
                                tvN2CellSinr.setText(String.valueOf(tempCellinfo.get(2).SINR));
                                tvN3CellName.setText("");
                                tvN3CellCI.setText("");
                                tvN3CellPCI.setText("");
                                tvN3CellTAC.setText("");
                                tvN3CellDBm.setText("");
                                tvN3CellRsrq.setText("");
                                tvN3CellSinr.setText("");
                                tvN4CellName.setText("");
                                tvN4CellCI.setText("");
                                tvN4CellPCI.setText("");
                                tvN4CellTAC.setText("");
                                tvN4CellDBm.setText("");
                                tvN4CellRsrq.setText("");
                                tvN4CellSinr.setText("");
                            }
                            if (tempCellinfo.size() == 4) {
                                tvSCellName.setText(String.valueOf(tempCellinfo.get(0).cellName));
                                //tvSCellCI.setText(String.valueOf(tempCellinfo.get(0).CId));
                                tvSCellCI.setText(String.valueOf(tempCellinfo.get(0).CId) + earfcnToString(tempCellinfo.get(0).ERFCN));
                                tvSCellPCI.setText(String.valueOf(tempCellinfo.get(0).pci));
                                tvSCellTAC.setText(String.valueOf(tempCellinfo.get(0).tac));
                                tvSCellDBm.setText(String.valueOf(tempCellinfo.get(0).signalStrength));
                                tvSCellRsrq.setText(String.valueOf(tempCellinfo.get(0).rsrq));
                                tvSCellSinr.setText(String.valueOf(tempCellinfo.get(0).SINR));
                                tvN1CellName.setText(String.valueOf(tempCellinfo.get(1).cellName));
                                tvN1CellCI.setText(String.valueOf(tempCellinfo.get(1).CId));
                                tvN1CellPCI.setText(String.valueOf(tempCellinfo.get(1).pci));
                                tvN1CellTAC.setText(String.valueOf(tempCellinfo.get(1).tac));
                                tvN1CellDBm.setText(String.valueOf(tempCellinfo.get(1).signalStrength));
                                tvN1CellRsrq.setText(String.valueOf(tempCellinfo.get(1).rsrq));
                                tvN1CellSinr.setText(String.valueOf(tempCellinfo.get(1).SINR));
                                tvN2CellName.setText(String.valueOf(tempCellinfo.get(2).cellName));
                                tvN2CellCI.setText(String.valueOf(tempCellinfo.get(2).CId));
                                tvN2CellPCI.setText(String.valueOf(tempCellinfo.get(2).pci));
                                tvN2CellTAC.setText(String.valueOf(tempCellinfo.get(2).tac));
                                tvN2CellDBm.setText(String.valueOf(tempCellinfo.get(2).signalStrength));
                                tvN2CellRsrq.setText(String.valueOf(tempCellinfo.get(2).rsrq));
                                tvN2CellSinr.setText(String.valueOf(tempCellinfo.get(2).SINR));
                                tvN3CellName.setText(String.valueOf(tempCellinfo.get(3).cellName));
                                tvN3CellCI.setText(String.valueOf(tempCellinfo.get(3).CId));
                                tvN3CellPCI.setText(String.valueOf(tempCellinfo.get(3).pci));
                                tvN3CellTAC.setText(String.valueOf(tempCellinfo.get(3).tac));
                                tvN3CellDBm.setText(String.valueOf(tempCellinfo.get(3).signalStrength));
                                tvN3CellRsrq.setText(String.valueOf(tempCellinfo.get(3).rsrq));
                                tvN3CellSinr.setText(String.valueOf(tempCellinfo.get(3).SINR));
                                tvN4CellName.setText("");
                                tvN4CellCI.setText("");
                                tvN4CellPCI.setText("");
                                tvN4CellTAC.setText("");
                                tvN4CellDBm.setText("");
                                tvN4CellRsrq.setText("");
                                tvN4CellSinr.setText("");
                            }
                            if (tempCellinfo.size() >= 5) {

                                tvSCellName.setText(String.valueOf(tempCellinfo.get(0).cellName));
                                //tvSCellCI.setText(String.valueOf(tempCellinfo.get(0).CId));
                                tvSCellCI.setText(String.valueOf(tempCellinfo.get(0).CId) + earfcnToString(tempCellinfo.get(0).ERFCN));
                                tvSCellPCI.setText(String.valueOf(tempCellinfo.get(0).pci));
                                tvSCellTAC.setText(String.valueOf(tempCellinfo.get(0).tac));
                                tvSCellDBm.setText(String.valueOf(tempCellinfo.get(0).signalStrength));
                                tvSCellRsrq.setText(String.valueOf(tempCellinfo.get(0).rsrq));
                                tvSCellSinr.setText(String.valueOf(tempCellinfo.get(0).SINR));
                                tvN1CellName.setText(String.valueOf(tempCellinfo.get(1).cellName));
                                tvN1CellCI.setText(String.valueOf(tempCellinfo.get(1).CId));
                                tvN1CellPCI.setText(String.valueOf(tempCellinfo.get(1).pci));
                                tvN1CellTAC.setText(String.valueOf(tempCellinfo.get(1).tac));
                                tvN1CellDBm.setText(String.valueOf(tempCellinfo.get(1).signalStrength));
                                tvN1CellRsrq.setText(String.valueOf(tempCellinfo.get(1).rsrq));
                                tvN1CellSinr.setText(String.valueOf(tempCellinfo.get(1).SINR));
                                tvN2CellName.setText(String.valueOf(tempCellinfo.get(2).cellName));
                                tvN2CellCI.setText(String.valueOf(tempCellinfo.get(2).CId));
                                tvN2CellPCI.setText(String.valueOf(tempCellinfo.get(2).pci));
                                tvN2CellTAC.setText(String.valueOf(tempCellinfo.get(2).tac));
                                tvN2CellDBm.setText(String.valueOf(tempCellinfo.get(2).signalStrength));
                                tvN2CellRsrq.setText(String.valueOf(tempCellinfo.get(2).rsrq));
                                tvN2CellSinr.setText(String.valueOf(tempCellinfo.get(2).SINR));
                                tvN3CellName.setText(String.valueOf(tempCellinfo.get(3).cellName));
                                tvN3CellCI.setText(String.valueOf(tempCellinfo.get(3).CId));
                                tvN3CellPCI.setText(String.valueOf(tempCellinfo.get(3).pci));
                                tvN3CellTAC.setText(String.valueOf(tempCellinfo.get(3).tac));
                                tvN3CellDBm.setText(String.valueOf(tempCellinfo.get(3).signalStrength));
                                tvN3CellRsrq.setText(String.valueOf(tempCellinfo.get(3).rsrq));
                                tvN3CellSinr.setText(String.valueOf(tempCellinfo.get(3).SINR));
                                tvN4CellName.setText(String.valueOf(tempCellinfo.get(4).cellName));
                                tvN4CellCI.setText(String.valueOf(tempCellinfo.get(4).CId));
                                tvN4CellPCI.setText(String.valueOf(tempCellinfo.get(4).pci));
                                tvN4CellTAC.setText(String.valueOf(tempCellinfo.get(4).tac));
                                tvN4CellDBm.setText(String.valueOf(tempCellinfo.get(4).signalStrength));
                                tvN4CellRsrq.setText(String.valueOf(tempCellinfo.get(4).rsrq));
                                tvN4CellSinr.setText(String.valueOf(tempCellinfo.get(4).SINR));
                            }
                        }

                    } else if (msg.what == 0) {
                        ProgressDlgUtil.showProgressDlg("正在读取LOG，请稍等....", MapViewActivity.this);
                    } else if (msg.what == 3) {
                        ProgressDlgUtil.showProgressDlg("正在上传LOG，请稍等....", MapViewActivity.this);
                    } else if (msg.what == 5) {
                        ProgressDlgUtil.stopProgressDlg();
                        Toast.makeText(MapViewActivity.this, "上传LOG成功！", Toast.LENGTH_LONG).show();
                    } else if (msg.what == 4) {
                        ProgressDlgUtil.stopProgressDlg();
                        Toast.makeText(MapViewActivity.this, "上传LOG失败！", Toast.LENGTH_LONG).show();
                    } else if (msg.what == 100) {
                        ProgressDlgUtil.stopProgressDlg();
                        Toast.makeText(MapViewActivity.this, "成功读取LOG,开始回放!", Toast.LENGTH_LONG).show();
                        myApplication.isPlayingLog = true; //标示正在log回放操作模式
                        logPlayControlTbr.setVisibility(View.VISIBLE);
                        flagThreadrun = false;   //关闭地图信号打点模式
                        sectorShowDelay = 0;
                        logSaveTbr.setVisibility(View.GONE);
                        logSaveStopTbr.setVisibility(View.GONE);
                        logPlayButton.setVisibility(View.INVISIBLE);
                        logPlayCloseButton.setVisibility(View.VISIBLE);
                        logPlayStopButton.setVisibility(View.VISIBLE);
                        logPlayEndButton.setVisibility(View.VISIBLE);
                        logPlayStartButton.setVisibility(View.VISIBLE);
                        logPlayFastButton.setVisibility(View.VISIBLE);
                        logPlaySlowButton.setVisibility(View.VISIBLE);
                        sysTimeTextView.setText("回放模式");
                        mBaiduMap.clear();
                        mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(gpstobaiduCoordiConverter(new LatLng(Double.parseDouble(logExcelArray[1][logGPSLatId]), Double.parseDouble(logExcelArray[1][logGPSLongId]))));
                        mBaiduMap.animateMapStatus(mapstatusUpdatePoint);
                        sectorShowLTE();
                        centerMaptimer = 0;
                        logPlayTimer = new Timer();
                        logPlayTask = new LogPlayTask();
                        logPlayTimer.schedule(logPlayTask, 1000, logPlaySpeed);
                        flagLogPlaying = true;  //标示正在log回放的打点过程中，主要用于各个回放控制键的状态识别
                        sCellTableRow.setVisibility(View.GONE);
                        logPlayEntryList.clear();
                        logPlayListView.setVisibility(View.VISIBLE);
                        logPlayAdapter = new FruitAdapter(MapViewActivity.this, R.layout.listitem_logplay, logPlayEntryList);
                        logPlayListView.setAdapter(logPlayAdapter);

                        logPlayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    flagLogListViewClick = true;
                                    logIdSelected = position + 1;
                                    LogPlayEntry logEntry = logPlayEntryList.get(position);
                                    Toast.makeText(MapViewActivity.this, logEntry.logEntryCellName + " Log index:" + position, Toast.LENGTH_SHORT).show();
                                    if (flagLogPlaying) {
                                        logPlayTimer.cancel();
                                        centerMaptimer = 0;
                                        flagLogPlaying = false;
                                        logPlayStopButton.setImageResource(android.R.drawable.ic_media_play);
                                    }
                                    flagLogPointSelect = true;

                                    logPlayTimer = new Timer();
                                    logPlayTask = new LogSelectShowTask();
                                    //long logdelay=0;
                                    logPlayTimer.schedule(logPlayTask, 0);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        logPlayAdapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if (flagLogPointSelect) {
                                    //logPlayListView.setSelection(logIdSelected-1);
                                    logPlayListView.requestFocusFromTouch();
                                    //logPlayListView.setItemChecked(logIdSelected, true);
                                    //System.out.println("ZGQlog:"+logPlayAdapter.getCount());
                                    //System.out.println("ZGQlog:"+logIdSelected);

                                    //logPlayListView.setSelection(logIdSelected);

                                    //logPlayListView.smoothScrollToPositionFromTop(logIdSelected,0);
                                    //logPlayListView.setSelection(logPlayListView.getHeaderViewsCount()+logIdSelected);
                                    logPlayListView.setSelectionFromTop(logIdSelected, 0);
                                    logPlayListView.smoothScrollToPosition(logIdSelected);
                                    //System.out.println("ZGQlog:"+logIdSelected);
                                    //logPlayListView.setSelection(logPlayAdapter.getPosition(logIdSelected));

                                } else {
                                    logPlayListView.setSelection(logPlayAdapter.getCount() - 1);
                                }
                            }
                        });

                    } else if (msg.what == 101) {
                        logPlayTimer.cancel();
                        logPlayAdapter.notifyDataSetChanged();
                        flagLogPlaying = false;
                        ProgressDlgUtil.stopProgressDlg();
                        Toast.makeText(MapViewActivity.this, "LOG播放完毕！", Toast.LENGTH_SHORT).show();
                    } else if (msg.what == 200) {
                        for (int i = 0; i < numsUserLayer; i++) {
                            //nameUserLayer=userLayerNameList.get(i);
                            //indexUserLayer=i;
                            CheckBox checkBoxLayer = new CheckBox(MapViewActivity.this);
                            checkBoxLayer.setText(userLayerList.get(i).userLayerName);
                            checkBoxLayer.setTextSize(12);
                            checkBoxLayer.setChecked(false);
                            checkBoxLayer.setId(i);
                            checkBoxLayer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (myApplication.USER_AUTH_INFO.vipUser == 0) {
                                        Toast.makeText(MapViewActivity.this, "非VIP用户不支持显示自定义图层！", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    int s = v.getId();
                                    if (((CheckBox) v).isChecked()) {
                                        //int s=v.getId();
                                        userLayerList.get(s).flagUserLayerShow = true;
                                        userLayerList.get(s).userLayerShowTimer = new Timer();
                                        userLayerList.get(s).userLayerShowTask = new UserLayerShowTask(userLayerList.get(s).userLayerName, userLayerList.get(s).userLayerFieldName_lon, userLayerList.get(s).userLayerFieldName_lat, userLayerList.get(s).userLayerInfoFieldNameList, userLayerList.get(s).tempUserLayerPolylines, userLayerList.get(s).userLayerIcon, userLayerList.get(s).userLayerIconColor, userLayerList.get(s).userLayerIconSize, userLayerList.get(s).userLayerIndex, userLayerList.get(s).userLayerType);
                                        userLayerList.get(s).userLayerShowTimer.schedule(userLayerList.get(s).userLayerShowTask, 0);

                                    } else {
                                        //int s=v.getId();
                                        userLayerList.get(s).flagUserLayerShow = false;
                                        if (userLayerList.get(s).tempUserLayerPolylines != null) {
                                            for (int j = 0; j < userLayerList.get(s).tempUserLayerPolylines.size(); j++) {
                                                userLayerList.get(s).tempUserLayerPolylines.get(j).remove();
                                            }
                                            userLayerList.get(s).tempUserLayerPolylines.clear();
                                        }
                                    }
                                    mapLayerControlTb.setVisibility(View.GONE);
                                    mapMenuShow = false;
                                }
                            });
                            //checkBoxListUserLayer.add(checkBoxLayer);
                            TableRow tableRow = new TableRow(MapViewActivity.this);
                            checkBoxLayer.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.8f));
                            //tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                            ImageButton buttonLayerDelete = new ImageButton(MapViewActivity.this);
                            buttonLayerDelete.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_delete));
                            buttonLayerDelete.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                            ;
                            buttonLayerDelete.setBackgroundColor(0x00000000);
                            buttonLayerDelete.setAdjustViewBounds(true);
                            buttonLayerDelete.setScaleType(ImageView.ScaleType.CENTER);
                            buttonLayerDelete.setMaxHeight(100);
                            buttonLayerDelete.setMaxWidth(100);
                            buttonLayerDelete.setId(i);
                            buttonLayerDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    indexUserLayer = v.getId();
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MapViewActivity.this);
                                    builder1.setCancelable(false);
                                    builder1.setMessage("是否删除该图层?");
                                    builder1.setTitle("提示");
                                    builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            nameUserLayerDelete = userLayerList.get(indexUserLayer).userLayerName;
                                            List<String> keyFieldListDel = new ArrayList<>();
                                            keyFieldListDel.add("userlayername_" + nameUserLayerDelete);
                                            keyFieldListDel.add("userlayerlon_" + nameUserLayerDelete);
                                            keyFieldListDel.add("userlayerlat_" + nameUserLayerDelete);
                                            List<String> keys = AppSettingActivity.readAllSetKey(MapViewActivity.this, "userlayerconfig");
                                            for (int i = 0; i < keys.size(); i++) {
                                                if (keys.get(i).indexOf("userlayerinfo_" + nameUserLayerDelete + "_") != -1) {
                                                    keyFieldListDel.add(keys.get(i));
                                                }
                                            }
                                            AppSettingActivity.deleteSettingInfo(MapViewActivity.this, "userlayerconfig", keyFieldListDel);
                                            db_sec.execSQL("drop table " + "tb_userlayer_" + nameUserLayerDelete);
                                            Toast.makeText(MapViewActivity.this, "成功删除自定义图层" + nameUserLayerDelete, Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                    builder1.setNegativeButton("取消", null);
                                    builder1.create().show();
                                }
                            });


                            ImageButton buttonLayerEdit = new ImageButton(MapViewActivity.this);
                            buttonLayerEdit.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_edit));
                            buttonLayerEdit.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                            buttonLayerEdit.setBackgroundColor(0x00000000);
                            buttonLayerEdit.setAdjustViewBounds(true);
                            buttonLayerEdit.setScaleType(ImageView.ScaleType.CENTER);
                            buttonLayerEdit.setMaxHeight(100);
                            buttonLayerEdit.setMaxWidth(100);
                            buttonLayerEdit.setId(i);
                            buttonLayerEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    indexUserLayer = v.getId();
                                    viewClicking = v;
                                    if (flagUserLayerEdit && nameUserLayerEdit.equals(userLayerList.get(indexUserLayer).userLayerName)) {
                                        //保存图层线程....
                                        flagUserLayerEdit = false;
                                        ((ImageButton) viewClicking).setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
                                        viewClicking = null;
                                        Toast.makeText(MapViewActivity.this, nameUserLayerEdit + "图层编辑模式已关闭。", Toast.LENGTH_SHORT).show();
                                        nameUserLayerEdit = "";
                                        return;
                                    }
                                    //nameUserLayerEdit=userLayerList.get(indexUserLayer).userLayerName;
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MapViewActivity.this);
                                    builder1.setCancelable(false);
                                    builder1.setMessage("是否编辑" + nameUserLayerEdit + "图层?");
                                    builder1.setTitle("提示");
                                    builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //userLayerList.get(indexUserLayer).userLayerCheckBox.setChecked(true);
                                            if (flagUserLayerEdit) {
                                                Toast.makeText(MapViewActivity.this, nameUserLayerEdit + "图层正处于编辑模式，请保存或关闭该图层的编辑模式。", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            nameUserLayerEdit = userLayerList.get(indexUserLayer).userLayerName;
                                            flagAutoCenterMap = false;
                                            flagUserLayerEdit = true;
                                            Toast.makeText(MapViewActivity.this, "手动地图模式", Toast.LENGTH_SHORT).show();
                                            userLayerList.get(indexUserLayer).userLayerCheckBox.performClick();
                                            ((ImageButton) viewClicking).setImageDrawable(getResources().getDrawable(R.drawable.ok_close_25_zgqicon));
                                            mapLayerControlTb.setVisibility(View.GONE);
                                            mapMenuShow = false;
                                        }
                                    });
                                    builder1.setNegativeButton("取消", null);
                                    builder1.create().show();
                                }
                            });

                            Spinner spinner_icon = new Spinner(MapViewActivity.this);
                            spinner_icon.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.9f));
                            List<String> data_list = new ArrayList<String>();
                            data_list.add("梯形");
                            data_list.add("正方形");
                            data_list.add("定位图标");
                            data_list.add("星型图标");
                            data_list.add("圆形图标");
                            //适配器
                            arr_adapter = new ArrayAdapter<String>(MapViewActivity.this, R.layout.my_spinner_item, data_list);
                            //设置样式
                            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //加载适配器
                            spinner_icon.setAdapter(arr_adapter);
                            spinner_icon.setId(i);
                            //spinner_icon.setSelection(0,true);
                            spinner_icon.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
                                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    // TODO Auto-generated method stub
                                    int s = arg0.getId();
                                    userLayerList.get(arg0.getId()).userLayerIcon = arr_adapter.getItem(arg2).toString();//文本说明
                                    mBaiduMap.clear();
                                    sectorShowLTE();
                                    userLayerShow();
                                    mrShowLTE();
                                }

                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub
                                }
                            });


                            Spinner spinner_color = new Spinner(MapViewActivity.this);
                            spinner_color.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.9f));
                            List<String> color_list = new ArrayList<String>();
                            color_list.add("红色");
                            color_list.add("黄色");
                            color_list.add("蓝色");
                            color_list.add("绿色");
                            color_list.add("紫色");
                            color_list.add("橙色");
                            color_list.add("棕色");
                            color_list.add("黑色");
                            color_list.add("白色");
                            //适配器
                            color_adapter = new ArrayAdapter<String>(MapViewActivity.this, R.layout.my_spinner_item, color_list);
                            //设置样式
                            color_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //加载适配器
                            spinner_color.setAdapter(color_adapter);
                            //spinner_color.setSelection(0,true);
                            spinner_color.setId(i);

                            spinner_color.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
                                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    // TODO Auto-generated method stub
                                    userLayerList.get(arg0.getId()).userLayerIconColor = color_adapter.getItem(arg2).toString();//文本说明
                                    mBaiduMap.clear();
                                    sectorShowLTE();
                                    userLayerShow();
                                    mrShowLTE();
                                }

                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub
                                }
                            });

                            TextView tv_iconsize = new TextView(MapViewActivity.this);
                            tv_iconsize.setText("SIZE:");
                            tv_iconsize.setTextSize(11);//sp
                            //tv_iconsize.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
                            tv_iconsize.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));

                            EditText et_iconsize = new EditText(MapViewActivity.this);
                            et_iconsize.setText("10");
                            et_iconsize.setTextSize(11);//sp
                            et_iconsize.clearFocus();
                            et_iconsize.setId(i);
                            et_iconsize.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                            et_iconsize.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus) {
                                        // 此处为得到焦点时的处理内容
                                        return;
                                    } else {
                                        // 此处为失去焦点时的处理内容
                                        if (isNumeric(((EditText) v).getText().toString().trim())) {
                                            try {
                                                userLayerList.get(v.getId()).userLayerIconSize = Math.abs(Double.parseDouble(((EditText) v).getText().toString().trim()));
                                                mBaiduMap.clear();
                                                sectorShowLTE();
                                                userLayerShow();
                                                mrShowLTE();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(MapViewActivity.this, userLayerList.get(v.getId()).userLayerName + ":" + userLayerList.get(v.getId()).userLayerIconSize, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MapViewActivity.this, "图标尺寸的类型为数字，单位为米，请正确输入！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                            userLayerList.get(i).userlayerdelete = buttonLayerDelete;
                            userLayerList.get(i).userLayerCheckBox = checkBoxLayer;
                            //TextView txView= new TextView(MapViewActivity.this);
                            //txView.setText("        ");
                            //txView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
                            tableRow.addView(checkBoxLayer);
                            tableRow.addView(spinner_icon);
                            tableRow.addView(spinner_color);
                            tableRow.addView(tv_iconsize);
                            tableRow.addView(et_iconsize);
                            //tableRow.addView(txView);
                            tableRow.addView(buttonLayerEdit);
                            tableRow.addView(buttonLayerDelete);
                            mapLayerControlTb.addView(tableRow);
                        }
                    } else if (msg.what == 11) {
                        //处理临时log的保存、上传、本地删除
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MapViewActivity.this);
                        builder1.setCancelable(false);
                        builder1.setMessage("上次测试LOG:" + tempLogName + "未正常保存，是否保存？");
                        builder1.setTitle("提示");
                        builder1.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logFileSaveName = tempLogName.substring(tempLogName.indexOf("temp") + 4);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //对原来的临时文件
                                        //oldPath like "mnt/sda/sda1/我.png"
                                        File file = new File(myApplication.logFileSavePath + tempLogName);
                                        //newPath like "mnt/sda/sda1/我的照片.png"
                                        // 如果newPath已经存在文件，不做任何处理的话，调用renameTo此方法会把已存在的文件覆盖掉
                                        file.renameTo(new File(myApplication.logFileSavePath + logFileSaveName));
                                    }
                                }).start();

                                AlertDialog.Builder dlg = new AlertDialog.Builder(MapViewActivity.this);
                                dlg.setCancelable(false);
                                dlg.setTitle("提示");
                                dlg.setMessage("LOG已成功保存在" + myApplication.logFileSavePath + "目录中。\r\n是否同步上传至LOG云服务器？");
                                dlg.setNegativeButton("取消", null);
                                dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //上传log文件
                                                File logUploadFile = new File(myApplication.logFileSavePath + logFileSaveName);
                                                Message msg3 = new Message();
                                                msg3.what = 3;   //发送开始上传LOG消息
                                                handler.sendMessage(msg3);
                                                try {
                                                    new MyFTP().uploadSingleFile(logUploadFile, myApplication.logFilesFtpDir, new MyFTP.UploadProgressListener() {
                                                        @Override
                                                        public void onUploadProgress(String currentStep, long uploadSize, File file) {
                                                            if (currentStep.equals(FtpActivity.FTP_UPLOAD_SUCCESS)) {
                                                                Message msg5 = new Message();
                                                                msg5.what = 5;   //发送上传LOG成功消息
                                                                handler.sendMessage(msg5);
                                                            } else if (currentStep.equals(FtpActivity.FTP_UPLOAD_FAIL)) {
                                                                Message msg4 = new Message();
                                                                msg4.what = 4;   //发送上传LOG失败消息
                                                                handler.sendMessage(msg4);
                                                            } else {

                                                            }
                                                            //return;
                                                        }
                                                    });

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    Message msg4 = new Message();
                                                    msg4.what = 4;   //发送上传LOG失败消息
                                                    handler.sendMessage(msg4);
                                                }
                                            }
                                        }).start();
                                    }
                                });
                                dlg.show();

                            }
                        });

                        builder1.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(myApplication.logFileSavePath + tempLogName);
                                file.delete();
                                Toast.makeText(MapViewActivity.this, "临时LOG已删除！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder1.create().show();
                    } else if (msg.what == 501) {

                        JSONArray jsonArray = mSendRecMessage.getJsonArrayRes();
                        List<List<String>> pksList = JsonTools.jsonToList(jsonArray);

                        Timer pksShowTimer = new Timer();
                        TimerTask pksShowTimerTask = new ParkingPlacesShowTask(pksList);
                        pksShowTimer.schedule(pksShowTimerTask, sectorShowDelay);

                    } else if (msg.what == 502) {
                        tvOutServCountLuchen.setText(String.valueOf(outServiceCount[0]));
                        tvOutServCountLongwan.setText(String.valueOf(outServiceCount[1]));
                        tvOutServCountOuhai.setText(String.valueOf(outServiceCount[2]));
                        tvOutServCountYueqing.setText(String.valueOf(outServiceCount[3]));
                        tvOutServCountRuian.setText(String.valueOf(outServiceCount[4]));
                        tvOutServCountCangnan.setText(String.valueOf(outServiceCount[5]));
                        tvOutServCountYongjia.setText(String.valueOf(outServiceCount[6]));
                        tvOutServCountPingyang.setText(String.valueOf(outServiceCount[7]));
                        tvOutServCountWencheng.setText(String.valueOf(outServiceCount[8]));
                        tvOutServCountTaishun.setText(String.valueOf(outServiceCount[9]));
                        tvOutServCountDongtou.setText(String.valueOf(outServiceCount[10]));
                        tvOutServCountCellLuchen.setText(String.valueOf(outServiceCellCount[0]));
                        tvOutServCountCellLongwan.setText(String.valueOf(outServiceCellCount[1]));
                        tvOutServCountCellOuhai.setText(String.valueOf(outServiceCellCount[2]));
                        tvOutServCountCellYueqing.setText(String.valueOf(outServiceCellCount[3]));
                        tvOutServCountCellRuian.setText(String.valueOf(outServiceCellCount[4]));
                        tvOutServCountCellCangnan.setText(String.valueOf(outServiceCellCount[5]));
                        tvOutServCountCellYongjia.setText(String.valueOf(outServiceCellCount[6]));
                        tvOutServCountCellPingyang.setText(String.valueOf(outServiceCellCount[7]));
                        tvOutServCountCellWencheng.setText(String.valueOf(outServiceCellCount[8]));
                        tvOutServCountCellTaishun.setText(String.valueOf(outServiceCellCount[9]));
                        tvOutServCountCellDongtou.setText(String.valueOf(outServiceCellCount[10]));
                    } else if (msg.what == 601) {
                        String rcvMsg = myUdpClient.getRcvMsg();
                        checkBoxsNbImeiSelect.clear();

                        //if(!rcvMsg.equals("")){
                        rcvMsg = rcvMsg.substring(rcvMsg.indexOf("NbTermsList:") + 12);
                        String[] nbImeiList = rcvMsg.split(",");
                            /*
                            while(rcvMsg.indexOf(",")<=rcvMsg.length()-1){
                                nbImeiList.add(rcvMsg.substring(0,rcvMsg.indexOf(",")));
                                if(rcvMsg.indexOf(",")==rcvMsg.length()){
                                    break;
                                }
                                rcvMsg=rcvMsg.substring(rcvMsg.indexOf(",")+1);
                            }
                            */
                        //}

                        LayoutInflater factory_list = LayoutInflater.from(MapViewActivity.this);
                        View termsSelectView = factory_list.inflate(R.layout.sharegps_select_dialog, null);
                        LinearLayout linearLayoutTermsSelect = (LinearLayout) termsSelectView.findViewById(R.id.linearlayout_sharegpsdialog);

                        for (int i = 0; i < nbImeiList.length; i++) {
                            if (!nbImeiList[i].equals("")) {
                                CheckBox checkBoxNbImei = new CheckBox(MapViewActivity.this);
                                checkBoxNbImei.setText(nbImeiList[i]);
                                linearLayoutTermsSelect.addView(checkBoxNbImei);
                                checkBoxsNbImeiSelect.add(checkBoxNbImei);
                            }
                        }
                        AlertDialog.Builder ad2 = new AlertDialog.Builder(MapViewActivity.this);
                        ad2.setCancelable(false);
                        ad2.setTitle("请选择NB终端(IMEI):");
                        ad2.setView(termsSelectView);
                        ad2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (checkBoxsNbImeiSelect.size() == 0) {
                                    Toast.makeText(MapViewActivity.this, "未发现NB终端，请先启动NB测试软件", Toast.LENGTH_SHORT).show();
                                    myUdpClient.setFlagSendGpsOn(false);
                                    myUdpClient.setFlagUdpThreadSleepOn(false);
                                    myUdpClient.setUdpLife(false);
                                    shareGpsMapButton.setBackgroundColor(Color.TRANSPARENT);
                                    flagShareGpsOn = false;
                                    return;
                                }
                                String nbImei = "";
                                for (int i = 0; i < checkBoxsNbImeiSelect.size(); i++) {
                                    if (checkBoxsNbImeiSelect.get(i).isChecked()) {
                                        nbImei = String.valueOf(checkBoxsNbImeiSelect.get(i).getText());
                                        myUdpClient.nbImei = nbImei;
                                        myUdpClient.setFlagSendGpsOn(true);
                                        myUdpClient.setFlagUdpThreadSleepOn(false);
                                        myUdpClient.setUdpLife(false);
                                        Toast.makeText(MapViewActivity.this, "成功启动与" + nbImei + "的GPS分享", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }
                                Toast.makeText(MapViewActivity.this, "请先选择NB终端", Toast.LENGTH_LONG).show();
                                myUdpClient.setFlagSendGpsOn(false);
                                myUdpClient.setFlagUdpThreadSleepOn(false);
                                myUdpClient.setUdpLife(false);
                                shareGpsMapButton.setBackgroundColor(Color.TRANSPARENT);
                                flagShareGpsOn = false;
                            }
                        });
                        ad2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myUdpClient.setFlagSendGpsOn(false);
                                myUdpClient.setFlagUdpThreadSleepOn(false);
                                myUdpClient.setUdpLife(false);
                                shareGpsMapButton.setBackgroundColor(Color.TRANSPARENT);
                                flagShareGpsOn = false;
                            }
                        });
                        ad2.show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            intManager();
            //manager= new BMapManager();
            //manager.init();
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            SDKInitializer.initialize(getApplicationContext());
            setContentView(R.layout.mapview);

            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            screenWidth = metric.widthPixels;     // 屏幕宽度（像素）
            screenHeight = metric.heightPixels;   // 屏幕高度（像素）

            powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
            wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "yuntu:MyLock");

            sCellHideButton = (Button) findViewById(R.id.map_scellbut);
            sCellTitleBar = (TableRow) findViewById(R.id.map_scelltbr);
            sCellTitleBar2 = (TableRow) findViewById(R.id.map_scelltbr2);
            sCellTableRow = (TableRow) findViewById(R.id.map_scelltbr1);
            nCell1TableRow = (TableRow) findViewById(R.id.map_ncell1tbr);
            nCell2TableRow = (TableRow) findViewById(R.id.map_ncell2tbr);
            nCell3TableRow = (TableRow) findViewById(R.id.map_ncell3tbr);
            nCell4TableRow = (TableRow) findViewById(R.id.map_ncell4tbr);
            colorbarSINRTbr = (TableRow) findViewById(R.id.map_colorbarSINR);
            colorbarMrTbr = (TableRow) findViewById(R.id.map_colorbarMr);
            colorbarMrQuanTbr = (TableRow) findViewById(R.id.map_colorbarMrQuantity);
            mapContorlTbr = (TableRow) findViewById(R.id.map_mapcontroltbr);
            tablerowMapEdit = (TableRow) findViewById(R.id.map_editcontroltbr);
            mapMenuMoreLayout = (LinearLayout) findViewById(R.id.linearlayout_menu_more);
            nbCellControlLayout = (LinearLayout) findViewById(R.id.linearlayout_nbcell);
            mapLayerControlTb = (LinearLayout) findViewById(R.id.map_layercontroltbl);
            cellInfoLinearLayout = (LinearLayout) findViewById(R.id.map_linearlayoutcell);
            logSaveTbr = (TableRow) findViewById(R.id.map_logsavebuttbr);
            logSaveStopTbr = (TableRow) findViewById(R.id.map_logsavestoptbr);
            logPlayTbr = (TableRow) findViewById(R.id.map_logplaytbr);
            logPlayControlTbr = (TableRow) findViewById(R.id.map_logplaycontroltbr);
            tableRowOutServArea = (TableRow) findViewById(R.id.map_outservice_area_tbr);
            tableRowOutServCount = (TableRow) findViewById(R.id.map_outservice_count_tbr);
            tableRowOutServCellCount = (TableRow) findViewById(R.id.map_outservice_countcell_tbr);


            logPlayListView = (ListView) findViewById(R.id.map_scelllistview);

            textViewUpdateMapData = (TextView) findViewById(R.id.tv_mapview_updatemapdata);

            sCellLongTextView = (TextView) findViewById(R.id.map_tvlong);
            sCellLatTextView = (TextView) findViewById(R.id.map_tvlat);
            sysTimeTextView = (TextView) findViewById(R.id.map_tvtime);
            sysDateTextView = (TextView) findViewById(R.id.map_tvdate);
            tvScellTime = (TextView) findViewById(R.id.map_scelltime);
            tvSCellName = (TextView) findViewById(R.id.map_scellnametv);
            tvSCellCI = (TextView) findViewById(R.id.map_scellCItv);
            tvSCellPCI = (TextView) findViewById(R.id.map_scellPCItv);
            tvSCellTAC = (TextView) findViewById(R.id.map_scellTACtv);
            tvSCellDBm = (TextView) findViewById(R.id.map_scelldBmtv);
            tvSCellRsrq = (TextView) findViewById(R.id.map_scellRsrqtv);
            tvSCellSinr = (TextView) findViewById(R.id.map_scellSinrtv);
            tvN1CellName = (TextView) findViewById(R.id.map_ncell1nametv);
            tvN1CellCI = (TextView) findViewById(R.id.map_ncell1CItv);
            tvN1CellPCI = (TextView) findViewById(R.id.map_ncell1PCItv);
            tvN1CellTAC = (TextView) findViewById(R.id.map_ncell1TACtv);
            tvN1CellDBm = (TextView) findViewById(R.id.map_ncell1dBmtv);
            tvN1CellRsrq = (TextView) findViewById(R.id.map_ncell1Rsrqtv);
            tvN1CellSinr = (TextView) findViewById(R.id.map_ncell1Sinrtv);
            tvN2CellName = (TextView) findViewById(R.id.map_ncell2nametv);
            tvN2CellCI = (TextView) findViewById(R.id.map_ncell2CItv);
            tvN2CellPCI = (TextView) findViewById(R.id.map_ncell2PCItv);
            tvN2CellTAC = (TextView) findViewById(R.id.map_ncell2TACtv);
            tvN2CellDBm = (TextView) findViewById(R.id.map_ncell2dBmtv);
            tvN2CellRsrq = (TextView) findViewById(R.id.map_ncell2Rsrqtv);
            tvN2CellSinr = (TextView) findViewById(R.id.map_ncell2Sinrtv);
            tvN3CellName = (TextView) findViewById(R.id.map_ncell3nametv);
            tvN3CellCI = (TextView) findViewById(R.id.map_ncell3CItv);
            tvN3CellPCI = (TextView) findViewById(R.id.map_ncell3PCItv);
            tvN3CellTAC = (TextView) findViewById(R.id.map_ncell3TACtv);
            tvN3CellDBm = (TextView) findViewById(R.id.map_ncell3dBmtv);
            tvN3CellRsrq = (TextView) findViewById(R.id.map_ncell3Rsrqtv);
            tvN3CellSinr = (TextView) findViewById(R.id.map_ncell3Sinrtv);
            tvN4CellName = (TextView) findViewById(R.id.map_ncell4nametv);
            tvN4CellCI = (TextView) findViewById(R.id.map_ncell4CItv);
            tvN4CellPCI = (TextView) findViewById(R.id.map_ncell4PCItv);
            tvN4CellTAC = (TextView) findViewById(R.id.map_ncell4TACtv);
            tvN4CellDBm = (TextView) findViewById(R.id.map_ncell4dBmtv);
            tvN4CellRsrq = (TextView) findViewById(R.id.map_ncell4Rsrqtv);
            tvN4CellSinr = (TextView) findViewById(R.id.map_ncell4Sinrtv);

            tvOutServCountLuchen = (TextView) findViewById(R.id.tv_mapview_outServCount_lucheng);
            tvOutServCountLongwan = (TextView) findViewById(R.id.tv_mapview_outServCount_longwan);
            tvOutServCountOuhai = (TextView) findViewById(R.id.tv_mapview_outServCount_ouhai);
            tvOutServCountYueqing = (TextView) findViewById(R.id.tv_mapview_outServCount_yuqing);
            tvOutServCountRuian = (TextView) findViewById(R.id.tv_mapview_outServCount_ruian);
            tvOutServCountCangnan = (TextView) findViewById(R.id.tv_mapview_outServCount_cangnan);
            tvOutServCountYongjia = (TextView) findViewById(R.id.tv_mapview_outServCount_yongjia);
            tvOutServCountPingyang = (TextView) findViewById(R.id.tv_mapview_outServCount_pingyang);
            tvOutServCountWencheng = (TextView) findViewById(R.id.tv_mapview_outServCount_wencheng);
            tvOutServCountTaishun = (TextView) findViewById(R.id.tv_mapview_outServCount_taishun);
            tvOutServCountDongtou = (TextView) findViewById(R.id.tv_mapview_outServCount_dongtou);

            tvOutServCountCellLuchen = (TextView) findViewById(R.id.tv_mapview_outServCountCell_lucheng);
            tvOutServCountCellLongwan = (TextView) findViewById(R.id.tv_mapview_outServCountCell_longwan);
            tvOutServCountCellOuhai = (TextView) findViewById(R.id.tv_mapview_outServCountCell_ouhai);
            tvOutServCountCellYueqing = (TextView) findViewById(R.id.tv_mapview_outServCountCell_yuqing);
            tvOutServCountCellRuian = (TextView) findViewById(R.id.tv_mapview_outServCountCell_ruian);
            tvOutServCountCellCangnan = (TextView) findViewById(R.id.tv_mapview_outServCountCell_cangnan);
            tvOutServCountCellYongjia = (TextView) findViewById(R.id.tv_mapview_outServCountCell_yongjia);
            tvOutServCountCellPingyang = (TextView) findViewById(R.id.tv_mapview_outServCountCell_pingyang);
            tvOutServCountCellWencheng = (TextView) findViewById(R.id.tv_mapview_outServCountCell_wencheng);
            tvOutServCountCellTaishun = (TextView) findViewById(R.id.tv_mapview_outServCountCell_taishun);
            tvOutServCountCellDongtou = (TextView) findViewById(R.id.tv_mapview_outServCountCell_dongtou);

            tvLevelGreen = (TextView) findViewById(R.id.tv_mapview_levelgreen);
            tvLevelLightGreen = (TextView) findViewById(R.id.tv_mapview_levelligtgreen);
            tvLevelBlue = (TextView) findViewById(R.id.tv_mapview_levelblue);
            tvLevelYellow = (TextView) findViewById(R.id.tv_mapview_levelyellow);
            tvLevelRed = (TextView) findViewById(R.id.tv_mapview_levelred);
            tvLevelBlack = (TextView) findViewById(R.id.tv_mapview_levelblack);
            tvLevelGreen.setOnClickListener(this);
            tvLevelLightGreen.setOnClickListener(this);
            tvLevelBlue.setOnClickListener(this);
            tvLevelYellow.setOnClickListener(this);
            tvLevelRed.setOnClickListener(this);
            tvLevelBlack.setOnClickListener(this);

            changeMapButton = (ImageButton) findViewById(R.id.map_changemap);
            searchMapButton = (ImageButton) findViewById(R.id.map_search);
            menuMoreButton = (ImageButton) findViewById(R.id.map_menu_more_but);
            distanceMapButton = (ImageButton) findViewById(R.id.map_menu_distance);
            locateMapButton = (ImageButton) findViewById(R.id.map_menu_locate);
            polylineSelectMapButton = (ImageButton) findViewById(R.id.map_menu_polyline);
            circleSelectMapButton = (ImageButton) findViewById(R.id.map_menu_circle);
            specialTestMapButton = (ImageButton) findViewById(R.id.map_menu_specialtest);
            logSaveButton = (ImageButton) findViewById(R.id.map_logsavebut);
            logSaveStopButton = (ImageButton) findViewById(R.id.map_logsavestop);
            locCenterButton = (ImageButton) findViewById(R.id.map_mylocbut);
            clearOverlayButton = (ImageButton) findViewById(R.id.map_clearoverlay);
            logPlayButton = (ImageButton) findViewById(R.id.map_logplay);
            logPlayCloseButton = (ImageButton) findViewById(R.id.map_logplayclose);
            logPlayStopButton = (ImageButton) findViewById(R.id.map_logplaystop);
            logPlayFastButton = (ImageButton) findViewById(R.id.map_logplayfast);
            logPlaySlowButton = (ImageButton) findViewById(R.id.map_logplayslow);
            logPlayEndButton = (ImageButton) findViewById(R.id.map_logplayend);
            logPlayStartButton = (ImageButton) findViewById(R.id.map_logplaystart);
            buttonMapEditOk = (ImageButton) findViewById(R.id.map_editok_but);
            buttonMapEditCancel = (ImageButton) findViewById(R.id.map_editcancel_but);
            buttonMapEditStatic = (ImageButton) findViewById(R.id.map_editstatic_but);
            buttonPowerSaving = (ImageButton) findViewById(R.id.map_menu_savepower);
            buttonOritation = (ImageButton) findViewById(R.id.map_menu_oritation);
            buttonWaterCamera = (ImageButton) findViewById(R.id.map_menu_watercamera);
            buttonTakePhoto = (ImageButton) findViewById(R.id.btn_map_takephoto);
            buttonOutService = (ImageButton) findViewById(R.id.map_menu_outservice_but);
            buttonOutServiceList = (ImageButton) findViewById(R.id.map_menu_outservicelist_but);
            shareGpsMapButton = (ImageButton) findViewById(R.id.map_menu_sharegps);
            webTestMapButton = (ImageButton) findViewById(R.id.map_menu_webtest);
            sreenOnMapButton = (ImageButton) findViewById(R.id.map_menu_sreenOn);
            textReaderMapButton = (ImageButton) findViewById(R.id.map_menu_textReader);

            radioGroupMapLayer = (RadioGroup) findViewById(R.id.rdgroup_maplayer);
            radioGroupMrLayer = (RadioGroup) findViewById(R.id.rdgroup_mrlayer);
            radioButtonMapNormal = (RadioButton) findViewById(R.id.rbut_mapnormal);
            radioButtonMapSat = (RadioButton) findViewById(R.id.rbut_mapsatlite);
            buttonNewLayer = (Button) findViewById(R.id.button_newlayer);
            checkBoxSector = (CheckBox) findViewById(R.id.checkBox_sector);
            checkBoxMrLayer = (CheckBox) findViewById(R.id.checkBox_mrlayer);
            checkBoxMrInfo = (CheckBox) findViewById(R.id.checkBox_mrinfo);
            editTextMrRadius = (EditText) findViewById(R.id.et_map_mrradius);
            checkBoxSinr = (CheckBox) findViewById(R.id.checkBox_sinr);
            checkBoxKeepLine = (CheckBox) findViewById(R.id.checkBox_keepscell_line);
            checkBoxSectorGsm = (CheckBox) findViewById(R.id.checkBox_sector_GSM);
            checkBoxSectorFdd = (CheckBox) findViewById(R.id.checkBox_sector_fdd);
            checkBoxSectorNb = (CheckBox) findViewById(R.id.checkBox_sector_nbiot);

            sCellHideButton.setOnClickListener(this);
            clearOverlayButton.setOnClickListener(this);
            changeMapButton.setOnClickListener(this);
            searchMapButton.setOnClickListener(this);
            menuMoreButton.setOnClickListener(this);
            distanceMapButton.setOnClickListener(this);
            locateMapButton.setOnClickListener(this);
            polylineSelectMapButton.setOnClickListener(this);
            circleSelectMapButton.setOnClickListener(this);
            specialTestMapButton.setOnClickListener(this);
            logSaveButton.setOnClickListener(this);
            logSaveStopButton.setOnClickListener(this);
            locCenterButton.setOnClickListener(this);

            checkBoxSector.setOnClickListener(this);
            checkBoxSectorGsm.setOnClickListener(this);
            checkBoxSectorFdd.setOnClickListener(this);
            checkBoxSectorNb.setOnClickListener(this);

            checkBoxMrLayer.setOnClickListener(this);
            checkBoxMrInfo.setOnClickListener(this);
            checkBoxSinr.setOnClickListener(this);
            checkBoxKeepLine.setOnClickListener(this);
            buttonNewLayer.setOnClickListener(this);
            logPlayButton.setOnClickListener(this);
            logPlayCloseButton.setOnClickListener(this);
            logPlayStopButton.setOnClickListener(this);
            logPlayFastButton.setOnClickListener(this);
            logPlaySlowButton.setOnClickListener(this);
            logPlayEndButton.setOnClickListener(this);
            logPlayStartButton.setOnClickListener(this);
            buttonMapEditOk.setOnClickListener(this);
            buttonMapEditCancel.setOnClickListener(this);
            buttonMapEditStatic.setOnClickListener(this);
            buttonPowerSaving.setOnClickListener(this);
            buttonOritation.setOnClickListener(this);
            buttonWaterCamera.setOnClickListener(this);
            buttonTakePhoto.setOnClickListener(this);
            textViewUpdateMapData.setOnClickListener(this);
            buttonOutService.setOnClickListener(this);
            buttonOutServiceList.setOnClickListener(this);
            shareGpsMapButton.setOnClickListener(this);
            webTestMapButton.setOnClickListener(this);
            sreenOnMapButton.setOnClickListener(this);
            textReaderMapButton.setOnClickListener(this);

            //.setOnClickListener(this);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrowicon2_s_512_512_deg0);
            Bitmap sBitmap = scaleBitmap(bitmap, 64);
            arrowbitmap = BitmapDescriptorFactory.fromBitmap(sBitmap);

            editTextMrRadius.setText(String.valueOf(Math.round(myApplication.MR_MAP_SHOW_R)));
            editTextMrRadius.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // 此处为得到焦点时的处理内容
                        return;
                    } else {
                        // 此处为失去焦点时的处理内容
                        if (isNumeric(editTextMrRadius.getText().toString().trim())) {
                            try {
                                myApplication.MR_MAP_SHOW_R = Math.abs(Double.parseDouble(editTextMrRadius.getText().toString().trim()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (myApplication.MR_MAP_SHOW_R > 500) {
                                Toast.makeText(MapViewActivity.this, "MR显示半径设置过大可能导致程序异常退出，请谨慎设置！", Toast.LENGTH_LONG).show();
                                flagAutoCenterMap = false;
                            }
                        } else {
                            Toast.makeText(MapViewActivity.this, "MR显示半径为整数，单位为米，请正确输入！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            radioGroupMapLayer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                    switch (checkedId) {
                        case R.id.rbut_mapnormal:
                            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                            mapLayerControlTb.setVisibility(View.GONE);
                            mapMenuShow = false;
                            break;
                        case R.id.rbut_mapsatlite:
                            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                            mapLayerControlTb.setVisibility(View.GONE);
                            mapMenuShow = false;
                            break;
                        default:
                            break;
                    }
                }
            });

            radioGroupMrLayer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                    switch (checkedId) {
                        case R.id.rbut_mrcoverpercent:
                            mapLayerControlTb.setVisibility(View.GONE);
                            flagMrShowContent = 1;
                            if (checkBoxMrLayer.isChecked()) {
                                colorbarMrTbr.setVisibility(View.VISIBLE);
                                colorbarMrQuanTbr.setVisibility(View.GONE);
                            }
                            if (flagMrLayerShow) {
                                flagMrLayerMapMoved = true;
                                mrShowLTE();
                            }
                            break;
                        case R.id.rbut_mrpoorquantity:
                            mapLayerControlTb.setVisibility(View.GONE);
                            flagMrShowContent = 2;
                            myApplication.preMrArea = "DEFAULT";
                            if (checkBoxMrLayer.isChecked()) {
                                colorbarMrTbr.setVisibility(View.VISIBLE);
                                colorbarMrQuanTbr.setVisibility(View.GONE);
                            }
                            if (flagMrLayerShow) {
                                flagMrLayerMapMoved = true;
                                mrShowLTE();
                            }
                            break;
                        case R.id.rbut_mrtotalquantity:
                            mapLayerControlTb.setVisibility(View.GONE);
                            flagMrShowContent = 3;
                            if (checkBoxMrLayer.isChecked()) {
                                colorbarMrTbr.setVisibility(View.GONE);
                                colorbarMrQuanTbr.setVisibility(View.VISIBLE);
                            }
                            if (flagMrLayerShow) {
                                flagMrLayerMapMoved = true;
                                mrShowLTE();
                            }
                            break;
                        default:
                            break;
                    }
                }
            });

            myApplication = (MyApplication) getApplicationContext();

            myApplication.screenWidth = screenWidth;
            myApplication.screenHeigh = screenHeight;
            //MyApplication.COLLECT_PEROID = MyApplication.COLLECT_PEROID / 4; //地图模式下，加快信号采样与打点速度;
            hmPos = new LatLng(myApplication.newGpsLat, myApplication.newGpsLong);
            init();
            sysDateTextView.setText(myApplication.myPhoneInfo1.sysTime.substring(0, myApplication.myPhoneInfo1.sysTime.indexOf("_")));

            MyDatabaseHelper dbhelper_sec = new MyDatabaseHelper(MapViewActivity.this, CreateCellInfoDBActivity.DBNAME1, CreateCellInfoDBActivity.TABLENAME1, null, 1);
            db_sec = dbhelper_sec.getWritableDatabase();
            MyDatabaseHelper dbhelper_mr = new MyDatabaseHelper(MapViewActivity.this, CreateCellInfoDBActivity.DBNAME1, CreateCellInfoDBActivity.TABLENAME_MR, null, 1);
            db_mr = dbhelper_mr.getWritableDatabase();


            mBaiduMap = mapView.getMap();

            mBaiduMap.setOnPolylineClickListener(new BaiduMap.OnPolylineClickListener() {
                int mkey = 0;
                String polylineinfo;

                @Override
                public boolean onPolylineClick(Polyline polyline) {
                    try {
                        if (flagAutoCenterMap) {
                            flagAutoCenterMap = false;
                            Toast.makeText(MapViewActivity.this, "手动地图模式", Toast.LENGTH_SHORT).show();
                        }
                        if (polyline.getExtraInfo().containsKey("keylogid")) {
                            mkey = 1;
                            if (flagLogPlaying) {
                                logPlayTimer.cancel();
                                centerMaptimer = 0;
                                flagLogPlaying = false;
                                logPlayStopButton.setImageResource(android.R.drawable.ic_media_play);
                            }
                            flagLogPointSelect = true;
                            logIdSelected = Integer.parseInt((String) polyline.getExtraInfo().get("keylogid"));
                            logPlayTimer = new Timer();
                            logPlayTask = new LogSelectShowTask();
                            //long logdelay=0;
                            logPlayTimer.schedule(logPlayTask, 0);
                            return true;
                        } else if (polyline.getExtraInfo().containsKey("keyCellName")) {
                            mkey = 2;
                            polylineinfo = (String) polyline.getExtraInfo().get("keyCellName");
                        } else if (polyline.getExtraInfo().containsKey("keyMr")) {
                            mkey = 3;
                            polylineinfo = (String) polyline.getExtraInfo().get("keyMr");
                        } else if (polyline.getExtraInfo().containsKey("keyCellNameGsm")) {
                            mkey = 4;
                            polylineinfo = (String) polyline.getExtraInfo().get("keyCellNameGsm");
                        }
                    } catch (Exception e) {
                        return true;
                    }
                    //Toast.makeText(MapViewActivity.this,"小区:"+polylineinfo,Toast.LENGTH_SHORT).show();
                    //动态生成一个Button对象，用户在地图中显示InfoWindow
                    final Button textInfo = new Button(getApplicationContext());
                    textInfo.setBackgroundColor(Color.CYAN);
                    //textInfo.setBackgroundResource(R.drawable.grape_pic);
                    textInfo.setPadding(20, 20, 20, 20);
                    textInfo.setTextColor(Color.BLACK);
                    textInfo.setTextSize(12);
                    textInfo.setText(polylineinfo);
                    //得到点击的覆盖物的经纬度
                    List<LatLng> polylineList = polyline.getPoints();
                    textInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mkey == 2) {
                                Toast.makeText(MapViewActivity.this, "小区详细信息", Toast.LENGTH_LONG).show();
                                myApplication.link_CellId = polylineinfo.substring(polylineinfo.indexOf("CI:") + 3, polylineinfo.indexOf(" ENODEB"));
                                Intent intent = new Intent(MapViewActivity.this, CellInfoWebViewActivity.class);
                                startActivity(intent);
                                mBaiduMap.hideInfoWindow();
                            } else if (mkey == 3) {
                                mBaiduMap.hideInfoWindow();
                            } else if (mkey == 4) {
                                myApplication.link_CellIdGsm = polylineinfo.substring(polylineinfo.indexOf("CI:") + 3, polylineinfo.indexOf(" 小区名:"));
                                Intent intent = new Intent(MapViewActivity.this, CellInfoGsmWebViewActivity.class);
                                startActivity(intent);
                                mBaiduMap.hideInfoWindow();
                            }
                        }
                    });
                    //将marker所在的经纬度的信息转化成屏幕上的坐标
                    Point p = mBaiduMap.getProjection().toScreenLocation(polylineList.get(0));
                    p.y -= 90;
                    LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                    //初始化infoWindow，最后那个参数表示显示的位置相对于覆盖物的竖直偏移量，这里也可以传入一个监听器
                    infoWindow = new InfoWindow(textInfo, llInfo, 0);
                    mBaiduMap.showInfoWindow(infoWindow);//显示此infoWindow
                    return false;
                }
            });

            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                String markerinfo = "";
                int mkey = 0;

                @Override
                public boolean onMarkerClick(Marker marker) {
                    // TODO Auto-generated method stub
                    try {
                        if (flagAutoCenterMap) {
                            flagAutoCenterMap = false;
                            Toast.makeText(MapViewActivity.this, "手动地图模式", Toast.LENGTH_SHORT).show();
                        }
                        if (marker.getExtraInfo().containsKey("keyMr")) {
                            mkey = 3;
                            markerinfo = (String) marker.getExtraInfo().get("keyMr");
                        }
                    } catch (Exception e) {
                        return true;
                    }
                    //动态生成一个Button对象，用户在地图中显示InfoWindow
                    Button textInfo = new Button(getApplicationContext());
                    textInfo.setBackgroundColor(Color.CYAN);
                    //textInfo.setBackgroundResource(R.drawable.grape_pic);
                    textInfo.setPadding(20, 20, 20, 20);
                    textInfo.setTextColor(Color.BLACK);
                    textInfo.setTextSize(12);
                    textInfo.setText(markerinfo);
                    //得到点击的覆盖物的经纬度
                    LatLng markPos = marker.getPosition();
                    textInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mkey == 3) {
                                mBaiduMap.hideInfoWindow();
                            }
                        }
                    });
                    //将marker所在的经纬度的信息转化成屏幕上的坐标
                    Point p = mBaiduMap.getProjection().toScreenLocation(markPos);
                    p.y -= 90;
                    LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                    //初始化infoWindow，最后那个参数表示显示的位置相对于覆盖物的竖直偏移量，这里也可以传入一个监听器
                    infoWindow = new InfoWindow(textInfo, llInfo, 0);
                    mBaiduMap.showInfoWindow(infoWindow);//显示此infoWindow
                    return false;
                }
            });

            mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
                LatLng startLng, finishLng;

                @Override
                public void onMapStatusChangeStart(MapStatus mapStatus) {
                    startLng = mapStatus.target;
                    mapDragStartTime = System.currentTimeMillis();
                }

                @Override
                public void onMapStatusChangeFinish(MapStatus mapStatus) {
                    // 滑动搜索
                    //System.out.println("ZGQ:你轻微移动了地图1!");
                    finishLng = mapStatus.target;
                    mapDragEndTime = System.currentTimeMillis();
                    mapDragTimeIntervel = mapDragTimeIntervel + (mapDragEndTime - mapDragStartTime);
                    if (startLng.latitude != finishLng.latitude || startLng.longitude != finishLng.longitude) {
                        double x = (finishLng.longitude - startLng.longitude) * 100000;
                        double y = (finishLng.latitude - startLng.latitude) * 100000;
                        if (Math.abs(x) > 20 || Math.abs(y) > 20) {
                            //在这处理滑动
                            //System.out.println("ZGQ:你移动了地图>100px!");
                            mapdragdistX = mapdragdistX + x;
                            mapdragdistY = mapdragdistY + y;
                            //mapdrapDistance=mapdrapDistance+mdis;
                            //if(Math.abs(mapdragdistX)>=screenWidth*3/10 || Math.abs(mapdragdistY)>=screenHeight*3/10){
                            if ((Math.abs(mapdragdistX) >= (myApplication.SECTOR_MAP_SHOW_R * 6 / 10) || Math.abs(mapdragdistY) >= (myApplication.SECTOR_MAP_SHOW_R * 6 / 10)) && mapDragTimeIntervel > 500) {
                                mapDragTimeIntervel = 0;
                                centerMaptimer = 0;
                                mapdragdistX = 0;
                                mapdragdistY = 0;
                                //mapdrapDistance=0;
                                flagSectorMapMoved = true;
                                flagSectorGsmMapMoved = true;
                                flagSectorFddMapMoved = true;
                                flagSectorNbiotMapMoved = true;
                                flagMrLayerMapMoved = true;

                                for (int i = 0; i < numsUserLayer; i++) {
                                    flagUserLayerMapMovedList.set(i, true);
                                }
                                LatLng bdLng = PositionUtility.bd09_To_Gps84(finishLng.latitude, finishLng.longitude);
                                myApplication.dragMapCenLong = bdLng.longitude;
                                myApplication.dragMapCenLat = bdLng.latitude;
                                sectorShowLTE();
                                mrShowLTE();
                                userLayerShow();
                            }
                        }
                    }
                }

                @Override
                public void onMapStatusChange(MapStatus mapStatus) {
                    //System.out.println("ZGQ:自动居中");
                }
            });

            mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    try {
                        mBaiduMap.hideInfoWindow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public boolean onMapPoiClick(MapPoi mapPoi) {
                    return false;
                }
            });

            mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng latLng) {

                    try {
                        if (flagMapPolySelect) {
                            //图层圈选模式
                            OverlayOptions dotOp = new DotOptions().center(latLng).color(Color.CYAN).radius(12);
                            mapPolySelectOverlays.add(mBaiduMap.addOverlay(dotOp));
                            if (mapPolySelectPoints.size() == 0) {
                                mapPolySelectfirstPoint = latLng;
                                mapPolySelectPoints.add(latLng);
                            } else {
                                List<LatLng> linePoints = new ArrayList<LatLng>();
                                linePoints.add(mapPolySelectPoints.get(mapPolySelectPoints.size() - 1));
                                linePoints.add(latLng);
                                OverlayOptions lineOp = new PolylineOptions().color(Color.BLUE).width(8).points(linePoints);
                                mapPolySelectOverlays.add(mBaiduMap.addOverlay(lineOp));
                                mapPolySelectPoints.add(latLng);
                            }

                        } else if (flagUserLayerEdit && !flagMapDistance) {
                            //自定义图层编辑模式
                            OverlayOptions dotOp = new DotOptions().center(latLng).color(Color.CYAN).radius(12);
                            mBaiduMap.addOverlay(dotOp);

                            //LatLng dotGps = PositionUtility.bd09_To_Gps84(latLng.latitude, latLng.longitude);
                            LatLng dotGps = new LatLng(latLng.latitude - logPoint.latitude + myApplication.newGpsLat, latLng.longitude - logPoint.longitude + myApplication.newGpsLong);

                            Button textInfo_dot = new Button(getApplicationContext());
                            textInfo_dot.setBackgroundColor(Color.CYAN);
                            textInfo_dot.setPadding(20, 20, 20, 20);
                            textInfo_dot.setTextColor(Color.BLACK);
                            textInfo_dot.setTextSize(12);
                            textInfo_dot.setText("经度:" + Math.round(dotGps.longitude * 100000) / 100000.0 + "\r\n" + "纬度:" + Math.round(dotGps.latitude * 100000) / 100000.0 + "\r\n" + "(点击编辑详细信息)");
                            //得到点击的覆盖物的经纬度
                            textInfo_dot.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mBaiduMap.hideInfoWindow();

                                }
                            });
                            //将marker所在的经纬度的信息转化成屏幕上的坐标
                            Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
                            p.y -= 90;
                            LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                            //初始化infoWindow，最后那个参数表示显示的位置相对于覆盖物的竖直偏移量，这里也可以传入一个监听器
                            infoWindow = new InfoWindow(textInfo_dot, llInfo, 0);
                            mBaiduMap.showInfoWindow(infoWindow);//显示此infoWindow
                        } else if (flagMapDistance && !flagUserLayerEdit) {
                            //地图测距模式
                            OverlayOptions dotOp = new DotOptions().center(latLng).color(Color.CYAN).radius(12);
                            mBaiduMap.addOverlay(dotOp);
                            if (dotA_Distance == null) {
                                //LatLng dotGps = PositionUtility.bd09_To_Gps84(latLng.latitude, latLng.longitude);

                                LatLng dotGps = new LatLng(latLng.latitude - logPoint.latitude + myApplication.newGpsLat, latLng.longitude - logPoint.longitude + myApplication.newGpsLong);

                                Button textInfo_dot = new Button(getApplicationContext());
                                textInfo_dot.setBackgroundColor(Color.CYAN);
                                textInfo_dot.setPadding(20, 20, 20, 20);
                                textInfo_dot.setTextColor(Color.BLACK);
                                textInfo_dot.setTextSize(12);
                                textInfo_dot.setText("经度:" + Math.round(dotGps.longitude * 100000) / 100000.0 + "\r\n" + "纬度:" + Math.round(dotGps.latitude * 100000) / 100000.0 + "\r\n" + "(点击编辑详细信息)");
                                //得到点击的覆盖物的经纬度
                                textInfo_dot.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mBaiduMap.hideInfoWindow();
                                    }
                                });
                                //将marker所在的经纬度的信息转化成屏幕上的坐标
                                Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
                                p.y -= 90;
                                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                                //初始化infoWindow，最后那个参数表示显示的位置相对于覆盖物的竖直偏移量，这里也可以传入一个监听器
                                infoWindow = new InfoWindow(textInfo_dot, llInfo, 0);
                                mBaiduMap.showInfoWindow(infoWindow);//显示此infoWindow
                                dotA_Distance = latLng;
                            } else {
                                List<LatLng> disPoints = new ArrayList<LatLng>();
                                disPoints.add(dotA_Distance);
                                disPoints.add(latLng);
                                OverlayOptions distanceLineOp = new PolylineOptions().color(Color.BLUE).width(8).points(disPoints);
                                mBaiduMap.addOverlay(distanceLineOp);
                                mBaiduMap.hideInfoWindow();
                                Button textInfo_dist = new Button(getApplicationContext());
                                textInfo_dist.setBackgroundColor(Color.CYAN);
                                textInfo_dist.setPadding(20, 20, 20, 20);
                                textInfo_dist.setTextColor(Color.BLACK);
                                textInfo_dist.setTextSize(12);
                                double dis = (Math.round(DistanceUtil.getDistance(latLng, dotA_Distance) * 100) / 100.0);
                                double tpdir = Math.round(getAngle(dotA_Distance, latLng) * 10) / 10.0;
                                textInfo_dist.setText("距离:" + dis + "米" + " 方向:" + tpdir + "度");
                                //得到点击的覆盖物的经纬度
                                textInfo_dist.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mBaiduMap.hideInfoWindow();
                                    }
                                });
                                //将marker所在的经纬度的信息转化成屏幕上的坐标
                                Point p1 = mBaiduMap.getProjection().toScreenLocation(latLng);
                                p1.y -= 90;
                                LatLng llInfo1 = mBaiduMap.getProjection().fromScreenLocation(p1);
                                //初始化infoWindow，最后那个参数表示显示的位置相对于覆盖物的竖直偏移量，这里也可以传入一个监听器
                                infoWindow = new InfoWindow(textInfo_dist, llInfo1, 0);
                                mBaiduMap.showInfoWindow(infoWindow);//显示此infoWindow
                                dotA_Distance = null;
                            }

                        } else if (flagMapDistance && flagUserLayerEdit) {
                            Toast.makeText(MapViewActivity.this, "测距模式和编辑图层模式不能同时开启，请先关闭其中一项任务", Toast.LENGTH_LONG).show();
                        } else {
                            //return;
                            //mBaiduMap.hideInfoWindow();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            sectorShowLTE();
            ThreadShow mapThreadShow = new ThreadShow();
            new Thread(mapThreadShow).start();
            UserLayerColector userLayerColector = new UserLayerColector();
            new Thread(userLayerColector).start();

            Timer tempLogCheckTimer = new Timer();
            TempLogCheck tempLogCheckTask = new TempLogCheck();
            tempLogCheckTimer.schedule(tempLogCheckTask, 1000);

            if (myApplication.USER_AUTH_INFO.vipUser == 1) {
                buttonOutService.setVisibility(View.VISIBLE);
            }

            try {
                myApplication.locationManager_app.removeUpdates(myApplication.locationListener_app);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                    return;
                }
                myApplication.locationManager_app.requestLocationUpdates(myApplication.gpsProvider_app, 3000, 1, myApplication.locationListener_app);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MyUdpClient myUdpClient;
    private int mLevelThres = 0;

    void editLevelThres(final int levelThres) {
        //LayoutInflater factory = LayoutInflater.from(this);
        //final View textEntryView = factory.inflate(R.layout.leveledit_input_dialog, null);
        //final EditText editTextLevelEdit = (EditText) textEntryView.findViewById(R.id.et_map_dialog_leveledit);
        final EditText editTextLevelEdit = new EditText(MapViewActivity.this);
        if (levelThres == 1) {
            editTextLevelEdit.setText(myApplication.levelGreen + "");
        } else if (levelThres == 2) {
            editTextLevelEdit.setText(myApplication.levelLightGreen + "");
        } else if (levelThres == 3) {
            editTextLevelEdit.setText(myApplication.levelBlue + "");
        } else if (levelThres == 4) {
            editTextLevelEdit.setText(myApplication.levelYellow + "");
        } else if (levelThres == 5) {
            editTextLevelEdit.setText(myApplication.levelRed + "");
        } else if (levelThres == 6) {
            editTextLevelEdit.setText(myApplication.levelBlack + "");
        }

        AlertDialog.Builder ad1 = new AlertDialog.Builder(MapViewActivity.this);
        ad1.setCancelable(false);
        ad1.setTitle("请输入门限值:");
        ad1.setView(editTextLevelEdit);
        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                try {
                    List<String> keys = new ArrayList<String>();
                    List<String> values = new ArrayList<String>();
                    int inputLevel = Integer.parseInt(editTextLevelEdit.getText().toString().trim());
                    if (levelThres == 1 && inputLevel > myApplication.levelLightGreen) {
                        myApplication.levelGreen = inputLevel;
                        keys.add("levelGreen");
                        values.add(inputLevel + "");
                        AppSettingActivity.saveSettingInfo(MapViewActivity.this, "levelThresConfig", keys, values);
                        tvLevelGreen.setText(inputLevel + "");
                    } else if (levelThres == 2 && inputLevel > myApplication.levelBlue && inputLevel < myApplication.levelGreen) {
                        myApplication.levelLightGreen = inputLevel;
                        keys.add("levelLightGreen");
                        values.add(inputLevel + "");
                        AppSettingActivity.saveSettingInfo(MapViewActivity.this, "levelThresConfig", keys, values);
                        tvLevelLightGreen.setText(inputLevel + "");
                    } else if (levelThres == 3 && inputLevel > myApplication.levelYellow && inputLevel < myApplication.levelLightGreen) {
                        myApplication.levelBlue = inputLevel;
                        keys.add("levelBlue");
                        values.add(inputLevel + "");
                        AppSettingActivity.saveSettingInfo(MapViewActivity.this, "levelThresConfig", keys, values);
                        tvLevelBlue.setText(inputLevel + "");
                    } else if (levelThres == 4 && inputLevel > myApplication.levelRed && inputLevel < myApplication.levelBlue) {
                        myApplication.levelYellow = inputLevel;
                        keys.add("levelYellow");
                        values.add(inputLevel + "");
                        AppSettingActivity.saveSettingInfo(MapViewActivity.this, "levelThresConfig", keys, values);
                        tvLevelYellow.setText(inputLevel + "");
                    } else if (levelThres == 5 && inputLevel > myApplication.levelBlack && inputLevel < myApplication.levelYellow) {
                        myApplication.levelRed = inputLevel;
                        keys.add("levelRed");
                        values.add(inputLevel + "");
                        AppSettingActivity.saveSettingInfo(MapViewActivity.this, "levelThresConfig", keys, values);
                        tvLevelRed.setText(inputLevel + "");
                    } else if (levelThres == 6 && inputLevel < myApplication.levelRed) {
                        myApplication.levelBlack = inputLevel;
                        keys.add("levelBlack");
                        values.add(inputLevel + "");
                        AppSettingActivity.saveSettingInfo(MapViewActivity.this, "levelThresConfig", keys, values);
                        tvLevelBlack.setText(inputLevel + "");
                    } else {
                        Toast.makeText(MapViewActivity.this, "电平门限超过范围，请重新输入！", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MapViewActivity.this, "电平门限格式不正确，请重新输入！", Toast.LENGTH_LONG).show();
                }
            }
        });
        ad1.setNegativeButton("取消", null);
        ad1.show();// 显示对话框
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tv_mapview_levelgreen:
                    mLevelThres = 1;
                    editLevelThres(mLevelThres);
                    break;
                case R.id.tv_mapview_levelligtgreen:
                    mLevelThres = 2;
                    editLevelThres(mLevelThres);
                    break;
                case R.id.tv_mapview_levelblue:
                    mLevelThres = 3;
                    editLevelThres(mLevelThres);
                    break;
                case R.id.tv_mapview_levelyellow:
                    mLevelThres = 4;
                    editLevelThres(mLevelThres);
                    break;
                case R.id.tv_mapview_levelred:
                    mLevelThres = 5;
                    editLevelThres(mLevelThres);
                    break;
                case R.id.tv_mapview_levelblack:
                    mLevelThres = 6;
                    editLevelThres(mLevelThres);
                    break;
                case R.id.map_menu_webtest:
                    startActivity(new Intent(MapViewActivity.this, WebTestPlanActivity.class));
                    break;
                case R.id.map_menu_sreenOn:
                    if (flagSreenOn == false) {
                        wakeLock.acquire();
                        sreenOnMapButton.setBackgroundColor(Color.GREEN);
                        flagSreenOn = true;
                        Toast.makeText(MapViewActivity.this, "开启屏幕常亮", Toast.LENGTH_LONG).show();
                    } else {
                        wakeLock.release();
                        sreenOnMapButton.setBackgroundColor(Color.TRANSPARENT);
                        flagSreenOn = false;
                        Toast.makeText(MapViewActivity.this, "关闭屏幕常亮", Toast.LENGTH_LONG).show();
                    }
                    mapMenuMoreLayout.setVisibility(View.GONE);
                    break;
                case R.id.map_menu_textReader:
                    startActivity(new Intent(MapViewActivity.this, OcrOptionActivity.class));
                    break;
                case R.id.map_menu_sharegps:
                    mapMenuMoreLayout.setVisibility(View.GONE);
                    if (flagShareGpsOn == false) {
                        flagShareGpsOn = true;
                        shareGpsMapButton.setBackgroundColor(Color.GREEN);
                        MyUdpClient searchUdpClient = new MyUdpClient(this, handler, myApplication);
                        myUdpClient = searchUdpClient;
                        new Thread(searchUdpClient).start();
                        Toast.makeText(MapViewActivity.this, "请选择分享GPS的对象", Toast.LENGTH_LONG).show();
                    } else {
                        flagShareGpsOn = false;
                        if (myUdpClient != null) {
                            myUdpClient.setFlagSendGpsOn(false);
                            myUdpClient.setFlagUdpThreadSleepOn(false);
                            myUdpClient.setUdpLife(false);
                        }
                        shareGpsMapButton.setBackgroundColor(Color.TRANSPARENT);
                        Toast.makeText(MapViewActivity.this, "关闭GPS共享", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.map_menu_outservicelist_but:
                    startActivity(new Intent(MapViewActivity.this, OutServiceWebViewActivity.class));
                    break;
                case R.id.map_menu_outservice_but:
                    if (flagOutServiceOn == false) {
                        flagOutServiceOn = true;
                        buttonOutService.setBackgroundColor(Color.GREEN);
                        sectorShowLTE();
                        tableRowOutServArea.setVisibility(View.VISIBLE);
                        tableRowOutServCount.setVisibility(View.VISIBLE);
                        tableRowOutServCellCount.setVisibility(View.VISIBLE);
                        buttonOutServiceList.setVisibility(View.VISIBLE);
                        Toast.makeText(MapViewActivity.this, "开启退服基站图层显示，退服数据刷新频率为5分钟", Toast.LENGTH_LONG).show();
                    } else {
                        flagOutServiceOn = false;
                        buttonOutService.setBackgroundColor(Color.TRANSPARENT);
                        buttonOutServiceList.setVisibility(View.INVISIBLE);
                        tableRowOutServArea.setVisibility(View.GONE);
                        tableRowOutServCount.setVisibility(View.GONE);
                        tableRowOutServCellCount.setVisibility(View.GONE);
                        Toast.makeText(MapViewActivity.this, "关闭退服基站图层显示", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.tv_mapview_updatemapdata:
                    startActivity(new Intent(MapViewActivity.this, CreateCellInfoDBActivity.class));
                    break;
                case R.id.map_menu_specialtest:
                    startActivity(new Intent(MapViewActivity.this, ComplaintTestActivity.class));
                    break;
                case R.id.btn_map_takephoto:
                    String tmpImgPath1 = myApplication.logFileSavePath + "Download/tmp_" + myApplication.USER_AUTH_INFO.userName + "_"
                            + myApplication.myPhoneInfo1.sysTime + "_" + "direc_" + myApplication.myPhoneInfo1.phoneDirection + ".JPG";
                    tempImagePath = String.copyValueOf(tmpImgPath1.toCharArray());
                    Intent intent_c1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Uri uri1 = Uri.fromFile(new File(tmpImgPath1));
                    Uri uri1 = getUriForFile(this, new File(tmpImgPath1));
                    //为拍摄的图片指定一个存储的路径
                    intent_c1.putExtra(MediaStore.EXTRA_OUTPUT, uri1);
                    //intent_c1.putExtra("android.intent.extra.quickCapture",true);//启用快捷拍照
                    startActivityForResult(intent_c1, 6);
                    break;
                case R.id.map_menu_watercamera:
                    if (myApplication.USER_AUTH_INFO.vipUser == 1) {
                        //startActivity(new Intent(MapViewActivity.this,WaterCameraActivity.class));

                        if (Build.VERSION.SDK_INT >= 23) {
                            if (!Settings.canDrawOverlays(getApplicationContext())) {
                                //启动Activity让用户授权
                                Toast.makeText(MapViewActivity.this, "请先打开网优云图的悬浮窗权限。", Toast.LENGTH_LONG).show();
                                Intent intent_g = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                startActivity(intent_g);
                                return;
                            } else {
                                //执行6.0以上绘制代码
                            }
                        } else {
                            //执行6.0以下绘制代码
                        }

                        if (flagWaterCameraOn) {
                            flagWaterCameraOn = false;
                            buttonWaterCamera.setBackgroundColor(Color.TRANSPARENT);
                            mapMenuMoreLayout.setVisibility(View.GONE);
                            buttonTakePhoto.setVisibility(View.INVISIBLE);
                            Toast.makeText(MapViewActivity.this, "水印相机已关闭。", Toast.LENGTH_LONG).show();

                        } else {
                            flagWaterCameraOn = true;
                            if (!myApplication.flagTopWindowShow) {
                                Intent intent_s = new Intent(MapViewActivity.this, TopWindowService.class);
                                //启动TopWindowService
                                startService(intent_s);
                                myApplication.flagTopWindowShow = true;
                                buttonOritation.setBackgroundColor(Color.GREEN);
                            }

                            //Intent intent_c = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //startActivityForResult(intent_c,6);

                            String tmpImgPath = myApplication.logFileSavePath + "Download/tmp_" + myApplication.USER_AUTH_INFO.userName + "_"
                                    + myApplication.myPhoneInfo1.sysTime + "_" + "direc_" + myApplication.myPhoneInfo1.phoneDirection + ".JPG";
                            tempImagePath = String.copyValueOf(tmpImgPath.toCharArray());

                            File newDir = new File(myApplication.logFileSavePath + "Download/");
                            if (!newDir.exists()) {
                                newDir.mkdirs();
                            }

                            Intent intent_c = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //Uri uri = Uri.fromFile(new File(tmpImgPath));
                            Uri uri = getUriForFile(this, new File(tmpImgPath));
                            //为拍摄的图片指定一个存储的路径
                            intent_c.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent_c, 6);

                            buttonWaterCamera.setBackgroundColor(Color.GREEN);
                            mapMenuMoreLayout.setVisibility(View.GONE);
                            buttonTakePhoto.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(MapViewActivity.this, "非VIP用户无权限使用水印相机。", Toast.LENGTH_LONG).show();
                    }

                    break;
                case R.id.map_menu_oritation:
                    //startActivity(new Intent(MapViewActivity.this,OritationActivity.class));
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!Settings.canDrawOverlays(getApplicationContext())) {
                            //启动Activity让用户授权
                            Toast.makeText(MapViewActivity.this, "请先打开网优云图的悬浮窗权限。", Toast.LENGTH_LONG).show();
                            Intent intent_g = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent_g.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent_g);
                            return;
                        } else {
                            //执行6.0以上绘制代码
                        }
                    } else {
                        //执行6.0以下绘制代码
                    }

                    if (myApplication.flagTopWindowShow) {
                        Intent intent_ss = new Intent(MapViewActivity.this, TopWindowService.class);
                        //终止TopWindowService
                        stopService(intent_ss);
                        myApplication.flagTopWindowShow = false;
                        buttonOritation.setBackgroundColor(Color.TRANSPARENT);
                        mapMenuMoreLayout.setVisibility(View.GONE);
                    } else {
                        Intent intent_s = new Intent(MapViewActivity.this, TopWindowService.class);
                        //启动TopWindowService
                        startService(intent_s);
                        myApplication.flagTopWindowShow = true;
                        buttonOritation.setBackgroundColor(Color.GREEN);
                        mapMenuMoreLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.map_menu_savepower:
                    if (flagPowerSavingMode) {
                        //关闭节电模式
                        flagPowerSavingMode = false;
                        buttonPowerSaving.setBackgroundColor(Color.TRANSPARENT);
                        mapMenuMoreLayout.setVisibility(View.GONE);

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            return;
                        }
                        myApplication.locationManager_app.removeUpdates(myApplication.locationListener_app);
                        if (myApplication.isSavingFile) {
                            myApplication.locationManager_app.requestLocationUpdates(myApplication.gpsProvider_app, 1000, 1, myApplication.locationListener_app);
                            if (MyApplication.COLLECT_PEROID > 2000) {
                                MyApplication.COLLECT_PEROID = MyApplication.COLLECT_PEROID / 3;
                            }

                        } else {
                            myApplication.locationManager_app.requestLocationUpdates(myApplication.gpsProvider_app, 3000, 1, myApplication.locationListener_app);
                            if (MyApplication.COLLECT_PEROID < 2000) {
                                MyApplication.COLLECT_PEROID = MyApplication.COLLECT_PEROID * 3;
                            }
                        }
                        setScreenLightForActivity(MapViewActivity.this, preScreenBrightness);
                        Toast.makeText(MapViewActivity.this, "关闭节电模式，当前信号采用周期：" + MyApplication.COLLECT_PEROID / 1000 + "秒", Toast.LENGTH_LONG).show();

                    } else {
                        //开启节电模式
                        flagPowerSavingMode = true;
                        buttonPowerSaving.setBackgroundColor(Color.GREEN);
                        mapMenuMoreLayout.setVisibility(View.GONE);

                        myApplication.locationManager_app.removeUpdates(myApplication.locationListener_app);
                        if (myApplication.isSavingFile) {
                            myApplication.locationManager_app.requestLocationUpdates(myApplication.gpsProvider_app, 3000, 1, myApplication.locationListener_app);
                        } else {
                            myApplication.locationManager_app.requestLocationUpdates(myApplication.gpsProvider_app, 10000, 1, myApplication.locationListener_app);
                        }
                        preScreenBrightness = getScreenBrightness(MapViewActivity.this);
                        setScreenLightForActivity(MapViewActivity.this, 0);
                        if (MyApplication.COLLECT_PEROID < 2000) {
                            MyApplication.COLLECT_PEROID = MyApplication.COLLECT_PEROID * 3;
                        }
                        Toast.makeText(MapViewActivity.this, "开启节电模式，当前信号采用周期：" + MyApplication.COLLECT_PEROID / 1000 + "秒", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.map_editstatic_but:
                    if (myApplication.userSelectPolygonList.size() > 0) {
                        startActivity(new Intent(MapViewActivity.this, StaticSelectActivity.class));
                    } else {
                        Toast.makeText(MapViewActivity.this, "请先在地图上绘制选择区域！", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.map_editok_but:
                    if (flagMapPolySelect) {
                        if (mapPolySelectPoints.size() >= 3) {
                            OverlayOptions userPolygonOp = new PolygonOptions().points(mapPolySelectPoints).stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xAAFFFF00);
                            myApplication.userSelectPolygonShowList.add((Polygon) mBaiduMap.addOverlay(userPolygonOp));
                            for (int i = 0; i < mapPolySelectOverlays.size(); i++) {
                                mapPolySelectOverlays.get(i).remove();
                            }
                            //myApplication.userSelectPolygonList.add(DeepCopy.deepCopy(mapPolySelectPoints));
                            myApplication.userSelectPolygonList.add(DeepCopy.deepCopyForLatLng(mapPolySelectPoints));
                            mapPolySelectOverlays.clear();
                            mapPolySelectPoints.clear();
                        } else {
                            Toast.makeText(MapViewActivity.this, "多边形选择至少要在地图上插入3个锚点！当前只有" + mapPolySelectPoints.size() + "个锚点。", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case R.id.map_editcancel_but:
                    if (flagMapPolySelect) {
                        int s = mapPolySelectOverlays.size();
                        int s1 = mapPolySelectPoints.size();
                        int s2 = myApplication.userSelectPolygonShowList.size();
                        //System.out.println("ZGQ mapPolySelectOverlays: "+s);
                        //System.out.println("ZGQ mapPolySelectPoints: "+s1);
                        if (s >= 2) {
                            mapPolySelectPoints.remove(s1 - 1);
                            mapPolySelectOverlays.get(s - 1).remove();
                            mapPolySelectOverlays.get(s - 2).remove();
                            mapPolySelectOverlays.remove(s - 1);
                            mapPolySelectOverlays.remove(s - 2);
                        } else if (s == 1) {
                            mapPolySelectPoints.remove(s1 - 1);
                            mapPolySelectOverlays.get(s - 1).remove();
                            mapPolySelectOverlays.remove(s - 1);
                        } else if (s == 0 && s2 > 0) {
                            myApplication.userSelectPolygonShowList.get(s2 - 1).remove();
                            myApplication.userSelectPolygonShowList.remove(s2 - 1);
                            myApplication.userSelectPolygonList.remove(s2 - 1);
                        }
                    }
                    break;
                case R.id.map_scellbut:
                    if (flagScellHide) {
                        //sCellListView1.setVisibility(View.VISIBLE);
                        //sCellHideButton.setText("服务小区");
                        cellInfoLinearLayout.setVisibility(View.VISIBLE);
                        if (myApplication.isPlayingLog) {
                            sCellTableRow.setVisibility(View.GONE);
                        }
                        flagScellHide = false;
                    } else {
                        cellInfoLinearLayout.setVisibility(View.INVISIBLE);
                        //sCellHideButton.setText("服务小区");
                        flagScellHide = true;
                    }
                    break;
                case R.id.map_logsavebut:
                    if (myApplication.USER_AUTH_INFO.vipUser == 0) {
                        Toast.makeText(MapViewActivity.this, "非VIP用户不支持LOG录制功能，请联系13868808810开通VIP功能", Toast.LENGTH_LONG).show();
                        return;
                    }

                    AlertDialog.Builder builder_save = new AlertDialog.Builder(this);
                    builder_save.setCancelable(false);
                    builder_save.setMessage("请选择测试模式\r\n1.普通模式：较省电，信号采样周期为3秒，GPS更新周期为3秒\r\n2.精准模式：信号采样周期1秒，GPS更新周期1秒");
                    builder_save.setTitle("提示");
                    builder_save.setPositiveButton("普通模式", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                myApplication.locationManager_app.removeUpdates(myApplication.locationListener_app);
                                if (ActivityCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MapViewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                                    return;
                                }
                                myApplication.locationManager_app.requestLocationUpdates(myApplication.gpsProvider_app, 3000, 1, myApplication.locationListener_app);
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            if(myApplication.COLLECT_PEROID<2000){
                                myApplication.COLLECT_PEROID=myApplication.COLLECT_PEROID*3;
                            }
                            myApplication.isSavingFile = true;
                            Toast.makeText(MapViewActivity.this, "开始记录LOG！当前信号采样周期："+myApplication.COLLECT_PEROID/1000+"秒", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder_save.setNegativeButton("精准模式", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                myApplication.locationManager_app.removeUpdates(myApplication.locationListener_app);
                                if (ActivityCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MapViewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                                    return;
                                }
                                myApplication.locationManager_app.requestLocationUpdates(myApplication.gpsProvider_app, 1000, 1, myApplication.locationListener_app);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if(myApplication.COLLECT_PEROID>2000){
                                myApplication.COLLECT_PEROID=myApplication.COLLECT_PEROID/3;
                            }
                            myApplication.isSavingFile = true;
                            Toast.makeText(MapViewActivity.this, "开始记录LOG！当前信号采样周期："+myApplication.COLLECT_PEROID/1000+"秒", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder_save.create().show();

                    logSaveButton.setVisibility(View.INVISIBLE);
                    logPlayButton.setVisibility(View.INVISIBLE);
                    logSaveStopButton.setVisibility(View.VISIBLE);
                    break;
                case R.id.map_logsavestop:
                    //List<MyApplication.LogRecord> ss=myApplication.savedCellInfoList;
                    if (myApplication.savedCellInfoList == null) {
                        Toast.makeText(this, "没有记录!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String filetime = sDateFormat.format(new Date(System.currentTimeMillis()));
                    //logFileSaveName="log_"+ myApplication.USER_AUTH_INFO.userName + "_"+filetime  + ".xls";
                    logFileSaveName="log_"+ myApplication.USER_AUTH_INFO.userName + "_"+filetime;
                    final EditText editText_filename=new EditText(MapViewActivity.this);
                    editText_filename.setText(logFileSaveName);
                    editText_filename.setSelection(logFileSaveName.length());

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setCancelable(false);
                    builder1.setMessage("是否保存测试log？");
                    builder1.setTitle("提示");
                    builder1.setView(editText_filename);
                    builder1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                if(!editText_filename.getText().toString().trim().equals("")){
                                    logFileSaveName=editText_filename.getText().toString().trim()+".xls";
                                }else{
                                    logFileSaveName=logFileSaveName+".xls";
                                }
                                myApplication.locationManager_app.removeUpdates(myApplication.locationListener_app);
                                if (ActivityCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MapViewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                                    return;
                                }
                                myApplication.locationManager_app.requestLocationUpdates(myApplication.gpsProvider_app, 3000, 1, myApplication.locationListener_app);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            if(myApplication.COLLECT_PEROID<2000){
                                myApplication.COLLECT_PEROID=myApplication.COLLECT_PEROID*3;
                            }



                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    MyLogFile myLogFile1 = new MyLogFile();
                                    try {
                                        myLogFile1.createExcel(logFileSaveName, myApplication.logFileSavePath, myApplication.savedCellInfoList);

                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                        System.out.println("ZGQ:Log保存失败!");
                                    } finally {
                                        myApplication.savedCellInfoList.clear();
                                        myApplication.isSavingFile = false;
                                    }

                                    System.out.println("ZGQ:Log保存成功!");
                                }
                            }).start();

                            dialog.dismiss();
                            logSaveButton.setVisibility(View.VISIBLE);
                            logPlayButton.setVisibility(View.VISIBLE);
                            logSaveStopButton.setVisibility(View.GONE);

                            AlertDialog.Builder dlg = new AlertDialog.Builder(MapViewActivity.this);
                            dlg.setCancelable(false);
                            dlg.setTitle("上传LOG提示");
                            dlg.setMessage("LOG已成功保存在" + myApplication.logFileSavePath + "目录中。\r\n是否同步上传至LOG云服务器？");
                            dlg.setNegativeButton("取消",null);
                            dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                        //上传log文件
                                            File logUploadFile = new File(myApplication.logFileSavePath+logFileSaveName);
                                            Message msg3 = new Message();
                                            msg3.what = 3;   //发送开始上传LOG消息
                                            handler.sendMessage(msg3);
                                            try {
                                                new MyFTP().uploadSingleFile(logUploadFile,myApplication.logFilesFtpDir,new MyFTP.UploadProgressListener(){
                                                    @Override
                                                    public void onUploadProgress(String currentStep, long uploadSize, File file) {
                                                        if(currentStep.equals(FtpActivity.FTP_UPLOAD_SUCCESS)) {
                                                            Message msg5 = new Message();
                                                            msg5.what = 5;   //发送上传LOG成功消息
                                                            handler.sendMessage(msg5);
                                                        } else if(currentStep.equals(FtpActivity.FTP_UPLOAD_FAIL)){
                                                            Message msg4 = new Message();
                                                            msg4.what = 4;   //发送上传LOG失败消息
                                                            handler.sendMessage(msg4);
                                                        }else{

                                                        }
                                                        //return;
                                                    }
                                                });

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                Message msg4 = new Message();
                                                msg4.what = 4;   //发送上传LOG失败消息
                                                handler.sendMessage(msg4);
                                            }
                                        }
                                    }).start();
                                }
                            });
                            dlg.show();
                            // Toast.makeText(MapViewActivity.this,"log文件保存在"+myApplication.logFileSavePath+",保存成功!",Toast.LENGTH_LONG);
                            //zgqExcelRowsList.clear();
                        }
                    });
                    builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder dlg = new AlertDialog.Builder(MapViewActivity.this);
                            dlg.setCancelable(false);
                            dlg.setTitle("提示");
                            dlg.setMessage("是否继续记录LOG?");
                            dlg.setPositiveButton("继续记录", null);
                            dlg.setNegativeButton("停止记录", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    myApplication.savedCellInfoList.clear();
                                    myApplication.isSavingFile = false;
                                    logSaveStopButton.setVisibility(View.GONE);
                                    logSaveButton.setVisibility(View.VISIBLE);
                                    logPlayButton.setVisibility(View.VISIBLE);
                                    try {
                                        myApplication.locationManager_app.removeUpdates(myApplication.locationListener_app);
                                        if (ActivityCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapViewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(MapViewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                                            return;
                                        }
                                        myApplication.locationManager_app.requestLocationUpdates(myApplication.gpsProvider_app, 3000, 1, myApplication.locationListener_app);
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                        if(myApplication.COLLECT_PEROID<2000){
                                        myApplication.COLLECT_PEROID=myApplication.COLLECT_PEROID*3;
                                    }
                                }
                            });
                            dlg.show();
                            dialog.dismiss();
                        }
                    });
                    builder1.create().show();
                    break;

                case R.id.map_mylocbut:
                    //mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(gpstobaiduCoordiConverter(new LatLng(myApplication.newGpsLat, myApplication.newGpsLong)));
                    //mBaiduMap.animateMapStatus(mapstatusUpdatePoint);
                    mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(logPoint);
                    mBaiduMap.animateMapStatus(mapstatusUpdatePoint);
                    centerMaptimer = 0;
                    if(!flagAutoCenterMap){
                        flagAutoCenterMap = true;
                        Toast.makeText(MapViewActivity.this,"地图自动跟随模式",Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.map_search:
                    //Toast.makeText(this,"开发中...",Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(MapViewActivity.this,MapSearchActivity.class),4);
                    break;
                case R.id.map_menu_more_but:
                    if(mapMenuMoreLayout.isShown()){
                        mapMenuMoreLayout.setVisibility(View.GONE);
                    }else{
                        mapMenuMoreLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.map_menu_locate:
                    mapMenuMoreLayout.setVisibility(View.GONE);

                    LayoutInflater factory = LayoutInflater.from(this);
                    final View textEntryView = factory.inflate(R.layout.latlon_input_dialog, null);
                    final EditText editTextInputlon = (EditText) textEntryView.findViewById(R.id.et_map_dialog_lon);
                    final EditText editTextInputlat = (EditText)textEntryView.findViewById(R.id.et_map_dialog_lat);
                    AlertDialog.Builder ad1 = new AlertDialog.Builder(MapViewActivity.this);
                    ad1.setCancelable(false);
                    ad1.setTitle("请输入定位信息:");
                    ad1.setView(textEntryView);
                    ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            try {
                                double inputlon = Double.parseDouble(editTextInputlon.getText().toString().trim());
                                double inputlat = Double.parseDouble(editTextInputlat.getText().toString().trim());

                                LatLng tppoint = new LatLng(inputlat, inputlon);
                                LatLng tppoint_bd = gpstobaiduCoordiConverter(tppoint);
                                mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(gpstobaiduCoordiConverter(tppoint));
                                mBaiduMap.animateMapStatus(mapstatusUpdatePoint);
                                centerMaptimer = 0;

                                Button textInfo_dot = new Button(getApplicationContext());
                                textInfo_dot.setBackgroundColor(Color.CYAN);
                                textInfo_dot.setPadding(20, 20, 20, 20);
                                textInfo_dot.setTextColor(Color.BLACK);
                                textInfo_dot.setTextSize(12);
                                textInfo_dot.setText("经度:" + Math.round(inputlon * 100000) / 100000.0 + "\r\n" + "纬度:" + Math.round(inputlat * 100000) / 100000.0 );
                                //得到点击的覆盖物的经纬度
                                textInfo_dot.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mBaiduMap.hideInfoWindow();
                                    }
                                });
                                //将marker所在的经纬度的信息转化成屏幕上的坐标
                                Point p = mBaiduMap.getProjection().toScreenLocation(tppoint_bd);
                                p.y -= 120;
                                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                                //初始化infoWindow，最后那个参数表示显示的位置相对于覆盖物的竖直偏移量，这里也可以传入一个监听器
                                infoWindow = new InfoWindow(textInfo_dot, llInfo, 0);
                                mBaiduMap.showInfoWindow(infoWindow);//显示此infoWindow
                                BitmapDescriptor bitmaploc = BitmapDescriptorFactory.fromResource(R.drawable.locate_32_zgqpic);
                                OverlayOptions markerloc = new MarkerOptions().icon(bitmaploc).position(tppoint_bd);
                                mBaiduMap.addOverlay(markerloc);

                            }
                            catch(Exception e){
                                e.printStackTrace();
                                Toast.makeText(MapViewActivity.this,"经纬度数据格式不正确，请重新输入！",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    ad1.setNegativeButton("取消", null);
                    ad1.show();// 显示对话框

                    break;
                case R.id.map_menu_distance:
                    mapMenuMoreLayout.setVisibility(View.GONE);
                    if(flagMapDistance){
                        flagMapDistance=false;
                        distanceMapButton.setBackgroundColor(Color.TRANSPARENT);
                        Toast.makeText(MapViewActivity.this,"关闭地图测距模式",Toast.LENGTH_SHORT).show();
                    }else {
                        flagMapDistance=true;
                        distanceMapButton.setBackgroundColor(Color.GREEN);
                        Toast.makeText(MapViewActivity.this,"开启地图测距模式，请长按地图增加锚点。",Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.map_menu_polyline:
                    mapMenuMoreLayout.setVisibility(View.GONE);
                    if(flagMapDistance || flagUserLayerEdit){
                        Toast.makeText(MapViewActivity.this,"请先关闭测距模式或者图层编辑模式！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    checkBoxsPolySelectLayer.clear();
                    mapPolySelectedLayers.clear();
                    mapPolySelectPoints.clear();
                    if(flagMapPolySelect){
                        flagMapPolySelect=false;
                        polylineSelectMapButton.setBackgroundColor(Color.TRANSPARENT);
                        tablerowMapEdit.setVisibility(View.GONE);
                        Toast.makeText(MapViewActivity.this,"关闭地图圈选模式",Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        //Toast.makeText(MapViewActivity.this,"开发中...",Toast.LENGTH_SHORT).show();
                        List<String> layerNames=MapSearchActivity.readUserTables(MapViewActivity.this,MapSearchActivity.DBNAME1,MapSearchActivity.TABLENAME1);
                        LayoutInflater factory_poly = LayoutInflater.from(this);
                        View tableSelectView = factory_poly.inflate(R.layout.layertable_select_dialog, null);
                        LinearLayout linearLayoutLayerSelect=(LinearLayout)tableSelectView.findViewById(R.id.linearlayout_layersdialog);
                        for(int i=0;i<layerNames.size();i++) {
                            CheckBox checkBoxLayer = new CheckBox(MapViewActivity.this);
                            checkBoxLayer.setText(layerNames.get(i));
                            linearLayoutLayerSelect.addView(checkBoxLayer);
                            checkBoxsPolySelectLayer.add(checkBoxLayer);
                        }
                        AlertDialog.Builder ad2 = new AlertDialog.Builder(MapViewActivity.this);
                        ad2.setCancelable(false);
                        ad2.setTitle("请选择对象图层:");
                        ad2.setView(tableSelectView);
                        ad2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(checkBoxsPolySelectLayer.size()==0){
                                    Toast.makeText(MapViewActivity.this,"未选择图层，请选择对象图层",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                for(int i=0;i<checkBoxsPolySelectLayer.size();i++){
                                    if(checkBoxsPolySelectLayer.get(i).isChecked()){
                                        mapPolySelectedLayers.add(String.valueOf(checkBoxsPolySelectLayer.get(i).getText()));
                                    }
                                }
                                flagMapPolySelect=true;
                                polylineSelectMapButton.setBackgroundColor(Color.GREEN);
                                tablerowMapEdit.setVisibility(View.VISIBLE);
                                myApplication.userSelectPolyLayerName=mapPolySelectedLayers.get(0);
                                Toast.makeText(MapViewActivity.this,"开启地图圈选模式，您选择的对象图层："+mapPolySelectedLayers.get(0)+"，请长按地图增加锚点！",Toast.LENGTH_LONG).show();
                            }
                        });
                        ad2.setNegativeButton("取消",null);
                        ad2.show();
                    }
                    break;
                case R.id.map_menu_circle:
                    mapMenuMoreLayout.setVisibility(View.GONE);
                    Toast.makeText(MapViewActivity.this,"开发中...",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.map_changemap:
                    if (!mapMenuShow) {
                        mapLayerControlTb.setVisibility(View.VISIBLE);
                        mapMenuShow = true;
                    } else {
                        mapLayerControlTb.setVisibility(View.GONE);
                        mapMenuShow = false;
                    }

                    break;
                case R.id.checkBox_sector:
                    if (((CheckBox) v).isChecked()) {
                        flagSectorLayerShow = true;
                        sectorShowLTE();
                    } else {
                        flagSectorLayerShow = false;
                        if (tempSectorShow != null) {
                            for (int k = 0; k < tempSectorShow.size(); k++) {
                                tempSectorShow.get(k).remove();
                            }
                            tempSectorShow.clear();
                        }
                        if (preSCellLine != null) {
                            preSCellLine.remove();
                        }
                    }
                    mapLayerControlTb.setVisibility(View.GONE);
                    mapMenuShow = false;
                    break;

                case R.id.checkBox_sector_GSM:
                    if (((CheckBox) v).isChecked()) {
                        flagSectorGsmLayerShow = true;
                        sectorShowLTE();
                    } else {
                        flagSectorGsmLayerShow = false;
                        if (tempSectorGsmShow != null) {
                            for (int k = 0; k < tempSectorGsmShow.size(); k++) {
                                tempSectorGsmShow.get(k).remove();
                            }
                            tempSectorGsmShow.clear();
                        }
                        //if (preSCellLine != null) {
                        //    preSCellLine.remove();
                        //}
                    }
                    mapLayerControlTb.setVisibility(View.GONE);
                    mapMenuShow = false;
                    break;
                case R.id.checkBox_mrlayer:
                    if (((CheckBox) v).isChecked()) {
                        flagMrLayerShow = true;
                        if(flagMrShowContent==3){
                            colorbarMrQuanTbr.setVisibility(View.VISIBLE);
                        }else{
                            colorbarMrTbr.setVisibility(View.VISIBLE);
                        }

                        mrShowLTE();
                    } else {
                        flagMrLayerShow = false;
                        if(flagMrShowContent==3){
                            colorbarMrQuanTbr.setVisibility(View.GONE);
                        }else{
                            colorbarMrTbr.setVisibility(View.GONE);
                        }

                        if (tempMRShow != null) {
                            for (int k = 0; k < tempMRShow.size(); k++) {
                                tempMRShow.get(k).remove();
                            }
                            tempMRShow.clear();
                        }
                    }
                    mapLayerControlTb.setVisibility(View.GONE);
                    mapMenuShow = false;
                    break;
                case R.id.checkBox_mrinfo:
                    if (((CheckBox) v).isChecked()) {
                        flagMrInfoShow = true;
                    } else {
                        flagMrInfoShow = false;
                    }
                    if(flagMrLayerShow){
                        flagMrLayerMapMoved=true;
                        mrShowLTE();
                    }
                    mapLayerControlTb.setVisibility(View.GONE);
                    mapMenuShow = false;
                    break;
                case R.id.checkBox_sinr:
                    if (((CheckBox) v).isChecked()) {
                        flagSinrLayerShow = true;
                        colorbarSINRTbr.setVisibility(View.VISIBLE);
                    } else {
                        flagSinrLayerShow = false;
                        colorbarSINRTbr.setVisibility(View.GONE);
                    }
                    mapLayerControlTb.setVisibility(View.GONE);
                    mapMenuShow = false;
                    break;
                case R.id.checkBox_keepscell_line:
                    if (((CheckBox) v).isChecked()) {
                        flagKeepScellLine = true;
                    } else {
                        flagKeepScellLine = false;
                    }
                    mapLayerControlTb.setVisibility(View.GONE);
                    mapMenuShow = false;
                    break;
                case R.id.button_newlayer:
                    Intent intent3 = new Intent(MapViewActivity.this, LayerFileSelectActivity.class);
                    startActivityForResult(intent3,3);//requestCode 3
                    break;
                case R.id.map_clearoverlay:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    builder2.setCancelable(false);
                    builder2.setMessage("是否清除地图上的所有轨迹?");
                    builder2.setTitle("提示");
                    builder2.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBaiduMap.clear();
                            mapLogOverlays.clear();
                            mapPolySelectOverlays.clear();
                            mapPolySelectPoints.clear();
                            myApplication.userSelectPolygonList.clear();
                            myApplication.userSelectPolygonShowList.clear();
                            sectorShowLTE();
                            userLayerShow();
                            mrShowLTE();
                        }
                    });
                    builder2.setNegativeButton("取消", null);
                    builder2.create().show();
                    break;
                case R.id.map_logplay:
                    String[] logPosItems = new String[] {"X","Y"};
                    AlertDialog.Builder builderlog = new AlertDialog.Builder(this);
                    builderlog.setCancelable(false);
                    builderlog.setMessage("请选择打开log文件的位置");
                    builderlog.setTitle("提示");
                    /*
                    builderlog.setSingleChoiceItems(logPosItems, 1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(logPosItems[which].equals("手机存储")){
                                final Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                                intent1.setType("application/*");//设置类型
                                //intent1.setDataAndType(Uri.parse(myApplication.logFileSavePath),"application/*");
                                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(intent1, 1);
                            }else{
                                final Intent intent2 = new Intent(MapViewActivity.this, CreateCellInfoDBActivity.class);
                                startActivityForResult(intent2,2);
                            }
                            Toast.makeText(MapViewActivity.this, logPosItems[which], Toast.LENGTH_SHORT).show();

                        }
                    });
                    */
                    //builderlog.setNegativeButton("取消", null);
                    builderlog.setPositiveButton("LOG服务器", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Intent intent2 = new Intent(MapViewActivity.this, LogSelectFtpActivity.class);
                            startActivityForResult(intent2,2);//requestCode 2
                        }
                    });
                    builderlog.setNeutralButton("手机存储", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                            intent1.setType("application/*");//设置类型
                            //intent1.setDataAndType(Uri.parse(myApplication.logFileSavePath),"application/*");
                            intent1.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent1, 1);//requestCode 1
                        }
                    });
                    builderlog.create().show();
                    break;
                case R.id.map_logplayfast:
                    if (logPlaySpeed <= 125) {
                        logPlaySpeed = 125;
                    } else {
                        logPlaySpeed = logPlaySpeed / 2;
                    }
                    if (flagLogPlaying) {
                        logPlayTimer.cancel();
                        logPlayTimer = new Timer();
                        logPlayTask = new LogPlayTask();
                        //long logdelay=0;
                        logPlayTimer.schedule(logPlayTask, 0, logPlaySpeed);
                    }
                    Toast.makeText(MapViewActivity.this, "回放速度×" + 1000 / (double) logPlaySpeed, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.map_logplayslow:
                    if (logPlaySpeed >= 4000) {
                        logPlaySpeed = 4000;
                    } else {
                        logPlaySpeed = logPlaySpeed * 2;
                    }
                    if (flagLogPlaying) {
                        logPlayTimer.cancel();
                        logPlayTimer = new Timer();
                        logPlayTask = new LogPlayTask();
                        //long logdelay=0;
                        logPlayTimer.schedule(logPlayTask, 0, logPlaySpeed);
                    }
                    Toast.makeText(MapViewActivity.this, "回放速度×" + 1000 / (double) logPlaySpeed, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.map_logplayend:
                    logPlayTimer.cancel();
                    ProgressDlgUtil.showProgressDlg("正在飞速快进...",MapViewActivity.this);
                    flagLogPlayToEnd = true;
                    logPlayTimer = new Timer();
                    logPlayTask = new LogPlayTask();
                    //long logdelay=0;
                    logPlayTimer.schedule(logPlayTask, 500);
                    flagLogPlaying = false;
                    logPlayStopButton.setEnabled(false);
                    break;
                case R.id.map_logplaystart:
                    logPlayTimer.cancel();
                    mBaiduMap.clear();
                    logPlayEntryList.clear();
                    logPlayAdapter.notifyDataSetChanged();
                    centerMaptimer = 0;
                    flagLogPlayToEnd = false;
                    logPlayIndex = 1;
                    logPlayTimer = new Timer();
                    logPlayTask = new LogPlayTask();
                    //long logdelay=0;
                    flagLogPlaying = true;
                    logPlayTimer.schedule(logPlayTask, 1000, logPlaySpeed);
                    flagLogPointSelect = false;
                    logPlayStopButton.setEnabled(true);
                    logPlayStopButton.setImageResource(android.R.drawable.ic_media_pause);
                    Toast.makeText(MapViewActivity.this,"重新开始播放LOG！", Toast.LENGTH_SHORT).show();
                    mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(gpstobaiduCoordiConverter(new LatLng(Double.parseDouble(logExcelArray[1][logGPSLatId]),Double.parseDouble(logExcelArray[1][logGPSLongId]))));
                    mBaiduMap.animateMapStatus(mapstatusUpdatePoint);
                    centerMaptimer = 0;
                    sectorShowLTE();
                    mrShowLTE();
                    userLayerShow();
                    break;
                case R.id.map_logplaystop:
                    if (flagLogPlaying) {
                        logPlayTimer.cancel();
                        centerMaptimer = 0;
                        flagLogPlaying = false;
                        logPlayStopButton.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        logPlayTimer = new Timer();
                        logPlayTask = new LogPlayTask();
                        //long logdelay=0;
                        logPlayTimer.schedule(logPlayTask, 0, logPlaySpeed);
                        flagLogPlaying = true;
                        flagLogPointSelect = false;
                        logPlayStopButton.setImageResource(android.R.drawable.ic_media_pause);
                    }
                    break;
                case R.id.map_logplayclose:

                    AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                    builder3.setCancelable(false);
                    builder3.setMessage("是否结束LOG回放?");
                    builder3.setTitle("提示");
                    builder3.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logPlayTimer.cancel();
                            mBaiduMap.clear();
                            flagLogPlaying = false;
                            flagLogPointSelect = false;
                            flagLogPlayToEnd = false;
                            logExcelArray = null;
                            logPlayIndex = 1;
                            logPlayStopButton.setEnabled(true);
                            logPlayStopButton.setImageResource(android.R.drawable.ic_media_pause);
                            logPlaySlowButton.setVisibility(View.GONE);
                            logPlayFastButton.setVisibility(View.GONE);
                            logPlayStartButton.setVisibility(View.GONE);
                            logPlayEndButton.setVisibility(View.GONE);
                            logPlayStopButton.setVisibility(View.GONE);
                            logPlayCloseButton.setVisibility(View.GONE);
                            logPlayButton.setVisibility(View.VISIBLE);
                            Toast.makeText(MapViewActivity.this,"当前为LOG回放模式，如需测试信号，请先退出本界面后重新进入！",Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder3.setNegativeButton("取消", null);
                    builder3.create().show();

                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String excelPath;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null && requestCode!=6) {
            if(requestCode==2){
                excelPath=myApplication.logFileSavePath+myApplication.ftpSelectlogFileName;
            }else {
                System.out.println("ZGQ:选择的log文件Uri = " + data.toString());
                //通过Uri获取真实路径
                //excelPath = CreateCellInfoDBActivity.getRealFilePath(this, data.getData());
                excelPath = GetPathFromUri4kitkat.getPath(this,data.getData());
            }
            System.out.println("ZGQ:logPath = " + excelPath);//    /storage/emulated/0/test.xls
            if (excelPath.contains(".xls")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setCancelable(false);
                builder1.setMessage("是否确定回放该LOG文件?");
                builder1.setTitle("提示");
                builder1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nbCellControlLayout.setVisibility(View.GONE);
                        flagNbCellShow=false;
                        //载入excel
                        //Toast.makeText(MapViewActivity.this,"正在加载LOG...",Toast.LENGTH_LONG).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Message msg1 = new Message();
                                    msg1.what = 0;   //发送开始导入消息
                                    handler.sendMessage(msg1);
                                    logExcelArray = MyLogFile.readExcel(excelPath);
                                    logPlayRows=logExcelArray.length-1;
                                    logCNameId= Arrays.binarySearch(logExcelArray[0],"logCellName");
                                    logCIId=Arrays.binarySearch(logExcelArray[0],"logCId");
                                    logPCIId=Arrays.binarySearch(logExcelArray[0],"logPci");
                                    logTACId=Arrays.binarySearch(logExcelArray[0],"logTac");
                                    logdBmId=Arrays.binarySearch(logExcelArray[0],"logSignalStrength");
                                    logRSRQId=Arrays.binarySearch(logExcelArray[0],"logRsrq");
                                    logSINRId=Arrays.binarySearch(logExcelArray[0],"logSINR");
                                    logGPSLongId=Arrays.binarySearch(logExcelArray[0],"logGPSlong");
                                    logGPSLatId=Arrays.binarySearch(logExcelArray[0],"logGPSlat");
                                    logSysTimeId=Arrays.binarySearch(logExcelArray[0],"logSysTime");
                                    logSecLatId=Arrays.binarySearch(logExcelArray[0],"secMidGPSlat");
                                    logSecLongId=Arrays.binarySearch(logExcelArray[0],"secMidGPSlong");
                                    logCellTypeId=Arrays.binarySearch(logExcelArray[0],"logType");
                                    logPSCId=Arrays.binarySearch(logExcelArray[0],"logPsc");
                                    logLACId=Arrays.binarySearch(logExcelArray[0],"logLac");
                                    Message msgSuc=new Message();
                                    msgSuc.what=100;
                                    handler.sendMessage(msgSuc);

                                    /*
                                    File tpFile=new File(excelPath);
                                    if(tpFile.exists()){
                                        tpFile.delete();
                                    }
                                    */

                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        dialog.dismiss();

                    }
                });
                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder1.create().show();
            } else {
                Toast.makeText(this, "此文件不是excel2003格式", Toast.LENGTH_LONG).show();
            }
        }else if(resultCode == 5 && requestCode == 4){
            //基站查询界面返回结果显示
            flagSearchLocateMap=true;
            LatLng tppoint=new LatLng(myApplication.searchResultLat,myApplication.searchResultLon);
            mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(gpstobaiduCoordiConverter(tppoint));
            mBaiduMap.animateMapStatus(mapstatusUpdatePoint);
            myApplication.sectorShowCenLong = myApplication.searchResultLon;
            myApplication.sectorShowCenLat = myApplication.searchResultLat;
            flagAutoCenterMap=false;
            centerMaptimer = 0;
            sectorShowLTE();
            userLayerShow();
            mrShowLTE();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    flagSearchLocateMap=false;
                }
            }).start();


        }else if(resultCode == RESULT_OK && requestCode==6){
            try{
            //Bundle extras = data.getExtras();
            // 获取图片信息
            //Bitmap bitmap = (Bitmap) extras.get("data");
            // 保存图片

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileInputStream fis = null;
                        //把图片转化为字节流
                        try {
                            fis = new FileInputStream(tempImagePath);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        //把流转化图片
                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                        String imageFileName=myApplication.USER_AUTH_INFO.userName+"_"
                                +myApplication.myPhoneInfo1.sysTime+"_"+"CI_"+myApplication.cellInfoList_map.get(0).CId+"_dir_"+myApplication.myPhoneInfo1.phoneDirection+".JPG";
                        final String imageFilePath = myApplication.logFileSavePath+"Download/"+ imageFileName;
                        //图片加水印
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = sdf.format(new Date(System.currentTimeMillis()));
                        String tptext="卫星数："+myApplication.numsGpsSatllite+"\r\n"
                                +"经度："+myApplication.newGpsLong+"\r\n"
                                +"纬度："+myApplication.newGpsLat+"\r\n"
                                +"海拔高度："+myApplication.myPhoneInfo1.termAltitude+"\r\n"
                                +"方向角："+myApplication.myPhoneInfo1.phoneDirection+"\r\n"
                                +"倾角："+myApplication.myPhoneInfo1.phoneDowntilt+"\r\n"
                                +"时间："+time+"\r\n"
                                +"小区名称："+myApplication.cellInfoList_map.get(0).cellName+"\r\n"
                                +"地址："+myApplication.myPhoneInfo1.termAddress;

                        String exiftext="Quantity of Satllites:"+myApplication.numsGpsSatllite+"\r\n"
                                +"Longitude:"+myApplication.newGpsLong+"\r\n"
                                +"Latitude:"+myApplication.newGpsLat+"\r\n"
                                +"Altitude:"+myApplication.myPhoneInfo1.termAltitude+"\r\n"
                                +"Direction:"+myApplication.myPhoneInfo1.phoneDirection+"\r\n"
                                +"Downtilt:"+myApplication.myPhoneInfo1.phoneDowntilt+"\r\n"
                                +"Time:"+time+"\r\n"
                                +"Cell Id:"+myApplication.cellInfoList_map.get(0).CId+"\r\n"
                                +"Address:"+myApplication.myPhoneInfo1.termAddress;
                        /*
                        try {
                            //exiftext = new String(tptext.getBytes("ISO-8859-1"), "GBK");
                            exiftext = new String(tptext.getBytes(), "gb2312");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }*/

                        myApplication.waterText=String.copyValueOf(tptext.toCharArray());

                        //Bitmap mWater = BitmapFactory.decodeResource(getResources(), R.drawable.logo_yuntu_48_zgqpic);
                        //Bitmap img = WaterStampUtils.createBitmapWithMarker(bitmap, mWater, myApplication.waterText);

                        Bitmap img = WaterStampUtils.createBitmapWithNoMarker(bitmap,myApplication.waterText);

                        try {
                            ImageUtil.saveImage(imageFilePath,ImageUtil.bitmap2Bytes(img));
                            fis.close();
                            new File(tempImagePath).delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //压缩图片

                        ImageCompress compress = new ImageCompress();
                        ImageCompress.CompressOptions options = new ImageCompress.CompressOptions();
                        options.uri = Uri.fromFile(new File(imageFilePath));
                        options.maxWidth=2592;
                        options.maxHeight=3456;
                        Bitmap bitmap_compress = compress.compressFromUri(MapViewActivity.this, options);
                        try {
                            //String imageCompFilePath=imageFilePath.substring(0,imageFilePath.indexOf(".JPG"))+"_c.JPG";

                            String imageCompFilePath= Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).toString()+"/cloudmap/"+imageFileName;

                            File mkFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).toString()+"/cloudmap/");
                            if (!mkFile.exists()) {
                                mkFile.mkdirs();
                            }
                            ImageUtil.saveImage(imageCompFilePath,ImageUtil.bitmap2Bytes(bitmap_compress));
                            //添加exif信息
                            ExifInterface exifInterface = new ExifInterface(imageCompFilePath);

                            exifInterface.setAttribute(ExifInterface.TAG_MAKE,exiftext);

                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, MyGpsTools.convert(myApplication.newGpsLat));
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, MyGpsTools.latitudeRef(myApplication.newGpsLat));
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, MyGpsTools.convert(myApplication.newGpsLong));
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, MyGpsTools.longitudeRef(myApplication.newGpsLong));
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE,String.valueOf(myApplication.myPhoneInfo1.termAltitude));
                            /*
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,String.valueOf(myApplication.newGpsLong));
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE,String.valueOf(myApplication.newGpsLat));
                            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,String.valueOf(myApplication.myPhoneInfo1.phoneDirection));
                            exifInterface.setAttribute(ExifInterface.TAG_MAKE,myApplication.USER_AUTH_INFO.userName);
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_AREA_INFORMATION,myApplication.myPhoneInfo1.termAddress);
                            */
                            exifInterface.saveAttributes();

                            //System.out.println("ZGQ:"+new String(exifInterface.getAttribute(ExifInterface.TAG_MODEL).getBytes(),"UTF-8"));


                            new File(imageFilePath).delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();
                Toast.makeText(this, "照片已成功保存在手机目录：内部存储/DCIM/cloudmap/", Toast.LENGTH_LONG).show();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void intManager(){
        receiver = new MyBaiduSdkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        registerReceiver(receiver, filter);
    }

    private void init(){
        mapView = (MapView) findViewById(R.id.map_view);

        //BaiduMap管理具体的某一个MapView： 旋转，移动，缩放，事件。。。
        mBaiduMap = mapView.getMap();

        //设置缩放级别，默认级别为12
        MapStatusUpdate mapstatusUpdate = MapStatusUpdateFactory.zoomTo(19);
        mBaiduMap.setMapStatus(mapstatusUpdate);
        //mBaiduMap.setPadding(0, 1000, 0, 0);

        //设置地图中心点，默认是天安门
        mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(gpstobaiduCoordiConverter(hmPos));
        mBaiduMap.setMapStatus(mapstatusUpdatePoint );
        //mapView.showScaleControl(false);//默认是true，显示缩放按钮//mapView.showZoomControls(false);//默认是true，显示比例尺
        try {
            mBaiduMap.setCompassPosition(new android.graphics.Point(screenWidth / 14, screenHeight / 6));//设置指南针的屏幕位置
        }catch(Exception e){
            e.printStackTrace();
        }

        List<String> shPreUserLayerKeys=AppSettingActivity.readAllSetKey(MapViewActivity.this,"levelThresConfig");
        List<String> shPreUserLayerVals=AppSettingActivity.readAllSetValue(MapViewActivity.this,"levelThresConfig");
        if(shPreUserLayerKeys.size()>0){
            for(int i=0;i<shPreUserLayerKeys.size();i++){
                if(shPreUserLayerKeys.get(i).equals("levelGreen")){
                    myApplication.levelGreen=Integer.parseInt(shPreUserLayerVals.get(i));
                    tvLevelGreen.setText(myApplication.levelGreen+"");
                }else if(shPreUserLayerKeys.get(i).equals("levelLightGreen")){
                    myApplication.levelLightGreen=Integer.parseInt(shPreUserLayerVals.get(i));
                    tvLevelLightGreen.setText(myApplication.levelLightGreen+"");
                }else if(shPreUserLayerKeys.get(i).equals("levelBlue")){
                    myApplication.levelBlue=Integer.parseInt(shPreUserLayerVals.get(i));
                    tvLevelBlue.setText(myApplication.levelBlue+"");
                }else if(shPreUserLayerKeys.get(i).equals("levelYellow")){
                    myApplication.levelYellow=Integer.parseInt(shPreUserLayerVals.get(i));
                    tvLevelYellow.setText(myApplication.levelYellow+"");
                }else if(shPreUserLayerKeys.get(i).equals("levelRed")){
                    myApplication.levelRed=Integer.parseInt(shPreUserLayerVals.get(i));
                    tvLevelRed.setText(myApplication.levelRed+"");
                }else if(shPreUserLayerKeys.get(i).equals("levelBlack")){
                    myApplication.levelBlack=Integer.parseInt(shPreUserLayerVals.get(i));
                    tvLevelBlack.setText(myApplication.levelBlack+"");
                }
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!myApplication.isSavingFile){
                myExit();
            }
            else{
                Toast.makeText(MapViewActivity.this, "正在录制LOG，请先停止LOG录制！", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        //旋转，移动，缩放
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                //放大地图缩放级别，每次放大一个级别
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
            case KeyEvent.KEYCODE_2:
                //每次缩小一个级别
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
            case KeyEvent.KEYCODE_3:
                //以一个点为中心旋转//获取地图当前的状态
                MapStatus mapStatus = mBaiduMap.getMapStatus();
                float rotate = mapStatus.rotate;
                //Log.d(TAG,  "rotate:" + rotate);
                //旋转范围 0-360
                MapStatus newRotate = new MapStatus.Builder().rotate(rotate+30).build();
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(newRotate));
                break;
            case KeyEvent.KEYCODE_4:
                //以一条直线为轴，旋转 调整俯仰角 overlook//范围 0-45
                float overlook = mBaiduMap.getMapStatus().overlook;
                MapStatus overStatus = new MapStatus.Builder().overlook(overlook-5).build();
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(overStatus));
                break;
            case KeyEvent.KEYCODE_5:
                //移动
                MapStatusUpdate moveStatusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(40.065796,116.349868));
                //带动画的更新地图状态，还是300毫秒
                mBaiduMap.animateMapStatus(moveStatusUpdate);
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long mExitTime=0;
    public void myExit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MapViewActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();  // 内部自动调用 onDestroy()
        }
    }

    @Override
    protected void onResume(){
        //System.out.println("ZGQ:onResume:MapViewActivity");
        //mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause(){
        // TODO Auto-generated method stub
        //System.out.println("ZGQ:onPause:MapViewActivity");
        //mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
        System.out.println("ZGQ:onStop:MapViewActivity");
        //执行log自动保存
        if(myApplication.isSavingFile){
            //异常退出保存临时log
            try{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MyLogFile myLogFile1 = new MyLogFile();
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String filetime = sDateFormat.format(new Date(System.currentTimeMillis()));
                        logFileSaveName="templog_"+ myApplication.USER_AUTH_INFO.userName + "_"+filetime  + ".xls";
                        try {
                            myLogFile1.createExcel(logFileSaveName, myApplication.logFileSavePath, myApplication.savedCellInfoList);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        //System.out.println("ZGQ:异常退出Log保存成功!");
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
                //System.out.println("ZGQ:异常退出Log保存失败!");
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        System.out.println("ZGQ:onRestart:MapViewActivity");
        // Activity being restarted from stopped state
        if(myApplication.isSavingFile){
            //删除在之前onStop过程中保存的templog
            try{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File path = new File(myApplication.logFileSavePath);
                        File[] files = path.listFiles();// 读取文件夹下文件
                        List<String> fileNameList=getFileNamesPhoneDir(files,".xls");
                        //System.out.println("ZGQ:"+fileNameList);
                        for(int i=0;i<fileNameList.size();i++){
                            if(fileNameList.get(i).contains("templog_")){
                                tempLogName=fileNameList.get(i);
                                File tpfile=new File(myApplication.logFileSavePath+tempLogName);
                                tpfile.delete();
                                return;
                            }
                        }
                    }
                }).start();
            }catch (Exception e){
                    e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy(){
        System.out.println("ZGQ:on Destroy:MapViewActivity");
        if(myApplication.isSavingFile){
            try{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //先删除在之前onStop过程中保存的templog
                        File path = new File(myApplication.logFileSavePath);
                        File[] files = path.listFiles();// 读取文件夹下文件
                        List<String> fileNameList=getFileNamesPhoneDir(files,".xls");
                        //System.out.println("ZGQ:"+fileNameList);
                        for(int i=0;i<fileNameList.size();i++){
                            if(fileNameList.get(i).contains("templog_")){
                                tempLogName=fileNameList.get(i);
                                File tpfile=new File(myApplication.logFileSavePath+tempLogName);
                                tpfile.delete();
                                break;
                            }
                        }
                        //再保存templog
                        MyLogFile myLogFile1 = new MyLogFile();
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String filetime = sDateFormat.format(new Date(System.currentTimeMillis()));
                        logFileSaveName="templog_"+ myApplication.USER_AUTH_INFO.userName + "_"+filetime  + ".xls";
                        try {
                            myLogFile1.createExcel(logFileSaveName, myApplication.logFileSavePath, myApplication.savedCellInfoList);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        myApplication.savedCellInfoList.clear();
                    }
                }).start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        if(sectorShowTimer!=null){
            sectorShowTimer.cancel();
            sectorShowTimer=null;
        }
        if(sectorShowTask!=null){
            sectorShowTask.cancel();
            sectorShowTask=null;
        }
        if(sectorGsmShowTimer!=null){
            sectorGsmShowTimer.cancel();
            sectorGsmShowTimer=null;
        }
        if(sectorGsmShowTask!=null){
            sectorGsmShowTask.cancel();
            sectorGsmShowTask=null;
        }
        if(mrLayerShowTimer!=null){
            mrLayerShowTimer.cancel();
            mrLayerShowTimer=null;
            //flagMrLayerdrawing=false;
        }
        if(mrLayerShowTask!=null){
            mrLayerShowTask.cancel();
            mrLayerShowTask=null;
        }
        myApplication.userSelectPolygonList.clear();
        myApplication.userSelectPolygonShowList.clear();
        myApplication.userSelectPolyLayerName="";
        mapLogOverlays.clear();
        myApplication.isPlayingLog=false;
        if(myApplication.COLLECT_PEROID<2000){
            myApplication.COLLECT_PEROID=myApplication.COLLECT_PEROID*3;
        }
        //MyApplication.SECTOR_MAP_SHOW_R=600;
        db_sec.close();
        db_mr.close();
        mapView.onDestroy();
        unregisterReceiver(receiver);
        //logTask.cancel();
        flagThreadrun=false;

        super.onDestroy();

    }


    class MyBaiduSdkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
        //网络错误
        Toast.makeText(getApplicationContext(),"无网络",Toast.LENGTH_LONG).show();
        }
            else if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
        //key校验失败
        Toast.makeText(getApplicationContext(), "ZGQ:地图校验失败，请打开手机数据连接!",Toast.LENGTH_LONG).show();
        }
        }
    }

    private OverlayOptions optionDot;
    private OverlayOptions optionSinrDot;
    private OverlayOptions optionLine;
    private LatLng logPoint;
    private List<Overlay> mapLogOverlays=new ArrayList<>();


    private BitmapDescriptor arrowbitmap;


    //地图信号打点模式
    private class ThreadShow implements Runnable {
        @Override
        public void run() {
        // TODO Auto-generated method stub
        while (flagThreadrun) {
        try {

            if (flagfirstLoad == 0) {
                List<OverlayOptions> optionList = new ArrayList<>();
                for (int n = 0; n < myApplication.logCellInfoList.size(); n++) {
                    double logLat1 = myApplication.logCellInfoList.get(n).logGPSlat;
                    double logLong1 = myApplication.logCellInfoList.get(n).logGPSlong;
                    LatLng logPoint = gpstobaiduCoordiConverter(new LatLng(logLat1, logLong1));
                    //LatLng logPoint=new LatLng(baiduLoc.getLocation().getLatitude(),baiduLoc.getLocation().getLongitude());

                    //构建MarkerOption，用于在地图上添加Marker

                    int lev1 = myApplication.logCellInfoList.get(n).logSignalStrength;
                    int dotColor = levelToColor(lev1);
                    OverlayOptions option = new DotOptions().center(logPoint).radius(7).color(dotColor).visible(true);//AA 指定透明度。 00 是完全透明。 FF 是完全不透明
                    optionList.add(option);
                }
                mapLogOverlays.addAll(mBaiduMap.addOverlays(optionList));
                flagfirstLoad = 1;
                optionList.clear();
            }
            int i = myApplication.logCellInfoList.size() - 1;
            double logLat = myApplication.logCellInfoList.get(i).logGPSlat;
            double logLong = myApplication.logCellInfoList.get(i).logGPSlong;
            logPoint = gpstobaiduCoordiConverter(new LatLng(logLat, logLong));
            int lev = myApplication.logCellInfoList.get(i).logSignalStrength;
            double cellPoint_long=myApplication.logCellInfoList.get(i).secMidGPSlong;
            double cellPoint_lat=myApplication.logCellInfoList.get(i).secMidGPSlat;
            int dotColor = levelToColor(lev);
            int scellLineColor=levelToColorForCellLine(lev);
            //preDotColor = dotColor;
            optionDot = new DotOptions().center(logPoint).radius(7).color(dotColor).visible(true);
            mapLogOverlays.add(mBaiduMap.addOverlay(optionDot));

            if(flagArrowIconShow){ //添加方向箭头图标
                if(preArrowIcon!=null){
                    preArrowIcon.remove();
                }
                //BitmapDescriptor arrowbitmap = BitmapDescriptorFactory.fromResource(R.drawable.arrowicon_s_32_32_deg0);
                OverlayOptions arrowOption = new MarkerOptions().position(logPoint).perspective(true).icon(arrowbitmap).anchor(0.5f, 0.5f).rotate(360-myApplication.myPhoneInfo1.termDirection);
                preArrowIcon = mBaiduMap.addOverlay(arrowOption);
            }

            if(flagSinrLayerShow){
                int mSinr = myApplication.logCellInfoList.get(i).logSINR;
                int sinrColor = sinrToColor(mSinr);
                optionSinrDot = new DotOptions().center(gpstobaiduCoordiConverter(new LatLng(logLat+0.0001, logLong+0.0001))).radius(7).color(sinrColor).visible(true);
                mapLogOverlays.add(mBaiduMap.addOverlay(optionSinrDot));
            }

            if(flagSectorLayerShow) {  //添加手机与服务小区的连线
                if(preSCellLine!=null&&!flagKeepScellLine){preSCellLine.remove();}
                //preSCellLine=null;
                List<LatLng> polyPoint1 = new ArrayList<>();
                polyPoint1.add(gpstobaiduCoordiConverter(new LatLng(logLat, logLong)));
                polyPoint1.add(gpstobaiduCoordiConverter(new LatLng(cellPoint_lat, cellPoint_long)));
                optionLine = new PolylineOptions().width(4).color(scellLineColor).points(polyPoint1);
                if(flagKeepScellLine){
                    mapLogOverlays.add(mBaiduMap.addOverlay(optionLine));
                }else{
                    preSCellLine=mBaiduMap.addOverlay(optionLine);
                }
            }
            centerMaptimer = centerMaptimer + 1;
            if (myApplication.COLLECT_PEROID<2000 && centerMaptimer == 30 && flagAutoCenterMap) {
                mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(logPoint);
                mBaiduMap.animateMapStatus(mapstatusUpdatePoint);
                centerMaptimer = 0;
            }else if(myApplication.COLLECT_PEROID>2000 && centerMaptimer == 10 && flagAutoCenterMap){
                mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(logPoint);
                mBaiduMap.animateMapStatus(mapstatusUpdatePoint);
                centerMaptimer = 0;
            }

            if(myApplication.USER_AUTH_INFO.vipUser==0){
                //非VIP用户保留显示30*3秒轨迹
                if(mapLogOverlays.size()>30){
                    mapLogOverlays.get(0).remove();
                    mapLogOverlays.remove(0);
                    if(flagSinrLayerShow){
                        mapLogOverlays.get(0).remove();
                        mapLogOverlays.remove(0);
                    }
                }
            }else if(mapLogOverlays.size()>600){  //VIP用户保留显示15分钟轨迹
                mapLogOverlays.get(0).remove();
                mapLogOverlays.remove(0);
                if(flagSinrLayerShow){
                    mapLogOverlays.get(0).remove();
                    mapLogOverlays.remove(0);
                }
            }

            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
            Thread.sleep(MyApplication.COLLECT_PEROID);
            //System.out.println("send...");
             } catch (Exception e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             System.out.println("ZGQ:thread error...");}
             }
        }
    }

    private int logCNameId=0,logCIId=0,logPCIId=0,logTACId=0,logdBmId=0,logRSRQId=0,logSINRId=0,logGPSLongId=0,logGPSLatId=0,logSysTimeId=0,logSecLatId=0,logSecLongId=0,logCellTypeId=0,logPSCId=0,logLACId=0;
    private LatLng preLogPoint;

    private class LogPlayTask extends TimerTask {
        @Override
        public void run() {
            try{
                // TODO Auto-generated method stub
                List<LatLng> polyPoint1 = new ArrayList<>();
                if(flagLogPlayToEnd){
                    for (int i=logPlayIndex; i <= logPlayRows; i++) {

                        double logLat = Double.parseDouble(logExcelArray[i][logGPSLatId]);
                        double logLong = Double.parseDouble(logExcelArray[i][logGPSLongId]);
                        //double xlogLat=logLat;
                        //double xlogLong=logLong+0.000001;

                        LatLng logPoint = gpstobaiduCoordiConverter(new LatLng(logLat, logLong));
                        LatLng xlogPoint= new LatLng(logPoint.latitude,logPoint.longitude+0.00001);
                        //if(i==1){preLogPoint=logPoint;}
                        int lev =  Integer.parseInt(logExcelArray[i][logdBmId]);

                        LogPlayEntry tpLogEntry=new LogPlayEntry();
                        String msystime=logExcelArray[i][logSysTimeId];
                        tpLogEntry.logEntryTime=msystime.substring(msystime.indexOf("_")+1);
                        tpLogEntry.logEntryCellName=logExcelArray[i][logCNameId];
                        tpLogEntry.logEntryCId=Integer.parseInt(logExcelArray[i][logCIId]);
                        tpLogEntry.logEntryPci=Integer.parseInt(logExcelArray[i][logPCIId]);
                        tpLogEntry.logEntryTac=Integer.parseInt(logExcelArray[i][logTACId]);
                        tpLogEntry.logEntrySignalStrength=lev;
                        tpLogEntry.logEntryRsrq=logExcelArray[i][logRSRQId];
                        tpLogEntry.logEntrySINR=Integer.parseInt(logExcelArray[i][logSINRId]);
                        logPlayEntryList.add(tpLogEntry);

                        int polylineColor = levelToColor(lev);
                        polyPoint1.add(logPoint);
                        //polyPoint1.add(preLogPoint);
                        polyPoint1.add(xlogPoint);
                        OverlayOptions optionlog = new PolylineOptions().width(8).color(polylineColor).points(polyPoint1);
                        //OverlayOptions optionDot = new DotOptions().center(logPoint).radius(7).color(polylineColor).visible(true);//AA 指定透明度。 00 是完全透明。 FF 是完全不透明
                        //logOptionList.add(optionDot);
                        //mBaiduMap.addOverlay(optionDot);
                        Polyline mPolyLine= (Polyline) mBaiduMap.addOverlay(optionlog);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("keylogid",String.valueOf(logPlayIndex));
                        mPolyLine.setExtraInfo(bundle);
                        //preLogPoint=logPoint;
                        polyPoint1.clear();

                        if (flagKeepScellLine) {  //添加手机与服务小区的连线
                            int cellLineColor = levelToColorForCellLine(lev);
                            double cellPoint_long = Double.parseDouble(logExcelArray[i][logSecLongId]);
                            double cellPoint_lat = Double.parseDouble(logExcelArray[i][logSecLatId]);

                            List<LatLng> polyPoint2 = new ArrayList<>();
                            //polyPoint2.add(gpstobaiduCoordiConverter(new LatLng(logLat, logLong)));
                            polyPoint2.add(logPoint);
                            polyPoint2.add(gpstobaiduCoordiConverter(new LatLng(cellPoint_lat, cellPoint_long)));
                            mBaiduMap.addOverlay(new PolylineOptions().width(4).color(cellLineColor).points(polyPoint2));
                            //polyPoint2.clear();
                        }

                        logPlayIndex=logPlayIndex+1;
                    }
                    //mBaiduMap.addOverlays(logOptionList);
                    logPlayIndex=logPlayRows;
                    flagLogPlayToEnd = false;
                }
                else{
                    if(myApplication.isPlayingLog && logPlayIndex<=logPlayRows) {
                        int i = logPlayIndex;
                        logPlaySCellinfo.type = Integer.parseInt(logExcelArray[i][logCellTypeId]);
                        logPlaySCellinfo.CId = Integer.parseInt(logExcelArray[i][logCIId]);
                        logPlaySCellinfo.cellName = logExcelArray[i][logCNameId];
                        logPlaySCellinfo.lac = Integer.parseInt(logExcelArray[i][logLACId]);
                        logPlaySCellinfo.tac = Integer.parseInt(logExcelArray[i][logTACId]);
                        logPlaySCellinfo.psc = Integer.parseInt(logExcelArray[i][logPSCId]);
                        logPlaySCellinfo.pci = Integer.parseInt(logExcelArray[i][logPCIId]);
                        logPlaySCellinfo.signalStrength = Integer.parseInt(logExcelArray[i][logdBmId]);
                        logPlaySCellinfo.rsrq = logExcelArray[i][logRSRQId];
                        logPlaySCellinfo.SINR = Integer.parseInt(logExcelArray[i][logSINRId]);

                        LogPlayEntry tpLogEntry=new LogPlayEntry();
                        String msystime=logExcelArray[i][logSysTimeId];
                        tpLogEntry.logEntryTime=msystime.substring(msystime.indexOf("_")+1);
                        tpLogEntry.logEntryCellName=logPlaySCellinfo.cellName;
                        tpLogEntry.logEntryCId=logPlaySCellinfo.CId;
                        tpLogEntry.logEntryPci=logPlaySCellinfo.pci;
                        tpLogEntry.logEntryTac=logPlaySCellinfo.tac;
                        tpLogEntry.logEntrySignalStrength=logPlaySCellinfo.signalStrength;
                        tpLogEntry.logEntryRsrq=logPlaySCellinfo.rsrq;
                        tpLogEntry.logEntrySINR=logPlaySCellinfo.SINR;
                        logPlayEntryList.add(tpLogEntry);

                        double logLat = Double.parseDouble(logExcelArray[i][logGPSLatId]);
                        double logLong = Double.parseDouble(logExcelArray[i][logGPSLongId]);
                        logPoint = gpstobaiduCoordiConverter(new LatLng(logLat, logLong));
                        LatLng xlogPoint= new LatLng(logPoint.latitude,logPoint.longitude+0.00001);

                        //if(i==1){preLogPoint=logPoint;}
                        int lev = logPlaySCellinfo.signalStrength;
                        double cellPoint_long = Double.parseDouble(logExcelArray[i][logSecLongId]);
                        double cellPoint_lat = Double.parseDouble(logExcelArray[i][logSecLatId]);
                        //int dotColor = levelToColor(lev);
                        int polylineColor = levelToColor(lev);
                        int cellLineColor=levelToColorForCellLine(lev);
                        polyPoint1.add(logPoint);
                        polyPoint1.add(xlogPoint);
                        //polyPoint1.add(preLogPoint);
                        //preDotColor = dotColor;
                        OverlayOptions optionlog = new PolylineOptions().width(8).color(polylineColor).points(polyPoint1);
                        //OverlayOptions optionDot = new DotOptions().center(logPoint).radius(7).color(polylineColor).visible(true);
                        //Dot mDot= (Dot) mBaiduMap.addOverlay(optionDot);
                        //mBaiduMap.addOverlay(optionDot);
                        Polyline mPolyLine= (Polyline) mBaiduMap.addOverlay(optionlog);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("keylogid",String.valueOf(logPlayIndex));
                        mPolyLine.setExtraInfo(bundle);
                        //preLogPoint=logPoint;
                        polyPoint1.clear();

                        if (flagSinrLayerShow) {
                            int mSinr = logPlaySCellinfo.SINR;
                            int sinrColor = sinrToColor(mSinr);
                            optionSinrDot = new DotOptions().center(gpstobaiduCoordiConverter(new LatLng(logLat + 0.0002, logLong + 0.0002))).radius(7).color(sinrColor).visible(true);
                            mBaiduMap.addOverlay(optionSinrDot);
                        }

                        if (preSCellLine != null&&!flagKeepScellLine) {
                            preSCellLine.remove();
                        }

                        if (flagSectorLayerShow) {  //添加手机与服务小区的连线
                            List<LatLng> polyPoint2 = new ArrayList<>();
                            //polyPoint2.add(gpstobaiduCoordiConverter(new LatLng(logLat, logLong)));
                            polyPoint2.add(logPoint);
                            polyPoint2.add(gpstobaiduCoordiConverter(new LatLng(cellPoint_lat, cellPoint_long)));
                            optionLine = new PolylineOptions().width(4).color(cellLineColor).points(polyPoint2);
                            preSCellLine = mBaiduMap.addOverlay(optionLine);
                            //polyPoint2.clear();
                        }
                        centerMaptimer = centerMaptimer + 1;
                        if (centerMaptimer == 20 && flagAutoCenterMap) {
                            mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(logPoint);
                            mBaiduMap.animateMapStatus(mapstatusUpdatePoint);
                            centerMaptimer = 0;
                        }
                        logPlayIndex = logPlayIndex + 1;
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                        //System.out.println("send...");

                    }
                }
                if(logPlayIndex>=logPlayRows){
                    logPlayIndex=logPlayRows;
                    Message msg = new Message();
                    msg.what = 101;
                    handler.sendMessage(msg);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private class LogSelectShowTask extends TimerTask {
        @Override
        public void run() {
            try{
                // TODO Auto-generated method stub
                //List<LatLng> polyPoint1 = new ArrayList<>();
                int i=logIdSelected;
                logPlaySCellinfo.type = Integer.parseInt(logExcelArray[i][logCellTypeId]);
                logPlaySCellinfo.CId = Integer.parseInt(logExcelArray[i][logCIId]);
                logPlaySCellinfo.cellName = logExcelArray[i][logCNameId];
                logPlaySCellinfo.lac = Integer.parseInt(logExcelArray[i][logLACId]);
                logPlaySCellinfo.tac = Integer.parseInt(logExcelArray[i][logTACId]);
                logPlaySCellinfo.psc = Integer.parseInt(logExcelArray[i][logPSCId]);
                logPlaySCellinfo.pci = Integer.parseInt(logExcelArray[i][logPCIId]);
                logPlaySCellinfo.signalStrength = Integer.parseInt(logExcelArray[i][logdBmId]);
                logPlaySCellinfo.rsrq = logExcelArray[i][logRSRQId];
                logPlaySCellinfo.SINR = Integer.parseInt(logExcelArray[i][logSINRId]);
                double logLat = Double.parseDouble(logExcelArray[i][logGPSLatId]);
                double logLong = Double.parseDouble(logExcelArray[i][logGPSLongId]);
                logPoint = gpstobaiduCoordiConverter(new LatLng(logLat, logLong));
                double cellPoint_long = Double.parseDouble(logExcelArray[i][logSecLongId]);
                double cellPoint_lat = Double.parseDouble(logExcelArray[i][logSecLatId]);
                int polylineColor = levelToColorForCellLine(logPlaySCellinfo.signalStrength);
                if (preSCellLine != null) {
                    preSCellLine.remove();
                }

                if (flagSectorLayerShow) {  //添加手机与服务小区的连线
                            List<LatLng> polyPoint2 = new ArrayList<>();
                            //polyPoint2.add(gpstobaiduCoordiConverter(new LatLng(logLat, logLong)));
                            polyPoint2.add(logPoint);
                            polyPoint2.add(gpstobaiduCoordiConverter(new LatLng(cellPoint_lat, cellPoint_long)));
                            optionLine = new PolylineOptions().width(4).color(polylineColor).points(polyPoint2);
                            preSCellLine = mBaiduMap.addOverlay(optionLine);
                            //polyPoint2.clear();
                }
                if(flagLogListViewClick){
                    mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLng(logPoint);
                    mBaiduMap.animateMapStatus(mapstatusUpdatePoint);
                    flagLogListViewClick=false;
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
                //System.out.println("send...");

                }

            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    class SectorShowTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //System.out.println("ZGQ:sectorShowTask start");
            if(flagSectorLayerShow) {
                try {
                    flagSectorLteDrawing=true;
                    //System.out.println("ZGQ:sectorShowTask clearmap start");
                    if (tempSectorShow.size()>0) {
                        for (int k = 0; k < tempSectorShow.size(); k++) {
                            tempSectorShow.get(k).remove();
                        }
                        tempSectorShow.clear();
                        //System.out.println("ZGQ:sectorShowTask clearmap finish");
                    }

                    // 绘制基站sector图层
                    LatLng secCentPoint,secMinPoint,secMaxPoint,secMiddlePoint;
                    CellSectorLayer viewSectorLayer= new CellSectorLayer();

                    if(flagSectorMapMoved){
                        flagSectorMapMoved=false;
                        myApplication.sectorShowCenLong = myApplication.dragMapCenLong;
                        myApplication.sectorShowCenLat = myApplication.dragMapCenLat;
                    }else if(flagSearchLocateMap){
                        System.out.println("zgq:hh");
                        myApplication.sectorShowCenLong = myApplication.searchResultLon;
                        myApplication.sectorShowCenLat = myApplication.searchResultLat;
                    } else {
                        if(myApplication.isPlayingLog){
                            myApplication.sectorShowCenLong = Double.parseDouble(logExcelArray[logPlayIndex][logGPSLongId]);
                            myApplication.sectorShowCenLat = Double.parseDouble(logExcelArray[logPlayIndex][logGPSLatId]);
                        }
                        else{
                            myApplication.sectorShowCenLong = myApplication.newGpsLong;
                            myApplication.sectorShowCenLat = myApplication.newGpsLat;
                        }
                    }
                    //System.out.println("ZGQ:sectorShowTask cellSectorCellect start");
                    List<CellSectorLayer.CellSector> sectorList_show=viewSectorLayer.cellSectorCollect(myApplication.sectorShowCenLong,myApplication.sectorShowCenLat,db_sec);
                    //System.out.println("ZGQ:sectorShowTask cellSectorCellect finish");
                    if(sectorList_show.size()<100 && MyApplication.SECTOR_MAP_SHOW_R<5000){
                        MyApplication.SECTOR_MAP_SHOW_R=MyApplication.SECTOR_MAP_SHOW_R+200;
                        //MyApplication.MR_MAP_SHOW_R=MyApplication.MR_MAP_SHOW_R+200;
                        //MyApplication.USER_LAYER_MAP_SHOW_R=MyApplication.USER_LAYER_MAP_SHOW_R+400;
                    }else if(sectorList_show.size()>400){
                        MyApplication.SECTOR_MAP_SHOW_R=600;
                        //MyApplication.MR_MAP_SHOW_R=500;
                        //MyApplication.USER_LAYER_MAP_SHOW_R=1000;
                    }
                    List<Bundle> bundleList = new ArrayList<>();
                    List<OverlayOptions> optionList_polyline=new ArrayList<>();
                    //System.out.println("ZGQ:sectorShowTask sectorShow start");
                    for(int s=0;s<sectorList_show.size();s++){
                        //int s=0;
                        List<LatLng> polyPoint=new ArrayList<>();
                        secCentPoint=gpstobaiduCoordiConverter(new LatLng(sectorList_show.get(s).sec_lat,sectorList_show.get(s).sec_long));
                        secMiddlePoint=gpstobaiduCoordiConverter(new LatLng(sectorList_show.get(s).sec_dirlat,sectorList_show.get(s).sec_dirlong));
                        secMinPoint=gpstobaiduCoordiConverter(new LatLng(sectorList_show.get(s).sec_mindirlat,sectorList_show.get(s).sec_mindirlong));
                        secMaxPoint=gpstobaiduCoordiConverter(new LatLng(sectorList_show.get(s).sec_maxdirlat,sectorList_show.get(s).sec_maxdirlong));

                        polyPoint.add(secMinPoint);
                        polyPoint.add(secCentPoint);
                        polyPoint.add(secMaxPoint);
                        polyPoint.add(secMiddlePoint);
                        polyPoint.add(secMinPoint);
                        int secolor= sectorList_show.get(s).sec_color;
                        OverlayOptions option_polyline = new PolylineOptions().width(5).color(secolor).points(polyPoint);//AA 指定透明度。 00 是完全透明。 FF 是完全不透明
                        optionList_polyline.add(option_polyline);
                        //OverlayOptions option_arc= new ArcOptions().width(4).color(secolor).points(secMinPoint,secMiddlePoint,secMaxPoint);
                        //Polyline mpolyline=(Polyline) mBaiduMap.addOverlay(option_polyline);
                        //Arc mArc = (Arc)mBaiduMap.addOverlay(option_arc);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("keyCellName",
                                sectorList_show.get(s).sec_cellName+"\r\n " +
                                sectorList_show.get(s).sec_siteName+"\r\n " +
                                "CI:"+sectorList_show.get(s).sec_CELLID+ " ENODEB:"+sectorList_show.get(s).sec_ENBID+"\r\n " +
                                "频段:"+sectorList_show.get(s).sec_freqBand+"  频率:"+sectorList_show.get(s).sec_freqNumber+"\r\n " +
                                "方向角:"+Math.round(sectorList_show.get(s).sec_direction)+"  PCI:"+sectorList_show.get(s).sec_PCI+"\r\n " +
                                "经度:"+sectorList_show.get(s).sec_long + "  纬度:"+sectorList_show.get(s).sec_lat+"\r\n " +
                                "(点击显示详细信息)");
                        bundleList.add(bundle);
                        //mpolyline.setExtraInfo(bundle);
                        //tempSectorShow.add(mpolyline);
                        //tempSectorShow.add(mArc);
                            /*
                            Marker marker = null;
                            LatLng markerPoint = null;
                            MarkerOptions markerOption = null;
                            BitmapDescriptor bitmap =BitmapDescriptorFactory.fromResource(R.drawable.grape_pic);;
                            markerPoint = secMiddlePoint;
                            markerOption = new MarkerOptions().position(markerPoint).icon(bitmap);
                            marker = (Marker) mBaiduMap.addOverlay(markerOption);
                            //Bundle用于通信
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("66", sectorList_show.get(s).sec_cellName);
                                marker.setExtraInfo(bundle);//将bundle值传入marker中，给baiduMap设置监听时可以得到它
                            */
                    }
                    List<Overlay> mpolylineList =mBaiduMap.addOverlays(optionList_polyline);
                    for(int i=0;i<mpolylineList.size();i++){
                        (mpolylineList.get(i)).setExtraInfo(bundleList.get(i));
                        //tempSectorShow.add(mpolylineList.get(i));
                    }
                    tempSectorShow.addAll(mpolylineList);
                    //System.out.println("ZGQ:sectorShowTask sectorShow finish");
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                    flagSectorLteDrawing=false;

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("ZGQ:Sector show Task error...");
                }
            }
        }
    }

    class SectorGsmShowTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(flagSectorGsmLayerShow) {
                try {
                    flagSectorGsmDrawing=true;
                    if (tempSectorGsmShow.size()>0) {
                        for (int k = 0; k < tempSectorGsmShow.size(); k++) {
                            tempSectorGsmShow.get(k).remove();
                        }
                        tempSectorGsmShow.clear();
                    }
                    // 绘制基站sector图层
                    LatLng secCentPoint,secMinPoint,secMaxPoint,secMiddlePoint;
                    CellSectorGsmLayer viewSectorLayer= new CellSectorGsmLayer();

                    if(flagSectorGsmMapMoved){
                        //如果是手动拖动地图
                        flagSectorGsmMapMoved=false;
                        myApplication.sectorShowCenLong = myApplication.dragMapCenLong;
                        myApplication.sectorShowCenLat = myApplication.dragMapCenLat;
                    }else if(flagSearchLocateMap){
                        myApplication.sectorShowCenLong = myApplication.searchResultLon;
                        myApplication.sectorShowCenLat = myApplication.searchResultLat;
                    } else {
                        if(myApplication.isPlayingLog){
                            //如果是Log自动播放
                            myApplication.sectorShowCenLong = Double.parseDouble(logExcelArray[logPlayIndex][logGPSLongId]);
                            myApplication.sectorShowCenLat = Double.parseDouble(logExcelArray[logPlayIndex][logGPSLatId]);
                        }
                        else{
                            //如果是信号测试
                            myApplication.sectorShowCenLong = myApplication.newGpsLong;
                            myApplication.sectorShowCenLat = myApplication.newGpsLat;
                        }
                    }
                    //System.out.println("ZGQ:sectorShowTask cellSectorCellect start");
                    List<CellSectorGsmLayer.CellSectorGsm> sectorList_show=viewSectorLayer.cellSectorGsmCollect(myApplication.sectorShowCenLong,myApplication.sectorShowCenLat,db_sec);
                    //System.out.println("ZGQ:sectorShowTask cellSectorCellect finish");

                    List<Bundle> bundleList = new ArrayList<>();
                    List<OverlayOptions> optionList_polyline=new ArrayList<>();
                    //System.out.println("ZGQ:sectorShowTask sectorShow start");
                    for(int s=0;s<sectorList_show.size();s++){
                        //int s=0;
                        List<LatLng> polyPoint=new ArrayList<>();
                        secCentPoint=gpstobaiduCoordiConverter(new LatLng(sectorList_show.get(s).sec_lat,sectorList_show.get(s).sec_long));
                        secMiddlePoint=gpstobaiduCoordiConverter(new LatLng(sectorList_show.get(s).sec_dirlat,sectorList_show.get(s).sec_dirlong));
                        secMinPoint=gpstobaiduCoordiConverter(new LatLng(sectorList_show.get(s).sec_mindirlat,sectorList_show.get(s).sec_mindirlong));
                        secMaxPoint=gpstobaiduCoordiConverter(new LatLng(sectorList_show.get(s).sec_maxdirlat,sectorList_show.get(s).sec_maxdirlong));

                        polyPoint.add(secMinPoint);
                        polyPoint.add(secCentPoint);
                        polyPoint.add(secMaxPoint);
                        polyPoint.add(secMiddlePoint);
                        polyPoint.add(secMinPoint);
                        int secolor= sectorList_show.get(s).sec_color;
                        OverlayOptions option_polyline = new PolylineOptions().width(5).color(secolor).points(polyPoint);//AA 指定透明度。 00 是完全透明。 FF 是完全不透明
                        optionList_polyline.add(option_polyline);
                        //OverlayOptions option_arc= new ArcOptions().width(4).color(secolor).points(secMinPoint,secMiddlePoint,secMaxPoint);
                        //Polyline mpolyline=(Polyline) mBaiduMap.addOverlay(option_polyline);
                        //Arc mArc = (Arc)mBaiduMap.addOverlay(option_arc);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("keyCellNameGsm",
                                "区县:"+sectorList_show.get(s).sec_area+" 站名:"+sectorList_show.get(s).sec_siteName+"\r\n " +
                                "CI:"+sectorList_show.get(s).sec_CELLID+ " 小区名:"+sectorList_show.get(s).sec_cellName+"\r\n " +
                                "SiteID:"+sectorList_show.get(s).sec_siteid+ " BTSID:"+sectorList_show.get(s).sec_btsid+"\r\n " +
                                "BSC:"+sectorList_show.get(s).sec_bsc+ " LAC:"+sectorList_show.get(s).sec_lac+"\r\n " +
                                "经度:"+sectorList_show.get(s).sec_long+ " 纬度:"+sectorList_show.get(s).sec_lat +"\r\n " +
                                "NCC:"+sectorList_show.get(s).sec_ncc+ " BCC:"+sectorList_show.get(s).sec_bcc+" BCCH:"+sectorList_show.get(s).sec_bcch+"\r\n " +
                                "频段:"+sectorList_show.get(s).sec_freqBand+"  天线挂高:"+sectorList_show.get(s).sec_atennahight+"\r\n " +
                                "方位角:"+sectorList_show.get(s).sec_direction +"  水平波瓣角:"+sectorList_show.get(s).sec_bandwidth+"\r\n " +
                                "电子下倾角:"+sectorList_show.get(s).sec_elecdowntilt+ " 机械下倾角:"+sectorList_show.get(s).sec_mechdowntilt+"\r\n " +"(点击显示详细信息)"
                        );
                        bundleList.add(bundle);

                    }
                    List<Overlay> mpolylineList =mBaiduMap.addOverlays(optionList_polyline);
                    for(int i=0;i<mpolylineList.size();i++){
                        (mpolylineList.get(i)).setExtraInfo(bundleList.get(i));
                        //tempSectorShow.add(mpolylineList.get(i));
                    }
                    tempSectorGsmShow.addAll(mpolylineList);
                    //System.out.println("ZGQ:sectorShowTask sectorShow finish");

                    flagSectorGsmDrawing=false;

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("ZGQ:SectorGsm show Task error...");
                }
            }
        }
    }

    class MrShowTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                System.out.println("ZGQ:flagMrLayerdrawing:"+flagMrLayerdrawing);
                if(flagMrLayerShow&&!flagMrLayerdrawing) {
                    System.out.println("ZGQ:MrShowTask sectorShow start");
                    flagMrLayerdrawing=true;
                        if (tempMRShow.size()>0) {
                            if(myApplication.MR_MAP_SHOW_R>1000000) {
                                mBaiduMap.clear();
                            }else{
                                for (int k = 0; k < tempMRShow.size(); k++) {
                                    tempMRShow.get(k).remove();
                                }
                            }
                            tempMRShow.clear();
                        }
                        System.out.println("ZGQ:MrShowTask clear finish");
                        // 绘制基站MR栅格图层
                        LatLng mrGridLeftTopPoint,mrGridRightTopPoint,mrGridRightBottomPoint,mrGridLeftBottomPoint;
                        MRGridLayer viewMRLayer= new MRGridLayer();

                        if(flagMrLayerMapMoved){
                            flagMrLayerMapMoved=false;
                            myApplication.MRShowCenLong = myApplication.dragMapCenLong;
                            myApplication.MRShowCenLat = myApplication.dragMapCenLat;
                        }else if(flagSearchLocateMap){
                            myApplication.MRShowCenLong = myApplication.searchResultLon;
                            myApplication.MRShowCenLat = myApplication.searchResultLat;
                        } else {
                            if(myApplication.isPlayingLog){
                                myApplication.MRShowCenLong = Double.parseDouble(logExcelArray[logPlayIndex][logGPSLongId]);
                                myApplication.MRShowCenLat = Double.parseDouble(logExcelArray[logPlayIndex][logGPSLatId]);
                            }
                            else{
                                myApplication.MRShowCenLong = myApplication.newGpsLong;
                                myApplication.MRShowCenLat = myApplication.newGpsLat;
                            }
                        }
                        System.out.println("ZGQ:MrShowTask mrGridCollect start");
                        List<MRGridLayer.MRGrid> mrGridList_show=viewMRLayer.mrGridCollect(myApplication.MRShowCenLong,myApplication.MRShowCenLat,db_mr,flagMrShowContent,getApplicationContext());
                        System.out.println("ZGQ:MrShowTask mrGridCollect finish");
                        if(mrGridList_show==null) {
                            flagMrLayerdrawing=false;
                            return;
                        }
                        NumberFormat pcFm =NumberFormat.getPercentInstance();
                        pcFm.setMaximumFractionDigits(2); //最大小数位数
                        List<Bundle> mrbundleList = new ArrayList<>();
                        List<OverlayOptions> mroptionList_polyline=new ArrayList<>();
                        System.out.println("ZGQ:MrShowTask show start");
                        for(int s=0;s<mrGridList_show.size();s++){
                            //System.out.println("ZGQ:MrShowTask show at "+s);
                            List<LatLng> polyPoint=new ArrayList<>();
                            mrGridLeftTopPoint=gpstobaiduCoordiConverter(new LatLng(mrGridList_show.get(s).mrgrid_lefttoplat,mrGridList_show.get(s).mrgrid_lefttoplong));
                            mrGridRightTopPoint=gpstobaiduCoordiConverter(new LatLng(mrGridList_show.get(s).mrgrid_righttoplat,mrGridList_show.get(s).mrgrid_righttoplong));
                            mrGridRightBottomPoint=gpstobaiduCoordiConverter(new LatLng(mrGridList_show.get(s).mrgrid_rightbottomlat,mrGridList_show.get(s).mrgrid_rightbottomlong));
                            mrGridLeftBottomPoint=gpstobaiduCoordiConverter(new LatLng(mrGridList_show.get(s).mrgrid_leftbottomlat,mrGridList_show.get(s).mrgrid_leftbottomlong));

                            polyPoint.add(mrGridLeftTopPoint);
                            polyPoint.add(mrGridRightTopPoint);
                            polyPoint.add(mrGridRightBottomPoint);
                            polyPoint.add(mrGridLeftBottomPoint);
                            polyPoint.add(mrGridLeftTopPoint);
                            int gridcolor= mrGridList_show.get(s).mr_gridcolor;
                            OverlayOptions option_polyline = new PolylineOptions().width(6).color(gridcolor).points(polyPoint);//AA 指定透明度。 00 是完全透明。 FF 是完全不透明
                            mroptionList_polyline.add(option_polyline);
                            //Polyline mpolyline=(Polyline) mBaiduMap.addOverlay(option_polyline);
                            if(flagMrInfoShow){
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("keyMr", "区域:"+mrGridList_show.get(s).mr_area+"\r\n"+
                                        "栅格序号:"+mrGridList_show.get(s).mr_gridnumber+"\r\n" +
                                        "MR覆盖率:"+pcFm.format(mrGridList_show.get(s).mr_coverpercent)+"\r\n" +
                                        "MR总条数:"+mrGridList_show.get(s).mr_quantity+"\r\n" +
                                        "MR弱覆盖条数:"+mrGridList_show.get(s).mr_poorquantity +"\r\n" +
                                        "其他信息:"+mrGridList_show.get(s).mr_info);
                                mrbundleList.add(bundle);
                                //mpolyline.setExtraInfo(bundle);
                            }
                            //tempMRShow.add(mpolyline);

                        }
                        List<Overlay> mrpolylineList =mBaiduMap.addOverlays(mroptionList_polyline);
                        for(int i=0;i<mrpolylineList.size();i++){
                            if(flagMrInfoShow){
                                (mrpolylineList.get(i)).setExtraInfo(mrbundleList.get(i));
                            }
                            //tempMRShow.add(mrpolylineList.get(i));
                        }
                        tempMRShow.addAll(mrpolylineList);
                        System.out.println("ZGQ:MrShowTask show finish");

                        flagMrLayerdrawing=false;
                        //Message msg = new Message();
                        //msg.what = 2;
                        //handler.sendMessage(msg);

                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("ZGQ:MR show Task error...");
                flagMrLayerdrawing=false;
            }
        }
    }

    class UserLayerShowTask extends TimerTask {
        public String mLayerName;
        public String mLonName;
        public String mLatName;
        public List<String> mInfoNameList=new ArrayList<>();
        public String mShape="trapezoid";
        public String mIconColorStr;
        public int mIconColor=0xAAFF0000;
        public double mIconSize=10.0; //单位米
        //public List<Polyline> mTpPolyLinesShow;
        public List<Overlay> mTpPolyLinesShow;
        public int mLayerIndex;
        public int mLayerType; //xls =1 kml=2

        public UserLayerShowTask(String layerName,String lonName,String latName,List<String> infoNameList,List<Overlay> tpPolyLines,String tpicon,String tpcolor,double tpsize,int layerNumber,int layertype){
            mLayerName=layerName;
            mLonName=lonName;
            mLatName=latName;
            mInfoNameList=infoNameList;
            mTpPolyLinesShow=tpPolyLines;
            mShape=tpicon;
            mIconColorStr=tpcolor;
            mIconSize=tpsize;
            mLayerIndex=layerNumber;
            mLayerType=layertype;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
                try {
                    mIconColor = nameToColor(mIconColorStr);
                    if (mTpPolyLinesShow.size() > 0) {
                        for (int k = 0; k < mTpPolyLinesShow.size(); k++) {
                            mTpPolyLinesShow.get(k).remove();
                        }
                        mTpPolyLinesShow.clear();
                    }
                    // 绘制基站MR栅格图层
                    LatLng mrGridLeftTopPoint, mrGridRightTopPoint, mrGridRightBottomPoint, mrGridLeftBottomPoint;
                    LatLng mrGridCenterPoint;
                    BitmapDescriptor xbitmap;
                    UserDefinedLayer viewUserLayer = new UserDefinedLayer(mLayerName, mLonName, mLatName, mInfoNameList, mShape, mIconColor, mIconSize, mLayerType);

                    if (flagUserLayerMapMovedList.get(mLayerIndex)) {
                        flagUserLayerMapMovedList.set(mLayerIndex, false);
                        myApplication.userLayerShowCenLong = myApplication.dragMapCenLong;
                        myApplication.userLayerShowCenLat = myApplication.dragMapCenLat;
                    }else if(flagSearchLocateMap){
                        myApplication.userLayerShowCenLong = myApplication.searchResultLon;
                        myApplication.userLayerShowCenLat = myApplication.searchResultLat;
                    } else {
                        if (myApplication.isPlayingLog) {
                            myApplication.userLayerShowCenLong = Double.parseDouble(logExcelArray[logPlayIndex][logGPSLongId]);
                            myApplication.userLayerShowCenLat = Double.parseDouble(logExcelArray[logPlayIndex][logGPSLatId]);
                        } else {
                            myApplication.userLayerShowCenLong = myApplication.newGpsLong;
                            myApplication.userLayerShowCenLat = myApplication.newGpsLat;
                        }
                    }
                    List<UserDefinedLayer.UserLayerMarker> userLayerMarkerList_show = viewUserLayer.userLayerDataCollect(myApplication.userLayerShowCenLong, myApplication.userLayerShowCenLat, db_sec, getApplicationContext());
                    //NumberFormat pcFm =NumberFormat.getPercentInstance();
                    //pcFm.setMaximumFractionDigits(2); //最大小数位数
                    List<Bundle> userbundleList = new ArrayList<>();
                    List<OverlayOptions> userOptionList_polyline = new ArrayList<>();

                    if (mLayerType == 2) {

                        for (int s = 0; s < userLayerMarkerList_show.size(); s++) {

                            if (userLayerMarkerList_show.get(s).mk_type_kml==1&&userLayerMarkerList_show.get(s).mk_PolyLinePoints_kml.size()>0) {

                                List<LatLng> polyPoint = userLayerMarkerList_show.get(s).mk_PolyLinePoints_kml;
                                int gridcolor = userLayerMarkerList_show.get(s).mk_color;
                                int fillcolor = userLayerMarkerList_show.get(s).mk_fillcolor;
                                OverlayOptions option_polyline = new PolylineOptions().width(8).color(gridcolor).points(polyPoint);//AA 指定透明度。 00 是完全透明。 FF 是完全不透明
                                userOptionList_polyline.add(option_polyline);
                                Bundle bundle = new Bundle();
                                String infostr = "图层名称:" + mLayerName;
                                for (int k = 0; k < mInfoNameList.size(); k++) {
                                    infostr = infostr + "\r\n" + mInfoNameList.get(k) + ":" + userLayerMarkerList_show.get(s).mk_infoList.get(k);
                                }
                                bundle.putSerializable("keyMr", infostr);
                                userbundleList.add(bundle);
                            } else if (userLayerMarkerList_show.get(s).mk_type_kml==2&&userLayerMarkerList_show.get(s).mk_LinePoints_kml.size()>0) {
                                List<LatLng> polyPoint = userLayerMarkerList_show.get(s).mk_LinePoints_kml;
                                int gridcolor = userLayerMarkerList_show.get(s).mk_color;
                                OverlayOptions option_polyline = new PolylineOptions().width(8).color(gridcolor).points(polyPoint);//AA 指定透明度。 00 是完全透明。 FF 是完全不透明
                                userOptionList_polyline.add(option_polyline);
                                Bundle bundle = new Bundle();
                                String infostr = "图层名称:" + mLayerName;
                                for (int k = 0; k < mInfoNameList.size(); k++) {
                                    infostr = infostr + "\r\n" + mInfoNameList.get(k) + ":" + userLayerMarkerList_show.get(s).mk_infoList.get(k);
                                }
                                bundle.putSerializable("keyMr", infostr);
                                userbundleList.add(bundle);
                            }else if (userLayerMarkerList_show.get(s).mk_type_kml==3&&userLayerMarkerList_show.get(s).mk_Point_kml!=null) {
                                LatLng dotPoint = userLayerMarkerList_show.get(s).mk_Point_kml;
                                int gridcolor = userLayerMarkerList_show.get(s).mk_color;
                                OverlayOptions option_polyline = new DotOptions().radius(6).color(gridcolor).center(dotPoint);//AA 指定透明度。 00 是完全透明。 FF 是完全不透明
                                userOptionList_polyline.add(option_polyline);
                                Bundle bundle = new Bundle();
                                String infostr = "图层名称:" + mLayerName;
                                for (int k = 0; k < mInfoNameList.size(); k++) {
                                    infostr = infostr + "\r\n" + mInfoNameList.get(k) + ":" + userLayerMarkerList_show.get(s).mk_infoList.get(k);
                                }
                                bundle.putSerializable("keyMr", infostr);
                                userbundleList.add(bundle);
                            }

                        }
                        List<Overlay> userLayerPolylineList = mBaiduMap.addOverlays(userOptionList_polyline);

                        for (int i = 0; i < userLayerPolylineList.size(); i++) {
                            (userLayerPolylineList.get(i)).setExtraInfo(userbundleList.get(i));
                            //mTpPolyLinesShow.add((Polyline)userLayerPolylineList.get(i));
                            mTpPolyLinesShow.add(userLayerPolylineList.get(i));
                        }

                    }
                    else{

                        if (mShape.equals("圆形图标")) {
                            xbitmap = BitmapDescriptorFactory.fromResource(R.drawable.btn_radio_on_holo_light);
                        } else if (mShape.equals("星型图标")) {
                            xbitmap = BitmapDescriptorFactory.fromResource(R.drawable.zgq_small_star);
                        } else if (mShape.equals("定位图标")) {
                            xbitmap = BitmapDescriptorFactory.fromResource(R.drawable.location_32_zgqpic);
                        } else {
                            xbitmap = BitmapDescriptorFactory.fromResource(R.drawable.zgq_small_star);
                        }
                        for (int s = 0; s < userLayerMarkerList_show.size(); s++) {

                            if (mShape.equals("梯形") || mShape.equals("正方形")) {

                                List<LatLng> polyPoint = new ArrayList<>();
                                mrGridLeftTopPoint = gpstobaiduCoordiConverter(new LatLng(userLayerMarkerList_show.get(s).mk_lefttoplat, userLayerMarkerList_show.get(s).mk_lefttoplong));
                                mrGridRightTopPoint = gpstobaiduCoordiConverter(new LatLng(userLayerMarkerList_show.get(s).mk_righttoplat, userLayerMarkerList_show.get(s).mk_righttoplong));
                                mrGridRightBottomPoint = gpstobaiduCoordiConverter(new LatLng(userLayerMarkerList_show.get(s).mk_rightbottomlat, userLayerMarkerList_show.get(s).mk_rightbottomlong));
                                mrGridLeftBottomPoint = gpstobaiduCoordiConverter(new LatLng(userLayerMarkerList_show.get(s).mk_leftbottomlat, userLayerMarkerList_show.get(s).mk_leftbottomlong));

                                polyPoint.add(mrGridLeftTopPoint);
                                polyPoint.add(mrGridRightTopPoint);
                                polyPoint.add(mrGridRightBottomPoint);
                                polyPoint.add(mrGridLeftBottomPoint);
                                polyPoint.add(mrGridLeftTopPoint);
                                int gridcolor = userLayerMarkerList_show.get(s).mk_color;
                                OverlayOptions option_polyline = new PolylineOptions().width(6).color(gridcolor).points(polyPoint);//AA 指定透明度。 00 是完全透明。 FF 是完全不透明
                                userOptionList_polyline.add(option_polyline);
                                Bundle bundle = new Bundle();
                                String infostr = "图层名称:" + mLayerName;
                                for (int k = 0; k < mInfoNameList.size(); k++) {
                                    infostr = infostr + "\r\n" + mInfoNameList.get(k) + ":" + userLayerMarkerList_show.get(s).mk_infoList.get(k);
                                }
                                bundle.putSerializable("keyMr", infostr);
                                userbundleList.add(bundle);
                            } else if (mShape.equals("圆形图标") || mShape.equals("定位图标") || mShape.equals("星型图标")) {
                                //定义Maker坐标点
                                mrGridCenterPoint = gpstobaiduCoordiConverter(new LatLng(userLayerMarkerList_show.get(s).mk_centlat, userLayerMarkerList_show.get(s).mk_centlong));
                                //构建MarkerOption，用于在地图上添加Marker
                                OverlayOptions option_marker = new MarkerOptions().position(mrGridCenterPoint).icon(xbitmap);
                                userOptionList_polyline.add(option_marker);
                                Bundle bundle = new Bundle();
                                String infostr = "图层名称:" + mLayerName;
                                for (int k = 0; k < mInfoNameList.size(); k++) {
                                    infostr = infostr + "\r\n" + mInfoNameList.get(k) + ":" + userLayerMarkerList_show.get(s).mk_infoList.get(k);
                                }
                                bundle.putSerializable("keyMr", infostr);
                                userbundleList.add(bundle);
                            }

                        }
                        List<Overlay> userLayerPolylineList = mBaiduMap.addOverlays(userOptionList_polyline);


                        for (int i = 0; i < userLayerPolylineList.size(); i++) {
                            (userLayerPolylineList.get(i)).setExtraInfo(userbundleList.get(i));
                            //mTpPolyLinesShow.add((Polyline)userLayerPolylineList.get(i));
                            mTpPolyLinesShow.add(userLayerPolylineList.get(i));
                        }
                    }
                    //Message msg = new Message();
                    //msg.what = 2;
                    //handler.sendMessage(msg);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("ZGQ:MR show Task error...");
                }
        }

    }

    private List<Overlay> tempPksShow = new ArrayList<>();
    private int[] outServiceCount={0,0,0,0,0,0,0,0,0,0,0};
    private int[] outServiceCellCount={0,0,0,0,0,0,0,0,0,0,0};
    class ParkingPlacesShowTask extends TimerTask {

        private List<List<String>> mList=new ArrayList<>();
        private int lonIndex=0;
        private int latIndex=0;
        private int countyIndex=0;
        private int isSiteOutofserviceIndex=0;
        public ParkingPlacesShowTask(List<List<String>> list){
            mList=list;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //System.out.println("ZGQ:sectorShowTask start");
            if(mList.size()>0) {
                try {
                    //flagSectorLteDrawing=true;
                    //System.out.println("ZGQ:sectorShowTask clearmap start");

                    if (tempPksShow.size()>0) {
                        for (int k = 0; k < tempPksShow.size(); k++) {
                            tempPksShow.get(k).remove();
                        }
                        tempPksShow.clear();
                        //System.out.println("ZGQ:sectorShowTask clearmap finish");
                    }
                    List<String> mInfoNameList=new ArrayList<>();
                    mInfoNameList=mList.get(0);
                    for(int i=0;i<mList.get(0).size();i++){
                        if(mList.get(0).get(i).contains("longitude")){
                            lonIndex=i;
                        }else if(mList.get(0).get(i).contains("latitude")){
                            latIndex=i;
                        }else if(mList.get(0).get(i).contains("COUNTY")){
                            countyIndex=i;
                        }else if(mList.get(0).get(i).contains("IsSiteOutofservice")){
                            isSiteOutofserviceIndex=i;
                        }
                    }
                    mList.remove(0);

                    BitmapDescriptor siteBitmap = BitmapDescriptorFactory.fromResource(R.drawable.location_32_zgqpic);
                    BitmapDescriptor cellBitmap = BitmapDescriptorFactory.fromResource(R.drawable.location_yellow_32_zgqpic);
                    List<OverlayOptions> userOptionList_polyline = new ArrayList<>();
                    List<Bundle> userbundleList = new ArrayList<>();

                    for(int i=0;i<outServiceCount.length;i++){
                        outServiceCount[i]=0;
                        outServiceCellCount[i]=0;
                    }
                    int isSite=0;
                    String tf_area="";
                    OverlayOptions option_marker;
                    for (int s = 0; s < mList.size(); s++) {
                        //定义Maker坐标点
                        LatLng mrGridCenterPoint = gpstobaiduCoordiConverter(new LatLng(Float.parseFloat(mList.get(s).get(latIndex)),Float.parseFloat(mList.get(s).get(lonIndex))));
                        //构建MarkerOption，用于在地图上添加Marker
                        isSite=Integer.parseInt(mList.get(s).get(isSiteOutofserviceIndex));
                        tf_area=mList.get(s).get(countyIndex);

                        if(isSite==1) {
                            if (tf_area.contains("鹿城")) {
                                outServiceCount[0]++;
                            } else if (tf_area.contains("龙湾")) {
                                outServiceCount[1]++;
                            } else if (tf_area.contains("瓯海")) {
                                outServiceCount[2]++;
                            } else if (tf_area.contains("乐清")) {
                                outServiceCount[3]++;
                            } else if (tf_area.contains("瑞安")) {
                                outServiceCount[4]++;
                            } else if (tf_area.contains("苍南")) {
                                outServiceCount[5]++;
                            } else if (tf_area.contains("永嘉")) {
                                outServiceCount[6]++;
                            } else if (tf_area.contains("平阳")) {
                                outServiceCount[7]++;
                            } else if (tf_area.contains("文成")) {
                                outServiceCount[8]++;
                            } else if (tf_area.contains("泰顺")) {
                                outServiceCount[9]++;
                            } else if (tf_area.contains("洞头")) {
                                outServiceCount[10]++;
                            }
                            option_marker = new MarkerOptions().position(mrGridCenterPoint).icon(siteBitmap);
                        }else{
                            if (tf_area.contains("鹿城")) {
                                outServiceCellCount[0]++;
                            } else if (tf_area.contains("龙湾")) {
                                outServiceCellCount[1]++;
                            } else if (tf_area.contains("瓯海")) {
                                outServiceCellCount[2]++;
                            } else if (tf_area.contains("乐清")) {
                                outServiceCellCount[3]++;
                            } else if (tf_area.contains("瑞安")) {
                                outServiceCellCount[4]++;
                            } else if (tf_area.contains("苍南")) {
                                outServiceCellCount[5]++;
                            } else if (tf_area.contains("永嘉")) {
                                outServiceCellCount[6]++;
                            } else if (tf_area.contains("平阳")) {
                                outServiceCellCount[7]++;
                            } else if (tf_area.contains("文成")) {
                                outServiceCellCount[8]++;
                            } else if (tf_area.contains("泰顺")) {
                                outServiceCellCount[9]++;
                            } else if (tf_area.contains("洞头")) {
                                outServiceCellCount[10]++;
                            }
                            option_marker = new MarkerOptions().position(mrGridCenterPoint).icon(cellBitmap);
                        }

                        userOptionList_polyline.add(option_marker);
                        Bundle bundle = new Bundle();
                        String infostr = "退服信息:";
                        for (int k = 0; k < mInfoNameList.size(); k++) {
                            infostr = infostr + "\r\n" + mInfoNameList.get(k) + ":" + mList.get(s).get(k);
                        }
                        bundle.putSerializable("keyMr", infostr);
                        userbundleList.add(bundle);


                    }
                    List<Overlay> userLayerPolylineList = mBaiduMap.addOverlays(userOptionList_polyline);
                    tempPksShow.addAll(userLayerPolylineList);

                    for (int i = 0; i < userLayerPolylineList.size(); i++) {
                        (userLayerPolylineList.get(i)).setExtraInfo(userbundleList.get(i));
                        //mTpPolyLinesShow.add((Polyline)userLayerPolylineList.get(i));
                    }

                    Message msg = new Message();
                    msg.what = 502;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("ZGQ:ParkingPlaces show Task error...");
                }
            }
        }
    }

    private SendRecMessageServerTask mSendRecMessage;
    class ParkingPlacesSearchTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //System.out.println("ZGQ:sectorShowTask start");
            if(true) {
                try {
                    //flagSectorLteDrawing=true;
                    //System.out.println("ZGQ:sectorShowTask clearmap start");

                    if(flagSectorMapMoved){
                        flagSectorMapMoved=false;
                        myApplication.sectorShowCenLong = myApplication.dragMapCenLong;
                        myApplication.sectorShowCenLat = myApplication.dragMapCenLat;
                    }else {
                        if(myApplication.isPlayingLog){
                            myApplication.sectorShowCenLong = Double.parseDouble(logExcelArray[logPlayIndex][logGPSLongId]);
                            myApplication.sectorShowCenLat = Double.parseDouble(logExcelArray[logPlayIndex][logGPSLatId]);
                        }
                        else{
                            myApplication.sectorShowCenLong = myApplication.newGpsLong;
                            myApplication.sectorShowCenLat = myApplication.newGpsLat;
                        }
                    }

                    Timer searchRoundParkingsTimer = new Timer();
                    SendRecMessageServerTask searchRoundParkingsTask = new SendRecMessageServerTask("searchRoundParkings","123456",myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"lplac",myApplication.sectorShowCenLong+"_"+myApplication.sectorShowCenLat,handler,501);
                    searchRoundParkingsTimer.schedule(searchRoundParkingsTask,0);
                    mSendRecMessage=searchRoundParkingsTask;

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("ZGQ:Parking-Places Search Task error...");
                }
            }
        }
    }

    class UserLayerColector implements Runnable{
        @Override
        public void run() {
            try{
                List<String> shPreUserLayerKeys=AppSettingActivity.readAllSetKey(MapViewActivity.this,"userlayerconfig");
                List<String> shPreUserLayerVals=AppSettingActivity.readAllSetValue(MapViewActivity.this,"userlayerconfig");

                for(int i=0;i<shPreUserLayerKeys.size();i++){
                    if(shPreUserLayerKeys.get(i).contains("userlayername_")){
                        userLayerNameList.add(shPreUserLayerVals.get(i));
                    }
                }
                myApplication.userLayerNames.clear();
                myApplication.userLayerNames.addAll(userLayerNameList);
                numsUserLayer=userLayerNameList.size();
                if(userLayerNameList.size()>0){
                    //userLayersFieldsArray=new String[userLayerNameList.size()][53];
                    for(int i=0;i<userLayerNameList.size();i++){
                        UserLayer tpUserLayer=new UserLayer();
                        tpUserLayer.userLayerName=userLayerNameList.get(i);
                        tpUserLayer.userLayerIndex=i;
                        for(int n=0;n<shPreUserLayerKeys.size();n++){
                            if(shPreUserLayerKeys.get(n).contains("userlayerlon_"+tpUserLayer.userLayerName)){
                                tpUserLayer.userLayerFieldName_lon=shPreUserLayerVals.get(n);
                            }else if(shPreUserLayerKeys.get(n).contains("userlayerlat_"+tpUserLayer.userLayerName)){
                                tpUserLayer.userLayerFieldName_lat=shPreUserLayerVals.get(n);
                            }else if(shPreUserLayerKeys.get(n).contains("userlayerinfo_"+tpUserLayer.userLayerName)){
                                tpUserLayer.userLayerInfoFieldNameList.add(shPreUserLayerVals.get(n));
                            }else if(shPreUserLayerKeys.get(n).contains("userlayertype_"+tpUserLayer.userLayerName)){
                                tpUserLayer.userLayerType=Integer.parseInt(shPreUserLayerVals.get(n));
                            }
                        }
                        userLayerList.add(tpUserLayer);
                        flagUserLayerMapMovedList.add(false);
                    }
                    sendMessage(handler,200);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    class UserLayer{
        public String userLayerName;
        public int userLayerIndex;
        public Boolean flagUserLayerShow=false;
        public String userLayerFieldName_lon;
        public String userLayerFieldName_lat;
        public List<String> userLayerInfoFieldNameList=new ArrayList<>();
        public Timer userLayerShowTimer;
        public TimerTask userLayerShowTask;
        public List<Overlay> tempUserLayerPolylines=new ArrayList<>();

        public TableRow userLayerTableRow;
        public ImageButton userlayerdelete;
        public CheckBox userLayerCheckBox;
        public String userLayerIcon="梯形";
        public String userLayerIconColor="红色";
        public double userLayerIconSize=10.0; //单位 米
        //public Boolean flagUserLayerMapMoved=false;
        public int userLayerType=0; //xls =1 kml =2
    }

    public static LatLng gpstobaiduCoordiConverter(LatLng mgpspoint) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(mgpspoint);
        return converter.convert();
    }



    public int levelToColor(int level){
        int dotColor=0x00000000;
        if (level>=myApplication.levelGreen){
            dotColor=0xFF008B00; //深绿
            return dotColor;
        }else if (level<myApplication.levelGreen&&level>=myApplication.levelLightGreen){
            dotColor=0xFF00FF00; //浅绿
            return dotColor;
        }else if (level<myApplication.levelLightGreen&&level>=myApplication.levelBlue){
            dotColor=0xFF00BFFF; //蓝色
            return dotColor;
        }else if (level<myApplication.levelBlue&&level>=myApplication.levelYellow){
            dotColor=0xFFFFFF00; //黄色
            return dotColor;
        }else if (level<myApplication.levelYellow&&level>=myApplication.levelRed){
            dotColor=0xFFFF0000; //红色
            return dotColor;
        }else if (level<myApplication.levelRed){
            dotColor=0xFF000000;  //黑色
            return dotColor;
        }else{
            return dotColor;
        }
    }

    public int levelToColorForCellLine(int level){
        int dotColor=0x88000000;
        if (level>=-80){
            dotColor=0x66008B00; //深绿
            return dotColor;
        }else if (level<-80&&level>=-90){
            dotColor=0x8800FF00; //浅绿
            return dotColor;
        }else if (level<-90&&level>=-100){
            dotColor=0x8800BFFF; //蓝色
            return dotColor;
        }else if (level<-100&&level>=-110){
            dotColor=0x88FFFF00; //黄色
            return dotColor;
        }else if (level<-110&&level>=-120){
            dotColor=0x88FF0000; //红色
            return dotColor;
        }else if (level<-120){
            dotColor=0x88000000;  //黑色
            return dotColor;
        }else{
            return dotColor;
        }
    }

    public  static double getAngle(LatLng A,LatLng B){
        double Rc=6378137;
        double Rj=6356725;

        double Am_RadLo=A.longitude*Math.PI/180.;
        double Am_RadLa=A.latitude*Math.PI/180.;
        double Bm_RadLo=B.longitude*Math.PI/180.;
        double Bm_RadLa=B.latitude*Math.PI/180.;

        double A_Ec=Rj+(Rc-Rj)*(90.-A.latitude)/90.;
        double A_Ed=A_Ec*Math.cos(Am_RadLa);
        double B_Ec=Rj+(Rc-Rj)*(90.-B.latitude)/90.;
        double B_Ed=B_Ec*Math.cos(Bm_RadLa);

        double dx=(Bm_RadLo-Am_RadLo)*A_Ed;
                double dy=(Bm_RadLa-Am_RadLa)*A_Ec;
                double angle=0.0;
                angle=Math.atan(Math.abs(dx/dy))*180./Math.PI;
                double dLo=B.longitude-A.longitude;
                double dLa=B.latitude-A.latitude;
                if(dLo>0&&dLa<=0){
                        angle=(90.-angle)+90;
                    }
                else if(dLo<=0&&dLa<0){
                        angle=angle+180.;
                    }else if(dLo<0&&dLa>=0){
                        angle= (90.-angle)+270;
                    }
                return angle;
            }

    public static int nameToColor(String colorName){
        int dotColor=0x00000000;
        if (colorName.equals("紫色")){
            dotColor=0xFFCC33CC; //紫色
            return dotColor;
        }else if (colorName.equals("绿色")){
            dotColor=0xFF00FF00; //浅绿
            return dotColor;
        }else if (colorName.equals("蓝色")){
            dotColor=0xFF00BFFF; //蓝色
            return dotColor;
        }else if (colorName.equals("黄色")){
            dotColor=0xFFFFFF00; //黄色
            return dotColor;
        }else if (colorName.equals("红色")){
            dotColor=0xFFFF0000; //红色
            return dotColor;
        }else if (colorName.equals("黑色")){
            dotColor=0xFF000000;  //黑色
            return dotColor;
        }else if (colorName.equals("橙色")){
            dotColor=0xFFFFCC00;  //橙色
            return dotColor;
        }else if (colorName.equals("棕色")){
            dotColor=0xFFCC0000;  //棕色
            return dotColor;
        }else if (colorName.equals("白色")){
            dotColor=0xFFFFFFFF;  //白色
            return dotColor;
        }else{
            return dotColor;
        }
    }

    public int sinrToColor(int sinr){
        int dotColor=0x00000000;
        if (sinr>=20){
            dotColor=0xFF008B00; //深绿
            return dotColor;
        }else if (sinr<20&&sinr>=10){
            dotColor=0xFF00FF00; //浅绿
            return dotColor;
        }else if (sinr<10&&sinr>=3){
            dotColor=0xFF00BFFF; //蓝色
            return dotColor;
        }else if (sinr<3&&sinr>=0){
            dotColor=0xFFFFFF00; //黄色
            return dotColor;
        }else if (sinr<0&&sinr>=-10){
            dotColor=0xFFFF0000; //红色
            return dotColor;
        }else if (sinr<-10){
            dotColor=0xFF000000;  //黑色
            return dotColor;
        }else{
            return dotColor;
        }
    }

    public static boolean isNumeric(String str){
        for (int i = 0; i < str.length(); i++){
            //System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public static void sendMessage(Handler handler,int i){
        Message msg = new Message();
        msg.what = i;
        handler.sendMessage(msg);
    }

    private  Timer parkingPlacesSearchTimer;
    private TimerTask parkingPlacesSearchTask;
    public void sectorShowLTE(){
        try{
            if(!flagSectorLayerShow&&!flagSectorGsmLayerShow){
                return;
            }else if(myApplication.USER_AUTH_INFO.vipUser==0){
                Toast.makeText(MapViewActivity.this,"非VIP用户不支持显示基站图层！",Toast.LENGTH_SHORT).show();
                return;
            }

            if(flagOutServiceOn==true) {
                parkingPlacesSearchTimer = new Timer();
                parkingPlacesSearchTask = new ParkingPlacesSearchTask();
                parkingPlacesSearchTimer.schedule(parkingPlacesSearchTask, sectorShowDelay);
            }

            if(flagSectorLayerShow&&!flagMrLayerdrawing&&!flagSectorLteDrawing){
                if(sectorShowTimer!=null){
                    sectorShowTimer.cancel();
                    sectorShowTimer=null;
                }
                if(sectorShowTask!=null){
                    sectorShowTask.cancel();
                    sectorShowTask=null;
                }
                sectorShowTimer = new Timer();
                sectorShowTask = new SectorShowTask();
                sectorShowTimer.schedule(sectorShowTask, sectorShowDelay);
            }else{
                System.out.println("zgq:wrong");
            }
            if(flagSectorGsmLayerShow&&!flagMrLayerdrawing&&!flagSectorGsmDrawing){
                if(sectorGsmShowTimer!=null){
                    sectorGsmShowTimer.cancel();
                    sectorGsmShowTimer=null;
                }
                if(sectorGsmShowTask!=null){
                    sectorGsmShowTask.cancel();
                    sectorGsmShowTask=null;
                }
                sectorGsmShowTimer = new Timer();
                sectorGsmShowTask = new SectorGsmShowTask();
                sectorGsmShowTimer.schedule(sectorGsmShowTask, sectorShowDelay);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void mrShowLTE(){
        try{
            if(!flagMrLayerShow){
                return;
            }else if(myApplication.USER_AUTH_INFO.vipUser==0){
                Toast.makeText(MapViewActivity.this,"非VIP用户不支持显示MR图层！",Toast.LENGTH_SHORT).show();
                return;
            }
            if(flagMrLayerShow&&!flagMrLayerdrawing){
                if(mrLayerShowTimer!=null){
                    mrLayerShowTimer.cancel();
                    mrLayerShowTimer=null;
                }
                if(mrLayerShowTask!=null){
                    mrLayerShowTask.cancel();
                    mrLayerShowTask=null;
                    //flagMrLayerdrawing=false;
                }
                mrLayerShowTimer = new Timer();
                mrLayerShowTask = new MrShowTask();
                mrLayerShowTimer.schedule(mrLayerShowTask, mrLayerShowDelay);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void userLayerShow(){
        try{

            if(!flagMrLayerdrawing){
                for(int i=0;i<userLayerList.size();i++){
                    if(userLayerList.get(i).flagUserLayerShow){

                        if(myApplication.USER_AUTH_INFO.vipUser==0){
                            Toast.makeText(MapViewActivity.this,"非VIP用户不支持显示自定义图层！",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(userLayerList.get(i).userLayerShowTimer!=null){
                            userLayerList.get(i).userLayerShowTimer.cancel();
                            userLayerList.get(i).userLayerShowTimer=null;
                        }
                        if(userLayerList.get(i).userLayerShowTask!=null){
                            userLayerList.get(i).userLayerShowTask.cancel();
                            userLayerList.get(i).userLayerShowTask=null;
                        }
                        userLayerList.get(i).userLayerShowTimer = new Timer();
                        userLayerList.get(i).userLayerShowTask = new UserLayerShowTask(userLayerList.get(i).userLayerName,userLayerList.get(i).userLayerFieldName_lon,userLayerList.get(i).userLayerFieldName_lat,userLayerList.get(i).userLayerInfoFieldNameList,userLayerList.get(i).tempUserLayerPolylines,userLayerList.get(i).userLayerIcon,userLayerList.get(i).userLayerIconColor,userLayerList.get(i).userLayerIconSize,userLayerList.get(i).userLayerIndex,userLayerList.get(i).userLayerType);
                        userLayerList.get(i).userLayerShowTimer.schedule(userLayerList.get(i).userLayerShowTask, userLayerShowDelay);


                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private String tempLogName="";
    private class TempLogCheck extends TimerTask{
        @Override
        public void run() {
            File path = new File(myApplication.logFileSavePath);
            File[] files = path.listFiles();// 读取文件夹下文件
            List<String> fileNameList=getFileNamesPhoneDir(files,".xls");
            //System.out.println("ZGQ:"+fileNameList);
            for(int i=0;i<fileNameList.size();i++){
                if(fileNameList.get(i).contains("templog_")){
                    tempLogName=fileNameList.get(i);
                    Message msg11=new Message();
                    msg11.what=11;
                    handler.sendMessage(msg11);
                    return;
                }
            }

        }
    }

    //读取指定目录下的所有.xls文件的文件名
    private static List<String> getFileNamesPhoneDir(File[] files,String filetype) {
        List<String> str=new ArrayList<>();
        if (files != null) {    // 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()){//检查此路径名的文件是否是一个目录(文件夹)
                    getFileNamesPhoneDir(file.listFiles(),filetype);
                } else {
                    String fileName = file.getName();
                    if (fileName.endsWith(filetype)) {
                        //String s=fileName.substring(0,fileName.lastIndexOf(".")).toString();
                        //str += fileName.substring(0,fileName.lastIndexOf("."))+"\n";
                        str.add(fileName);
                    }
                }
            }
        }
        return str;
    }

    private void setScreenLightForActivity(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    public static int getScreenBrightness(Activity context) {
        int value = 0;
        ContentResolver cr = context.getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {

        }
        return value;
    }

    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.example.administrator.phoneinfo.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


    /**
     * fuction: 设置固定的宽度，高度随之变化，使图片不会变形
     *
     * @param target
     * 需要转化bitmap参数
     * @param newWidth
     * 设置新的宽度
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap target, int newWidth)
    {
        try{
        int width = target.getWidth();
        int height = target.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        // float scaleHeight = ((float)newHeight) / height;
        int newHeight = (int) (scaleWidth * height);
        matrix.postScale(scaleWidth, scaleWidth);
        // Bitmap result = Bitmap.createBitmap(target,0,0,width,height,
        // matrix,true);
        Bitmap bmp = Bitmap.createBitmap(target, 0, 0, width, height, matrix,
                true);
        if (target != null && !target.equals(bmp) && !target.isRecycled())
        {
            target.recycle();
            target = null;
        }
        return bmp;// Bitmap.createBitmap(target, 0, 0, width, height, matrix,
        // true);
    }
    catch(Exception e) {
        e.printStackTrace();
        return null;
    }
    }


}