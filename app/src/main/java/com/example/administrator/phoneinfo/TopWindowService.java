package com.example.administrator.phoneinfo;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.formula.functions.T;

import static java.security.AccessController.getContext;

public class TopWindowService extends Service implements SensorEventListener {

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    LinearLayout mFloatLayoutInfo;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    ImageButton mFloatView;
    ImageButton mFloatViewHide;

    private static final String TAG = "TopWindowService";

    private SensorManager sensorManager;
    private Sensor acc_sensor;
    private Sensor mag_sensor;
    //加速度传感器数据
    float accValues[]=new float[3];
    //地磁传感器数据
    float magValues[]=new float[3];
    //旋转矩阵，用来保存磁场和加速度的数据
    float r[]=new float[9];
    //模拟方向传感器的数据（原始数据为弧度）
    float values[]=new float[3];

    private  int oritationCounts=0;
    //private int phoneDirection=0;
    //private int phoneDowntilt=0;
    //private int phoneRotation=0;

    private TextView textViewTopWinTime;
    private TextView textViewTopWinSat;
    private TextView textViewTopWinLon;
    private TextView textViewTopWinLat;
    private TextView textViewTopWinAlti;
    private TextView textViewTopWinDir;
    private TextView textViewTopWinTilt;
    private TextView textViewTopWinRot;
    private TextView textViewTopWinAddress;
    private MyApplication myApplication;

    Boolean flagInfoWindowOn=true;



    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "oncreat");
        myApplication = (MyApplication) getApplicationContext();
        createFloatView();

        try {

            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            //给传感器注册监听：
            sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, mag_sensor, SensorManager.SENSOR_DELAY_GAME);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private void createFloatView()
    {

        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            wmParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;;
        }else{
            wmParams.type = LayoutParams.TYPE_PHONE;
        }
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        //wmParams.y = 0;
        wmParams.y = myApplication.screenHeigh*5/9;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		 /*// 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;*/

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.topwindow_layout, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        mFloatLayoutInfo=(LinearLayout)mFloatLayout.findViewById(R.id.linearlayout_topwindow_info);
        mFloatView = (ImageButton)mFloatLayout.findViewById(R.id.button_topwindow_onoff);
        mFloatViewHide = (ImageButton)mFloatLayout.findViewById(R.id.button_topwindow_hide);

        textViewTopWinTime = (TextView)mFloatLayout.findViewById(R.id.tv_topwindow_datetime);
        textViewTopWinSat = (TextView)mFloatLayout.findViewById(R.id.tv_topwindow_satllite);
        textViewTopWinLon = (TextView)mFloatLayout.findViewById(R.id.tv_topwindow_longitude);
        textViewTopWinLat = (TextView)mFloatLayout.findViewById(R.id.tv_topwindow_latitude);
        textViewTopWinAlti = (TextView)mFloatLayout.findViewById(R.id.tv_topwindow_altitude);
        textViewTopWinDir = (TextView)mFloatLayout.findViewById(R.id.tv_topwindow_direction);
        textViewTopWinTilt = (TextView)mFloatLayout.findViewById(R.id.tv_topwindow_tilt);
        textViewTopWinRot = (TextView)mFloatLayout.findViewById(R.id.tv_topwindow_rotation);
        textViewTopWinAddress = (TextView)mFloatLayout.findViewById(R.id.tv_topwindow_address);


        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth()/2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight()/2);
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                // TODO Auto-generated method stub
                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth()/2;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "getMeasuredWidth:" + mFloatView.getMeasuredWidth()/2);
                //Log.i(TAG, "X" + event.getX());
                //减25为状态栏的高度
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight()/2 - 25;
                //wmParams.y = (int) event.getRawY() + mFloatView.getMeasuredHeight()/2 + 250;

                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "getMeasuredHeight:" + mFloatView.getMeasuredHeight()/2);
                //Log.i(TAG, "Y" + event.getY());
                //刷新
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

        mFloatView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                //Toast.makeText(TopWindowService.this, "onClick", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });

        mFloatViewHide.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                //Toast.makeText(TopWindowService.this, "onClick", Toast.LENGTH_SHORT).show();
                if(flagInfoWindowOn){
                    mFloatLayoutInfo.setVisibility(View.GONE);
                    flagInfoWindowOn=false;
                }else{
                    mFloatLayoutInfo.setVisibility(View.VISIBLE);
                    flagInfoWindowOn=true;
                }

            }
        });
    }


    //传感器状态改变时的回调方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (oritationCounts < 50) {
                oritationCounts = oritationCounts + 1;
            } else {
                oritationCounts = 0;
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accValues = event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
                } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    magValues = event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
                }
                /**public static boolean getRotationMatrix (float[] R, float[] I, float[] gravity, float[] geomagnetic)
                 * 填充旋转数组r
                 * r：要填充的旋转数组
                 * I:将磁场数据转换进实际的重力坐标中 一般默认情况下可以设置为null
                 * gravity:加速度传感器数据
                 * geomagnetic：地磁传感器数据
                 */
                SensorManager.getRotationMatrix(r, null, accValues, magValues);
                /**
                 * public static float[] getOrientation (float[] R, float[] values)
                 * R：旋转数组
                 * values ：模拟方向传感器的数据
                 */

                SensorManager.getOrientation(r, values);


                //将弧度转化为角度后输出
                //StringBuffer buff = new StringBuffer();
                int i = 0;
                for (float value : values) {
                    value = (float) Math.toDegrees(value);
                    int s = Math.round(value);
                    //buff.append(value+"  ");
                    //buff.append(s + "  ");
                    if (i == 0) {
                        myApplication.myPhoneInfo1.phoneDirection = (360 + s) % 360;
                    } else if (i == 1) {
                        myApplication.myPhoneInfo1.phoneDowntilt = (90 + s) % 180;
                    } else if (i == 2) {
                        myApplication.myPhoneInfo1.phoneRotation = s;
                    }
                    i = i + 1;
                }
                //buff.append("  方向角：" + phoneDirection + "  倾角：" + phoneDowntilt + "  旋转角：" + phoneRotation);
                //show_change.setText(buff.toString());
                textViewTopWinTime.setText(myApplication.myPhoneInfo1.sysTime);
                textViewTopWinSat.setText(String.valueOf(myApplication.numsGpsSatllite));
                textViewTopWinLon.setText(String.valueOf(myApplication.newGpsLong));
                textViewTopWinLat.setText(String.valueOf(myApplication.newGpsLat));
                textViewTopWinAlti.setText(String.valueOf(myApplication.myPhoneInfo1.termAltitude));
                textViewTopWinDir.setText(String.valueOf(myApplication.myPhoneInfo1.phoneDirection));
                myApplication.myPhoneInfo1.termDirection=myApplication.myPhoneInfo1.phoneDirection;
                textViewTopWinTilt.setText(String.valueOf(myApplication.myPhoneInfo1.phoneDowntilt));
                //textViewTopWinRot.setText(String.valueOf(myApplication.myPhoneInfo1.phoneRotation));
                textViewTopWinAddress.setText(myApplication.myPhoneInfo1.termAddress);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mFloatLayout != null)
        {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
        myApplication.flagTopWindowShow=false;
    }

}
