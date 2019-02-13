package com.example.administrator.phoneinfo;

/**
 * Created by Administrator on 2017/4/7.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;


import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;


public class HttpurlTools {
    /**
     * get方式
     * @param murl
     * @param map
     * @return
     */
    public static int doGet(String murl,Map<String,String> map){
        int code=0;
        StringBuffer sb=new StringBuffer(murl+"?");
        for(String key:map.keySet()){
            String value=map.get(key);
            sb.append(key+"="+value);
            sb.append("&");

        }
        sb.deleteCharAt(sb.length()-1);//截取最後一個&
        String mruls=sb.toString();

        try {
            URL url=new URL(mruls);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
//允許輸入流
            conn.setDoInput(true);
//允許輸出流
//conn.setDoOutput(true);
//get請求方式
            conn.setRequestMethod("GET");
//設置不緩存
            conn.setUseCaches(false);
// 设定传送的内容类型是可序列化的java对象
            // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
            conn.setRequestProperty("Content-type",
                    "application/x-java-serialized-object");
            // 连接，从上述url.openConnection()至此的配置必须要在connect之前完成，
            conn.connect();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            StringBuffer buffer=new StringBuffer();
            String line;
            while((line=br.readLine())!=null){
                buffer.append(line);
            }
            JSONObject jsonObject=new JSONObject(buffer.toString());
            code=jsonObject.getInt("code");



        } catch (MalformedURLException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return code;

    }
    public static int postMethod(String murl,Map<String,String> map){
        int code=0;
        StringBuffer sb=new StringBuffer();
        for(String key:map.keySet()){
            String value=map.get(key);
            sb.append(key+"="+value);
            sb.append("&");
        }
        sb.deleteCharAt(sb.length()-1);
        String params=sb.toString();
        try {


            URL url = new URL(murl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内需要设为true,
            // 默认情况下是false;
            connection.setDoOutput(true);
            // post方式不支持缓存
            connection.setUseCaches(false);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream(), "utf-8"));
            bw.write(params);
            bw.flush();
            bw.close();
            // 调用此方法就不必再使用conn.connect()方法
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpStatus.SC_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "utf-8"));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
                JSONObject jsonObject = new JSONObject(result.toString());
                code = jsonObject.getInt("code");
            } else {
                Log.i("error", "访问失败");
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }
}

