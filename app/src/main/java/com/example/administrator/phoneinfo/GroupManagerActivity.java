package com.example.administrator.phoneinfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.TEXT_ALIGNMENT_TEXT_END;

public class GroupManagerActivity extends AppCompatActivity implements View.OnClickListener {
    private MyApplication myApplication;
    private TextView textViewGroupName;
    private TextView textViewGroupMaster;
    private TextView textViewMemberNums;
    private TextView textViewUserName;
    private TextView textViewVipUser;
    private TextView textViewVipRemaindays;
    private TextView textViewLoginState;
    private TextView textViewLoginOut;

    private Button buttonCreateGroup;
    private Button buttonMemberManager;
    private Button buttonFilesManager;
    private Button buttonQuitGroup;
    private Button buttonAddMember;
    private Button buttonDeleteMember;
    private LinearLayout linearLayoutGroupManager;
    private LinearLayout linearLayoutGroupContent;
    private LinearLayout linearLayoutGroupMembers;
    //private JSONArray memberJsonArray=new JSONArray();
    private SendRecMessageServerTask searchMemberTask;
    private int indexMember;
    private int indexAuthority;
    private List<List<String>> memberAuthorityArray=new ArrayList<>();
    private List<String> memberUserNameList=new ArrayList<>();
    private List<String> memberIdList=new ArrayList<>();
    private List<CheckBox> memberCheckBoxList= new ArrayList<>();
    private String usernameDel="";
    private List<String> ftpGroupNames=new ArrayList<>();
    private int numsOfGroup=0;

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            try {
                if (msg.what == 201) {
                    textViewGroupMaster.setText(myApplication.USER_AUTH_INFO.masterName);
                }else if(msg.what == 212) {
                    //myApplication.USER_AUTH_INFO.masterName="";
                    //textViewGroupMaster.setText("");
                    //textViewGroupName.setText(groupNameShow(myApplication.USER_AUTH_INFO.groupName));
                    Toast.makeText(GroupManagerActivity.this,"成功退出群！",Toast.LENGTH_LONG).show();
                }else if(msg.what == 211) {
                    Toast.makeText(GroupManagerActivity.this, "群创建成功，现在可以邀请其他用户加入啦！", Toast.LENGTH_LONG).show();
                    textViewGroupName.setText(groupNameShow(myApplication.USER_AUTH_INFO.groupName));
                    textViewGroupMaster.setText(myApplication.USER_AUTH_INFO.userName);
                }else if(msg.what == 214) {
                    Toast.makeText(GroupManagerActivity.this,"群创建失败！",Toast.LENGTH_LONG).show();
                    textViewGroupMaster.setText(myApplication.USER_AUTH_INFO.masterName);
                } else if(msg.what==244){
                    Toast.makeText(GroupManagerActivity.this, "查找结果为空！", Toast.LENGTH_SHORT).show();
                }
                else if (msg.what == 203) {

                    JSONArray userJsonArray=searchMemberTask.getJsonArrayRes();
                    final String[] users=new String[userJsonArray.length()];
                    final String[] usersId= new String[userJsonArray.length()];
                    final String[] usersGroup= new String[userJsonArray.length()];
                    for (int i = 0; i < userJsonArray.length(); i++) {
                        JSONObject jsonObj = userJsonArray.getJSONObject(i);
                        usersId[i]=String.valueOf(jsonObj.get("id"));
                        users[i]=String.valueOf(jsonObj.get("username")+"  群:"+groupNameShow(String.valueOf(jsonObj.get("groupname"))));
                        usersGroup[i]=String.valueOf(jsonObj.get("groupname"));
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupManagerActivity.this);
                    builder.setTitle("请选择用户");
                    builder.setCancelable(false);
                    builder.setSingleChoiceItems(users, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String str = users[which];
                            if(memberIdList.contains(usersId[which])){
                                Toast.makeText(GroupManagerActivity.this, "该用户已经在本群中...", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(!usersGroup[which].equals("wzmcc_cellinfo/APP_DEFAULT")&&!usersGroup[which].equals("")){
                                Toast.makeText(GroupManagerActivity.this, "该用户在"+usersGroup[which]+"群中，需要他先退群之后，再将他加入本群！", Toast.LENGTH_LONG).show();
                                return;
                            }

                            Timer addMemberTimer = new Timer();
                            searchMemberTask = new SendRecMessageServerTask(myApplication.USER_AUTH_INFO.userName,myApplication.USER_AUTH_INFO.userPassword,myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"addme",usersId[which],handler,202);
                            addMemberTimer.schedule(searchMemberTask,0);
                            dialog.dismiss();
                            Toast.makeText(GroupManagerActivity.this, "成功添加！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else if (msg.what == 202||msg.what == 205) {

                    if(msg.what == 205){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<String> newFtpPaths=new ArrayList<String>();
                                newFtpPaths.add("/"+myApplication.USER_AUTH_INFO.groupName+"/");
                                newFtpPaths.add("/"+myApplication.USER_AUTH_INFO.groupName+"/cellinfo/");
                                newFtpPaths.add("/"+myApplication.USER_AUTH_INFO.groupName+"/MR_all/");
                                newFtpPaths.add("/"+myApplication.USER_AUTH_INFO.groupName+"/log/");
                                newFtpPaths.add("/"+myApplication.USER_AUTH_INFO.groupName+"/layer_files/");
                                MyFTP myFTP=new MyFTP();
                                if(myFTP.ftpMakeMultiDirectory(newFtpPaths)){

                                    myApplication.cellInfoFtpDir="/"+myApplication.USER_AUTH_INFO.groupName+"/cellinfo/";
                                    myApplication.mrFilesFtpDir="/"+myApplication.USER_AUTH_INFO.groupName+"/MR_all/";
                                    myApplication.logFilesFtpDir="/"+myApplication.USER_AUTH_INFO.groupName+"/log/";
                                    myApplication.layerFilesFtpDir="/"+myApplication.USER_AUTH_INFO.groupName+"/layer_files/";
                                    Message msg211 = new Message();
                                    msg211.what = 211;   //发送开始导入消息
                                    handler.sendMessage(msg211);
                                    try {
                                        List<String> sourceDirs=new ArrayList<String>();
                                        List<String> targetDirs=new ArrayList<String>();
                                        sourceDirs.add("/wzmcc_cellinfo/APP_DEFAULT/cellinfo/");
                                        sourceDirs.add("/wzmcc_cellinfo/APP_DEFAULT/MR_all/");
                                        targetDirs.add(myApplication.cellInfoFtpDir);
                                        targetDirs.add(myApplication.mrFilesFtpDir);
                                        //myFTP.copyFtpMultiDirectiory(sourceDirs,targetDirs);
                                        myFTP.copyFtpDirectiory(sourceDirs.get(0),targetDirs.get(0));
                                        Thread.sleep(5000);
                                        //MyFTP myFTP1=new MyFTP();
                                        myFTP.copyFtpDirectiory(sourceDirs.get(1),targetDirs.get(1));


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }else{
                                    Message msg214 = new Message();
                                    msg214.what = 214;   //发送开始导入消息
                                    handler.sendMessage(msg214);
                                    //Toast.makeText(GroupManagerActivity.this,"群文件空间创建失败！",Toast.LENGTH_LONG).show();
                                    myApplication.USER_AUTH_INFO.groupName="wzmcc_cellinfo/APP_DEFAULT";
                                    myApplication.USER_AUTH_INFO.flagGroupMaster=0;
                                }
                            }
                        }).start();
                    }

                    linearLayoutGroupMembers.removeAllViews();
                    memberAuthorityArray.clear();
                    memberUserNameList.clear();
                    memberIdList.clear();
                    memberCheckBoxList.clear();

                    JSONArray memberJsonArray=searchMemberTask.getJsonArrayRes();
                    textViewMemberNums.setText("共"+memberJsonArray.length()+"人");
                    numsOfGroup=memberJsonArray.length();
                    final String[] authorityNames={"群主","联网更新基站信息表","共享测试LOG","共享MR文件","共享自定义图层","提交修改基站信息"};
                    for (int i = 0; i < memberJsonArray.length(); i++) {
                        JSONObject jsonObj = memberJsonArray.getJSONObject(i);
                        memberIdList.add(String.valueOf(jsonObj.get("id")));
                        memberUserNameList.add(String.valueOf(jsonObj.get("username")));
                        CheckBox checkBoxGroupMember = new CheckBox(GroupManagerActivity.this);
                        checkBoxGroupMember.setText(String.valueOf(jsonObj.get("username")));
                        checkBoxGroupMember.setTextSize(12);
                        checkBoxGroupMember.setChecked(false);
                        checkBoxGroupMember.setId(i);
                        /*
                        checkBoxLayer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int s = v.getId();
                                if (((CheckBox) v).isChecked()) {
                                    //int s=v.getId();
                                    userLayerList.get(s).flagUserLayerShow = true;
                                    userLayerList.get(s).userLayerShowTimer = new Timer();
                                    userLayerList.get(s).userLayerShowTask = new MapViewActivity.UserLayerShowTask(userLayerList.get(s).userLayerName, userLayerList.get(s).userLayerFieldName_lon, userLayerList.get(s).userLayerFieldName_lat, userLayerList.get(s).userLayerInfoFieldNameList, userLayerList.get(s).tempUserLayerPolylines, userLayerList.get(s).userLayerIcon, userLayerList.get(s).userLayerIconColor, userLayerList.get(s).userLayerIconSize, userLayerList.get(s).userLayerIndex, userLayerList.get(s).userLayerType);
                                    userLayerList.get(s).userLayerShowTimer.schedule(userLayerList.get(s).userLayerShowTask, 0);

                                } else {
                                    //int s=v.getId();
                                    userLayerList.get(s).flagUserLayerShow = false;
                                    if (userLayerList.get(s).tempUserLayerPolylines != null) {
                                        for (int j = 0; j < userLayerList.get(s).tempUserLayerPolylines.size(); j++) {
                                            userLayerList.get(s).tempUserLayerPolylines.get(j).remove();
                                        }
                                        userLayerList.get(s).tempUserLayerPolylines.clear();
                                    }
                                }
                                mapLayerControlTb.setVisibility(View.GONE);
                                mapMenuShow = false;
                            }
                        });
                        */

                        //checkBoxListUserLayer.add(checkBoxLayer);
                        TableRow tableRow = new TableRow(GroupManagerActivity.this);
                        checkBoxGroupMember.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,0.85f));
                        //tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        memberCheckBoxList.add(checkBoxGroupMember);
                        tableRow.addView(checkBoxGroupMember);

                        List<String> memAthorityList=new ArrayList<>();
                        memAthorityList.add(String.valueOf(jsonObj.get("groupmaster")));
                        memAthorityList.add(String.valueOf(jsonObj.get("group_cellshare")));
                        memAthorityList.add(String.valueOf(jsonObj.get("group_logshare")));
                        memAthorityList.add(String.valueOf(jsonObj.get("group_mrshare")));
                        memAthorityList.add(String.valueOf(jsonObj.get("group_ulayershare")));
                        memAthorityList.add(String.valueOf(jsonObj.get("group_modifycellinfo")));
                        memberAuthorityArray.add(memAthorityList);

                        for (int j=0;j<memAthorityList.size();j++) {

                            ImageButton buttonAuth = new ImageButton(GroupManagerActivity.this);
                            if (memAthorityList.get(j).equals("1")) {
                                buttonAuth.setImageDrawable(getResources().getDrawable(R.drawable.ok_26_zgqpic));
                            } else {
                                buttonAuth.setImageDrawable(getResources().getDrawable(R.drawable.cancel_26_zgqpic));
                            }


                            buttonAuth.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                            buttonAuth.setBackgroundColor(0x00000000);
                            buttonAuth.setAdjustViewBounds(true);
                            buttonAuth.setScaleType(ImageView.ScaleType.CENTER);
                            buttonAuth.setMaxHeight(100);
                            buttonAuth.setMaxWidth(100);
                            buttonAuth.setId(i * 10 + j);
                            buttonAuth.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(myApplication.USER_AUTH_INFO.flagGroupMaster!=1){
                                        Toast.makeText(GroupManagerActivity.this,"非常抱歉，您不是群主，没有管理成员权限的功能",Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    indexMember = v.getId() / 10;
                                    indexAuthority = v.getId() % 10;
                                    //System.out.println("ZGQ:36/10=" + 36 / 10);
                                    //System.out.println("ZGQ:36%10=" + 36 % 10);
                                    //System.out.println("ZGQ:indexMember=" + indexMember);
                                    //System.out.println("ZGQ:indexAuthority=" + indexAuthority);
                                    if(memberIdList.get(indexMember).equals(String.valueOf(myApplication.USER_AUTH_INFO.userId))){
                                        Toast.makeText(GroupManagerActivity.this,"非常抱歉，不能修改自己的权限",Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupManagerActivity.this);
                                    builder1.setCancelable(false);
                                    builder1.setMessage("是否确定修改" + memberUserNameList.get(indexMember) + "成员的" + authorityNames[indexAuthority] + "权限?");
                                    builder1.setTitle("提示");
                                    builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Timer searchMemberTimer = new Timer();
                                            SendRecMessageServerTask editMemberAuthTask = new SendRecMessageServerTask(myApplication.USER_AUTH_INFO.userName, myApplication.USER_AUTH_INFO.userPassword, myApplication.myPhoneInfo1.deviceId, myApplication.USER_AUTH_INFO, "editA", memberIdList.get(indexMember) + "_" + indexAuthority+"__"+memberAuthorityArray.get(indexMember).get(indexAuthority), handler, 202);
                                            searchMemberTimer.schedule(editMemberAuthTask, 0);
                                            searchMemberTask=editMemberAuthTask;
                                            //finish();
                                        }
                                    });
                                    builder1.setNegativeButton("取消", null);
                                    builder1.create().show();
                                }
                            });
                            tableRow.addView(buttonAuth);
                        }

                        linearLayoutGroupMembers.addView(tableRow);

                    }
                }else if (msg.what == 206){
                    JSONArray memberJsonArray=searchMemberTask.getJsonArrayRes();
                    numsOfGroup=memberJsonArray.length();
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
        setContentView(R.layout.activity_group_manager);
        myApplication=(MyApplication) this.getApplicationContext();;

        textViewUserName=(TextView)findViewById(R.id.tv_group_username);
        textViewVipUser=(TextView)findViewById(R.id.tv_group_vipuser);
        textViewGroupName=(TextView)findViewById(R.id.tv_group_groupname);
        textViewGroupMaster=(TextView)findViewById(R.id.tv_group_groupmaster);
        textViewMemberNums=(TextView)findViewById(R.id.tv_group_memberNums);
        textViewVipRemaindays=(TextView)findViewById(R.id.tv_group_vipremaindays);
        textViewLoginState=(TextView)findViewById(R.id.tv_group_islogin);
        textViewLoginOut=(TextView)findViewById(R.id.tv_group_loginout);

        buttonCreateGroup=(Button)findViewById(R.id.btn_group_creategroup);
        buttonMemberManager=(Button)findViewById(R.id.btn_group_membermanager);
        buttonFilesManager=(Button)findViewById(R.id.btn_group_filemanager);
        buttonQuitGroup=(Button)findViewById(R.id.btn_group_quitgroup);
        buttonAddMember=(Button)findViewById(R.id.btn_group_addmember);
        buttonDeleteMember=(Button)findViewById(R.id.btn_group_delmember);

        linearLayoutGroupManager=(LinearLayout)findViewById(R.id.linearlayout_groupmanage);
        linearLayoutGroupContent=(LinearLayout)findViewById(R.id.linearlayout_groupcontent);
        linearLayoutGroupMembers=(LinearLayout)findViewById(R.id.linearlayout_groupmembers);

        Timer searchMasterTimer = new Timer();
        // login 或者 regst 或者 schma(查询群主) 或者 crgrp 新建群
        // 或者 allme查询所有成员信息  或者 schme查询成员 或者 addme 增加群成员 delme 删除群成员 或者 schus 查找用户
        // 或者 lstme 查询个人信息 或者  qutme 退出群 或者 editp修改成员权限

        SendRecMessageServerTask searchMasterTask = new SendRecMessageServerTask(myApplication.USER_AUTH_INFO.userName,myApplication.USER_AUTH_INFO.userPassword,myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"schma"," ",handler,201);
        searchMasterTimer.schedule(searchMasterTask,0);


        textViewUserName.setText(myApplication.USER_AUTH_INFO.userName);
        textViewVipRemaindays.setText(String.valueOf(myApplication.USER_AUTH_INFO.vipRemainDays)+"天");
        if(myApplication.flagUserLoginSucc){
            textViewLoginState.setText("已登录");
        }else {
            textViewLoginState.setText("未登录");
        }

        if(myApplication.USER_AUTH_INFO.vipUser==1){
            textViewVipUser.setText("VIP");
        }else{
            textViewVipUser.setText("体验用户");
        }

        textViewGroupName.setText(groupNameShow(myApplication.USER_AUTH_INFO.groupName));

        buttonMemberManager.setOnClickListener(this);
        buttonFilesManager.setOnClickListener(this);
        buttonAddMember.setOnClickListener(this);
        buttonDeleteMember.setOnClickListener(this);
        buttonQuitGroup.setOnClickListener(this);
        buttonCreateGroup.setOnClickListener(this);
        textViewLoginOut.setOnClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    MyFTP myFTP = new MyFTP();
                    FTPFile[] ftpFiles=myFTP.listFtpServerFiles("/wzmcc_cellinfo/");
                    for(int i=0;i<ftpFiles.length;i++){
                        ftpGroupNames.add(ftpFiles[i].getName());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        Timer searchMemberTimer1 = new Timer();
        searchMemberTask = new SendRecMessageServerTask(myApplication.USER_AUTH_INFO.userName,myApplication.USER_AUTH_INFO.userPassword,myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"allme"," ",handler,206);
        searchMemberTimer1.schedule(searchMemberTask,0);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_group_loginout:

                AlertDialog.Builder builder2 = new AlertDialog.Builder(GroupManagerActivity.this);
                builder2.setCancelable(false);
                builder2.setMessage("是否确定退出当前账号？");
                builder2.setTitle("提示");
                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myApplication.USER_AUTH_INFO.vipUser=0;
                        myApplication.flagUserLoginSucc=false;
                        startActivity(new Intent(GroupManagerActivity.this,LoginSocketActivity.class));
                        finish();
                    }
                });
                builder2.setNegativeButton("取消",null);
                builder2.create().show();

                break;
            case R.id.btn_group_filemanager:
                startActivity(new Intent(GroupManagerActivity.this,GroupFileManagerActivity.class));
                break;
            case R.id.btn_group_creategroup:
                //Toast.makeText(GroupManagerActivity.this,"开发中...",Toast.LENGTH_SHORT).show();
                if(myApplication.USER_AUTH_INFO.vipUser!=1){
                    Toast.makeText(GroupManagerActivity.this,"非VIP用户没有创建群的权限！",Toast.LENGTH_LONG).show();
                    return;
                }
                if(myApplication.USER_AUTH_INFO.flagGroupMaster==1){
                    Toast.makeText(GroupManagerActivity.this,"您是"+groupNameShow(myApplication.USER_AUTH_INFO.groupName)+"的群主，请先退群后再创建新的群组！",Toast.LENGTH_LONG).show();
                    return;
                }
                if(myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo/APP_DEFAULT")||myApplication.USER_AUTH_INFO.groupName.equals("")) {

                    final EditText et_gn = new EditText(this);
                    et_gn.setHint("请输入群名称");
                    new AlertDialog.Builder(this).setTitle("新建群").setView(et_gn)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String input_groupname = et_gn.getText().toString().trim();
                                    if(isContainChinese(input_groupname)){
                                        Toast.makeText(GroupManagerActivity.this, "群名称暂不支持中文字符，请重新输入！", Toast.LENGTH_LONG).show();
                                        return;
                                    }else if(input_groupname.length()<3){
                                        Toast.makeText(GroupManagerActivity.this, "群名称太短啦，至少3个字符以上，请重新输入！", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    if (input_groupname.equals("") || !userInputCheck(input_groupname)) {
                                        Toast.makeText(GroupManagerActivity.this, "不符合群命名规范，请重新输入！", Toast.LENGTH_LONG).show();
                                    } else if(ftpGroupNames.contains(input_groupname)){
                                        Toast.makeText(GroupManagerActivity.this, "群名称已经被注册啦，请重新命名！", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Timer creatGroupTimer = new Timer();
                                        searchMemberTask = new SendRecMessageServerTask(myApplication.USER_AUTH_INFO.userName, myApplication.USER_AUTH_INFO.userPassword, myApplication.myPhoneInfo1.deviceId, myApplication.USER_AUTH_INFO, "crgrp", myApplication.USER_AUTH_INFO.userId + "__" + input_groupname, handler, 205);
                                        creatGroupTimer.schedule(searchMemberTask, 0);
                                        myApplication.USER_AUTH_INFO.groupName = "wzmcc_cellinfo/" + input_groupname;
                                        myApplication.USER_AUTH_INFO.flagGroupMaster = 1;
                                        myApplication.USER_AUTH_INFO.masterName=myApplication.USER_AUTH_INFO.userName;
                                        linearLayoutGroupManager.setVisibility(View.GONE);
                                        linearLayoutGroupContent.setVisibility(View.VISIBLE);
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }else{
                    Toast.makeText(GroupManagerActivity.this,"您是"+groupNameShow(myApplication.USER_AUTH_INFO.groupName)+"的群成员，请先退群后再创建自己的群！",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_group_quitgroup:

                if(myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo/APP_DEFAULT")||myApplication.USER_AUTH_INFO.groupName.equals("")){
                    Toast.makeText(GroupManagerActivity.this,"您现在不属于任何用户自建群！",Toast.LENGTH_LONG).show();
                    return;
                }

                if(myApplication.USER_AUTH_INFO.flagGroupMaster==1&&numsOfGroup>1){
                    Toast.makeText(GroupManagerActivity.this,"您是群主，不能退群！可由其他群主修改您的群主权限后才可以退群。",Toast.LENGTH_LONG).show();
                    return;
                }

                if(myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo/APP_DEFAULT")||myApplication.USER_AUTH_INFO.groupName.equals("")){
                    Toast.makeText(GroupManagerActivity.this,"您现在不属于任何用户自建群！",Toast.LENGTH_LONG).show();
                }else {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupManagerActivity.this);
                    builder1.setCancelable(false);
                    if(numsOfGroup==1){
                        builder1.setMessage("是否确定退群?\r\n您是本群的最后一名成员，退群后所有群文件将被删除，请谨慎操作");
                    }else{
                        builder1.setMessage("是否确定退群?");
                    }
                    builder1.setTitle("提示");
                    builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String preGroupName=String.copyValueOf(myApplication.USER_AUTH_INFO.groupName.toCharArray());
                            myApplication.USER_AUTH_INFO.groupName="wzmcc_cellinfo/APP_DEFAULT";
                            myApplication.USER_AUTH_INFO.flagGroupMaster=0;
                            myApplication.USER_AUTH_INFO.masterName="";
                            textViewGroupMaster.setText("");
                            textViewGroupName.setText(groupNameShow(myApplication.USER_AUTH_INFO.groupName));

                            linearLayoutGroupManager.setVisibility(View.VISIBLE);
                            linearLayoutGroupContent.setVisibility(View.GONE);

                            Timer quitGroupTimer = new Timer();
                            searchMemberTask = new SendRecMessageServerTask(myApplication.USER_AUTH_INFO.userName,myApplication.USER_AUTH_INFO.userPassword,myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"delme",String.valueOf(myApplication.USER_AUTH_INFO.userId),handler,212);
                            quitGroupTimer.schedule(searchMemberTask,0);

                            if(numsOfGroup==1){
                                //如果该群没有其他成员，那么删除该群的FTP文件目录
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyFTP myFTP = new MyFTP();
                                        myFTP.deleteSubDirectory("/"+preGroupName+"/");
                                    }
                                }).start();
                            }
                        }
                    });
                    builder1.setNegativeButton("取消",null);
                    builder1.create().show();

                }
                break;
            case R.id.btn_group_membermanager:

                if(myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo/APP_DEFAULT")||myApplication.USER_AUTH_INFO.groupName.equals("")){
                    Toast.makeText(GroupManagerActivity.this,"您现在不属于任何用户自建群！",Toast.LENGTH_LONG).show();
                    return;
                }

                if(myApplication.USER_AUTH_INFO.vipUser==1){
                    linearLayoutGroupManager.setVisibility(View.GONE);
                    linearLayoutGroupContent.setVisibility(View.VISIBLE);
                    Timer searchMemberTimer = new Timer();
                    searchMemberTask = new SendRecMessageServerTask(myApplication.USER_AUTH_INFO.userName,myApplication.USER_AUTH_INFO.userPassword,myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"allme"," ",handler,202);
                    searchMemberTimer.schedule(searchMemberTask,0);
                }else
                {
                    Toast.makeText(GroupManagerActivity.this,"非VIP用户不支持管理群成员功能，请联系13868808810开通VIP功能",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_group_addmember:
                if(myApplication.USER_AUTH_INFO.flagGroupMaster==1){

                    final EditText et = new EditText(this);
                    et.setHint("请输入要查找的用户账号");
                    new AlertDialog.Builder(this).setTitle("搜索用户账号").setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                String input = et.getText().toString().trim();
                                if (input.equals("")) {
                                        Toast.makeText(GroupManagerActivity.this, "搜索内容不能为空！",Toast.LENGTH_LONG).show();
                                } else {
                                    Timer searchUserTimer = new Timer();
                                    searchMemberTask = new SendRecMessageServerTask(myApplication.USER_AUTH_INFO.userName,myApplication.USER_AUTH_INFO.userPassword,myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"schus",input,handler,203);
                                    searchUserTimer.schedule(searchMemberTask,0);
                                }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();

                }else {
                    Toast.makeText(GroupManagerActivity.this,"非常抱歉，您不是群主，没有添加成员的权限",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_group_delmember:
                if(myApplication.USER_AUTH_INFO.flagGroupMaster==1){
                    indexMember=-1;
                    for(int i=0;i<memberCheckBoxList.size();i++){
                        if(memberCheckBoxList.get(i).isChecked()){
                            usernameDel=memberCheckBoxList.get(i).getText().toString().trim();
                            indexMember=i;
                            break;
                        }
                    }
                    if(indexMember==-1){
                        Toast.makeText(GroupManagerActivity.this,"请先选择要删除的成员！",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(memberIdList.get(indexMember).equals(String.valueOf(myApplication.USER_AUTH_INFO.userId))){
                        Toast.makeText(GroupManagerActivity.this,"无法将自己从群里删除！",Toast.LENGTH_SHORT).show();
                        break;
                    }

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupManagerActivity.this);
                    builder1.setCancelable(false);
                    builder1.setMessage("是否将"+usernameDel+"用户从本群中删除?");
                    builder1.setTitle("提示");
                    builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Timer delMemberTimer = new Timer();
                            searchMemberTask = new SendRecMessageServerTask(myApplication.USER_AUTH_INFO.userName,myApplication.USER_AUTH_INFO.userPassword,myApplication.myPhoneInfo1.deviceId,myApplication.USER_AUTH_INFO,"delme",memberIdList.get(indexMember),handler,202);
                            delMemberTimer.schedule(searchMemberTask,0);
                        }
                    });
                    builder1.setNegativeButton("取消",null);
                    builder1.create().show();

                }else {
                    Toast.makeText(GroupManagerActivity.this,"非常抱歉，您不是群主，没有删除成员的权限",Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }

    }

    public static boolean userInputCheck(String str){
        if(str.indexOf(" ")!=-1){
            return  false;
        }else if(str.indexOf("/")!=-1){
            return  false;
        }else if(str.indexOf("__")!=-1){
            return  false;
        }else if(str.indexOf("(")!=-1){
            return  false;
        }else if(str.indexOf(")")!=-1){
            return  false;
        }else if(str.indexOf(" ")!=-1){
            return  false;
        }
        return true;
    }

    public static String groupNameShow(String rawName){
        String gpname_show="";
        if(rawName.equals("wzmcc_cellinfo")||rawName.equals(""))
        {
            gpname_show=rawName;
        }else if(rawName.contains("wzmcc_cellinfo/")){
            gpname_show=rawName.substring(rawName.indexOf("/")+1);
        }
        return  gpname_show;
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

}
