package com.example.administrator.phoneinfo;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;
//实现传感器事件监听：SensorEventListener
public class OritationActivity extends AppCompatActivity implements SensorEventListener{

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
    TextView show_change=null;
    private  int oritationCounts=0;
    private int phoneDirection=0;
    private int phoneDowntilt=0;
    private int phoneRotation=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oritation);
        try {
            show_change = (TextView) findViewById(R.id.tv_oritation_showchange);
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
                StringBuffer buff = new StringBuffer();
                int i = 0;
                for (float value : values) {
                    value = (float) Math.toDegrees(value);
                    int s = Math.round(value);
                    //buff.append(value+"  ");
                    buff.append(s + "  ");
                    if (i == 0) {
                        phoneDirection = (360 + s) % 360;
                    } else if (i == 1) {
                        phoneDowntilt = (90 + s) % 180;
                    } else if (i == 2) {
                        phoneRotation = s;
                    }
                    i = i + 1;
                }
                buff.append("  方向角：" + phoneDirection + "  倾角：" + phoneDowntilt + "  旋转角：" + phoneRotation);
                show_change.setText(buff.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    


}

