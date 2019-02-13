package com.example.administrator.phoneinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by lenovo on 2016/2/23.
 */
public class MyUdpClient implements Runnable{
    final static int udpPort = 10087;
    final static String hostIp = "120.199.120.82";
    private static DatagramSocket socket = null;
    private static DatagramPacket packetSend,packetRcv;
    private boolean udpLife = true; //udp生命线程
    private byte[] msgRcv = new byte[1024]; //接收消息
    private boolean flagSendGpsOn=false;
    private boolean flagUdpThreadSleepOn=true;

    String RcvMsg;
    private Context mContext;
    private Handler mHandler;
    public String nbImei="";
    public double lon=0;
    public double lat=0;
    public MyApplication my_Application;

    public MyUdpClient(Context context, Handler handler,MyApplication myApplication){
        super();
        mContext=context;
        mHandler=handler;
        my_Application=myApplication;
    }

    //返回udp生命线程因子是否存活
    public boolean isUdpLife(){
        if (udpLife){
            return true;
        }

        return false;
    }

    //更改UDP生命线程因子
    public void setUdpLife(boolean b){
        udpLife = b;
    }

    public void setFlagSendGpsOn(boolean b){
        flagSendGpsOn = b;
    }

    public void setFlagUdpThreadSleepOn(boolean b){
        flagUdpThreadSleepOn = b;
    }
    public String getRcvMsg(){
        return RcvMsg;
    }

    //发送消息
    public String send(String msgSend){
        InetAddress hostAddress = null;

        try {
            hostAddress = InetAddress.getByName(hostIp);
        } catch (UnknownHostException e) {
            Log.i("udpClient","未找到服务器");
            e.printStackTrace();
        }

/*        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            Log.i("udpClient","建立发送数据报失败");
            e.printStackTrace();
        }*/

        packetSend = new DatagramPacket(msgSend.getBytes() , msgSend.getBytes().length,hostAddress,udpPort);

        try {
            socket.send(packetSend);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("udpClient","发送失败");
        }
        //   socket.close();
        return msgSend;
    }

    @Override
    public void run() {

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(60000);//设置超时为60s
            send("GetNbTermsList");
        } catch (SocketException e) {
            Log.i("udpClient","建立接收数据报失败");
            e.printStackTrace();
        }
        packetRcv = new DatagramPacket(msgRcv,msgRcv.length);
        while (udpLife){
            try {
                Log.i("udpClient", "UDP监听");
                socket.receive(packetRcv);
                String tmpRcvMsg = new String(packetRcv.getData(),packetRcv.getOffset(),packetRcv.getLength());
                System.out.println("RcvMsg:"+tmpRcvMsg);
                //将收到的消息发给主界面
                //Intent RcvIntent = new Intent();
                //RcvIntent.setAction("udpRcvMsg");
                //RcvIntent.putExtra("udpRcvMsg", RcvMsg);
                //mContext.sendBroadcast(RcvIntent);
                if (tmpRcvMsg.contains("NbTermsList:")){
                    RcvMsg=new String(tmpRcvMsg);
                    Message msg601=new Message();
                    msg601.what=601;
                    mHandler.sendMessage(msg601);
                    break;
                }

                //Log.i("Rcv",RcvMsg);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        while(flagUdpThreadSleepOn){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (flagSendGpsOn){
            try {
                lon=my_Application.newGpsLong;
                lat=my_Application.newGpsLat;
                Log.i("udpClient", "发送经纬度:"+"shareGps:"+nbImei+","+lon+","+lat);
                send("shareGps:"+nbImei+","+lon+","+lat);
                Thread.sleep(3000);

                //Log.i("Rcv",RcvMsg);
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }

        Log.i("udpClient","UDP监听关闭");
        socket.close();
    }
}
