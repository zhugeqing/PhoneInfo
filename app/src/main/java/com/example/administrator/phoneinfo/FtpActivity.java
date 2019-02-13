package com.example.administrator.phoneinfo;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import com.example.administrator.phoneinfo.MyFTP.DeleteFileProgressListener;
import com.example.administrator.phoneinfo.MyFTP.DownLoadProgressListener;
import com.example.administrator.phoneinfo.MyFTP.UploadProgressListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FtpActivity extends Activity {

    private static final String TAG = "MainActivity";

    public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
    public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

    public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

    public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
    public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp);

        initView();
    }

    private void initView() {

        //上传功能
        //new FTP().uploadMultiFile为多文件上传
        //new FTP().uploadSingleFile为单文件上传
        Button buttonUpload = (Button) findViewById(R.id.button_ftp_upload);
        buttonUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 上传
                        File file = new File("/mnt/sdcard/ftpTest.docx");
                        try {

                            //单文件上传
                            new MyFTP().uploadSingleFile(file, "/fff",new UploadProgressListener(){

                                @Override
                                public void onUploadProgress(String currentStep,long uploadSize,File file) {
                                    // TODO Auto-generated method stub
                                    Log.d(TAG, currentStep);
                                    if(currentStep.equals(FtpActivity.FTP_UPLOAD_SUCCESS)){
                                        Log.d(TAG, "-----shanchuan--successful");
                                    } else if(currentStep.equals(FtpActivity.FTP_UPLOAD_LOADING)){
                                        long fize = file.length();
                                        float num = (float)uploadSize / (float)fize;
                                        int result = (int)(num * 100);
                                        Log.d(TAG, "-----shangchuan---"+result + "%");
                                    }
                                }
                            });
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

        //下载功能
        Button buttonDown = (Button)findViewById(R.id.button_ftp_download);
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 下载
                        try {

                            //单文件下载
                            new MyFTP().downloadSingleFile("/fff/ftpTest.docx","/mnt/sdcard/download/","ftpTest.docx",new DownLoadProgressListener(){

                                @Override
                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                    Log.d(TAG, currentStep);
                                    if(currentStep.equals(FtpActivity.FTP_DOWN_SUCCESS)){
                                        Log.d(TAG, "-----xiazai--successful");
                                    } else if(currentStep.equals(FtpActivity.FTP_DOWN_LOADING)){
                                        Log.d(TAG, "-----xiazai---"+downProcess + "%");
                                    }
                                }

                            });

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

        //删除功能
        Button buttonDelete = (Button)findViewById(R.id.button_ftp_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 删除
                        try {

                            new MyFTP().deleteSingleFile("/fff/ftpTest.docx",new DeleteFileProgressListener(){

                                @Override
                                public void onDeleteProgress(String currentStep) {
                                    Log.d(TAG, currentStep);
                                    if(currentStep.equals(FtpActivity.FTP_DELETEFILE_SUCCESS)){
                                        Log.d(TAG, "-----shanchu--success");
                                    } else if(currentStep.equals(FtpActivity.FTP_DELETEFILE_FAIL)){
                                        Log.d(TAG, "-----shanchu--fail");
                                    }
                                }

                            });

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

    }
}
