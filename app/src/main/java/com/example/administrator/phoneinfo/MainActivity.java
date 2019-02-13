package com.example.administrator.phoneinfo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.SearchView;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.administrator.phoneinfo.CreateCellInfoDBActivity.DBNAME1;
import static com.example.administrator.phoneinfo.CreateCellInfoDBActivity.TABLENAME1;
import static com.example.administrator.phoneinfo.CreateCellInfoDBActivity.TABLENAME_GSM;

public class MainActivity extends AppCompatActivity {
    public static final int NP_CELL_INFO_UPDATE = 1001;
    private int newVerCode = -1;
    private String newVerName = "";
    private String newVerInfo;

    public TelephonyManager phoneManager;

    private PhoneInfoThread phoneInfoThread;
    private boolean flagThreadrun = true;
    private int msgcount;
    public Handler mMainHandler;

    private MyPhoneStateListener MyListener;//MyPhoneStateListener类的对象，即设置一个监听器对象
    private boolean flagSigStrFromlistener = false;
    private int flagEarfcnMethodExist = 0;   //0代表未尝试，1代表存在，2代表不存在
    private int flagGsmArfcnMethodExist = 0;   //0代表未尝试，1代表存在，2代表不存在

    private boolean flagMainActStart = true;
    private List<SignalStrength> signalStrengthList = new ArrayList<SignalStrength>();

    private CellnfoRecycleViewAdapter myRecycleViewAdapter;
    private RecyclerView recyclerView;
    private List<CellGeneralInfo> HistoryServerCellList;
    private CellnfoRecycleViewAdapter historyRecycleViewAdapter;
    private RecyclerView historyrecyclerView;

    public MyApplication myApplication1;
    public LocationManager locationManager1;
    public short listenSINR_coef = 1;
    //public Boolean getLatLonFromGps = false; //true从手机GPS获取经纬度信息，false从Baidu服务器获取经纬度信息
    //public short phoneGpsSpan = 3000;// 默认3秒监听一次，如果能收到GPS，改为1秒监听一次

    public LocationClient mBDLocationClient = null;
    public BDLocationListener myBDLocationListener = new MyBDLocationListener();
    private SendRecMessageServerTask mSendRecMsgTask;

    public boolean onCreateOptionsMenu(Menu menu) {
        //使用菜单填充器获取menu下的菜单资源文件
        getMenuInflater().inflate(R.menu.search_share_menu, menu);
        //获取搜索的菜单组件
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        //设置搜索的事件
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast t = Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //获取分享的菜单子组件
        //MenuItem shareItem = menu.findItem(R.id.share);
        //ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        //通过setShareIntent调用getDefaultIntent()获取所有具有分享功能的App
        //shareActionProvider.setShareIntent(getDefaultIntent());
        return super.onCreateOptionsMenu(menu);
    }

    //设置可以调用手机内所有可以分享图片的应用
    private Intent getDefaultIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        //这里的类型可以按需求设置
        intent.setType("image/*");
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_readcellinfo:
                Intent intent = new Intent(MainActivity.this, CreateCellInfoDBActivity.class);
                //startActivityForResult(intent,1);
                startActivity(intent);
                //return true;
                break;
            case R.id.action_mapview:

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7);
                } else {
                    Intent intent_map = new Intent(MainActivity.this, MapViewActivity.class);
                    //startActivityForResult(intent,1);
                    startActivity(intent_map);
                    //return true;
                }

                break;
            case R.id.action_logging:

                if (myApplication1.flagUserLoginSucc) {
                    startActivity(new Intent(MainActivity.this, GroupManagerActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginSocketActivity.class));
                }
                //Toast.makeText(this, "用户登陆，开发中...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_groupmanage:
                startActivity(new Intent(MainActivity.this, GroupManagerActivity.class));
                //Toast.makeText(this, "群组管理，开发中...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_groupfiles:
                startActivity(new Intent(MainActivity.this, GroupFileManagerActivity.class));

                break;
            case R.id.action_setting:
                if (myApplication1.USER_AUTH_INFO.userName.equals("zhugeqingM9")) {
                    Intent intent_set = new Intent(MainActivity.this, AppSettingActivity.class);
                    startActivity(intent_set);
                } else {
                    Toast.makeText(MainActivity.this, "该功能暂未开放...", Toast.LENGTH_LONG).show();
                    return true;
                }
                break;
            case R.id.action_help:
                //Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
                //return true;
                Intent intent_help = new Intent(MainActivity.this, HelpWebViewActivity.class);
                startActivity(intent_help);
                break;
            case R.id.action_updateversion:
                startActivity(new Intent(MainActivity.this, AppUpdateActivity.class));

                break;
            case R.id.action_sharedownUri:
                //File sharepic=new File("drawable/yuntu2.png");
                //shareWechatFriend(MainActivity.this,"",sharepic);

                shareToWxFriend("drawable/yuntu2.png", "网优云图下载链接", "因微信限制，请将网优云图APP链接拷贝到手机浏览器中下载(不要直接在微信中点击)http://120.199.120.85:50080/wzgjgl_mobile/RedisQuery/CellQuery/AppDownLoadPage.aspx?UserToken=A7866216BFA9EFC0&_from=qrcode");

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    void InitProcessThread() {
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                try {

                    if (msg.what == NP_CELL_INFO_UPDATE) {
                        msgcount++;

                        //Bundle bundle = msg.getData();
                        if (myApplication1.cellInfoList != null && HistoryServerCellList != null) {
                            myRecycleViewAdapter.notifyDataSetChanged();
                            historyRecycleViewAdapter.notifyDataSetChanged();
                        }
                        TextView tvTime = (TextView) findViewById(R.id.tvTimeleaps);
                        tvTime.setText(myApplication1.myPhoneInfo1.sysTime);
                        TextView tvAllCellInfo = (TextView) findViewById(R.id.tvCellCount);
                        tvAllCellInfo.setText("(" + HistoryServerCellList.size() + ")");

                        TextView tvDeviceId = (TextView) findViewById(R.id.tvDeviceId);
                        tvDeviceId.setText("IMEI:" + myApplication1.myPhoneInfo1.deviceId);

                        TextView tvRatType = (TextView) findViewById(R.id.tvRatType);
                        tvRatType.setText("网络制式:" + myApplication1.myPhoneInfo1.ratType);
                        TextView tvMnc = (TextView) findViewById(R.id.tMnc);
                        tvMnc.setText("MNC:" + myApplication1.myPhoneInfo1.mnc);
                        TextView tvMcc = (TextView) findViewById(R.id.tvMcc);
                        tvMcc.setText("MCC:" + myApplication1.myPhoneInfo1.mcc);
                        TextView tvOperatorName = (TextView) findViewById(R.id.tvOperaterName);
                        tvOperatorName.setText("运营商:" + myApplication1.myPhoneInfo1.operaterName);
                        TextView tvImsi = (TextView) findViewById(R.id.tvImsi);
                        tvImsi.setText("IMSI:" + myApplication1.myPhoneInfo1.Imsi);
                        //TextView tvLine1Number = (TextView) findViewById(R.id.tvLine1Number);
                        //tvLine1Number.setText("LN:" + myApplication1.myPhoneInfo1.line1Number);

                        //TextView tvSerialNum = (TextView) findViewById(R.id.tvSerialNum);
                        //tvSerialNum.setText("SN:" + myApplication1.myPhoneInfo1.serialNumber);

                        TextView tvModel = (TextView) findViewById(R.id.tvModel);
                        tvModel.setText("Model:" + myApplication1.myPhoneInfo1.phoneModel);
                        TextView tvSoftwareVersion = (TextView) findViewById(R.id.tvSoftware);
                        tvSoftwareVersion.setText("Version:" + myApplication1.myPhoneInfo1.deviceSoftwareVersion);
                        TextView tvPhoneLongitude = (TextView) findViewById(R.id.tvPhoneGpsLong);
                        tvPhoneLongitude.setText("经度:" + myApplication1.myPhoneInfo1.phoneGPSlong);
                        TextView tvPhoneLatitude = (TextView) findViewById(R.id.tvPhoneGpsLat);
                        tvPhoneLatitude.setText("纬度:" + myApplication1.myPhoneInfo1.phoneGPSlat);
                        TextView tvLocType = (TextView) findViewById(R.id.tvloctype);
                        tvLocType.setText("定位类型:" + myApplication1.myPhoneInfo1.locType);
                        TextView tvSpeed = (TextView) findViewById(R.id.tvtermspeed);
                        tvSpeed.setText("当前速度:" + myApplication1.myPhoneInfo1.termSpeed + " 米/秒");
                        TextView tvAltitude = (TextView) findViewById(R.id.tvaltitude);
                        tvAltitude.setText("海拔高度:" + myApplication1.myPhoneInfo1.termAltitude + " 米");
                        TextView tvDirection = (TextView) findViewById(R.id.tvtermdir);
                        tvDirection.setText("方向:" + myApplication1.myPhoneInfo1.termDirection);
                        TextView tvAddress = (TextView) findViewById(R.id.tvtermaddr);
                        tvAddress.setText("地址:" + myApplication1.myPhoneInfo1.termAddress);
                    } else if (msg.what == 201) {
                        if (myApplication1.USER_AUTH_INFO.authresult.equals("fail_log")) {
                            Toast.makeText(MainActivity.this, "自动登录失败！请点击右上角“用户登录”菜单完成用户登录或新用户注册！", Toast.LENGTH_LONG).show();
                            myApplication1.flagUserLoginSucc = false;
                        } else if (myApplication1.USER_AUTH_INFO.authresult.equals("succ_log")) {
                            Toast.makeText(MainActivity.this, "自动登录成功！", Toast.LENGTH_SHORT).show();
                            myApplication1.flagUserLoginSucc = true;
                            myApplication1.cellInfoFtpDir = "/" + myApplication1.USER_AUTH_INFO.groupName + "/cellinfo/";
                            myApplication1.mrFilesFtpDir = "/" + myApplication1.USER_AUTH_INFO.groupName + "/MR_all/";
                            myApplication1.logFilesFtpDir = "/" + myApplication1.USER_AUTH_INFO.groupName + "/log/";
                            myApplication1.layerFilesFtpDir = "/" + myApplication1.USER_AUTH_INFO.groupName + "/layer_files/";
                        } else if (myApplication1.USER_AUTH_INFO.authresult.contains("[{")) {
                            //新版用户登陆收到的jason格式用户数据
                            JSONObject jsonObj = mSendRecMsgTask.getJsonArrayRes().getJSONObject(0);

                            myApplication1.USER_AUTH_INFO.userId = Integer.parseInt(String.valueOf(jsonObj.get("id")));
                            //myApplication1.USER_AUTH_INFO.userName = String.valueOf(jsonObj.get("username"));
                            //myApplication1.USER_AUTH_INFO.userPassword = String.valueOf(jsonObj.get("userpassword"));
                            myApplication1.USER_AUTH_INFO.vipUser = Integer.parseInt(String.valueOf(jsonObj.get("vipuser")));
                            myApplication1.USER_AUTH_INFO.groupName = String.valueOf(jsonObj.get("groupname"));
                            //myApplication1.USER_AUTH_INFO.userImei = String.valueOf(jsonObj.get("userImei"));
                            myApplication1.USER_AUTH_INFO.flagGroupMaster = Integer.parseInt(String.valueOf(jsonObj.get("groupmaster")));
                            myApplication1.USER_AUTH_INFO.flagGroupCellShare = Integer.parseInt(String.valueOf(jsonObj.get("group_cellshare")));
                            myApplication1.USER_AUTH_INFO.flagGroupLogShare = Integer.parseInt(String.valueOf(jsonObj.get("group_logshare")));
                            myApplication1.USER_AUTH_INFO.flagGroupMrShare = Integer.parseInt(String.valueOf(jsonObj.get("group_mrshare")));
                            myApplication1.USER_AUTH_INFO.flagGroupUserLayerShare = Integer.parseInt(String.valueOf(jsonObj.get("group_ulayershare")));
                            myApplication1.USER_AUTH_INFO.flagGroupModCell = Integer.parseInt(String.valueOf(jsonObj.get("group_modifycellinfo")));
                            myApplication1.USER_AUTH_INFO.vipRemainDays = Integer.parseInt(String.valueOf(jsonObj.get("vip_remaindays")));
                            myApplication1.cellInfoFtpDir = "/" + myApplication1.USER_AUTH_INFO.groupName + "/cellinfo/";
                            myApplication1.mrFilesFtpDir = "/" + myApplication1.USER_AUTH_INFO.groupName + "/MR_all/";
                            myApplication1.logFilesFtpDir = "/" + myApplication1.USER_AUTH_INFO.groupName + "/log/";
                            myApplication1.layerFilesFtpDir = "/" + myApplication1.USER_AUTH_INFO.groupName + "/layer_files/";
                            myApplication1.flagUserLoginSucc = true;
                            Toast.makeText(MainActivity.this, "自动登录成功！VIP有效期" + myApplication1.USER_AUTH_INFO.vipRemainDays + "天。", Toast.LENGTH_LONG).show();
                        }
                    } else if (msg.what == 11) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setCancelable(false);
                        String versiontip = "当前版本" + getVerName(MainActivity.this) + "，网优云图存在最新版本" + newVerName + "，是否升级?" + "\r\n" + newVerInfo;
                        builder1.setMessage(versiontip);
                        builder1.setTitle("提示");
                        builder1.setPositiveButton("升级", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent_update1 = new Intent(MainActivity.this, MyAppUpdateService.class);
                                startService(intent_update1);
                                Toast.makeText(MainActivity.this, "正在后台下载最新版本，请稍后...", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder1.setNegativeButton("取消", null);
                        builder1.create().show();
                    } else if (msg.what == 12) {
                        Toast.makeText(MainActivity.this, "当前为最新版本" + getVerName(MainActivity.this), Toast.LENGTH_LONG).show(); // 提示当前为最新版本
                    } else if (msg.what == 15) {
                        showHelpDialog();
                    }
                    super.handleMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };

        phoneInfoThread = new PhoneInfoThread(MainActivity.this);
        new Thread(phoneInfoThread).start();
        //启动自动登录
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> userInfoKeys = AppSettingActivity.readAllSetKey(MainActivity.this, "userInfo");
                    List<String> userInfoValues = AppSettingActivity.readAllSetValue(MainActivity.this, "userInfo");
                    for (int i = 0; i < userInfoKeys.size(); i++) {
                        if (userInfoKeys.get(i).equals("key_username")) {
                            myApplication1.USER_AUTH_INFO.userName = userInfoValues.get(i);
                        } else if (userInfoKeys.get(i).equals("key_password")) {
                            myApplication1.USER_AUTH_INFO.userPassword = userInfoValues.get(i);
                        } else if (userInfoKeys.get(i).equals("key_imei")) {
                            myApplication1.USER_AUTH_INFO.userImei = userInfoValues.get(i);
                        }
                    }
                    Timer loginTimer = new Timer();
                    SendRecMessageServerTask loginTask = new SendRecMessageServerTask(myApplication1.USER_AUTH_INFO.userName
                            , myApplication1.USER_AUTH_INFO.userPassword, myApplication1.USER_AUTH_INFO.userImei
                            , myApplication1.USER_AUTH_INFO, "logne", " ", mMainHandler, 201);
                    loginTimer.schedule(loginTask, 0);
                    mSendRecMsgTask = loginTask;

                    //
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//自定义标题栏
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.mycustomtitle);//使用布局文件来定义标
        myApplication1 = (MyApplication) this.getApplicationContext();

        /*
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        int memClass = activityManager.getMemoryClass();//64，以m为单位
        System.out.println("ZGQ:application memory class of the current device " + memClass + "MB");

        int largeMemoryClass = activityManager.getLargeMemoryClass();
        System.out.println("ZGQ:application large memory class of the current device " + largeMemoryClass + "MB");
        */
        //权限检查
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.READ_PHONE_STATE
                    , Manifest.permission.BODY_SENSORS
                    , Manifest.permission.CAMERA
                    , Manifest.permission.SYSTEM_ALERT_WINDOW
            }, 8);
        } else {
            //System.out.println("权限检查ok");
        }

        mBDLocationClient = new LocationClient(getApplicationContext());//声明LocationClient类
        mBDLocationClient.registerLocationListener(myBDLocationListener);//注册监听函数
        initBDLocation();
        mBDLocationClient.start();
        //System.out.println("ZGQ:BDlocation start :init");

        locationManager1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList1 = locationManager1.getProviders(true);
        String provider1;
        if (providerList1.contains(LocationManager.GPS_PROVIDER)) {
            provider1 = LocationManager.GPS_PROVIDER;
        } else if (providerList1.contains(LocationManager.NETWORK_PROVIDER)) {
            //provider1 = LocationManager.NETWORK_PROVIDER;
            provider1 = LocationManager.GPS_PROVIDER;

        } else {
            //Toast.makeText(this,"ZGQ:No location provider to use",Toast.LENGTH_LONG).show();
            provider1 = null;
            AlertDialog.Builder builder_gps = new AlertDialog.Builder(this);
            builder_gps.setCancelable(false).setMessage("建议您打开手机GPS开关，否则无法准确获取无线信号信息，影响各项功能的正常使用！").setTitle("提示");
            builder_gps.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder_gps.create().show();
        }

        /*
        Location location1 = null;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location1 = locationManager1.getLastKnownLocation(provider1);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "请打开GPS定位开关", Toast.LENGTH_LONG).show();
            provider1 = LocationManager.GPS_PROVIDER;
        }
        if (location1!= null) {
            myApplication1.newGpsLong = location1.getLongitude();
            myApplication1.newGpsLat = location1.getLatitude();
        } else {
            myApplication1.newGpsLong = 120.72144;
            myApplication1.newGpsLat = 28.01648;
        }
        */

        try {
            locationManager1.requestLocationUpdates(provider1, 15000, 20, locationListener1);
            locationManager1.addGpsStatusListener(statusListener);
            myApplication1.locationListener_app = locationListener1;
            myApplication1.locationManager_app = locationManager1;
            if (provider1 != null) {
                myApplication1.gpsProvider_app = String.copyValueOf(provider1.toCharArray());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) findViewById(R.id.myrcv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        myRecycleViewAdapter = new CellnfoRecycleViewAdapter(MainActivity.this, myApplication1.cellInfoList);
        recyclerView.setAdapter(myRecycleViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //
        HistoryServerCellList = new ArrayList<CellGeneralInfo>();
        historyrecyclerView = (RecyclerView) findViewById(R.id.historyrcv);
        LinearLayoutManager historylayoutManager = new LinearLayoutManager(this);
        historylayoutManager.setOrientation(OrientationHelper.VERTICAL);
        historyrecyclerView.setLayoutManager(historylayoutManager);
        historyRecycleViewAdapter = new CellnfoRecycleViewAdapter(MainActivity.this, HistoryServerCellList);
        historyrecyclerView.setAdapter(historyRecycleViewAdapter);
        historyrecyclerView.setItemAnimator(new DefaultItemAnimator());

        historyRecycleViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if ((historyRecycleViewAdapter.getItemCount() - 1) > 0) {
                    historyrecyclerView.scrollToPosition(historyRecycleViewAdapter.getItemCount() - 1);
                }
            }
        });


        phoneManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        msgcount = 0;
        myApplication1.logFileSavePath = MainActivity.this.getApplicationContext().getExternalFilesDir(null).toString() + "/";
        InitProcessThread();
        //MyListener = new MyPhoneStateListener();//初始化对象
        //phoneManager.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//Registers a listener object to receive notification of changes in specified telephony states.设置监听器监听特定事件的状态
        MyListener = new MyPhoneStateListener();//初始化对象
        phoneManager.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//Registers a listener object to receive notification of changes in specified telephony states.设置监听器监听特定事件的状态

        AppCheckVersionUpdate checkVersionThr = new AppCheckVersionUpdate();
        new Thread(checkVersionThr).start();
        //showHelpDialog();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 8: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限请求成功的操作
                    Intent intent_restart = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    intent_restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent_restart);
                } else {
                    // 权限请求失败的操作
                    Toast.makeText(this, "权限请求失败，将影响软件功能正常使用，可重新启动软件，取得授权。", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // case其他权限结果。。
        }
    }

    @Override
    protected void onDestroy() {
        System.out.println("ZGQ:MainActivity on Destroy");

        flagThreadrun = false;
        if (locationManager1 != null) {
            locationManager1.removeUpdates(locationListener1);
        }
        if (mBDLocationClient.isStarted()) {
            mBDLocationClient.stop();
        }
        //phoneManager.listen(MyListener,PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }


    public class MyBDLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取定位结果
            double bdLong = location.getLongitude();
            double bdLat = location.getLatitude();
            LatLng gpsLatLng = PositionUtility.bd09_To_Gps84(bdLat, bdLong);
            myApplication1.newGpsLong = (Math.round(gpsLatLng.longitude * 100000) / 100000.0);
            myApplication1.newGpsLat = (Math.round(gpsLatLng.latitude * 100000) / 100000.0);
            myApplication1.myPhoneInfo1.locType = "网络定位";
            myApplication1.myPhoneInfo1.termSpeed = location.getSpeed();
            myApplication1.myPhoneInfo1.termAltitude = location.getAltitude();
            myApplication1.myPhoneInfo1.termDirection = location.getDirection();
            myApplication1.myPhoneInfo1.termAddress = location.getAddrStr();
/*
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间
            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息
            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度
            if (location.getLocType() == BDLocation.TypeGpsLocation){
                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数
                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败!");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息

            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
            */
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            Toast.makeText(MainActivity.this, "ZGQ:连接了WIFI", Toast.LENGTH_LONG).show();
        }
    }

    private void initBDLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 30000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mBDLocationClient.setLocOption(option);
    }

    LocationListener locationListener1 = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (mBDLocationClient.isStarted()) {
                mBDLocationClient.stop();
            }
            myApplication1.newGpsLong = Math.round(location.getLongitude() * 100000) / 100000.0;
            myApplication1.newGpsLat = Math.round(location.getLatitude() * 100000) / 100000.0;
            myApplication1.myPhoneInfo1.locType = location.getProvider();
            myApplication1.myPhoneInfo1.termDirection = location.getBearing();
            myApplication1.myPhoneInfo1.termAltitude = Math.round(location.getAltitude() * 100) / 100.0;
            myApplication1.myPhoneInfo1.termSpeed = Math.round(location.getSpeed() * 100) / 100.0;

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            /*
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Toast.makeText(MainActivity.this, "GPS status change:AVAILABLE "+"provider:"+provider, Toast.LENGTH_SHORT).show();
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(MainActivity.this, "GPS status change:OUT_OF_SERVICE "+"provider:"+provider, Toast.LENGTH_SHORT).show();
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(MainActivity.this, "GPS status change:TEMPORARILY_UNAVAILABLE "+"provider:"+provider, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(MainActivity.this, "GPS status change "+"provider:"+provider, Toast.LENGTH_SHORT).show();
                    break;
            }
            */
        }

        @Override
        public void onProviderEnabled(String provider) {
            //System.out.println("ZGQ:GPS onProviderEnabled "+provider);
            if (mBDLocationClient.isStarted()) {
                mBDLocationClient.stop();
                //System.out.println("ZGQ:mBDLocationClient.stop cause provider "+provider+" enabled");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            //System.out.println("ZGQ:GPS onProviderDisabled "+provider);
            if (!mBDLocationClient.isStarted()) {
                mBDLocationClient.start();
                //System.out.println("ZGQ:mBDLocationClient.start cause "+provider+" disabled");
            }
        }
    };


    class PhoneInfoThread implements Runnable {
        private Context context;

        public PhoneInfoThread(Context context) {
            this.context = context;
            myApplication1.myPhoneInfo1.timecount = 0;
        }

        //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void run() {
            MyDatabaseHelper dbhelper = new MyDatabaseHelper(MainActivity.this, DBNAME1, TABLENAME1, null, 1);
            SQLiteDatabase db1 = dbhelper.getWritableDatabase();
            String[] tmpCellNameArray = new String[2];


            while (flagThreadrun) {
                try {
                    myApplication1.myPhoneInfo1.timecount++;
                    //System.out.println("ZGQ we are here");

                    getCellInfo(db1, tmpCellNameArray);

                    Message message = new Message();
                    message.what = NP_CELL_INFO_UPDATE;

                    //System.out.println("ZGQ: "+CellInfoList.toString());
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceId", myApplication1.myPhoneInfo1.deviceId);
                    message.setData(bundle);
                    mMainHandler.sendMessage(message);
                    Thread.sleep(MyApplication.COLLECT_PEROID);

                } catch (InterruptedException e) {
                    //System.out.println("ZGQ:错误");
                    e.printStackTrace();
                }
            }
            db1.close();
        }


        //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @TargetApi(Build.VERSION_CODES.N)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void getCellInfo(SQLiteDatabase db1, String[] cellNameList) {
            //System.out.println("ZGQ we are here1");
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 9);
                return;
            }
            MyApplication.LogRecord tempLogRecord = myApplication1.new LogRecord();
            //phoneManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            myApplication1.myPhoneInfo1.sysTime = sDateFormat.format(new Date(System.currentTimeMillis()));
            myApplication1.myPhoneInfo1.operaterName = phoneManager.getNetworkOperatorName();
            myApplication1.myPhoneInfo1.operaterId = phoneManager.getNetworkOperator();
            try {
                myApplication1.myPhoneInfo1.mnc = Integer.parseInt(myApplication1.myPhoneInfo1.operaterId.substring(0, 3));
                myApplication1.myPhoneInfo1.mcc = Integer.parseInt(myApplication1.myPhoneInfo1.operaterId.substring(3));
            } catch (Exception e) {
                e.printStackTrace();
                myApplication1.myPhoneInfo1.mnc = 0;
                myApplication1.myPhoneInfo1.mcc = 0;
            }
            myApplication1.myPhoneInfo1.phoneDatastate = phoneManager.getDataState();
            myApplication1.myPhoneInfo1.deviceId = phoneManager.getDeviceId();
            myApplication1.myPhoneInfo1.Imei = phoneManager.getSimSerialNumber();
            myApplication1.myPhoneInfo1.Imsi = phoneManager.getSubscriberId();
            myApplication1.myPhoneInfo1.line1Number = phoneManager.getLine1Number();
            myApplication1.myPhoneInfo1.serialNumber = phoneManager.getSimSerialNumber();
            myApplication1.myPhoneInfo1.deviceSoftwareVersion = Build.VERSION.RELEASE;
            myApplication1.myPhoneInfo1.phoneModel = Build.MODEL;
            myApplication1.myPhoneInfo1.phoneGPSlong = myApplication1.newGpsLong;
            myApplication1.myPhoneInfo1.phoneGPSlat = myApplication1.newGpsLat;
            tempLogRecord.logSysTime = myApplication1.myPhoneInfo1.sysTime;
            tempLogRecord.logGPSlong = myApplication1.myPhoneInfo1.phoneGPSlong;
            tempLogRecord.logGPSlat = myApplication1.myPhoneInfo1.phoneGPSlat;
            //for lte getCellLocation can not be used.
            //System.out.println("ZGQ we are here2");

            myApplication1.cellInfoList.clear();
            try {
                //System.out.println("ZGQ we are here3");
                List<CellInfo> allCellinfo;
                CellLocation myCellLocation;
                //List<NeighboringCellInfo> neighboringCellInfos;

                if (flagMainActStart) {
                    try {
                        allCellinfo = phoneManager.getAllCellInfo();
                        if (allCellinfo.size() <= 0) {
                            flagSigStrFromlistener = true;
                            flagMainActStart = false;
                        } else {
                            flagMainActStart = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        flagSigStrFromlistener = true;
                        flagMainActStart = false;
                    }
                }

                if (flagSigStrFromlistener == true) {

                    if (myApplication1.myPhoneInfo1.phoneModel.equals("SM-G9200") || myApplication1.myPhoneInfo1.phoneModel.equals("SM-G9300") || myApplication1.myPhoneInfo1.phoneModel.equals("SM-G9308") || myApplication1.myPhoneInfo1.phoneModel.contains(("SM-G93"))) {
                        phoneManager.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                    }

                    myCellLocation = phoneManager.getCellLocation();


                    //neighboringCellInfos=phoneManager.getNeighboringCellInfo();
                    CellGeneralInfo newCellInfo_s = new CellGeneralInfo();
                    newCellInfo_s.type = 1;
                    if (myCellLocation instanceof GsmCellLocation){
                        GsmCellLocation gsmCellLocation = (GsmCellLocation) myCellLocation;

                        if (gsmCellLocation != null) {
                            newCellInfo_s.CId = gsmCellLocation.getCid();
                            newCellInfo_s.cellName = searchCellName(newCellInfo_s.CId, db1, tempLogRecord);
                            newCellInfo_s.tac = gsmCellLocation.getLac();
                        } else {
                            newCellInfo_s.CId = 0;
                            newCellInfo_s.cellName = "无法获取CI信息";
                            newCellInfo_s.tac = 0;
                        }
                        newCellInfo_s.pci = tempLogRecord.logPci;
                    }else if(myCellLocation instanceof CdmaCellLocation){
                        CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) myCellLocation;
                        if (cdmaCellLocation != null) {
                            newCellInfo_s.CId = cdmaCellLocation.getBaseStationId();
                            newCellInfo_s.cellName = "不支持CDMA小区";
                            newCellInfo_s.pci = 0;
                            newCellInfo_s.tac = cdmaCellLocation.getNetworkId();
                        } else {
                            newCellInfo_s.CId = 0;
                            newCellInfo_s.cellName = "无法获取CI信息";
                            newCellInfo_s.pci = 0;
                            newCellInfo_s.tac = 0;
                        }
                    }

                    newCellInfo_s.signalStrength = lte_rsrp_listen;
                    newCellInfo_s.rsrq = String.valueOf(lte_rsrq_listen);
                    newCellInfo_s.SINR = lte_rssnr_listen;
                    tempLogRecord.logType = newCellInfo_s.type;
                    tempLogRecord.logCellName = newCellInfo_s.cellName;
                    tempLogRecord.logCId = newCellInfo_s.CId;
                    tempLogRecord.logLac = newCellInfo_s.lac;
                    tempLogRecord.logTac = newCellInfo_s.tac;
                    tempLogRecord.logPsc = newCellInfo_s.psc;
                    //tempLogRecord.logPci = newCellInfo_s.pci;
                    tempLogRecord.logSignalStrength = newCellInfo_s.signalStrength;
                    tempLogRecord.logRsrq = newCellInfo_s.rsrq;
                    tempLogRecord.logSINR = newCellInfo_s.SINR;
                    tempLogRecord.logRatType = myApplication1.myPhoneInfo1.ratType;
                    myApplication1.cellInfoList.add(newCellInfo_s);
                    myApplication1.logCellInfoList.add(tempLogRecord);
                    if (myApplication1.isSavingFile) {
                        myApplication1.savedCellInfoList.add(tempLogRecord);
                    }
                    HistoryServerCellList.add(newCellInfo_s);
                    if (HistoryServerCellList.size() > 100) {
                        HistoryServerCellList.remove(0);
                    }
                    if (myApplication1.logCellInfoList.size() > 300) {
                        myApplication1.logCellInfoList.remove(0);
                    }
                    myApplication1.cellInfoList_map = myApplication1.cellInfoList;

                } else {
                    allCellinfo = phoneManager.getAllCellInfo();
                    //System.out.println("ZGQ we are here5");
                    if (allCellinfo.size() > 0) {

                        myApplication1.myPhoneInfo1.cellcount = allCellinfo.size();
                        for (CellInfo cellInfo : allCellinfo) {   //System.out.println("ZGQ we are here6");
                            CellGeneralInfo newCellInfo = new CellGeneralInfo();
                            newCellInfo.type = 0;
                            if (cellInfo instanceof CellInfoGsm) {
                                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                                try {
                                    if (flagGsmArfcnMethodExist == 0) {
                                        Method method = cellInfoGsm.getCellIdentity().getClass().getMethod("getArfcn");
                                        flagGsmArfcnMethodExist = 1;
                                    }
                                } catch (Exception e) {
                                    flagGsmArfcnMethodExist = 2;
                                }

                                if (cellInfoGsm.isRegistered()) {
                                    newCellInfo.type = 1;
                                    myApplication1.myPhoneInfo1.ratType = "EDGE";
                                }
                                newCellInfo.CId = cellInfoGsm.getCellIdentity().getCid();
                                if (newCellInfo.CId > 65535) {
                                    newCellInfo.CId = 0;
                                }
                                try {
                                    newCellInfo.signalStrength = cellInfoGsm.getCellSignalStrength().getDbm();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                newCellInfo.rsrq = "";
                                //newCellInfo.lac = cellInfoGsm.getCellIdentity().getLac();
                                newCellInfo.tac = cellInfoGsm.getCellIdentity().getLac();
                                if (newCellInfo.tac > 65535) {
                                    newCellInfo.tac = 0;
                                }
                                //newCellInfo.RatType = TelephonyManager.NETWORK_TYPE_EDGE;
                                if (flagGsmArfcnMethodExist == 1) {
                                    newCellInfo.pci = cellInfoGsm.getCellIdentity().getArfcn();
                                } else {
                                    newCellInfo.pci = -1;
                                }

                                if (newCellInfo.type == 1) {
                                    newCellInfo.cellName = searchCellName(newCellInfo.CId, db1, tempLogRecord);
                                    tempLogRecord.logType = newCellInfo.type;
                                    tempLogRecord.logCellName = newCellInfo.cellName;
                                    tempLogRecord.logCId = newCellInfo.CId;
                                    tempLogRecord.logLac = newCellInfo.lac;
                                    tempLogRecord.logTac = newCellInfo.tac;
                                    tempLogRecord.logPsc = newCellInfo.psc;
                                    tempLogRecord.logPci = newCellInfo.pci;
                                    tempLogRecord.logSignalStrength = newCellInfo.signalStrength;
                                    tempLogRecord.logRsrq = newCellInfo.rsrq;
                                    tempLogRecord.logSINR = newCellInfo.SINR;
                                    tempLogRecord.logRatType = myApplication1.myPhoneInfo1.ratType;
                                }

                            } else if (cellInfo instanceof CellInfoWcdma) {
                                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                                if (cellInfoWcdma.isRegistered()) {
                                    newCellInfo.type = 1;
                                    myApplication1.myPhoneInfo1.ratType = "UMTS";
                                }
                                newCellInfo.cellName = "";
                                newCellInfo.CId = cellInfoWcdma.getCellIdentity().getCid();
                                if (newCellInfo.CId > 1000000000) {
                                    newCellInfo.CId = 0;
                                }
                                newCellInfo.psc = cellInfoWcdma.getCellIdentity().getPsc();
                                newCellInfo.pci = newCellInfo.psc;
                                newCellInfo.lac = cellInfoWcdma.getCellIdentity().getLac();
                                newCellInfo.tac = newCellInfo.lac;
                                if (newCellInfo.tac > 1000000000) {
                                    newCellInfo.lac = 0;
                                    newCellInfo.tac = 0;
                                }
                                newCellInfo.signalStrength = cellInfoWcdma.getCellSignalStrength().getDbm();
                                newCellInfo.SINR = cellInfoWcdma.getCellSignalStrength().getAsuLevel();

                                newCellInfo.rsrq = "";
                                if (newCellInfo.type == 1) {
                                    tempLogRecord.logType = newCellInfo.type;
                                    tempLogRecord.logCellName = newCellInfo.cellName;
                                    tempLogRecord.logCId = newCellInfo.CId;
                                    tempLogRecord.logLac = newCellInfo.lac;
                                    tempLogRecord.logTac = newCellInfo.tac;
                                    tempLogRecord.logPsc = newCellInfo.psc;
                                    tempLogRecord.logPci = newCellInfo.pci;
                                    tempLogRecord.logSignalStrength = newCellInfo.signalStrength;
                                    tempLogRecord.logRsrq = newCellInfo.rsrq;
                                    tempLogRecord.logSINR = newCellInfo.SINR;
                                    tempLogRecord.logRatType = myApplication1.myPhoneInfo1.ratType;
                                }


                            } else if (cellInfo instanceof CellInfoLte) {
                                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                                try {
                                    if (flagEarfcnMethodExist == 0) {
                                        Method method = cellInfoLte.getCellIdentity().getClass().getMethod("getEarfcn");
                                        flagEarfcnMethodExist = 1;
                                    }
                                } catch (Exception e) {
                                    flagEarfcnMethodExist = 2;
                                }

                                if (cellInfoLte.isRegistered()) {
                                    newCellInfo.type = 1;
                                }
                                if (newCellInfo.type == 1) {
                                    newCellInfo.CId = cellInfoLte.getCellIdentity().getCi();
                                    newCellInfo.cellName = searchCellName(newCellInfo.CId, db1, tempLogRecord);
                                    newCellInfo.tac = cellInfoLte.getCellIdentity().getTac();
                                    newCellInfo.pci = cellInfoLte.getCellIdentity().getPci();
                                    if (flagEarfcnMethodExist == 1) {
                                        newCellInfo.ERFCN = cellInfoLte.getCellIdentity().getEarfcn();
                                    } else if (flagEarfcnMethodExist == 2) {
                                        newCellInfo.ERFCN = -1;
                                    }
                                    //newCellInfo.signalStrength = 0 - cellInfoLte.getCellSignalStrength().getDbm() / 10;
                                    //String rsrqstr = cellInfoLte.getCellSignalStrength().toString();
                                    //System.out.println("ZGQ:"+rsrqstr);
                                    //newCellInfo.rsrq = String.valueOf(0 - Integer.parseInt(rsrqstr.substring(rsrqstr.indexOf("rsrq=") + 5, rsrqstr.indexOf("rssnr=") - 1)) / 10);
                                    newCellInfo.rsrq = String.valueOf(lte_rsrq_listen);
                                    newCellInfo.SINR = lte_rssnr_listen;
                                    newCellInfo.signalStrength = lte_rsrp_listen;
                                    tempLogRecord.logType = newCellInfo.type;
                                    tempLogRecord.logCellName = newCellInfo.cellName;
                                    tempLogRecord.logCId = newCellInfo.CId;
                                    tempLogRecord.logLac = newCellInfo.lac;
                                    tempLogRecord.logTac = newCellInfo.tac;
                                    tempLogRecord.logPsc = newCellInfo.psc;
                                    tempLogRecord.logPci = newCellInfo.pci;
                                    tempLogRecord.logSignalStrength = newCellInfo.signalStrength;
                                    tempLogRecord.logRsrq = newCellInfo.rsrq;
                                    tempLogRecord.logSINR = newCellInfo.SINR;
                                    tempLogRecord.logRatType = myApplication1.myPhoneInfo1.ratType;
                                    myApplication1.myPhoneInfo1.ratType = "LTE";

                                } else {

                                    if (flagEarfcnMethodExist == 1) {
                                        newCellInfo.CId = cellInfoLte.getCellIdentity().getEarfcn();
                                    } else {
                                        newCellInfo.CId = -1;
                                    }

                                    //newCellInfo.CId = cellInfoLte.getCellIdentity().getEarfcn();
                                    //newCellInfo.CId = 0;
                                    //newCellInfo.tac = 0;
                                    newCellInfo.pci = cellInfoLte.getCellIdentity().getPci();
                                    newCellInfo.cellName = searchNbCellName(tempLogRecord.logCId, newCellInfo.CId, newCellInfo.pci, myApplication1.newGpsLong, myApplication1.newGpsLat, db1);

                                    newCellInfo.signalStrength = cellInfoLte.getCellSignalStrength().getDbm();
                                    if (newCellInfo.signalStrength > 0) {
                                        newCellInfo.signalStrength = 0 - cellInfoLte.getCellSignalStrength().getDbm() / 10;
                                    }
                                    String rsrqstr = cellInfoLte.getCellSignalStrength().toString();
                                    //System.out.println("ZGQ:"+rsrqstr);
                                    try {
                                        newCellInfo.rsrq = String.valueOf(0 - Integer.parseInt(rsrqstr.substring(rsrqstr.indexOf("rsrq=") + 5, rsrqstr.indexOf("rssnr=") - 1)) / 10);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        newCellInfo.rsrq = "0";
                                    }
                                    //newCellInfo.SINR=0;

                                }

                            }
                            myApplication1.cellInfoList.add(newCellInfo);
                            if (newCellInfo.type == 1) {
                                myApplication1.logCellInfoList.add(tempLogRecord);
                                if (myApplication1.isSavingFile) {
                                    myApplication1.savedCellInfoList.add(tempLogRecord);
                                }
                                HistoryServerCellList.add(newCellInfo);
                                //delete first one if more than 5
                                if (HistoryServerCellList.size() > 100) {
                                    HistoryServerCellList.remove(0);
                                }
                                if (myApplication1.logCellInfoList.size() > 300) {
                                    myApplication1.logCellInfoList.remove(0);
                                }
                            }
                        }
                        myApplication1.cellInfoList_map = myApplication1.cellInfoList;
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
                //for older devices
                //System.out.println("ZGQ we are here4");
                /*
                GsmCellLocation location = (GsmCellLocation) phoneManager.getCellLocation();
                CellGeneralInfo newCellInfo = new CellGeneralInfo();
                newCellInfo.type = 1;
                newCellInfo.CId = location.getCid();
                newCellInfo.tac = location.getLac();
                newCellInfo.psc = location.getPsc();
                */

            }
        }

    }

    private int lte_rsrp_listen;
    private int lte_rsrq_listen;
    private int lte_rssnr_listen;
    private int gsm_siglev_listen;

    private class MyPhoneStateListener extends PhoneStateListener {
        //监听器类
        /*得到信号的强度由每个tiome供应商,有更新*/
        //TextView myText = (TextView)findViewById(R.id.myText);
        //TextView myText1=(TextView)findViewById(R.id.myText1);
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);//调用超类的该方法，在网络信号变化时得到回答信号

            System.out.println("ZGQ:onSignalStrengthsChanged:" + signalStrength);
            signalStrengthList.add(signalStrength);
            try {
                //int lte_sinr = (Integer) signalStrength.getClass().getMethod("getLteSignalStrength").invoke(signalStrength);
                int nettype = 0;
                lte_rsrp_listen = (Integer) signalStrength.getClass().getMethod("getLteRsrp").invoke(signalStrength);
                nettype = 1;
                if (lte_rsrp_listen >= -1) {
                    lte_rsrp_listen = (Integer) signalStrength.getClass().getMethod("getGsmSignalStrength").invoke(signalStrength);
                    nettype = 2;
                }
                if (lte_rsrp_listen >= -1) {
                    lte_rsrp_listen = (Integer) signalStrength.getClass().getMethod("getTdScdmaDbm").invoke(signalStrength);
                    nettype = 3;
                }
                if (lte_rsrp_listen >= -1) {
                    lte_rsrp_listen = (Integer) signalStrength.getClass().getMethod("getCdmaDbm").invoke(signalStrength);
                    nettype = 4;
                }
                if (lte_rsrp_listen >= -1) {
                    String[] tempstr;
                    tempstr = signalStrength.toString().split(" ");
                    lte_rsrp_listen = Integer.parseInt(tempstr[3]);
                    nettype = 1;
                }
                if (nettype == 1) {
                    myApplication1.myPhoneInfo1.ratType = "LTE";
                } else if (nettype == 2) {
                    myApplication1.myPhoneInfo1.ratType = "EDGE";
                } else if (nettype == 3) {
                    myApplication1.myPhoneInfo1.ratType = "TDSCDMA";
                } else if (nettype == 4) {
                    myApplication1.myPhoneInfo1.ratType = "CDMA";
                } else {
                    myApplication1.myPhoneInfo1.ratType = "LTE";
                }

                lte_rsrq_listen = (Integer) signalStrength.getClass().getMethod("getLteRsrq").invoke(signalStrength);
                lte_rssnr_listen = (Integer) signalStrength.getClass().getMethod("getLteRssnr").invoke(signalStrength) / listenSINR_coef;
                if (lte_rssnr_listen > 80) {
                    listenSINR_coef = 10;
                    lte_rssnr_listen = (Integer) signalStrength.getClass().getMethod("getLteRssnr").invoke(signalStrength) / listenSINR_coef;
                }
                //int lte_cqi = (Integer) signalStrength.getClass().getMethod("getLteCqi").invoke(signalStrength);


            } catch (Exception e) {

                // TODO Auto-generated catch block

                e.printStackTrace();
                return;
            }

            if (signalStrengthList.size() >= 3) {
                signalStrengthList.remove(0);
            }

            //Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = "+ String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();//cinr：Carrier to Interference plus Noise Ratio（载波与干扰和噪声比）
            //myText.setText("CDMA RSSI = "+ String.valueOf(signalStrength.getCdmaDbm()));
            //myText1.setText("GSM Cinr = "+ String.valueOf(signalStrength.getGsmSignalStrength()));
        }
    }

    private String searchCellName(int tpCId, SQLiteDatabase tpdb, MyApplication.LogRecord tpLogRecord) {
        nCellNameCachClearTimer = nCellNameCachClearTimer - 1;
        if (nCellNameCachClearTimer < 0) {
            nCellNameCachClearTimer = 60000 / myApplication1.COLLECT_PEROID;
        }
        String tpcellName = "";
        tpLogRecord.logPci = 0;
        myApplication1.iter_CellName = myApplication1.hash_cellName.keySet().iterator();
        while (myApplication1.iter_CellName.hasNext()) {
            int key = (Integer) myApplication1.iter_CellName.next();
            if (key == tpCId) {
                tpcellName = myApplication1.hash_cellName.get(key).hsValue_cellName;
                tpLogRecord.secMidGPSlong = myApplication1.hash_cellName.get(key).hsValue_dirLong;
                tpLogRecord.secMidGPSlat = myApplication1.hash_cellName.get(key).hsValue_dirLat;
                tpLogRecord.logPci = myApplication1.hash_cellName.get(key).hsValue_PCI;
                return tpcellName;
            }
        }

        if (tpdb == null) {
            tpcellName = "请导入基站信息表";
        } else {
            String queryStr;
            if (myApplication1.myPhoneInfo1.ratType.equals("EDGE")) {
                queryStr = "Select 小区名称,方位角,经度,纬度,使用频段,CELLID,bcch from " + TABLENAME_GSM + " where CELLID='" + String.valueOf(tpCId + "'");
            } else {
                queryStr = "Select 小区名,方位角,经度,纬度,频段,CELLID,PCI from " + TABLENAME1 + " where CELLID='" + String.valueOf(tpCId + "'");
            }
            Cursor cursor;
            try {
                if (!myApplication1.flagCellInfoLteExist) {
                    return tpcellName = "请导入基站信息表";
                }
                cursor = tpdb.rawQuery(queryStr, null);

                if (cursor.moveToNext()) {
                    // System.out.println("ZGQ:" + cursor.getString(0));
                    tpcellName = cursor.getString(0);
                    tpLogRecord.logPci = cursor.getInt(6);

                    if (cursor.getString(4).equals("FDD") || cursor.getString(4).equals("FDD频段") || cursor.getString(4).equals("FDD-1800")) {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * 6.7 / 6 * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * 6.7 / 6 * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    } else if (cursor.getString(4).equals("FDD-900")) {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * 7.5 / 6 * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * 7.5 / 6 * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    } else if (cursor.getString(4).equals("FDD-NB")) {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * 7.5 / 6 * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * 7.5 / 6 * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    } else if (cursor.getString(4).equals("F1") || cursor.getString(4).equals("F2") || cursor.getString(4).equals("F频段") || cursor.getString(4).equals("TDD-F")) {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    } else if (cursor.getString(4).equals("D1") || cursor.getString(4).equals("D2") || cursor.getString(4).equals("D3") || cursor.getString(4).equals("D频段") || cursor.getString(4).equals("TDD-D")) {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * 3 / 4 * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * 3 / 4 * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    } else if (cursor.getString(4).equals("E1") || cursor.getString(4).equals("E2") || cursor.getString(4).equals("E3") || cursor.getString(4).equals("E频段") || cursor.getString(4).equals("TDD-E")) {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * 1 / 2 * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * 1 / 2 * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    } else if (cursor.getString(4).contains("1800") && cursor.getDouble(1) > 0) {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * 8 / 6 * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * 8 / 6 * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    } else if (cursor.getString(4).contains("900") && cursor.getDouble(1) > 0) {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * 7 / 6 * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * 7 / 6 * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    } else if (cursor.getString(4).contains("1800") && cursor.getDouble(1) <= 0) {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * 4 / 6 * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * 4 / 6 * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    } else if (cursor.getString(4).contains("900") && cursor.getDouble(1) <= 0) {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * 3.5 / 6 * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * 3.5 / 6 * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    } else {
                        tpLogRecord.secMidGPSlong = cursor.getDouble(2) + MyApplication.SECTOR_R * 1 / 2 * Math.sin(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                        tpLogRecord.secMidGPSlat = cursor.getDouble(3) + MyApplication.SECTOR_R * 1 / 2 * Math.cos(cursor.getDouble(1) / 360 * 2 * Math.PI) / 100000;
                    }
                    MyApplication.HashValueCellName hsobj = myApplication1.new HashValueCellName();
                    hsobj.hsValue_cellName = tpcellName;
                    hsobj.hsValue_dirLong = tpLogRecord.secMidGPSlong;
                    hsobj.hsValue_dirLat = tpLogRecord.secMidGPSlat;
                    hsobj.hsValue_PCI = tpLogRecord.logPci;
                    myApplication1.hash_cellName.put(cursor.getInt(5), hsobj);

                } else {
                    tpcellName = "未知小区";
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ZGQ:基站信息表字段有误，请检查：小区名、方位角、经度、纬度 字段是否命名正确!");
                tpcellName = "请导入基站信息表";
                myApplication1.flagCellInfoLteExist = false;
            }
        }
        return tpcellName;
    }

    private double calcDisForTowGpsPoint(double lon1, double lat1, double lon2, double lat2) {
        double dis = 0.0;
        dis = Math.pow((lon1 - lon2) * 100000 * (lon1 - lon2) * 100000 + (lat1 - lat2) * 100000 * (lat1 - lat2) * 100000, 0.5);
        return dis;
    }

    private String ERFCNToBand(int ERFCN) {
        int band = -1;
        String qstr = "";
        if (ERFCN >= 38250 && ERFCN <= 38649) {
            band = 39;
            qstr = " AND (频段='F1' OR 频段='F2' OR 频段='F频段' OR 频段='TDD-F')";
        } else if (ERFCN >= 37750 && ERFCN <= 38249) {
            band = 38;
            qstr = " AND (频段='D1' OR 频段='D2' OR 频段='D3' OR 频段='D频段' OR 频段='TDD-D')";
        } else if (ERFCN >= 38650 && ERFCN <= 39649) {
            band = 40;
            qstr = " AND (频段='E1' OR 频段='E2' OR 频段='E3' OR 频段='E频段' OR 频段='TDD-E')";
        } else if (ERFCN >= 39650 && ERFCN <= 41589) {
            band = 41;
            qstr = " AND (频段='D3' OR 频段='D频段' OR 频段='TDD-D')";
        } else if (ERFCN >= 36200 && ERFCN <= 36349) {
            band = 34;
            qstr = " AND (频段='A' OR 频段='TDD-A')";
        } else if (ERFCN >= 1200 && ERFCN <= 1949) {
            band = 3;                 //移动FDD1800
            qstr = " AND (频段='FDD' OR 频段='FDD频段' OR 频段='FDD-1800')";
        } else if (ERFCN >= 3450 && ERFCN <= 3799) {
            band = 8;                //移动FDD900
            qstr = " AND 频段='FDD-900'";
        }
        return qstr;
    }

    private int nCellNameCachClearTimer = 60000 / myApplication1.COLLECT_PEROID;

    private String searchNbCellName(int sCellCi, int nbErfcn, int nbPci, double sCellLon, double sCellLat, SQLiteDatabase tpdb) {
        String tpNCellName = "未知小区";
        if (nCellNameCachClearTimer == 0) {
            myApplication1.hash_NCellName.clear();
        } else {
            Iterator iter_NCellName = myApplication1.hash_NCellName.keySet().iterator();
            while (iter_NCellName.hasNext()) {
                String key = iter_NCellName.next().toString();
                if (key.equals(nbErfcn + "_" + nbPci)) {
                    tpNCellName = myApplication1.hash_NCellName.get(key);
                    return tpNCellName;
                }
            }
        }

        if (tpdb == null) {
            tpNCellName = "请导入基站信息表";
        } else {
            String queryStr = "";
            String queryStr1 = "";
            if (myApplication1.myPhoneInfo1.ratType.equals("EDGE")) {
                //queryStr = "Select 小区名称,方位角,经度,纬度,使用频段,CELLID,bcch from " + TABLENAME_GSM + " where CELLID='" + String.valueOf(tpCId + "'");
                return "";
            } else if (myApplication1.myPhoneInfo1.ratType.equals("LTE")) {
                queryStr = "Select 小区名,方位角,经度,纬度,频段,CELLID,PCI from " + TABLENAME1 + " where 频点='" + nbErfcn + "'" + " and PCI=" + nbPci + " and CELLID!=" + sCellCi;
            } else {
                return "";
            }
            Cursor cursor;
            try {
                if (!myApplication1.flagCellInfoLteExist) {
                    return tpNCellName = "请导入基站信息表";
                }
                double tmpDis = 10000.0;
                String tmp_tpNCellName = "";
                cursor = tpdb.rawQuery(queryStr, null);
                if (cursor.getCount() == 0) {
                    String str_ERFCN = ERFCNToBand(nbErfcn);
                    queryStr1 = "Select 小区名,方位角,经度,纬度,频段,CELLID,PCI from " + TABLENAME1 + " where PCI=" + nbPci + " and CELLID!=" + sCellCi + str_ERFCN;
                    cursor = tpdb.rawQuery(queryStr1, null);
                    while (cursor.moveToNext()) {
                        double nlon = cursor.getDouble(2);
                        double nlat = cursor.getDouble(3);
                        double dis = calcDisForTowGpsPoint(sCellLon, sCellLat, nlon, nlat);
                        if (tmpDis > dis) {
                            tmpDis = dis;
                            tmp_tpNCellName = cursor.getString(0);
                        }
                    }
                    if (tmpDis < 10000.0) {
                        tpNCellName = tmp_tpNCellName;
                        myApplication1.hash_NCellName.put(nbErfcn + "_" + nbPci, tpNCellName);
                    }
                    cursor.close();

                } else {

                    while (cursor.moveToNext()) {
                        double nlon = cursor.getDouble(2);
                        double nlat = cursor.getDouble(3);
                        double dis = calcDisForTowGpsPoint(sCellLon, sCellLat, nlon, nlat);
                        if (dis < 2000) {
                            tpNCellName = cursor.getString(0);
                            myApplication1.hash_NCellName.put(nbErfcn + "_" + nbPci, tpNCellName);
                            cursor.close();
                            return tpNCellName;
                        } else {
                            if (tmpDis > dis) {
                                tmpDis = dis;
                                tmp_tpNCellName = cursor.getString(0);
                            }
                        }
                    }
                    if (tmpDis < 10000.0) {
                        tpNCellName = tmp_tpNCellName;
                        myApplication1.hash_NCellName.put(nbErfcn + "_" + nbPci, tpNCellName);
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                tpNCellName = "未知小区EX";
            }
        }
        return tpNCellName;
    }

    class AppCheckVersionUpdate implements Runnable {
        @Override
        public void run() {
            try {
                //获取欢迎页面图片
                int randomnum = (int) (Math.random() * 22);
                drawable_welcome = LoadImageFromWebOperations("http://120.199.120.85:50080/wzgjgl_mobile/UploadFiles_201701/cellquery/top_welcome"+randomnum+".jpg");
                Message msg15 = new Message();
                msg15.what = 15;
                mMainHandler.sendMessage(msg15);

                Thread.sleep(1000);
                if (getServerVer()) {
                    int vercode = getVerCode(MainActivity.this); // 用到前面第一节写的方法
                    if (newVerCode > vercode) {
                        // 更新新版本
                        Message msg11 = new Message();
                        msg11.what = 11;
                        mMainHandler.sendMessage(msg11);

                    } else {
                        Message msg12 = new Message();
                        msg12.what = 12;
                        mMainHandler.sendMessage(msg12);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private boolean getServerVer() {
        try {
            String verjson = readUrlFileParseStringNew("http://120.199.120.85:50080/wzgjgl_mobile/UploadFiles_201701/cellquery/ver.json");
            JSONArray array = new JSONArray(verjson);
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                try {
                    newVerCode = Integer.parseInt(obj.getString("verCode"));
                    newVerName = obj.getString("verName");
                    newVerInfo = obj.getString("verInfo");
                } catch (Exception e) {
                    newVerCode = -1;
                    newVerName = "";
                    newVerInfo = "";
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo("com.example.administrator.phoneinfo", 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo("com.example.administrator.phoneinfo", 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 从指定的URL中获取数组
     * @param urlPath
     * @return
     * @throws Exception
     */
    public static String readUrlFileParseString(String urlPath) throws Exception {
        //不支持中文
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inStream = conn.getInputStream();
        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);
        }
        inStream.close();
        return new String(outStream.toByteArray());//通过out.Stream.toByteArray获取到写的数据
    }

    public static String readUrlFileParseStringNew(String urlPath) throws Exception {
        //支持中文
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("contentType", "GBK");
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(inStream, "GBK"));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
            buffer.append("\r\n");
        }
        String str = buffer.toString();
        return str;
    }

    /**
     * 卫星状态监听器
     */
    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号

    private GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数
            try {
                LocationManager locationManager_m = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
                    return;
                }
                GpsStatus status = locationManager_m.getGpsStatus(null); //取当前状态
                String satelliteInfo = updateGpsStatus(event, status);
                myApplication1.numsGpsSatllite=Integer.parseInt(satelliteInfo);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private String updateGpsStatus(int event, GpsStatus status) {
        String sb2 = "0";
        if (status == null) {
            sb2="0";  //卫星个数
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            int maxSatellites = status.getMaxSatellites();
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            numSatelliteList.clear();
            int count = 0;
            while (it.hasNext() && count <= maxSatellites) {
                GpsSatellite s = it.next();
                numSatelliteList.add(s);
                count++;
            }
            sb2=String.valueOf(numSatelliteList.size());
        }
        return sb2;
    }

    private Drawable LoadImageFromWebOperations(String url)
    {
        try
        {
            InputStream is = (InputStream) new URL(url).getContent();
            //Drawable d = Drawable.createFromStream(is, "src name");
            Drawable d = Drawable.createFromResourceStream(this.getResources(), null, is, "src", null);
            return d;
        }catch (Exception e) {
            System.out.println("Exc="+e);
            return null;
        }
    }
    Drawable drawable_welcome=null;
    private void showHelpDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.show_help_dialog, null);

        //ImageView image = null;
        //image.setImageDrawable(drawable);

        ImageView imageView=(ImageView)textEntryView.findViewById(R.id.dialog_wellcome_image);
        //imageView.setImageDrawable(drawable);
        imageView.setImageDrawable(drawable_welcome);

        AlertDialog.Builder ad1 = new AlertDialog.Builder(MainActivity.this);
        ad1.setCancelable(false);
        ad1.setTitle("欢迎查看网优云图帮助菜单");
        ad1.setView(textEntryView);
        //ad1.setView(image);
        ad1.setPositiveButton("去看帮助", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                Intent intent_help = new Intent(MainActivity.this, HelpWebViewActivity.class);
                startActivity(intent_help);
                /*
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
                */
            }
        });
        ad1.setNegativeButton("取消", null);
        ad1.show();// 显示对话框
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isGpsSwitchOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 直接分享图片到微信好友
     */

    private void shareToWxFriend(String imgPath, String shareTitle, String shareContent) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(componentName);
        intent.setAction(Intent.ACTION_SEND);
        if ((imgPath == null) || (imgPath.equals(""))) {
            intent.setType("text/*");
        } else {
            File f = new File(imgPath);
            if ((f != null) && (f.exists()) && (f.isFile())) {
                intent.setType("image/*");
                intent.putExtra("Kdescription", shareContent);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        this.startActivity(Intent.createChooser(intent, "分享图片"));
    }


}


