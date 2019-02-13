package com.example.administrator.phoneinfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.administrator.phoneinfo.GroupManagerActivity.isContainChinese;

/**
 * Created by Administrator on 2017/4/7.
 */

public class LoginSocketActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    private MyApplication myApplication;

    private SendRecMessageServerTask mSendRecMessage;

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            try {
                if (msg.what == 201) {
                    if (myApplication.USER_AUTH_INFO.authresult.equals("fail_log")) {
                        Toast.makeText(LoginSocketActivity.this, myApplication.USER_AUTH_INFO.authresult + "登录失败！请检查用户名或者账号是否正确。如果您更换了一台终端，需要重新注册！", Toast.LENGTH_LONG).show();
                    }
                    else if (myApplication.USER_AUTH_INFO.authresult.contains("[{")) {
                        //新版用户注册后收到的jason格式用户数据
                        JSONObject jsonObj = mSendRecMessage.getJsonArrayRes().getJSONObject(0);

                        myApplication.USER_AUTH_INFO.userId=Integer.parseInt(String.valueOf(jsonObj.get("id")));
                        //myApplication.USER_AUTH_INFO.userName=String.valueOf(jsonObj.get("username"));
                        //myApplication.USER_AUTH_INFO.userPassword=String.valueOf(jsonObj.get("userpassword"));
                        myApplication.USER_AUTH_INFO.vipUser=Integer.parseInt(String.valueOf(jsonObj.get("vipuser")));
                        myApplication.USER_AUTH_INFO.groupName=String.valueOf(jsonObj.get("groupname"));

                        myApplication.cellInfoFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/cellinfo/";
                        myApplication.mrFilesFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/MR_all/";
                        myApplication.logFilesFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/log/";
                        myApplication.layerFilesFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/layer_files/";

                        //myApplication.USER_AUTH_INFO.userImei=String.valueOf(jsonObj.get("userImei"));
                        myApplication.USER_AUTH_INFO.flagGroupMaster=Integer.parseInt(String.valueOf(jsonObj.get("groupmaster")));
                        myApplication.USER_AUTH_INFO.flagGroupCellShare=Integer.parseInt(String.valueOf(jsonObj.get("group_cellshare")));
                        myApplication.USER_AUTH_INFO.flagGroupLogShare=Integer.parseInt(String.valueOf(jsonObj.get("group_logshare")));
                        myApplication.USER_AUTH_INFO.flagGroupMrShare=Integer.parseInt(String.valueOf(jsonObj.get("group_mrshare")));
                        myApplication.USER_AUTH_INFO.flagGroupUserLayerShare=Integer.parseInt(String.valueOf(jsonObj.get("group_ulayershare")));
                        myApplication.USER_AUTH_INFO.flagGroupModCell=Integer.parseInt(String.valueOf(jsonObj.get("group_modifycellinfo")));
                        myApplication.USER_AUTH_INFO.vipRemainDays=Integer.parseInt(String.valueOf(jsonObj.get("vip_remaindays")));

                        List<String> keys = new ArrayList<>();
                        List<String> values = new ArrayList<>();
                        keys.add("key_username");
                        keys.add("key_password");
                        keys.add("key_imei");
                        values.add(String.valueOf(jsonObj.get("username")));
                        values.add(String.valueOf(jsonObj.get("userpassword")));
                        values.add(String.valueOf(jsonObj.get("userImei")));
                        AppSettingActivity.saveSettingInfo(LoginSocketActivity.this, "userInfo", keys, values);
                        String vipinfo = "";
                        if (myApplication.USER_AUTH_INFO.vipUser == 1) {
                            vipinfo = "VIP";
                        } else {
                            vipinfo = "体验";
                        }
                        myApplication.flagUserLoginSucc=true;
                        AlertDialog.Builder dlg = new AlertDialog.Builder(LoginSocketActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("提示");
                        dlg.setMessage("注册成功！欢迎您，尊敬的" + vipinfo + "用户" + myApplication.USER_AUTH_INFO.userName+"！您当前的VIP有效期还剩"+myApplication.USER_AUTH_INFO.vipRemainDays+"天。");
                        dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(LoginSocketActivity.this, MainActivity.class));
                            }
                        });
                        dlg.show();
                    }
                    else if (myApplication.USER_AUTH_INFO.authresult.equals("succ_reg")) {
                        //老版用户注册后收到的用户数据
                        List<String> keys = new ArrayList<>();
                        List<String> values = new ArrayList<>();
                        keys.add("key_username");
                        keys.add("key_password");
                        keys.add("key_imei");
                        values.add(myApplication.USER_AUTH_INFO.userName);
                        values.add(myApplication.USER_AUTH_INFO.userPassword);
                        values.add(myApplication.myPhoneInfo1.deviceId);
                        AppSettingActivity.saveSettingInfo(LoginSocketActivity.this, "userInfo", keys, values);

                        myApplication.cellInfoFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/cellinfo/";
                        myApplication.mrFilesFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/MR_all/";
                        myApplication.logFilesFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/log/";
                        myApplication.layerFilesFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/layer_files/";
                        myApplication.flagUserLoginSucc=true;
                        AlertDialog.Builder dlg = new AlertDialog.Builder(LoginSocketActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("提示");
                        dlg.setMessage("注册成功！欢迎您，尊敬的" + myApplication.USER_AUTH_INFO.userName + "！您现在使用的是体验版，大部分重要功能将无法使用，如需解锁，请致电13868808810");
                        dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(LoginSocketActivity.this, MainActivity.class));
                            }
                        });
                        dlg.show();
                    }
                    else if (myApplication.USER_AUTH_INFO.authresult.equals("succ_log")) {

                        List<String> keys = new ArrayList<>();
                        List<String> values = new ArrayList<>();
                        keys.add("key_username");
                        keys.add("key_password");
                        keys.add("key_imei");
                        values.add(myApplication.USER_AUTH_INFO.userName);
                        values.add(myApplication.USER_AUTH_INFO.userPassword);
                        values.add(myApplication.myPhoneInfo1.deviceId);
                        AppSettingActivity.saveSettingInfo(LoginSocketActivity.this, "userInfo", keys, values);

                        String vipinfo = "";
                        if (myApplication.USER_AUTH_INFO.vipUser == 1) {
                            vipinfo = "VIP";
                        } else {
                            vipinfo = "体验";
                        }
                        myApplication.flagUserLoginSucc=true;
                        AlertDialog.Builder dlg = new AlertDialog.Builder(LoginSocketActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("提示");
                        dlg.setMessage("登录成功！欢迎您，尊敬的" + vipinfo + "用户" + myApplication.USER_AUTH_INFO.userName);
                        dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(LoginSocketActivity.this, MainActivity.class));
                            }
                        });
                        dlg.show();
                    }
                    else if (myApplication.USER_AUTH_INFO.authresult.equals("fail_reg")) {
                        Toast.makeText(LoginSocketActivity.this, myApplication.USER_AUTH_INFO.authresult + "注册失败！本终端注册过网优云图用户，请使用正确的用户名和密码登陆！如果您忘记了用户名或密码，请联系13868808810取回！", Toast.LENGTH_LONG).show();
                    }
                }else if(msg.what==202){
                    if (myApplication.USER_AUTH_INFO.authresult.contains("[{")) {
                        //新版用户登陆收到的jason格式用户数据

                        List<String> keys = new ArrayList<>();
                        List<String> values = new ArrayList<>();
                        keys.add("key_username");
                        keys.add("key_password");
                        keys.add("key_imei");
                        values.add(myApplication.USER_AUTH_INFO.userName);
                        values.add(myApplication.USER_AUTH_INFO.userPassword);
                        values.add(myApplication.myPhoneInfo1.deviceId);
                        AppSettingActivity.saveSettingInfo(LoginSocketActivity.this, "userInfo", keys, values);

                        JSONObject jsonObj = mSendRecMessage.getJsonArrayRes().getJSONObject(0);

                        myApplication.USER_AUTH_INFO.userId=Integer.parseInt(String.valueOf(jsonObj.get("id")));
                        //myApplication.USER_AUTH_INFO.userName=String.valueOf(jsonObj.get("username"));
                        //myApplication.USER_AUTH_INFO.userPassword=String.valueOf(jsonObj.get("userpassword"));
                        myApplication.USER_AUTH_INFO.vipUser=Integer.parseInt(String.valueOf(jsonObj.get("vipuser")));
                        myApplication.USER_AUTH_INFO.groupName=String.valueOf(jsonObj.get("groupname"));

                        myApplication.cellInfoFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/cellinfo/";
                        myApplication.mrFilesFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/MR_all/";
                        myApplication.logFilesFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/log/";
                        myApplication.layerFilesFtpDir = "/" + myApplication.USER_AUTH_INFO.groupName + "/layer_files/";

                        //myApplication.USER_AUTH_INFO.userImei=String.valueOf(jsonObj.get("userImei"));
                        myApplication.USER_AUTH_INFO.flagGroupMaster=Integer.parseInt(String.valueOf(jsonObj.get("groupmaster")));
                        myApplication.USER_AUTH_INFO.flagGroupCellShare=Integer.parseInt(String.valueOf(jsonObj.get("group_cellshare")));
                        myApplication.USER_AUTH_INFO.flagGroupLogShare=Integer.parseInt(String.valueOf(jsonObj.get("group_logshare")));
                        myApplication.USER_AUTH_INFO.flagGroupMrShare=Integer.parseInt(String.valueOf(jsonObj.get("group_mrshare")));
                        myApplication.USER_AUTH_INFO.flagGroupUserLayerShare=Integer.parseInt(String.valueOf(jsonObj.get("group_ulayershare")));
                        myApplication.USER_AUTH_INFO.flagGroupModCell=Integer.parseInt(String.valueOf(jsonObj.get("group_modifycellinfo")));
                        myApplication.USER_AUTH_INFO.vipRemainDays=Integer.parseInt(String.valueOf(jsonObj.get("vip_remaindays")));
                        myApplication.flagUserLoginSucc=true;
                        String vipinfo = "";
                        if (myApplication.USER_AUTH_INFO.vipUser == 1) {
                            vipinfo = "VIP";
                        } else {
                            vipinfo = "体验";
                        }
                        myApplication.flagUserLoginSucc=true;
                        AlertDialog.Builder dlg = new AlertDialog.Builder(LoginSocketActivity.this);
                        dlg.setCancelable(false);
                        dlg.setTitle("提示");
                        dlg.setMessage("登录成功！欢迎您，尊敬的" + vipinfo + "用户" + myApplication.USER_AUTH_INFO.userName+"！您当前的VIP有效期还剩"+myApplication.USER_AUTH_INFO.vipRemainDays+"天。");
                        dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(LoginSocketActivity.this, MainActivity.class));
                            }
                        });
                        dlg.show();

                    }else if (myApplication.USER_AUTH_INFO.authresult.equals("fail_log")) {
                        Toast.makeText(LoginSocketActivity.this, myApplication.USER_AUTH_INFO.authresult + "登录失败！请检查用户名或者账号是否正确。如果您更换了一台终端，需要重新注册！", Toast.LENGTH_LONG).show();
                    }
                }
                super.handleMessage(msg);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private EditText etUserName,etUserPassWord;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myApplication=(MyApplication) getApplicationContext();
        etUserName=(EditText)findViewById(R.id.et_name);
        etUserPassWord=(EditText)findViewById(R.id.et_pwd);
        List<String> userInfoKeys = AppSettingActivity.readAllSetKey(this, "userInfo");
        List<String> userInfoValues = AppSettingActivity.readAllSetValue(this, "userInfo");
        for (int i = 0; i < userInfoKeys.size(); i++) {
            if (userInfoKeys.get(i).equals("key_username")) {
                etUserName.setText(userInfoValues.get(i));
            } else if (userInfoKeys.get(i).equals("key_password")) {
                etUserPassWord.setText(userInfoValues.get(i));
            }
        }

        Button buttonLogin = (Button) this.findViewById(R.id.btn_login);
        Button buttonRegst = (Button) this.findViewById(R.id.btn_regist);
        Button buttonEditPsw = (Button) this.findViewById(R.id.btn_login_editpsw);

        buttonEditPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(LoginSocketActivity.this);
                final View textEntryView = factory.inflate(R.layout.editpsw_input_dialog, null);
                final EditText editTextUserName = (EditText) textEntryView.findViewById(R.id.et_login_dialog_username);
                final EditText editTextPassword = (EditText)textEntryView.findViewById(R.id.et_login_dialog_password);
                AlertDialog.Builder ad1 = new AlertDialog.Builder(LoginSocketActivity.this);
                ad1.setCancelable(false);
                ad1.setTitle("提示:");
                ad1.setView(textEntryView);
                ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        try {
                            String newUserName = editTextUserName.getText().toString().trim();
                            String newPassword = editTextPassword.getText().toString().trim();
                            if(newUserName.equals("")||newPassword.equals("")){
                                Toast.makeText(LoginSocketActivity.this,"用户名与密码不能为空，请输入用户名及密码!",Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(!GroupManagerActivity.userInputCheck(newUserName)||!GroupManagerActivity.userInputCheck(newPassword)){
                                Toast.makeText(LoginSocketActivity.this,"输入格式不正确，请重新输入！",Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(isContainChinese(newUserName)||isContainChinese(newPassword)){
                                Toast.makeText(LoginSocketActivity.this, "账号和密码暂不支持中文字符，请重新输入！", Toast.LENGTH_LONG).show();
                                return;
                            }else if(newUserName.length()<3||newPassword.length()<3){
                                Toast.makeText(LoginSocketActivity.this, "账号或密码太短啦，至少3个字符以上，请重新输入！", Toast.LENGTH_LONG).show();
                                return;
                            }

                            Timer editPswTimer = new Timer();
                            SendRecMessageServerTask editPswTask = new SendRecMessageServerTask(newUserName,newPassword,myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"editu"," ",handler,202);
                            editPswTimer.schedule(editPswTask,0);
                            mSendRecMessage=editPswTask;

                            myApplication.USER_AUTH_INFO.userName=newUserName;
                            myApplication.USER_AUTH_INFO.userPassword=newPassword;
                            etUserName.setText(newUserName);
                            etUserPassWord.setText(newPassword);

                        }
                        catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(LoginSocketActivity.this,"修改失败，请重新修改！",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                ad1.setNegativeButton("取消", null);
                ad1.show();// 显示对话框
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tpname=etUserName.getText().toString().trim();
                String tppsw=etUserPassWord.getText().toString().trim();
                if(tpname.equals("")||tppsw.equals("")){
                    Toast.makeText(LoginSocketActivity.this,"用户名或密码不能为空，请输入用户名及密码!",Toast.LENGTH_SHORT).show();
                    return;
                }
                myApplication.USER_AUTH_INFO.userName=etUserName.getText().toString().trim();
                myApplication.USER_AUTH_INFO.userPassword=etUserPassWord.getText().toString().trim();
                myApplication.USER_AUTH_INFO.userImei=myApplication.myPhoneInfo1.deviceId;
                try{
                    Timer loginTimer = new Timer();
                    SendRecMessageServerTask loginTask = new SendRecMessageServerTask(tpname,tppsw,myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"logne"," ",handler,202);
                    loginTimer.schedule(loginTask,0);
                    mSendRecMessage=loginTask;
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        buttonRegst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tpname=etUserName.getText().toString().trim();
                String tppsw=etUserPassWord.getText().toString().trim();
                if(tpname.equals("")||tppsw.equals("")){
                    Toast.makeText(LoginSocketActivity.this,"用户名或密码不能为空，请输入用户名及密码!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isContainChinese(tpname)||isContainChinese(tppsw)){
                    Toast.makeText(LoginSocketActivity.this, "账号和密码暂不支持中文字符，请重新输入！", Toast.LENGTH_LONG).show();
                    return;
                }else if(tpname.length()<3||tppsw.length()<3){
                    Toast.makeText(LoginSocketActivity.this, "账号或密码太短啦，至少3个字符以上，请重新输入！", Toast.LENGTH_LONG).show();
                    return;
                }

                myApplication.USER_AUTH_INFO.userName=etUserName.getText().toString().trim();
                myApplication.USER_AUTH_INFO.userPassword=etUserPassWord.getText().toString().trim();
                myApplication.USER_AUTH_INFO.userImei=myApplication.myPhoneInfo1.deviceId;
                try{
                    Timer regstTimer = new Timer();
                    SendRecMessageServerTask regstTask = new SendRecMessageServerTask(tpname,tppsw,myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"regne"," ",handler,201);
                    regstTimer.schedule(regstTask,0);
                    mSendRecMessage=regstTask;
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }



}



