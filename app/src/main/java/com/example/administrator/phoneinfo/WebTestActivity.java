package com.example.administrator.phoneinfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class WebTestActivity extends AppCompatActivity {

    private MyApplication myApplication;
    private ImageButton butWebTestStart;
    private WebView webViewWebTest;
    private SendRecMessageForWebTest sendRecMessageForWebTest;
    private int testServerId;
    private int testServerStatus=0;  //0表示为停止测试，1表示开始测试
    private String testServerUri="http://sc.seeyouyima.com/forum-iOS-66720669-26BE0138FAF75181_750_1000.jpg";
    private int testServerInterval=3000;
    private int testServerTimes=10;
    private Boolean flagTestPageDestoryed=false;
    private int testTimesCount=0;
    private TextView tvTestCount;
    private TextView tvTestTimes;
    private TextView tvTestResult;
    private TextView tvTestInterval;
    private TextView tvTestUri;
    private TextView tvTestStatus;
    private TextView tvTestCellName;
    private long webLoadTimer=0;
    private long webLoadTimerStart=0;
    private long webLoadTimerEnd=0;
    private String testResult="OK";
    private String cellName="";
    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            try {
                if (msg.what==101) {

                    //JSONObject jsonObj = sendRecMessageForWebTest.getJsonArrayRes().getJSONObject(0);
                    JSONObject jsonObj = jsonArrayRes.getJSONObject(0);
                    testServerId=Integer.parseInt(String.valueOf(jsonObj.get("id")));
                    testServerStatus=Integer.parseInt(String.valueOf(jsonObj.get("testStatus")));
                    testServerUri=String.valueOf(String.valueOf(jsonObj.get("testUri"))).trim();
                    testServerInterval=Integer.parseInt(String.valueOf(jsonObj.get("testInterval")));
                    testServerTimes=Integer.parseInt(String.valueOf(jsonObj.get("testTimes")));

                    tvTestTimes.setText(String.valueOf(testServerTimes));
                    tvTestInterval.setText(String.valueOf(testServerInterval));
                    try{
                        cellName=myApplication.cellInfoList_map.get(0).cellName;
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    tvTestCellName.setText(cellName);
                    tvTestUri.setText(String.valueOf(testServerUri));

                    if(testServerStatus==1){
                        flagTestStart=true;
                        tvTestStatus.setText("正在测试");
                    }else if(testServerStatus==0){
                        flagTestStart=false;
                        tvTestStatus.setText("停止测试");
                    }

                }else if(msg.what==100){
                    webViewWebTest.clearCache(true);
                    webViewWebTest.loadUrl(testServerUri);
                    tvTestCount.setText(String.valueOf(testTimesCount));
                    tvTestResult.setText(String.valueOf(testResult+"  耗时:"+(webLoadTimerEnd-webLoadTimerStart)));

                }
                super.handleMessage(msg);
            } catch(Exception e){
                e.printStackTrace();
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webtest);
        myApplication = (MyApplication) getApplicationContext();
        powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        butWebTestStart=(ImageButton) findViewById(R.id.btn_webtest_webteststart);
        tvTestCount=(TextView)findViewById(R.id.tv_webtest_testcount);
        tvTestTimes=(TextView)findViewById(R.id.tv_webtest_testtimes);
        tvTestResult=(TextView)findViewById(R.id.tv_webtest_testresult);
        tvTestInterval=(TextView)findViewById(R.id.tv_webtest_testinterval);
        tvTestUri=(TextView)findViewById(R.id.tv_webtest_testuri);
        tvTestStatus=(TextView)findViewById(R.id.tv_webtest_teststatus);
        tvTestCellName=(TextView)findViewById(R.id.tv_webtest_testcellname);
        tvTestStatus.setText("请点击右侧图标，开始测试...");

        webViewWebTest=(WebView) findViewById(R.id.webview_webtest);
        WebSettings webSettings = webViewWebTest.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webViewWebTest.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void  onPageStarted(WebView view, String url, Bitmap favicon) {
                //设定加载开始的操作
                webLoadTimerStart=System.currentTimeMillis();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                //设定加载结束的操作
                webLoadTimerEnd=System.currentTimeMillis();
            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
                switch(errorCode)
                {
                    case HttpStatus.SC_NOT_FOUND:
                        //view.loadUrl("file:///android_assets/error_handle.html");
                        testResult="FAIL:404";
                        break;
                }
            }

        });

        butWebTestStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new CheckWebTestServerCmd()).start();
                butWebTestStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_red_pause_48_48));
                //webViewWebTest.loadUrl("http://sc.seeyouyima.com/forum-iOS-66720669-26BE0138FAF75181_750_1000.jpg");


            }
        });

        wakeLock.acquire();
        Toast.makeText(this, "开启屏幕常亮", Toast.LENGTH_LONG).show();

    }

    private Boolean flagTestStart=false;


    class CheckWebTestServerCmd implements Runnable{
        @Override
        public void run() {
            try {
                while(!flagTestPageDestoryed){
                    while(!flagTestStart&&!flagTestPageDestoryed){
                        //sendRecMessageForWebTest = new SendRecMessageForWebTest(myApplication.USER_AUTH_INFO.userName,myApplication.myPhoneInfo1.Imsi,myApplication.myPhoneInfo1.deviceId,"lswts"," ",handler,101);
                        //webTestTimer.schedule(sendRecMessageForWebTest,0);
                        //sendRecMessageForWebTest = new SendRecMessageForWebTest(myApplication.USER_AUTH_INFO.userName,myApplication.myPhoneInfo1.Imsi,myApplication.myPhoneInfo1.deviceId,"lswts"," ",handler,101);

                        sendRecMsg();
                        Thread.sleep(10000);
                        //sendRecMessageForWebTest.cancel();
                    }
                    for(int i=0;i<testServerTimes;i++){
                        if(flagTestPageDestoryed){
                            break;
                        }else{
                            Message msg100 = new Message();
                            msg100.what = 100;   //发送开始导入消息
                            testTimesCount=i+1;
                            handler.sendMessage(msg100);
                            Thread.sleep(testServerInterval);
                        }
                    }
                    sendRecMsg();
                    Thread.sleep(2000);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        flagTestPageDestoryed=true;
        wakeLock.release();
        super.onDestroy();
    }

    public JSONArray jsonArrayRes=new JSONArray();

    public void sendRecMsg() {

        //System.out.println("Client：Connecting");
        //IP地址和端口号（对应服务端），我这的IP是本地路由器的IP地址
        Socket socket = null;
        try {
            //socket = new Socket("10.60.104.109",10086);
            socket = new Socket("120.199.120.82",10087);
            //socket = new Socket("192.168.99.147",10086);
            //发送给服务端的消息
            String message = "ZGQusername="+myApplication.USER_AUTH_INFO.userName+"_ZGQpassword="+myApplication.myPhoneInfo1.Imsi+"_ZGQIMEI="+myApplication.myPhoneInfo1.deviceId.trim()+"_ZGQACT="+"lswts"+"_ZGQADD="+" ";

            //System.out.println("Client Sending: '" + message + "'");
            //第二个参数为True则为自动flush
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(message);

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"GBK"));
            //String receiveMsg = br.readLine();
            String line;
            StringBuilder receiveMsg = new StringBuilder();
            try {
                while ((line = br.readLine()) != null) {
                    // stringBuilder.append(line);
                    receiveMsg.append(line);
                }
                br.close();
                //is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //System.out.println(receiveMsg);
            String strResp=receiveMsg.substring(0,8);
            if(strResp.contains("[{")){
                //收到jason格式数据
                if(jsonArrayRes!=null){
                    jsonArrayRes = new JSONArray(receiveMsg.toString());
                }
            } else{

            }
            Message msg1 = new Message();
            msg1.what = 101;   //发送开始导入消息
            handler.sendMessage(msg1);

            //out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Message msg244 = new Message();
            msg244.what = 244;   //发送错误消息
            handler.sendMessage(msg244);

        }
        finally {
            //关闭Socket
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println("Client:Socket closed");
        }
    }

}

