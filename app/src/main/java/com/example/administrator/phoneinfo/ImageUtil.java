package com.example.administrator.phoneinfo;

/**
 * Created by Administrator on 2017/5/30.
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

public class ImageUtil {
    private static final String SDCARD_CACHE_IMG_PATH = Environment
            .getExternalStorageDirectory().getPath() + "/llc/images/";

    /**
     * 保存图片到SD卡
     * @param imagePath
     * @param buffer
     * @throws IOException
     */
    public static void saveImage(String imagePath, byte[] buffer)
            throws IOException {
        File f = new File(imagePath);
        if (f.exists()) {
            return;
        } else {
            File parentFile = f.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(imagePath);
            fos.write(buffer);
            fos.flush();
            fos.close();
        }
    }

    /**
     * 从SD卡加载图片
     * @param imagePath
     * @return
     */
    public static Bitmap getImageFromLocal(String imagePath){
        File file = new File(imagePath);
        if(file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            file.setLastModified(System.currentTimeMillis());
            return bitmap;
        }
        return null;
    }

    /**
     * Bitmap转换到Byte[]
     * @param bm
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bas);
        return bas.toByteArray();
    }

    /**
     * 从本地或者服务端加载图片
     * @return
     * @throws IOException
     */
    public static Bitmap loadImage(final String imagePath,final String imgUrl,final ImageCallback callback) {
        Bitmap bitmap = getImageFromLocal(imagePath);
        if(bitmap != null){
            return bitmap;
        }else{//从网上加载
            final Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if(msg.obj!=null){
                        Bitmap bitmap = (Bitmap) msg.obj;
                        callback.loadImage(bitmap, imagePath);
                    }
                }
            };

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(imgUrl);
                        Log.e("图片加载", imgUrl);
                        URLConnection conn = url.openConnection();
                        conn.connect();
                        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(),8192) ;
                        Bitmap bitmap = BitmapFactory.decodeStream(bis);
                        //保存文件到sd卡
                        saveImage(imagePath,bitmap2Bytes(bitmap));
                        Message msg = handler.obtainMessage();
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    } catch (IOException e) {
                        Log.e(ImageUtil.class.getName(), "保存图片到本地存储卡出错！");
                    }
                }
            };
            //ThreadPoolManager.getInstance().addTask(runnable);
            handler.post(runnable);
        }
        return null;
    }

    // 返回图片存到sd卡的路径
    public static String getCacheImgPath() {
        return SDCARD_CACHE_IMG_PATH;
    }

    public static String md5(String paramString) {
        String returnStr;
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramString.getBytes());
            returnStr = byteToHexString(localMessageDigest.digest());
            return returnStr;
        } catch (Exception e) {
            return paramString;
        }
    }

    /**
     * 将指定byte数组转换成16进制字符串
     *
     * @param b
     * @return
     */
    public static String byteToHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    /**
     *
     * @author Mathew
     *
     */
    public interface ImageCallback{
        public void loadImage(Bitmap bitmap,String imagePath);
    }

    public static int getPreviewDegree(Activity activity) {
                // 获得手机的方向
                int rotation = activity.getWindowManager().getDefaultDisplay()
                       .getRotation();
                int degree = 0;
                // 根据手机的方向计算相机预览画面应该选择的角度
                switch (rotation) {
                        case Surface.ROTATION_0:
                                degree = 90;
                                break;
                       case Surface.ROTATION_90:
                                degree = 0;
                                break;
                        case Surface.ROTATION_180:
                                degree = 270;
                                break;
                        case Surface.ROTATION_270:
                                degree = 180;
                                break;
                    }
                return degree;
    }



}
