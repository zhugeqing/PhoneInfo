package com.example.administrator.phoneinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppSettingActivity extends AppCompatActivity {

    private HashMap<String,EditText> haMapSetEdit;
    private Iterator iter_SetEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);

        LinearLayout linearLayoutSets=(LinearLayout)findViewById(R.id.linearlayout_setting);
        Button buttonEditSettings=(Button)findViewById(R.id.button_setting_edit);

        MyApplication myApplication=(MyApplication) this.getApplicationContext();
        List<String> spKeyList=new ArrayList<>();
        List<String> spValueList=new ArrayList<>();
        Context mContext=getApplicationContext();
        SharedPreferences sharedPre = getSharedPreferences("config", MODE_PRIVATE);
        if(sharedPre.getAll().isEmpty()){
            spKeyList.add("CellInfoFileName_LTE");
            spValueList.add(String.valueOf(myApplication.CellInfoFileName_LTE));
            spKeyList.add("SECTOR_MAP_SHOW_R");
            spValueList.add(String.valueOf(myApplication.SECTOR_MAP_SHOW_R));
            spKeyList.add("SECTOR_R");
            spValueList.add(String.valueOf(myApplication.SECTOR_R));
            spKeyList.add("MR_MAP_SHOW_R");
            spValueList.add(String.valueOf(myApplication.MR_MAP_SHOW_R));
            spKeyList.add("mrFtpServerIp");
            spValueList.add(String.valueOf(myApplication.mrFtpServerIp));
            spKeyList.add("mrFtpServerPort");
            spValueList.add(String.valueOf(myApplication.mrFtpServerPort));
            spKeyList.add("mrFtpUser");
            spValueList.add(String.valueOf(myApplication.mrFtpUser));
            spKeyList.add("mrFtpPassword");
            spValueList.add(String.valueOf(myApplication.mrFtpPassword));
            spKeyList.add("mrFilesFtpDir");
            spValueList.add(String.valueOf(myApplication.mrFilesFtpDir));
            spKeyList.add("logFilesFtpDir");
            spValueList.add(String.valueOf(myApplication.logFilesFtpDir));
            spKeyList.add("mrPoorQuaPercentThre10");
            spValueList.add(String.valueOf(myApplication.mrPoorQuaPercentThre10));
            spKeyList.add("mrPoorQuaPercentThre30");
            spValueList.add(String.valueOf(myApplication.mrPoorQuaPercentThre30));
            spKeyList.add("mrPoorQuaPercentThre60");
            spValueList.add(String.valueOf(myApplication.mrPoorQuaPercentThre60));
            saveSettingInfo(mContext,"config",spKeyList,spValueList);
            spKeyList.clear();
            spValueList.clear();
        }
        spKeyList=readAllSetKey(mContext,"config");
        spValueList=readAllSetValue(mContext,"config");
        //TableRow.LayoutParams tblayout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT);
        haMapSetEdit=new HashMap<String,EditText>();

        for(int i=0;i<spKeyList.size();i++){
            TextView textViewKey=new TextView(AppSettingActivity.this);
            textViewKey.setText(spKeyList.get(i)+": ");
            textViewKey.setLayoutParams(new TextSwitcher.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            EditText editTextValue=new EditText(AppSettingActivity.this);
            editTextValue.setLayoutParams(new TextSwitcher.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            editTextValue.setText(spValueList.get(i));
            editTextValue.setSingleLine(true);
            editTextValue.setEnabled(false);
            haMapSetEdit.put(spKeyList.get(i),editTextValue);
            //TableRow tbRow=new TableRow(AppSettingActivity.this);
            //tbRow.setLayoutParams(tblayout);
            //tbRow.addView(textViewKey);
            //tbRow.addView(editTextValue);
            linearLayoutSets.addView(textViewKey);
            linearLayoutSets.addView(editTextValue);
        }
        iter_SetEdit=haMapSetEdit.entrySet().iterator();
        buttonEditSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                while (iter_SetEdit.hasNext()) {
                    Map.Entry entry = (java.util.Map.Entry)iter_SetEdit.next();
                    String mkey=entry.getKey().toString();
                    haMapSetEdit.get(mkey).setEnabled(true);
                }
            }
        });
    }

    public static void saveSettingInfo(Context context,String spName, List<String> mKeys, List<String> mValues) {
        //获取SharedPreferences对象
        SharedPreferences sharedPre = context.getSharedPreferences(spName, context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        for(int i=0;i<mKeys.size();i++){
            editor.putString(mKeys.get(i),mValues.get(i));

        }
        editor.commit();//提交
    }

    public static void deleteSettingInfo(Context context,String spName, List<String> mKeys) {
        //获取SharedPreferences对象
        SharedPreferences sharedPre = context.getSharedPreferences(spName, context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        for(int i=0;i<mKeys.size();i++){
            editor.remove(mKeys.get(i));
        }
        editor.commit();//提交
    }

    public static List<String> readAllSetKey(Context context,String spName) {
        //获取SharedPreferences对象
        List<String> mKeyList=new ArrayList<>();
        SharedPreferences sharedPre = context.getSharedPreferences(spName, context.MODE_PRIVATE);
        Map<String, ?> allContent = sharedPre.getAll();
        //遍历map的方法
        for(Map.Entry<String, ?>  entry : allContent.entrySet()){
            //content+=(entry.getKey()+entry.getValue());
            mKeyList.add(entry.getKey());
        }
        return mKeyList;
    }
    public static List<String> readAllSetValue(Context context,String spName) {
        //获取SharedPreferences对象
        List<String> mValueList=new ArrayList<>();
        SharedPreferences sharedPre = context.getSharedPreferences(spName, context.MODE_PRIVATE);
        Map<String, ?> allContent = sharedPre.getAll();
        //遍历map的方法
        for(Map.Entry<String, ?>  entry : allContent.entrySet()){
            //content+=(entry.getKey()+entry.getValue());
            mValueList.add(String.valueOf(entry.getValue()));
        }
        return mValueList;
    }
}