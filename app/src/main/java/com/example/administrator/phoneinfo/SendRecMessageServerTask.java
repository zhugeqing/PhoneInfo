package com.example.administrator.phoneinfo;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/4/10.
 */

class SendRecMessageServerTask extends TimerTask {
    public String mUserName;
    public String mPassword;
    public String mDeviceId;
    public String mAction;
    public String mAddInfo;
    public UserAuthInfo mUserAuthInfo;
    public Handler mHandler;
    public int mMsgNo;
    public JSONArray jsonArrayRes=new JSONArray();

    public SendRecMessageServerTask(String uname, String pwd, String devId, UserAuthInfo userAuInfo, String action,String addinfo, Handler handler, int msgNo){
        mUserName=uname;
        mPassword=pwd;
        mDeviceId=devId;
        mAction=action;  // login 或者 regst(regne新版注册) 或者 schma(查询群主) 或者 crgrp 新建群
        // 或者 allme查询所有成员信息  或者 schme查询成员 或者 addme 增加群成员 delme 删除群成员
        // 或者 lstme 查询个人信息 或者  qutme 退出群 或者 editp修改成员权限 或者 editu修改账号或密码
        // 或者 lplac 查询周边停车位
        // 或者 lswts 查询服务器端web感知测试状态
        mUserAuthInfo=userAuInfo;
        mAddInfo=addinfo;
        mHandler=handler;
        mMsgNo=msgNo;
    }

    public JSONArray getJsonArrayRes(){
        return jsonArrayRes;
    }

    @Override
    public void run() {

        //System.out.println("Client：Connecting");
        //IP地址和端口号（对应服务端），我这的IP是本地路由器的IP地址
        Socket socket = null;
        try {
            //socket = new Socket("10.60.104.109",10086);
            socket = new Socket("120.199.120.82",10086);
            //socket = new Socket("192.168.99.147",10086);
            //发送给服务端的消息
            String message = "ZGQusername="+mUserName+"_ZGQpassword="+mPassword+"_ZGQIMEI="+mDeviceId.trim()+"_ZGQACT="+mAction+"_ZGQADD="+mAddInfo;
            mUserAuthInfo.userName=mUserName;
            mUserAuthInfo.userPassword=mPassword;
            mUserAuthInfo.userImei=mDeviceId.trim();

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
            if(strResp.equals("succ_log")||strResp.equals("succ_reg")){
                mUserAuthInfo.authresult=strResp;
                mUserAuthInfo.vipUser=Integer.parseInt(receiveMsg.substring(receiveMsg.indexOf("_zgqmvip:")+9,receiveMsg.indexOf("_zgqmgpn:")));
                mUserAuthInfo.groupName=receiveMsg.substring(receiveMsg.indexOf("_zgqmgpn:")+9,receiveMsg.indexOf("_zgqmgpAut:"));
                mUserAuthInfo.flagGroupMaster=Integer.parseInt(receiveMsg.substring(receiveMsg.indexOf("_zgqmgpAut:")+11,receiveMsg.indexOf("_zgqmgpAut:")+12));
                mUserAuthInfo.flagGroupCellShare=Integer.parseInt(receiveMsg.substring(receiveMsg.indexOf("_zgqmgpAut:")+12,receiveMsg.indexOf("_zgqmgpAut:")+13));
                mUserAuthInfo.flagGroupLogShare=Integer.parseInt(receiveMsg.substring(receiveMsg.indexOf("_zgqmgpAut:")+13,receiveMsg.indexOf("_zgqmgpAut:")+14));
                mUserAuthInfo.flagGroupMrShare=Integer.parseInt(receiveMsg.substring(receiveMsg.indexOf("_zgqmgpAut:")+14,receiveMsg.indexOf("_zgqmgpAut:")+15));
                mUserAuthInfo.flagGroupUserLayerShare=Integer.parseInt(receiveMsg.substring(receiveMsg.indexOf("_zgqmgpAut:")+15,receiveMsg.indexOf("_zgqmgpAut:")+16));
                mUserAuthInfo.userId=Integer.parseInt(receiveMsg.substring(receiveMsg.indexOf("_zgqmgpId:")+10));

            }else if(strResp.equals("succ_sch")){
                mUserAuthInfo.masterName=receiveMsg.substring(receiveMsg.indexOf("_zgqmast:")+9);
            }else if(strResp.contains("[{")){
                //收到jason格式数据
                if(jsonArrayRes!=null){
                    jsonArrayRes = new JSONArray(receiveMsg.toString());
                    mUserAuthInfo.authresult=strResp;
                }
            } else{
                mUserAuthInfo.authresult=strResp;
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
