package com.example.administrator.phoneinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 启动自定义水印相机
 *
 * Created by tanksu on 16/6/28.
 */
public class WaterCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Context mContext;
    private SurfaceView mSurfaceView;
    private ImageButton imgvBtn_takePic, imgvBtn_switchFlash, imgvBtn_switchCamera;
    private Button btn_back;
    private TextView tv_time, tv_username, tv_address, tv_date, tv_operation;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private String curDate = "", curTime = "", curAddress = "", userName = "", userOperation = "";
    private final int REQUEST_CODE = 1001;
    private Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    private int mCameraId;
    private long currentTimeMillis = 0;
    private Intent waterIntent;
    private MyApplication myApplication;

    /**
     * 这是点击surfaceview聚焦所调用的方法
     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //success = true，聚焦成功，否则聚焦失败
            //在这里我们可以在点击相机后是否聚焦成功，然后做我们的一些操作，这里我就省略了，大家自行根据需要添加
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_camera);
        initViews();
        initData();
        initListener();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        myApplication = (MyApplication) this.getApplicationContext();

        mSurfaceView = (SurfaceView) findViewById(R.id.sfv_camera_camera);
        imgvBtn_takePic = (ImageButton) findViewById(R.id.btn_camera_takePic);
        tv_time = (TextView) findViewById(R.id.tv_camera_time);
        tv_username = (TextView) findViewById(R.id.tv_camera_username);
        tv_address = (TextView) findViewById(R.id.tv_camera_address);
        tv_date = (TextView) findViewById(R.id.tv_camera_date);
        tv_operation = (TextView) findViewById(R.id.tv_camera_operation);
        imgvBtn_switchFlash = (ImageButton) findViewById(R.id.imgvBtn_camera_switchFlash);
        imgvBtn_switchFlash.setImageResource(R.drawable.cameraflash_auto_72_zgppic);
        imgvBtn_switchCamera = (ImageButton) findViewById(R.id.imgvBtn_camera_switchCamera);
        imgvBtn_switchCamera.setImageResource(R.drawable.camera_change_72_zgppic);
        btn_back = (Button) findViewById(R.id.imgvBtn_camera_back);
        mContext = this;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        try{
        mSurfaceView.setFocusable(true);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.addCallback(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd"); //获取当前时间，作为图片的命名，再转换为常用时间格式
        currentTimeMillis = System.currentTimeMillis();
        curDate = formatter.format(currentTimeMillis);
        tv_date.setText(curDate);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault()); //获取24小时制的时间
        curTime = format.format(currentTimeMillis);
        tv_time.setText(curTime);
        /*
        Intent intent = getIntent(); //我写的这个类，是要用startActivityForResult来启动的，传入的参数可以根据自己需求来定，我这里传过来的信息有
        //地址CUR_ADDRESS，用户名USER_NAME，用户操作USER_OPERATION，然后把信息设置到空间里面去，同时还要保存intent。
        //而时间和日期，则是在本类中自己获取，同样设置入控件里面去
        if (intent != null) {
            waterIntent = intent;
            curAddress = intent.getStringExtra(StaticParam.CUR_ADDRESS);
            userName = intent.getStringExtra(StaticParam.USER_NAME);
            userOperation = intent.getStringExtra(StaticParam.USER_OPERATION);
            tv_operation.setText(userOperation);
            tv_address.setText(curAddress);
            tv_username.setText(userName);
        }else {
            toast("intent equals null,please try again!");
        }
        */
        waterIntent = new Intent();
        curAddress = myApplication.newGpsLong +" "+myApplication.newGpsLat;
        userName = myApplication.USER_AUTH_INFO.userName;
        //userOperation = intent.getStringExtra(StaticParam.USER_OPERATION);
        tv_operation.setText("拍照");
        tv_address.setText(curAddress);
        tv_username.setText(userName);

        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        //这个方法是点击拍照的方法
        imgvBtn_takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, new PicCallBacKImpl(WaterCameraActivity.this));
            }
        });
        //设置闪光灯的模式，有禁止，自动和打开闪光灯三种模式
        imgvBtn_switchFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CameraUtil.setFlashMode(mCamera, imgvBtn_switchFlash);
            }
        });
        //这个是切换前后摄像头的操作，因为时间关系没有做
        imgvBtn_switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        //取消按钮，finish本页面
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaterCameraActivity.this.finish();
            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCamera.autoFocus(autoFocusCallback); //设置相机为自动对焦模式，就不用认为去点击了
                return false;
            }
        });

    }


    /**
     * 我们在此周期方法里面打开摄像头
     */
    @Override
    protected void onStart() {
        if (this.checkCameraHardware(this) && (mCamera == null)) {
            openCamera();//打开后置摄像头
        }
        super.onStart();
    }

    /**
     * 拍照回调类
     */
    class PicCallBacKImpl implements Camera.PictureCallback {
        private Activity mActivity;

        public PicCallBacKImpl(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            //bitmap = ImageUtil.matrixImageView(bitmap, 90);
            String path = ViewPhotoActivity.saveBitmap(null, String.valueOf(currentTimeMillis), bitmap);
            if (path != null && path.length() > 0) {
                waterIntent.setClass(mActivity, ViewPhotoActivity.class);
                waterIntent.putExtra("waterphoto_PIC_PATH", path);
                waterIntent.putExtra("waterphoto_CUR_DATE", curDate);
                waterIntent.putExtra("waterphoto_CUR_TIME", curTime);
                waterIntent.putExtra("waterphoto_CUR_TIME_MILLIS", currentTimeMillis);
                mActivity.startActivityForResult(waterIntent, REQUEST_CODE);
            } else {
                Toast.makeText(WaterCameraActivity.this,"Can't save the picture",Toast.LENGTH_LONG).show();
                camera.stopPreview();
                camera.release();
                camera = null;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(RESULT_OK == resultCode){
            switch (requestCode){
                case REQUEST_CODE: //处理返回结果
                    setResult(RESULT_OK, data); //将结果直接给设置为，启动水印相机的返回结果
                    break;
                default:
                    break;
            }
            WaterCameraActivity.this.finish();//结束本页面，就会将结果返回到调用本页的那个activity了
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera = Camera.open(mCameraId);
            Camera.getCameraInfo(mCameraId, cameraInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startPreview(mCamera, mSurfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 检查设备是否有摄像头
     *
     * @param context context
     * @return boolean
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    /**
     * 打开后置摄像头
     */
    private void openCamera() {

        try{
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, cameraInfo);
            this.cameraInfo = cameraInfo;
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) { //后置摄像头 CAMERA_FACING_FRONT
                mCamera = Camera.open();
                mCamera.startPreview();//开始预览相机
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 开始预览相机
     *
     * @param camera        camera
     * @param surfaceHolder surfaceHolder
     */
    private void startPreview(Camera camera, SurfaceHolder surfaceHolder) {
        camera.setDisplayOrientation(ImageUtil.getPreviewDegree(WaterCameraActivity.this));
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        camera.startPreview();//调用此方法，然后真正的预览相机
    }

    /**
     * 停止相机预览
     */
    private void stopPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        super.onDestroy();
    }

}

