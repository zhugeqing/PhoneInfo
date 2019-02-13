package com.example.administrator.phoneinfo;

/**
 * Created by Administrator on 2017/4/7.
 */

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG="LoginActivity";
    private EditText  et_name,et_pwd;
    private Button btn_login;
    private String name,pwd;
    private Map<String,String> map;
    //192.168.0.190服务端的url
    private String url="http://192.168.0.190:8080/UploadTask/LoginServlet";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
// TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//输入时隐藏小键盘
        initView();
        // InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.hideSoftInputFromWindow(et_name.getWindowToken(), 0);
        // imm.hideSoftInputFromWindow(et_pwd.getWindowToken(), 0);
    }
    private void initView() {
//初始化操作
        et_name=(EditText) findViewById(R.id.et_name);
        et_pwd=(EditText) findViewById(R.id.et_pwd);
        btn_login=(Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(v);
            }
        });
    }
    public void Login(View v){
        //通过客户端发送get或者post两种请求方式输入用户名跟密码返回code信息验证用户登录成功返回1失败返回0
        name=et_name.getText().toString().trim();
        pwd=et_pwd.getText().toString().trim();
        map=new HashMap<String, String>();
        map.put("name", name);
        map.put("password",pwd);
        new MyTask().execute(url);
    }



    class MyTask extends AsyncTask<String, Void, Integer>{


        @Override
        protected Integer doInBackground(String... params) {
// 在后台执行
//int code=HttpTools.getMethod(params[0], map);
//int code = HttpTools.postMethod(params[0], map);
            //int code = HttpurlTools.doGet(params[0], map);
            int  code = HttpurlTools.postMethod(params[0], map);
            return code;
        }
        @Override
        protected void onPreExecute() {
// 首先执行
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Integer result) {
//
            super.onPostExecute(result);
            if(result==0){
                Toast.makeText(LoginActivity.this, "用戶名跟密码不正确", Toast.LENGTH_SHORT).show();
                Log.i(TAG, ""+result);
            }else if(result==1){
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                Log.i(TAG, ""+result);
            }
        }
    }
}

