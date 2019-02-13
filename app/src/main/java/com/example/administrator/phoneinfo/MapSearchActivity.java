package com.example.administrator.phoneinfo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;
import static com.example.administrator.phoneinfo.ProgressDlgUtil.progressDlg;

public class MapSearchActivity extends AppCompatActivity {

    public static final String DBNAME1 = "CellInfo.db";//无线小区信息数据库
    public static final String TABLENAME1 = "CellInfo_LTE";//LTE小区信息表

    private MyDatabaseHelper dbhelper;
    private SQLiteDatabase db1;
    private MyApplication myApplication;
    private List<String> dbTableNames=new ArrayList<>();
    private List<CheckBox> checkboxTables=new ArrayList<>();
    private List<String> talbesCheckedList;
    private LinearLayout linearLayoutDbTables;
    private LinearLayout linearLayoutDetail;
    private EditText editTextSearch;
    private List<ResultEntrySearch> resultListSearch=new ArrayList<>();
    private ListView searchResultLV;
    private SearchResultAdapter searchResultAdapter;
    private List<String[]> detailArray=new ArrayList<>();
    private ResultEntrySearch srEntryFordetail;
    private String searchWordsList="";

    //private List<ZgqExcelRow> zgqExcelRowsList=new ArrayList<ZgqExcelRow>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchmap);
        myApplication = (MyApplication) getApplicationContext();
        dbhelper = new MyDatabaseHelper(this, DBNAME1,TABLENAME1, null, 1);
        editTextSearch=(EditText)findViewById(R.id.et_searchcontent);

        SharedPreferences pref = getSharedPreferences("config_search",MODE_PRIVATE);
        String savedSearchStr=pref.getString("key_MapSearchWords","");
        if(!savedSearchStr.equals("")) {
            editTextSearch.setText(savedSearchStr);//第二个参数为默认值
            editTextSearch.setSelection(savedSearchStr.length());
        }
        ImageButton searchButton=(ImageButton) findViewById(R.id.btn_searchnow);
        linearLayoutDbTables=(LinearLayout)findViewById(R.id.linearlayout_tablenames);
        linearLayoutDetail=(LinearLayout)findViewById(R.id.linearlayout_resultdetail);
        searchResultLV=(ListView)findViewById(R.id.lv_mapsearchresult);
        searchResultAdapter=new SearchResultAdapter(MapSearchActivity.this,R.layout.listitem_searchresult,resultListSearch);
        searchResultLV.setAdapter(searchResultAdapter);
        searchResultLV.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){
                srEntryFordetail=resultListSearch.get(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<talbesCheckedList.size();i++) {
                            String tpTableName1=talbesCheckedList.get(i);
                            detailArray=searchTableForDetail(MapSearchActivity.this,DBNAME1,tpTableName1,"id",srEntryFordetail.resultId);
                        }
                        Message msg18 = new Message();
                        msg18.what = 18;   //发送开始导入消息
                        handler.sendMessage(msg18);
                    }
                }).start();
            }
        });

        searchResultLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ResultEntrySearch srEntry=resultListSearch.get(position);
                    myApplication.searchResultLon=srEntry.resultLongitude;
                    myApplication.searchResultLat=srEntry.resultLatitude;
                    Intent intent=new Intent(MapSearchActivity.this,MapViewActivity.class);
                    setResult(5,intent);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> tptablenames=readUserTables(MapSearchActivity.this, DBNAME1, TABLENAME1);

                    for(int i=0;i<tptablenames.size();i++){
                        if(!tptablenames.get(i).contains("tb_userlayer_")){
                            dbTableNames.add(tptablenames.get(i));
                        }
                    }

                    if (dbTableNames.size() > 0) {
                        Message msg8 = new Message();
                        msg8.what = 8;   //发送开始导入消息
                        handler.sendMessage(msg8);
                    }
                }catch(Exception e){
                        e.printStackTrace();
                }
            }
        }).start();


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchStr=editTextSearch.getText().toString().trim();
                List<String> spKeys=new ArrayList<>();
                List<String> spValues=new ArrayList<>();

                String tempSearchStr=searchStr;
                searchWordsList="";
                if(tempSearchStr.equals("")) {
                    Toast.makeText(MapSearchActivity.this, "请输入查找内容！", Toast.LENGTH_SHORT).show();
                    return;
                }else if(tempSearchStr.indexOf(" ")>-1){
                    while(tempSearchStr.indexOf(" ")>-1){
                        if(searchWordsList.equals("")){
                            searchWordsList=tempSearchStr.substring(0,tempSearchStr.indexOf(" "));
                        } else{
                            searchWordsList=searchWordsList+"%"+tempSearchStr.substring(0,tempSearchStr.indexOf(" "));
                        }
                        tempSearchStr=tempSearchStr.substring(tempSearchStr.indexOf(" ")).trim();
                    }
                    searchWordsList=searchWordsList+"%"+tempSearchStr;
                }else{
                    searchWordsList=tempSearchStr;
                }
                spKeys.add("key_MapSearchWords");
                spValues.add(searchStr);
                AppSettingActivity.saveSettingInfo(MapSearchActivity.this,"config_search",spKeys,spValues);

                resultListSearch.clear();
                talbesCheckedList=new ArrayList<>();
                for(int i=0;i<checkboxTables.size();i++){
                    if(checkboxTables.get(i).isChecked()){
                        talbesCheckedList.add(String.valueOf(checkboxTables.get(i).getText()));
                    }
                }
                if(talbesCheckedList.size()==0){
                    Toast.makeText(MapSearchActivity.this,"请选择要查询的表格",Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            for(int i=0;i<talbesCheckedList.size();i++) {
                                String tpTableName=talbesCheckedList.get(i);
                                resultListSearch.addAll(searchTable(MapSearchActivity.this,DBNAME1,tpTableName,"小区名",searchWordsList));
                            }
                            if(resultListSearch.size()>0){
                                Message msg1 = new Message();
                                msg1.what = 1;   //发送开始导入消息
                                handler.sendMessage(msg1);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });

    }

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            //ProgressDialog myProgressDialog=null;
            if (msg.what == 8)  {
                for(int i=0;i<dbTableNames.size();i++){
                    if(!dbTableNames.get(i).equals("android_metadata")&&!dbTableNames.get(i).equals("sqlite_sequence")){
                        CheckBox checkBoxTable=new CheckBox(MapSearchActivity.this);
                        checkBoxTable.setText(dbTableNames.get(i));
                        if(dbTableNames.get(i).equals("CellInfo_LTE")){
                            checkBoxTable.setChecked(true);
                        }
                        else{
                            checkBoxTable.setChecked(false);
                        }
                        checkboxTables.add(checkBoxTable);
                        linearLayoutDbTables.addView(checkBoxTable);}
                }
            }else if(msg.what==1){
                linearLayoutDetail.removeAllViews();
                searchResultLV.setVisibility(View.VISIBLE);
                searchResultAdapter.notifyDataSetChanged();
                Toast.makeText(MapSearchActivity.this,"提示：“单击”显示该条目详细信息，“长按”进行地图定位",Toast.LENGTH_LONG).show();
            }else if(msg.what==18){
                if(detailArray.size()>0){
                    linearLayoutDetail.removeAllViews();
                    String[] entryNames=detailArray.get(0);
                    String[] entryValues=detailArray.get(1);
                    for(int i=0;i<entryNames.length;i++){
                        TextView textEntryName=new TextView(MapSearchActivity.this);
                        textEntryName.setText(entryNames[i]+": ");
                        textEntryName.setLayoutParams(new TextSwitcher.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                        EditText textEntryValue=new EditText(MapSearchActivity.this);
                        textEntryValue.setEnabled(false);
                        textEntryValue.setText(entryValues[i]);
                        textEntryValue.setLayoutParams(new TextSwitcher.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                        linearLayoutDetail.addView(textEntryName);
                        linearLayoutDetail.addView(textEntryValue);
                    }

                }

            }
            super.handleMessage(msg);
        }
    };

    public static List<String> readUserTables(Context mContext,String dbname,String tablename) {
        List<String> array_tbName = new ArrayList<>();
        try{
                MyDatabaseHelper dbhelper=new MyDatabaseHelper(mContext,dbname,tablename,null,1);
                SQLiteDatabase db1 = dbhelper.getWritableDatabase();
                Cursor cursor_tbname = db1.rawQuery("select name from sqlite_master where type='table' order by name", null);
                while (cursor_tbname.moveToNext()) {
                    //遍历数据库中所有表的表名
                    if(!cursor_tbname.getString(0).equals("android_metadata")&&!cursor_tbname.getString(0).equals("sqlite_sequence")){
                        array_tbName.add(cursor_tbname.getString(0));
                        //System.out.println("ZGQ:tablename= " + cursor_tbname.getString(0));
                    }
                }
                if (cursor_tbname != null) {
                    try {
                        cursor_tbname.close();
                    } catch (Exception e) {
                    }
                }
                db1.close();
        }catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"数据库读取失败！",Toast.LENGTH_LONG).show();
        }
        return array_tbName;
    }

    public List<ResultEntrySearch> searchTable(Context mContext,String dbname,String tablename,String searchField,String searchContent) {
        //Boolean contentIsNumeric;
        List<ResultEntrySearch> mSearchResult=new ArrayList<>();

        try{
            MyDatabaseHelper dbhelper=new MyDatabaseHelper(mContext,dbname,tablename,null,1);
            SQLiteDatabase db1 = dbhelper.getWritableDatabase();
            String sqlstr="select id,小区名,经度,纬度 from "+tablename+" where "+ searchField +" like '%"+searchContent+"%' order by id";
            Cursor cursor_result = db1.rawQuery(sqlstr, null);
            if(cursor_result.getCount()==0){
                System.out.println("数据库表查询结果为0");
                return mSearchResult;
            }
            while (cursor_result.moveToNext()) {
                //遍历数据库中所有表的表名
                ResultEntrySearch tpentry=new ResultEntrySearch();
                tpentry.resultTableName=tablename;
                tpentry.resultId=cursor_result.getInt(0);
                tpentry.resultContent=cursor_result.getString(1);
                tpentry.resultLongitude=cursor_result.getDouble(2);
                tpentry.resultLatitude=cursor_result.getDouble(3);
                mSearchResult.add(tpentry);
            }
            if (cursor_result != null) {
                try {
                    cursor_result.close();
                } catch (Exception e) {
                }
            }
            db1.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("数据库表查询失败");
        }
        return mSearchResult;
    }

    public static List<String[]> searchTableForDetail(Context mContext,String dbname,String tablename,String searchField,int keyId) {

        List<String[]> mSearchResult=new ArrayList<>();
        try{
            MyDatabaseHelper dbhelper=new MyDatabaseHelper(mContext,dbname,tablename,null,1);
            SQLiteDatabase db2 = dbhelper.getWritableDatabase();
            String sqlstr="select * from "+tablename+" where "+ searchField +" = "+keyId;
            Cursor cursor_res = db2.rawQuery(sqlstr, null);
            if(cursor_res.getCount()==0){
                System.out.println("数据库表查询结果为0");
                return mSearchResult;
            }
            String[] columnNames=cursor_res.getColumnNames();
            mSearchResult.add(columnNames);
            while (cursor_res.moveToNext()) {
                //遍历数据库中所有表的表名
                String[] searchRes=new String[columnNames.length];
                for(int i=0;i<columnNames.length;i++){
                   switch (cursor_res.getType(i)){
                       case FIELD_TYPE_NULL:
                            searchRes[i]="";
                           break;
                       case FIELD_TYPE_INTEGER:
                           searchRes[i]=String.valueOf(cursor_res.getInt(i));
                           break;
                       case FIELD_TYPE_FLOAT:
                           searchRes[i]=String.valueOf(cursor_res.getDouble(i));
                           break;
                       case FIELD_TYPE_STRING:
                           searchRes[i]=cursor_res.getString(i);
                           break;
                       case FIELD_TYPE_BLOB:
                           searchRes[i]=String.valueOf(cursor_res.getBlob(i));
                           break;
                       default:
                           break;
                   }
                }
                mSearchResult.add(searchRes);
            }
            if (cursor_res != null) {
                try {
                    cursor_res.close();
                } catch (Exception e) {
                }
            }
            db2.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("数据库表查询失败");
        }
        return mSearchResult;
    }


    protected void onDestroy(){
        //卸载super的前后是没有却别的
        if(db1!=null) {
            db1.close();
        }
        super.onDestroy();

    }

    public class ResultEntrySearch{
        public String resultTableName;
        public String resultContent;
        public double resultLongitude;
        public double resultLatitude;
        public int resultId;
    }

}

