package com.example.administrator.phoneinfo;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2017/4/19.
 */
public class MyAppUpdateService extends Service {
    /**
     * 安卓系统下载类
     **/
    private DownloadManager manager;
    /**
     * 接收下载完的广播
     **/
    private DownloadCompleteReceiver receiver;
    private String url="http://120.199.120.85:50080/wzgjgl_mobile/UploadFiles_201701/cellquery/app-debug.apk";
    private String DOWNLOADPATH = "/apk/";//下载路径，如果不定义自己的路径，6.0的手机不自动安装
    private Context mContext;



    /**
     * 初始化下载器
     **/

    private void initDownManager() {
        if(!isApkInDebug(MyAppUpdateService.this)){
            url="http://120.199.120.85:50080/wzgjgl_mobile/UploadFiles_201701/cellquery/app-release.apk";
        }

        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(url));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
        down.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        down.setMimeType(mimeString);
        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载后文件存放的位置
        //down.setDestinationInExternalPublicDir(DOWNLOADPATH, "cloudmap.apk");
        down.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS, "cloudmap.apk");


        down.setTitle("cloudmap");
        // 将下载请求放入队列
        manager.enqueue(down);
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext=getApplicationContext();
        //url = intent.getStringExtra("url");
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath() +DOWNLOADPATH+ "cloudmap.apk";
        //String path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) +DOWNLOADPATH+ "cloudmap.apk";
        String tppath = String.valueOf(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        File tempPath = new File(tppath);//外部存储路径
        if (!tempPath.exists()) {//创建目录
                    try {
                            tempPath.mkdirs();
                        } catch (Exception e) {
                            e.printStackTrace();
                    }
        }


        String path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/cloudmap.apk";

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            // 调用下载
            initDownManager();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        if (receiver != null)
            // 注销下载广播
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (manager.getUriForDownloadedFile(downId) != null) {
                    //自动安装apk
                    Uri sssm=manager.getUriForDownloadedFile(downId);

                    installAPK(manager.getUriForDownloadedFile(downId), context);
                    //installAPK(context);
                } else {
                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                }
                //停止服务并关闭广播
                MyAppUpdateService.this.stopSelf();
            }
        }

        private void installAPK(Uri apk, Context context) {
            try{
                if (Build.VERSION.SDK_INT < 23) {
                    Intent intents = new Intent();
                    intents.setAction("android.intent.action.VIEW");
                    intents.addCategory("android.intent.category.DEFAULT");
                    intents.setType("application/vnd.android.package-archive");
                    intents.setData(apk);
                    intents.setDataAndType(apk, "application/vnd.android.package-archive");
                    intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intents);
                } else if(Build.VERSION.SDK_INT == 23){
                    //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +DOWNLOADPATH+ "cloudmap.apk");
                    //File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) +DOWNLOADPATH+ "cloudmap.apk");
                    File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/cloudmap.apk");
                    if (file.exists()) {
                        openFile(file, context);
                    }
                }else if(Build.VERSION.SDK_INT >= 24){
                    File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/cloudmap.apk");
                    Intent intent = new Intent();
                    Uri uri;
                        // com.xxx.xxx.fileprovider为上述manifest中provider所配置相同；apkFile为问题1中的外部存储apk文件</pre>
                        uri = FileProvider.getUriForFile(context, "com.example.administrator.phoneinfo.fileprovider", file);
                        intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//7.0以后，系统要求授予临时uri读取权限，安装完毕以后，系统会自动收回权限，次过程没有用户交互

                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    context.startActivity(intent);
                    }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void installAPK(Context context) {
            //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +DOWNLOADPATH+ "cloudmap.apk");
            //File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) +DOWNLOADPATH+ "cloudmap.apk");
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/cloudmap.apk");
            if (file.exists()) {
                openFile(file, context);
            } else {
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void openFile(File file, Context context) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            context.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(context, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }

    }

    public String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    /**
          * 判断当前应用是否是debug状态
     **/

    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
