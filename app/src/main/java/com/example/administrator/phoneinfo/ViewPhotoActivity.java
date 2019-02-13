package com.example.administrator.phoneinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ViewPhotoActivity extends AppCompatActivity {
    /*
    * 查看拍照的类
     * <p/>
     * Created by tanksu on 16/6/29.
     */
        private ImageView imgv_photo;
        private TextView tv_cancel, tv_ok;
        private int width, height;
        private RelativeLayout rl_layout;
        private String picPath = "", curDate = "", curTime = "", curAddress = "", userName = "", userOperation = "";
        private TextView tv_time, tv_date, tv_userName, tv_address, tv_operation;
        private CheckBox cb_savePic;
        private long currentTimeMillis;
        private String signal;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
            setContentView(R.layout.activity_view_photo);
            initViews();
            initData();
            initListener();
        }

        /**
         * 初始化控件
         */
        private void initViews() {
            imgv_photo = (ImageView) findViewById(R.id.imgv_viewphoto_photo);
            tv_cancel = (TextView) findViewById(R.id.tv_viewphoto_cancel);
            tv_ok = (TextView) findViewById(R.id.tv_viewphoto_ok);
            rl_layout = (RelativeLayout) findViewById(R.id.rl_viewphoto_layout);
            tv_time = (TextView) findViewById(R.id.tv_viewphoto_time);
            tv_date = (TextView) findViewById(R.id.tv_viewphoto_date);
            tv_userName = (TextView) findViewById(R.id.tv_viewphoto_userName);
            tv_address = (TextView) findViewById(R.id.tv_viewphoto_address);
            cb_savePic = (CheckBox) findViewById(R.id.cb_viewphoto_savePic);
            tv_operation = (TextView) findViewById(R.id.tv_viewphoto_operation);
        }

        /**
         * 初始化数据
         */
        private void initData() {
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            width = display.getWidth();
            height = display.getHeight();
            Intent intent = getIntent();
            if (intent != null) {
                //这里的目标是，将所有传过来的的信息都去取出来，设置到每个相应的空间里面去
                //有人会问我为什么要这样做，其实我在拍照的时候，还没有真正的拿到一张具有水印的照片
                //我这里采用的是截屏的方式，所以呢，就要重新吧信息展现出来
                //其实还有很多的方法可以做水印相机，例如用位图来“画”信息等，但是有简单的方法，为什么不用呢，非要去弄一些很复杂的方法？！
                picPath = intent.getStringExtra("waterphoto_PIC_PATH");
                curDate = intent.getStringExtra("waterphoto_CUR_DATE");
                curTime = intent.getStringExtra("waterphoto_CUR_TIME");
                userName = intent.getStringExtra("waterphoto_USER_NAME");
                curAddress = intent.getStringExtra("waterphoto_CUR_ADDRESS");
                userOperation = intent.getStringExtra("waterphoto_USER_OPERATION");
                signal = intent.getStringExtra("waterphoto_TS_HUB_OP_SIGNAL");
                currentTimeMillis = intent.getLongExtra("waterphoto_CUR_TIME_MILLIS", System.currentTimeMillis());
                tv_time.setText(curTime);
                tv_date.setText(curDate);
                tv_userName.setText(userName);
                tv_address.setText(curAddress);
                tv_operation.setText(userOperation);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.outWidth = width;
                options.outHeight = height;
                Bitmap bitmap = getPressedBitmap(picPath, width, height);//方法在下面，根据路径，获取第一步拍照存本地的图片
                /**
                 *
                 *
                 * 根据图片路径，得到压缩过的位图
                 *
                 * @param path
                 * @param width
                 * @param height
                 * @return returnBitmap
                public static Bitmap getPressedBitmap(String path, int width, int height) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                options.inSampleSize = getBitmapSampleSize(options, width, height);//getBitmapSampleSize(options, width, height)
                options.inJustDecodeBounds = false;
                Bitmap returnBitmap = BitmapFactory.decodeFile(path, options);
                return returnBitmap;
                }
                 * 根据要去的宽高，压缩图片
                 *
                 * @param options   options
                 * @param reqWidth  reqWidth
                 * @param reqHeight reqHeight
                 * @return inSimpleSize
                public static int getBitmapSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
                int imgWidth = options.outWidth;
                int imgHeight = options.outHeight;
                int inSimpleSize = 1;
                if (imgWidth > imgHeight || imgWidth < imgHeight) {
                final int heightRatio = imgWidth / reqWidth;
                final int widthRatio = imgHeight / reqHeight;
                inSimpleSize = widthRatio < heightRatio ? widthRatio : heightRatio;
                }
                return inSimpleSize;
                }
                 */
                imgv_photo.setImageBitmap(bitmap);
            } else {
                Toast.makeText(ViewPhotoActivity.this,"Can't save the picture",Toast.LENGTH_LONG).show();
            }
        }

        /**
         * 初始化监听器
         */
        private void initListener() {
            //点击使用图片按钮，就可以在启动水印相机的onactivityresult回调里面，获取到图片的路径，然后获取图片即可使用了
            tv_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Bitmap bitmap = getScreenPhoto(rl_layout);
                    saveBitmap(picPath, String.valueOf(currentTimeMillis), bitmap);//根据路径保存图片

                    Intent intent = new Intent(ViewPhotoActivity.this, WaterCameraActivity.class);
                    //intent.putExtra(StaticParam.PIC_PATH, picPath);//这里最主要的，就是将储存在本地的图片的路径作为结果返回
                    //intent.putExtra(StaticParam.IS_SAVE_PIC, cb_savePic.isChecked());//这里就是是否用户要保存这张图片的选项
                    //intent.putExtra(StaticParam.TS_HUB_OP_SIGNAL, signal);
                    setResult(RESULT_OK, intent); //如果是OK，就设置为OK结果
                    ViewPhotoActivity.this.finish();
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            deleteImageFromSDCard(picPath);//重新拍照，就将本地的图片给删除掉，然后重新拍照
                        }
                    }).start();
                    ViewPhotoActivity.this.finish();
                }
            });
        }

        /**
         * 截屏，这里就是截屏的地方了，我这里是截屏RelativeLayout，
         * 只要你将需要的信息放到这个RelativeLayout里面去就可以截取下来了
         *
         * @param waterPhoto waterPhoto
         * @return Bitmap
         */
        public Bitmap getScreenPhoto(RelativeLayout waterPhoto) {
            View view = waterPhoto;
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();
            int width = view.getWidth();
            int height = view.getHeight();
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            view.destroyDrawingCache();
            bitmap = null;
            return bitmap1;
        }

    /**
     *
     *
     * 根据图片路径，得到压缩过的位图
     *
     * @param path
     * @param width
     * @param height
     * @return returnBitmap
     */
    public static Bitmap getPressedBitmap(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        options.inSampleSize = getBitmapSampleSize(options, width, height);//getBitmapSampleSize(options, width, height)
        options.inJustDecodeBounds = false;
        Bitmap returnBitmap = BitmapFactory.decodeFile(path, options);
        return returnBitmap;
    }
    /*
     * 根据要去的宽高，压缩图片
     *
     * @param options   options
     * @param reqWidth  reqWidth
     * @param reqHeight reqHeight
     * @return inSimpleSize
    */
    public static int getBitmapSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int imgWidth = options.outWidth;
        int imgHeight = options.outHeight;
        int inSimpleSize = 1;
        if (imgWidth > imgHeight || imgWidth < imgHeight) {
            final int heightRatio = imgWidth / reqWidth;
            final int widthRatio = imgHeight / reqHeight;
            inSimpleSize = widthRatio < heightRatio ? widthRatio : heightRatio;
        }
        return inSimpleSize;
    }

    /**
     * 根据路径和名字保存图片
     *
     * @param path    path
     * @param imgName imgName
     * @param bitmap  bitmap
     * @return createPath
     */
    public static String saveBitmap(String path, String imgName, Bitmap bitmap) {
        String savePath = null;
        if (path == null) { //if path is null
            File fileSDCardDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String imgPath = fileSDCardDir.getAbsolutePath() + "/s/waterCamera/";
            File fileDir = new File(imgPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            String photoName = imgName + ".JPG";
            imgPath = imgPath + photoName;
            /*
            File fileIphoto = new File(imgPath);
            if (!fileIphoto.exists()) {
                try {
                    fileIphoto.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            savePath = fileIphoto.getPath();
            //saveBitmap(bitmap, fileIphoto);
            */

            try {
                ImageUtil.saveImage(imgPath,ImageUtil.bitmap2Bytes(bitmap));
                savePath=imgPath;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return savePath;
        } else { //if path isn't null, override the photo
            File oldFile = new File(path);
            if (oldFile.exists()) {
                oldFile.delete();
            }
            File newFile = new File(path);
            if (!newFile.exists()) {
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //saveBitmap(bitmap, newFile);
            savePath = newFile.getPath();

            try {
                ImageUtil.saveImage(savePath,ImageUtil.bitmap2Bytes(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return savePath;
        }
    }

    public static void deleteImageFromSDCard(String picPath){
        File picfile=new File(picPath);
        if(picfile!=null){
            picfile.delete();
        }
    }

}