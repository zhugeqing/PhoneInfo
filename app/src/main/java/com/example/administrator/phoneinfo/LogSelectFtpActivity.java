package com.example.administrator.phoneinfo;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LogSelectFtpActivity extends AppCompatActivity {

    private MyApplication myApplication;
    private List<String> logFileName=new ArrayList<>();
    private LinearLayout linearLayoutLogFiles;
    private List<CheckBox> checkboxLogFiles=new ArrayList<>();
    private Button buttonLogSelect;

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
           if (msg.what==8) {
                for(int i=0;i<logFileName.size();i++){
                    CheckBox checkBoxMr=new CheckBox(LogSelectFtpActivity.this);
                    checkBoxMr.setText(logFileName.get(i));
                    checkBoxMr.setChecked(false);
                    checkboxLogFiles.add(checkBoxMr);
                    linearLayoutLogFiles.addView(checkBoxMr);
                }
            }else if(msg.what==3){
               Intent intent1 = new Intent(LogSelectFtpActivity.this, MapViewActivity.class);
               setResult(RESULT_OK,intent1); //这理有2个参数(int resultCode, Intent intent)
               finish();
           }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logselectftp);
        myApplication = (MyApplication) getApplicationContext();
        linearLayoutLogFiles=(LinearLayout)findViewById(R.id.linearlayout_logfiles);
        buttonLogSelect=(Button)findViewById(R.id.button_logselect);
        buttonLogSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myApplication.USER_AUTH_INFO.vipUser==0){
                    Toast.makeText(LogSelectFtpActivity.this,"非VIP用户不支持LOG云服务功能，请联系13868808810开通VIP功能",Toast.LENGTH_LONG).show();
                    return;
                }

                if(checkboxLogFiles.size()>0){
                    for(int i=0;i<checkboxLogFiles.size();i++){
                        if(checkboxLogFiles.get(i).isChecked()){
                            myApplication.ftpSelectlogFileName=logFileName.get(i);
                            break;
                        }
                    }
                    if(!myApplication.ftpSelectlogFileName.equals("")){
                        Toast.makeText(LogSelectFtpActivity.this,"正在下载log文件:"+myApplication.ftpSelectlogFileName,Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                MyFTP myFTP=new MyFTP();
                                String ftpdir=myApplication.logFilesFtpDir;
                                String savepath=myApplication.logFileSavePath;
                                String filename=myApplication.ftpSelectlogFileName;
                                myFTP.downloadSingleFile(ftpdir,savepath,filename,new MyFTP.DownLoadProgressListener() {
                                    @Override
                                    public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                        //System.out.println("正在下载");
                                        //return;
                                    }
                                });
                                Thread.sleep(1000);
                                Message msg1 = new Message(); //发送开始显示消息
                                msg1.what = 3;
                                handler.sendMessage(msg1);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                System.out.println("LOG下载失败！");
                            }
                        }
                    }).start();
                    }

                }else{
                    Toast.makeText(LogSelectFtpActivity.this,"当前服务器上没有LOG文件，请先上传LOG！",Toast.LENGTH_LONG).show();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    FTPFile[] logFiles=new MyFTP().listFtpServerFiles(myApplication.logFilesFtpDir);
                    for(int i=0;i<logFiles.length;i++){
                        logFileName.add(logFiles[i].getName());
                    }
                    if(logFileName.size()>0){
                        Message msg1 = new Message(); //发送开始显示消息
                        msg1.what = 8;
                        handler.sendMessage(msg1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if(logFileName.size()>0){
                        Message msg1 = new Message(); //发送开始显示消息
                        msg1.what = 8;
                        handler.sendMessage(msg1);
                    }
                }
            }
        }).start();


    }
}

