package com.example.administrator.phoneinfo;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/4/10.
 */

class SendRecMessageForWebTest {
    public String mUserName;
    public String mImsi;
    public String mDeviceId;
    public String mAction;
    public String mAddInfo;
    //public UserAuthInfo mUserAuthInfo;
    public Handler mHandler;
    public int mMsgNo;
    public JSONArray jsonArrayRes=new JSONArray();

    public SendRecMessageForWebTest(String uname, String devImsi, String devId, String action, String addinfo, Handler handler, int msgNo){
        mUserName=uname;
        mImsi=devImsi;
        mDeviceId=devId;
        mAction=action;
        //  lswts 查询服务器端web感知测试状态
        //mUserAuthInfo=userAuInfo;
        mAddInfo=addinfo;
        mHandler=handler;
        mMsgNo=msgNo;
    }

    public JSONArray getJsonArrayRes(){
        return jsonArrayRes;
    }

    public void sendRecMsg() {

        //System.out.println("Client：Connecting");
        //IP地址和端口号（对应服务端），我这的IP是本地路由器的IP地址
        Socket socket = null;
        try {
            //socket = new Socket("10.60.104.109",10086);
            socket = new Socket("120.199.120.82",10087);
            //socket = new Socket("192.168.99.147",10086);
            //发送给服务端的消息
            String message = "ZGQusername="+mUserName+"_ZGQpassword="+mImsi+"_ZGQIMEI="+mDeviceId.trim()+"_ZGQACT="+mAction+"_ZGQADD="+mAddInfo;

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
            msg1.what = mMsgNo;   //发送开始导入消息
            mHandler.sendMessage(msg1);

            //out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Message msg244 = new Message();
            msg244.what = 244;   //发送错误消息
            mHandler.sendMessage(msg244);

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
