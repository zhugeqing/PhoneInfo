package com.example.administrator.phoneinfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class GroupFileManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private List<View> listViews;
    private ImageView cursorIv;
    private TextView tabLog, tabCellInfo, tabMr,tabUserLayer;
    private TextView[] titles;
    private ViewPager viewPager;
    private LinearLayout linearLayoutLog, linearLayoutCellInfo,linearLayoutMr,linearLayoutUserLayer;
    private MyApplication myApplication;
    private Button buttonUpload,buttonDownload,buttonDelete;

    /**
      * 偏移量（手机屏幕宽度 / 选项卡总数 - 选项卡长度） / 2
      */
     private int offset = 0;
    /**
      * 下划线图片宽度
      */
    private int lineWidth;
            /**
      * 当前选项卡的位置
      */
    private int current_index = 0;
            /**
      * 选项卡总数
      */
    private static final int TAB_COUNT = 4;

    private static final int TAB_0 = 0;
    private static final int TAB_1 = 1;
    private static final int TAB_2 = 2;
    private static final int TAB_3 = 3;
    private int currentTab=-1;

    Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            try {
                if (msg.what == 0) {
                    linearLayoutLog.removeAllViews();
                    mCheckBoxListLog.clear();
                    mCheckBoxListLog=showFilesByCheckBox(mFileNameListLog,mFileSizeListLog,mFileDateListLog,linearLayoutLog,GroupFileManagerActivity.this);
                }else if(msg.what==1){
                    linearLayoutCellInfo.removeAllViews();
                    mCheckBoxListCellInfo.clear();
                    mCheckBoxListCellInfo= showFilesByCheckBox(mFileNameListCellInfo,mFileSizeListCellInfo,mFileDateListCellInfo,linearLayoutCellInfo,GroupFileManagerActivity.this);
                }else if(msg.what==2){
                    linearLayoutMr.removeAllViews();
                    mCheckBoxListMr.clear();
                    mCheckBoxListMr= showFilesByCheckBox(mFileNameListMr,mFileSizeListMr,mFileDateListMr,linearLayoutMr,GroupFileManagerActivity.this);
                }else if(msg.what==3){
                    linearLayoutUserLayer.removeAllViews();
                    mCheckBoxListUserLayer.clear();
                    mCheckBoxListUserLayer= showFilesByCheckBox(mFileNameListUserLayer,mFileSizeListUserLayer,mFileDateListUserLayer,linearLayoutUserLayer,GroupFileManagerActivity.this);
                }else if(msg.what==10){
                    Toast.makeText(GroupFileManagerActivity.this,"成功删除文件！",Toast.LENGTH_LONG).show();
                }else if(msg.what==11){
                    ProgressDlgUtil.stopProgressDlg();
                    Toast.makeText(GroupFileManagerActivity.this,"上传成功！",Toast.LENGTH_LONG).show();
                }else if(msg.what==12){
                    ProgressDlgUtil.stopProgressDlg();
                    Toast.makeText(GroupFileManagerActivity.this,"上传失败！",Toast.LENGTH_LONG).show();
                }else if(msg.what==14){
                    ProgressDlgUtil.stopProgressDlg();
                    Toast.makeText(GroupFileManagerActivity.this,"下载失败！",Toast.LENGTH_LONG).show();
                } else if(msg.what==15){
                    ProgressDlgUtil.stopProgressDlg();
                    Toast.makeText(GroupFileManagerActivity.this,"下载成功！",Toast.LENGTH_LONG).show();
                }
                super.handleMessage(msg);
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_file_manager);
        myApplication=(MyApplication) this.getApplicationContext();;
        initUI();
        initImageView();
        initVPager();

        buttonUpload=(Button)findViewById(R.id.btn_groupfile_upload);
        buttonDownload=(Button)findViewById(R.id.btn_groupfile_download);
        buttonDelete=(Button)findViewById(R.id.btn_groupfile_delete);
        buttonUpload.setOnClickListener(this);
        buttonDownload.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        linearLayoutLog=(LinearLayout)listViews.get(0).findViewById(R.id.linearlayout_groupfiles_log);
        linearLayoutCellInfo=(LinearLayout)listViews.get(1).findViewById(R.id.linearlayout_groupfiles_cellinfo);
        linearLayoutMr=(LinearLayout)listViews.get(2).findViewById(R.id.linearlayout_groupfiles_mr);
        linearLayoutUserLayer=(LinearLayout)listViews.get(3).findViewById(R.id.linearlayout_groupfiles_userlayer);
        readFilesFromFtp(myApplication.logFilesFtpDir,0,0);
        readFilesFromFtp(myApplication.cellInfoFtpDir,1,1000);
        readFilesFromFtp(myApplication.mrFilesFtpDir,2,2000);
        readFilesFromFtp(myApplication.layerFilesFtpDir,3,3000);

    }
    /*** 初始化布局和监听*/
    private void initUI() {
        viewPager = (ViewPager) findViewById(R.id.vp_groupfile_vPager);
        cursorIv = (ImageView) findViewById(R.id.iv_tab_bottom_img);
        tabLog = (TextView) findViewById(R.id.tv_groupfiles_log);
        tabCellInfo = (TextView) findViewById(R.id.tv_groupfiles_cellinfo);
        tabMr = (TextView) findViewById(R.id.tv_groupfiles_mr);
        tabUserLayer = (TextView) findViewById(R.id.tv_groupfiles_userlayer);

        tabLog.setOnClickListener(this);
        tabCellInfo.setOnClickListener(this);
        tabMr.setOnClickListener(this);
        tabUserLayer.setOnClickListener(this);
    }

    /*** 初始化底部下划线*/
    private void initImageView() {
                // 获取图片宽度
                lineWidth = BitmapFactory.decodeResource(getResources(), R.drawable.red_line_90_2).getWidth();
                // Android提供的DisplayMetrics可以很方便的获取屏幕分辨率
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int screenW = dm.widthPixels; // 获取分辨率宽度
                offset = (screenW / TAB_COUNT - lineWidth) / 2;  // 计算偏移值
                Matrix matrix = new Matrix();
                matrix.postTranslate(offset, 0);
                // 设置下划线初始位置
                cursorIv.setImageMatrix(matrix);
    }

    /*** 初始化ViewPager并添加监听事件*/
    private void initVPager() {




        listViews = new ArrayList<>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(R.layout.tab_groupfile_log, null));
        listViews.add(mInflater.inflate(R.layout.tab_groupfile_cellinfo, null));
        listViews.add(mInflater.inflate(R.layout.tab_groupfile_mr, null));
        listViews.add(mInflater.inflate(R.layout.tab_groupfile_userlayer, null));
        viewPager.setAdapter(new MyPagerAdapter(listViews));
        viewPager.setCurrentItem(0);
        titles = new TextView[]{tabLog,tabCellInfo,tabMr,tabUserLayer};
        viewPager.setOffscreenPageLimit(titles.length);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int one = offset * 2 + lineWidth;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // 下划线开始移动前的位置
                float fromX = one * current_index;
                // 下划线移动完毕后的位置
                float toX = one * position;
                Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
                animation.setFillAfter(true);
                animation.setDuration(500);
                // 给图片添加动画
                cursorIv.startAnimation(animation);
                // 当前Tab的字体变成红色
                titles[position].setTextColor(Color.RED);
                titles[current_index].setTextColor(Color.BLACK);
                current_index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /*** ViewPager适配器*/
    public class MyPagerAdapter extends PagerAdapter {

        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    //然后进入系统的文件管理,选择文件后
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
                System.out.println("ZGQ:选择的文件Uri = " + data.toString());
                //通过Uri获取真实路径
                //final String excelPath = CreateCellInfoDBActivity.getRealFilePath(this, data.getData());
                final String excelPath = GetPathFromUri4kitkat.getPath(this,data.getData());

                System.out.println("ZGQ:excelPath = " + excelPath);//    /storage/emulated/0/test.xls

                if(excelPath==null){
                    Toast.makeText(this, "该文件管理器无法获取文件路径，请重新选择其他的文件管理器上传！", Toast.LENGTH_LONG).show();
                }else if (excelPath.contains(".xls") || excelPath.contains(".kml") || excelPath.contains(".kmz")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("是否确定上传该文件？");
                    builder1.setTitle("提示");
                    builder1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressDlgUtil.showProgressDlg("正在上传...", GroupFileManagerActivity.this);
                            final String uploadPathFtp;
                            final int msgNumber;
                            if (currentTab == TAB_0) {
                                uploadPathFtp = myApplication.logFilesFtpDir;
                                msgNumber=0;
                            } else if (currentTab == TAB_1) {
                                uploadPathFtp = myApplication.cellInfoFtpDir;
                                msgNumber=1;
                            } else if (currentTab == TAB_2) {
                                uploadPathFtp = myApplication.mrFilesFtpDir;
                                msgNumber=2;
                            } else {
                                uploadPathFtp = myApplication.layerFilesFtpDir;
                                msgNumber=3;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        File logUploadFile = new File(excelPath);
                                        new MyFTP().uploadSingleFile(logUploadFile, uploadPathFtp, new MyFTP.UploadProgressListener() {
                                            @Override
                                            public void onUploadProgress(String currentStep, long uploadSize, File file) {
                                                if (currentStep.equals(FtpActivity.FTP_UPLOAD_SUCCESS)) {
                                                    Message msg11 = new Message();
                                                    msg11.what = 11;   //发送上传成功消息
                                                    mHandler.sendMessage(msg11);
                                                    readFilesFromFtp(uploadPathFtp,msgNumber,0);

                                                } else if (currentStep.equals(FtpActivity.FTP_UPLOAD_FAIL)) {
                                                    Message msg12 = new Message();
                                                    msg12.what = 12;   //发送上传失败消息
                                                    mHandler.sendMessage(msg12);
                                                }else {

                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Message msg12 = new Message(); //发送上传失败消息
                                        msg12.what = 12;
                                        mHandler.sendMessage(msg12);
                                    }

                                }
                            }).start();
                            //zgqExcelRowsList.clear();
                        }
                    });
                    builder1.setNegativeButton("取消", null);
                    builder1.create().show();
                } else {
                    Toast.makeText(this, "此文件格式暂不支持上传", Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_groupfile_download:
                    currentTab = viewPager.getCurrentItem();

                    if(myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo/APP_DEFAULT")){
                        if(currentTab==TAB_0||currentTab==TAB_3){
                            Toast.makeText(GroupFileManagerActivity.this,"您不是群主或者您不是VIP用户，没有下载信息表的权限。",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else{
                        if(currentTab==TAB_1||currentTab==TAB_2||currentTab==TAB_3){
                            if(myApplication.USER_AUTH_INFO.flagGroupMaster!=1||myApplication.USER_AUTH_INFO.vipUser==0){
                                Toast.makeText(GroupFileManagerActivity.this,"您不是群主或者您不是VIP用户，没有下载信息表的权限。",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }


                    if(currentTab==TAB_0){
                        if(myApplication.USER_AUTH_INFO.vipUser==0){
                            Toast.makeText(GroupFileManagerActivity.this,"您不是VIP用户，没有下载LOG的权限。",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    if (currentTab == TAB_0)
                    {
                        final List<String> downloadFileNames = new ArrayList<>();
                        for (int i = 0; i < mCheckBoxListLog.size(); i++) {
                            if (mCheckBoxListLog.get(i).isChecked()) {
                                downloadFileNames.add(mCheckBoxListLog.get(i).getText().toString());
                            }
                        }
                        if(downloadFileNames.size()==0){
                            Toast.makeText(GroupFileManagerActivity.this,"请选择要下载的文件！",Toast.LENGTH_LONG).show();
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(GroupFileManagerActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("提示");
                        dlg.setMessage("是否确定下载文件？\r\n本次下载保存到手机目录"+myApplication.logFileSavePath);
                        dlg.setNegativeButton("取消",null);
                        dlg.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDlgUtil.showProgressDlg("正在下载文件...",GroupFileManagerActivity.this);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyFTP myFTP = new MyFTP();
                                        try {
                                            myFTP.downloadMultiFilesAtOneFtpPath(myApplication.logFilesFtpDir, myApplication.logFileSavePath, downloadFileNames, new MyFTP.DownLoadProgressListener() {
                                                @Override
                                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                                    if(currentStep==FtpActivity.FTP_DISCONNECT_SUCCESS){
                                                        Message msg15 = new Message(); //发送取消读取进度条消息
                                                        msg15.what = 15;
                                                        mHandler.sendMessage(msg15);
                                                    }else if(currentStep==FtpActivity.FTP_DOWN_FAIL){
                                                        Message msg14 = new Message(); //发送取消读取进度条消息
                                                        msg14.what = 14;
                                                        mHandler.sendMessage(msg14);
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                        dlg.show();
                    }
                    else if (currentTab == TAB_1) {

                        final List<String> downloadFileNames = new ArrayList<>();
                        for (int i = 0; i < mCheckBoxListCellInfo.size(); i++) {
                            if (mCheckBoxListCellInfo.get(i).isChecked()) {
                                downloadFileNames.add(mCheckBoxListCellInfo.get(i).getText().toString());
                            }
                        }
                        if(downloadFileNames.size()==0){
                            Toast.makeText(GroupFileManagerActivity.this,"请选择要下载的文件！",Toast.LENGTH_LONG).show();
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(GroupFileManagerActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("提示");
                        dlg.setMessage("是否确定下载文件？\r\n本次下载保存到手机目录"+myApplication.logFileSavePath);
                        dlg.setNegativeButton("取消",null);
                        dlg.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDlgUtil.showProgressDlg("正在下载文件...",GroupFileManagerActivity.this);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyFTP myFTP = new MyFTP();
                                        try {
                                            myFTP.downloadMultiFilesAtOneFtpPath(myApplication.cellInfoFtpDir, myApplication.logFileSavePath, downloadFileNames, new MyFTP.DownLoadProgressListener() {
                                                @Override
                                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                                    if(currentStep==FtpActivity.FTP_DISCONNECT_SUCCESS){
                                                        Message msg15 = new Message(); //发送取消读取进度条消息
                                                        msg15.what = 15;
                                                        mHandler.sendMessage(msg15);
                                                    }else if(currentStep==FtpActivity.FTP_DOWN_FAIL){
                                                        Message msg14 = new Message(); //发送取消读取进度条消息
                                                        msg14.what = 14;
                                                        mHandler.sendMessage(msg14);
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                        dlg.show();


                    }
                    else if (currentTab == TAB_2) {
                        //Toast.makeText(this, "当前Tab:" + currentTab, Toast.LENGTH_SHORT).show();
                        final List<String> downloadFileNames = new ArrayList<>();
                        for (int i = 0; i < mCheckBoxListMr.size(); i++) {
                            if (mCheckBoxListMr.get(i).isChecked()) {
                                downloadFileNames.add(mCheckBoxListMr.get(i).getText().toString());
                            }
                        }
                        if(downloadFileNames.size()==0){
                            Toast.makeText(GroupFileManagerActivity.this,"请选择要下载的文件！",Toast.LENGTH_LONG).show();
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(GroupFileManagerActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("提示");
                        dlg.setMessage("是否确定下载文件？\r\n本次下载保存到手机目录"+myApplication.logFileSavePath);
                        dlg.setNegativeButton("取消",null);
                        dlg.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDlgUtil.showProgressDlg("正在下载文件...",GroupFileManagerActivity.this);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyFTP myFTP = new MyFTP();
                                        try {
                                            myFTP.downloadMultiFilesAtOneFtpPath(myApplication.mrFilesFtpDir, myApplication.logFileSavePath, downloadFileNames, new MyFTP.DownLoadProgressListener() {
                                                @Override
                                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                                    if(currentStep==FtpActivity.FTP_DISCONNECT_SUCCESS){
                                                        Message msg15 = new Message(); //发送取消读取进度条消息
                                                        msg15.what = 15;
                                                        mHandler.sendMessage(msg15);
                                                    }else if(currentStep==FtpActivity.FTP_DOWN_FAIL){
                                                        Message msg14 = new Message(); //发送取消读取进度条消息
                                                        msg14.what = 14;
                                                        mHandler.sendMessage(msg14);
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                        dlg.show();
                    }
                    else if (currentTab == TAB_3) {
                        //Toast.makeText(this, "当前Tab:" + currentTab, Toast.LENGTH_SHORT).show();
                        final List<String> downloadFileNames = new ArrayList<>();
                        for (int i = 0; i < mCheckBoxListUserLayer.size(); i++) {
                            if (mCheckBoxListUserLayer.get(i).isChecked()) {
                                downloadFileNames.add(mCheckBoxListUserLayer.get(i).getText().toString());
                            }
                        }
                        if(downloadFileNames.size()==0){
                            Toast.makeText(GroupFileManagerActivity.this,"请选择要下载的文件！",Toast.LENGTH_LONG).show();
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(GroupFileManagerActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("提示");
                        dlg.setMessage("是否确定下载文件？\r\n本次下载保存到手机目录"+myApplication.logFileSavePath);
                        dlg.setNegativeButton("取消",null);
                        dlg.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDlgUtil.showProgressDlg("正在下载文件...",GroupFileManagerActivity.this);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyFTP myFTP = new MyFTP();
                                        try {
                                            myFTP.downloadMultiFilesAtOneFtpPath(myApplication.layerFilesFtpDir, myApplication.logFileSavePath, downloadFileNames, new MyFTP.DownLoadProgressListener() {
                                                @Override
                                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                                    if(currentStep==FtpActivity.FTP_DISCONNECT_SUCCESS){
                                                        Message msg15 = new Message(); //发送取消读取进度条消息
                                                        msg15.what = 15;
                                                        mHandler.sendMessage(msg15);
                                                    }else if(currentStep==FtpActivity.FTP_DOWN_FAIL){
                                                        Message msg14 = new Message(); //发送取消读取进度条消息
                                                        msg14.what = 14;
                                                        mHandler.sendMessage(msg14);
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                        dlg.show();
                    }
                    break;
                case R.id.btn_groupfile_upload:
                    if(myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo/APP_DEFAULT")||myApplication.USER_AUTH_INFO.groupName.equals("")){
                        Toast.makeText(GroupFileManagerActivity.this,"请先加入某个群，或者自己创建群，才可以上传文件。",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(myApplication.USER_AUTH_INFO.vipUser==0){
                        Toast.makeText(GroupFileManagerActivity.this,"非VIP用户无法上传文件。",Toast.LENGTH_LONG).show();
                        return;
                    }

                    currentTab = viewPager.getCurrentItem();
                    final Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                    intent1.setType("application/*");//设置类型
                    intent1.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent1, 1);

                    break;

                case R.id.btn_groupfile_delete:
                    // 删除文件
                    if(myApplication.USER_AUTH_INFO.flagGroupMaster!=1||myApplication.USER_AUTH_INFO.vipUser==0||myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo/APP_DEFAULT")){
                        Toast.makeText(GroupFileManagerActivity.this,"您不是群主或者您不是VIP用户，没有删除群文件的权限。",Toast.LENGTH_LONG).show();
                        return;
                    }
                    currentTab = viewPager.getCurrentItem();
                    if (currentTab == TAB_0)
                    {
                        final List<String> deleteFileNames = new ArrayList<>();
                        for (int i = 0; i < mCheckBoxListLog.size(); i++) {
                            if (mCheckBoxListLog.get(i).isChecked()) {
                                deleteFileNames.add(myApplication.logFilesFtpDir + mCheckBoxListLog.get(i).getText().toString());
                            }
                        }
                        if(deleteFileNames.size()==0){
                            Toast.makeText(GroupFileManagerActivity.this,"请选择要删除的文件！",Toast.LENGTH_LONG).show();
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(GroupFileManagerActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("警告");
                        dlg.setMessage("是否确定删除所选择的文件！删除文件后将无法恢复，请谨慎操作！");
                        dlg.setNegativeButton("取消",null);
                        dlg.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyFTP myFTP = new MyFTP();
                                        try {
                                            myFTP.deleteMultiFile(deleteFileNames, new MyFTP.DeleteFileProgressListener() {
                                                @Override
                                                public void onDeleteProgress(String currentStep) {
                                                    if(currentStep.equals(FtpActivity.FTP_DISCONNECT_SUCCESS)){

                                                        Message msg10 = new Message(); //发送取消读取进度条消息
                                                        msg10.what = 10;
                                                        mHandler.sendMessage(msg10);
                                                        readFilesFromFtp(myApplication.logFilesFtpDir,0,0);
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                        dlg.show();
                    }
                    else if (currentTab == TAB_1) {
                        //Toast.makeText(this, "当前Tab:" + currentTab, Toast.LENGTH_SHORT).show();
                        final List<String> deleteFileNames = new ArrayList<>();
                        for (int i = 0; i < mCheckBoxListCellInfo.size(); i++) {
                            if (mCheckBoxListCellInfo.get(i).isChecked()) {
                                deleteFileNames.add(myApplication.cellInfoFtpDir + mCheckBoxListCellInfo.get(i).getText().toString());
                            }
                        }
                        if(deleteFileNames.size()==0){
                            Toast.makeText(GroupFileManagerActivity.this,"请选择要删除的文件！",Toast.LENGTH_LONG).show();
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(GroupFileManagerActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("警告");
                        dlg.setMessage("是否确定删除所选择的文件！删除文件后将无法恢复，请谨慎操作！");
                        dlg.setNegativeButton("取消",null);
                        dlg.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyFTP myFTP = new MyFTP();
                                        try {
                                            myFTP.deleteMultiFile(deleteFileNames, new MyFTP.DeleteFileProgressListener() {
                                                @Override
                                                public void onDeleteProgress(String currentStep) {
                                                    if(currentStep.equals(FtpActivity.FTP_DISCONNECT_SUCCESS)){

                                                        Message msg10 = new Message(); //发送取消读取进度条消息
                                                        msg10.what = 10;
                                                        mHandler.sendMessage(msg10);
                                                        readFilesFromFtp(myApplication.cellInfoFtpDir,1,0);
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                        dlg.show();


                    }
                    else if (currentTab == TAB_2) {
                        //Toast.makeText(this, "当前Tab:" + currentTab, Toast.LENGTH_SHORT).show();
                        final List<String> deleteFileNames = new ArrayList<>();
                        for (int i = 0; i < mCheckBoxListMr.size(); i++) {
                            if (mCheckBoxListMr.get(i).isChecked()) {
                                deleteFileNames.add(myApplication.mrFilesFtpDir + mCheckBoxListMr.get(i).getText().toString());
                            }
                        }
                        if(deleteFileNames.size()==0){
                            Toast.makeText(GroupFileManagerActivity.this,"请选择要删除的文件！",Toast.LENGTH_LONG).show();
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(GroupFileManagerActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("警告");
                        dlg.setMessage("是否确定删除所选择的文件！删除文件后将无法恢复，请谨慎操作！");
                        dlg.setNegativeButton("取消",null);
                        dlg.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyFTP myFTP = new MyFTP();
                                        try {
                                            myFTP.deleteMultiFile(deleteFileNames, new MyFTP.DeleteFileProgressListener() {
                                                @Override
                                                public void onDeleteProgress(String currentStep) {
                                                    if(currentStep.equals(FtpActivity.FTP_DISCONNECT_SUCCESS)){

                                                        Message msg10 = new Message(); //发送取消读取进度条消息
                                                        msg10.what = 10;
                                                        mHandler.sendMessage(msg10);
                                                        readFilesFromFtp(myApplication.mrFilesFtpDir,2,0);
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                        dlg.show();
                    }
                    else if (currentTab == TAB_3) {
                        //Toast.makeText(this, "当前Tab:" + currentTab, Toast.LENGTH_SHORT).show();
                        final List<String> deleteFileNames = new ArrayList<>();
                        for (int i = 0; i < mCheckBoxListUserLayer.size(); i++) {
                            if (mCheckBoxListUserLayer.get(i).isChecked()) {
                                deleteFileNames.add(myApplication.layerFilesFtpDir + mCheckBoxListUserLayer.get(i).getText().toString());
                            }
                        }
                        if(deleteFileNames.size()==0){
                            Toast.makeText(GroupFileManagerActivity.this,"请选择要删除的文件！",Toast.LENGTH_LONG).show();
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(GroupFileManagerActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("警告");
                        dlg.setMessage("是否确定删除所选择的文件！删除文件后将无法恢复，请谨慎操作！");
                        dlg.setNegativeButton("取消",null);
                        dlg.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyFTP myFTP = new MyFTP();
                                        try {
                                            myFTP.deleteMultiFile(deleteFileNames, new MyFTP.DeleteFileProgressListener() {
                                                @Override
                                                public void onDeleteProgress(String currentStep) {
                                                    if(currentStep.equals(FtpActivity.FTP_DISCONNECT_SUCCESS)){

                                                        Message msg10 = new Message(); //发送取消读取进度条消息
                                                        msg10.what = 10;
                                                        mHandler.sendMessage(msg10);
                                                        readFilesFromFtp(myApplication.layerFilesFtpDir,3,0);
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                        dlg.show();
                    }
                    break;
                case R.id.tv_groupfiles_log:
                    // 避免重复加载
                    if (viewPager.getCurrentItem() != TAB_0) {
                        viewPager.setCurrentItem(TAB_0);
                    }
                    break;
                case R.id.tv_groupfiles_cellinfo:
                    if (viewPager.getCurrentItem() != TAB_1) {
                        viewPager.setCurrentItem(TAB_1);
                    }
                    break;
                case R.id.tv_groupfiles_mr:
                    if (viewPager.getCurrentItem() != TAB_2) {
                        viewPager.setCurrentItem(TAB_2);
                    }
                    break;
                case R.id.tv_groupfiles_userlayer:
                    if (viewPager.getCurrentItem() != TAB_3) {
                        viewPager.setCurrentItem(TAB_3);
                    }
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String mFtpDir;
    private List<String> mFileNameListLog=new ArrayList<>();
    private List<String> mFileSizeListLog=new ArrayList<>();
    private List<String> mFileDateListLog=new ArrayList<>();
    private List<String> mFileNameListCellInfo=new ArrayList<>();
    private List<String> mFileSizeListCellInfo=new ArrayList<>();
    private List<String> mFileDateListCellInfo=new ArrayList<>();
    private List<String> mFileNameListMr=new ArrayList<>();
    private List<String> mFileSizeListMr=new ArrayList<>();
    private List<String> mFileDateListMr=new ArrayList<>();
    private List<String> mFileNameListUserLayer=new ArrayList<>();
    private List<String> mFileSizeListUserLayer=new ArrayList<>();
    private List<String> mFileDateListUserLayer=new ArrayList<>();

    private List<CheckBox> mCheckBoxListLog=new ArrayList<>();
    private List<CheckBox> mCheckBoxListCellInfo=new ArrayList<>();
    private List<CheckBox> mCheckBoxListMr=new ArrayList<>();
    private List<CheckBox> mCheckBoxListUserLayer=new ArrayList<>();

    public void readFilesFromFtp(String ftpdir,final int msgNo,final int tpDelay) {
        final int tabIndex;
        final String tpftpDir=ftpdir;
        if(ftpdir.equals(myApplication.logFilesFtpDir)){
            tabIndex=0;
            mFileNameListLog.clear();
            mFileSizeListLog.clear();
            mFileDateListLog.clear();
        }else if(ftpdir.equals(myApplication.cellInfoFtpDir)){
            tabIndex=1;
            mFileNameListCellInfo.clear();
            mFileSizeListCellInfo.clear();
            mFileDateListCellInfo.clear();
        }else if(ftpdir.equals(myApplication.mrFilesFtpDir)){
            tabIndex=2;
            mFileNameListMr.clear();
            mFileSizeListMr.clear();
            mFileDateListMr.clear();
        }else if(ftpdir.equals(myApplication.layerFilesFtpDir)){
            tabIndex=3;
            mFileNameListUserLayer.clear();
            mFileSizeListUserLayer.clear();
            mFileDateListUserLayer.clear();
        }else{
            tabIndex=0;
            mFileNameListLog.clear();
            mFileSizeListLog.clear();
            mFileDateListLog.clear();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(tpDelay);
                    FTPFile[] layerFiles = new MyFTP().listFtpServerFiles(tpftpDir);
                    for (int i = 0; i < layerFiles.length; i++) {

                        String filesize=fileSizeUnitTrans(layerFiles[i].getSize());

                        Date date = new Date(layerFiles[i].getTimestamp().getTimeInMillis());
                        java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String timestring = f.format(date);

                        if(tabIndex==0){
                            mFileNameListLog.add(layerFiles[i].getName());
                            mFileSizeListLog.add(filesize);
                            mFileDateListLog.add(timestring);
                        }else if(tabIndex==1){
                            mFileNameListCellInfo.add(layerFiles[i].getName());
                            mFileSizeListCellInfo.add(filesize);
                            mFileDateListCellInfo.add(timestring);
                        }else if(tabIndex==2){
                            mFileNameListMr.add(layerFiles[i].getName());
                            mFileSizeListMr.add(filesize);
                            mFileDateListMr.add(timestring);
                        }else if(tabIndex==3){
                            mFileNameListUserLayer.add(layerFiles[i].getName());
                            mFileSizeListUserLayer.add(filesize);
                            mFileDateListUserLayer.add(timestring);
                        }
                    }
                    //if (layerFiles.length > 0) {
                        Message msgX = new Message(); //发送开始显示消息
                        msgX.what = msgNo;
                        mHandler.sendMessage(msgX);
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msgX = new Message(); //发送开始显示消息
                    msgX.what = msgNo;
                    mHandler.sendMessage(msgX);
                }
            }
        }).start();
    }

    public List<CheckBox> showFilesByCheckBox(List<String> fileNameList,List<String> fileSizeList,List<String> fileDateList, LinearLayout xLinearLayout, Context context){
        List<CheckBox> checkBoxList=new ArrayList<>();
        for(int i=0;i<fileNameList.size();i++){

            CheckBox checkBoxFile=new CheckBox(context);
            checkBoxFile.setTextSize(12);
            checkBoxFile.setText(fileNameList.get(i));
            checkBoxFile.setChecked(false);
            checkBoxFile.setPadding(0,0,50,0);
            checkBoxList.add(checkBoxFile);

            TextView textViewSize=new TextView(context);
            textViewSize.setTextSize(12);
            textViewSize.setText(fileSizeList.get(i));

            TextView textViewDate=new TextView(context);
            textViewDate.setTextSize(12);
            textViewDate.setText(fileDateList.get(i));

            TableRow tableRow = new TableRow(context);
            //tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,90));
            //checkBoxFile.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,0.7f));
            //textViewSize.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1.2f));
            //textViewDate.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f));

            checkBoxFile.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,3.0f));
            textViewSize.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,0.8f));
            textViewDate.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,1.0f));

            tableRow.addView(checkBoxFile);
            tableRow.addView(textViewSize);
            tableRow.addView(textViewDate);

            xLinearLayout.addView(tableRow);

        }
        return checkBoxList;
    }

    public static String fileSizeUnitTrans(Long fileSize){
       String xFileSize="";
        if(fileSize>=1024 && fileSize<1048576){
            xFileSize=String.valueOf(fileSize/1024)+"KB";
        }else if(fileSize>=1048576){
            xFileSize=String.valueOf(Math.round(Float.valueOf(fileSize)/1024/1024*10.0)/10.0)+"MB";
        }else{
            xFileSize=String.valueOf(fileSize)+"B";
        }
        return xFileSize;
    }


}

