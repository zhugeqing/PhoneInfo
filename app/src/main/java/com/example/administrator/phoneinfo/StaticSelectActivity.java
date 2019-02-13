package com.example.administrator.phoneinfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
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
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;
import static android.view.View.TEXT_ALIGNMENT_TEXT_END;
import static com.example.administrator.phoneinfo.MapSearchActivity.DBNAME1;

public class StaticSelectActivity extends AppCompatActivity {
    private MyApplication myApplication;
    private List<String[]> resultListSelect=new ArrayList<>();
    private List<Integer> resultFieldsType= new ArrayList<>();
    private TextView textViewLayerName;
    private TextView textViewStaticCounts;
    private ArrayAdapter arr_adapter;
    private LinearLayout linearLayoutStatic;
    private List<TextView> tvStaticResultList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_select);
        myApplication=(MyApplication) this.getApplicationContext();
        textViewLayerName=(TextView)findViewById(R.id.tv_static_layername);
        textViewStaticCounts=(TextView)findViewById(R.id.tv_static_counts);
        linearLayoutStatic=(LinearLayout)findViewById(R.id.linearlayout_staticresult);
        Button buttonStaticOutput=(Button)findViewById(R.id.btn_static_export);
        buttonStaticOutput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(StaticSelectActivity.this);
                builder1.setCancelable(false);
                builder1.setMessage("是否导出清单，保存至" + myApplication.logFileSavePath + "目录?");
                builder1.setTitle("提示");
                builder1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
                                String filetime = sDateFormat.format(new Date(System.currentTimeMillis()));
                                try {
                                    String excelFileSaveName = "Sel_" + myApplication.USER_AUTH_INFO.userName + filetime + ".xls";
                                    if(MyLogFile.createExcelfromArray(excelFileSaveName, myApplication.logFileSavePath,resultListSelect)){
                                        Message msg8 = new Message();
                                        msg8.what = 8;   //发送开始导入消息
                                        handler.sendMessage(msg8);
                                    }else{
                                        Message msg2 = new Message();
                                        msg2.what = 2;   //发送开始导入消息
                                        handler.sendMessage(msg2);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.out.println("ZGQ:清单保存失败!");
                                } finally {

                                }
                            }
                        }).start();
                    }
                });
                builder1.setNegativeButton("取消",null);
                builder1.show();
            }
        });

        new Thread(new AreaSelectCalcThread()).start();

    }


    private Handler handler = new Handler() {

            public void handleMessage(Message msg) {
            try{
                if (msg.what == 8) {
                    Toast.makeText(StaticSelectActivity.this, "成功导出exel文件至" + myApplication.logFileSavePath, Toast.LENGTH_LONG).show();
                }else if(msg.what==2){
                    Toast.makeText(StaticSelectActivity.this, "导出失败!", Toast.LENGTH_LONG).show();
                }else if(msg.what==1){
                    textViewLayerName.setText(myApplication.userSelectPolyLayerName);
                    textViewStaticCounts.setText(String.valueOf(resultListSelect.size()-1));
                    int colmns=resultListSelect.get(0).length;
                    for(int i=0;i<colmns;i++){
                        TableRow tableRow=new TableRow(StaticSelectActivity.this);

                        TextView textViewcolumnName = new TextView(StaticSelectActivity.this);
                        textViewcolumnName.setText(resultListSelect.get(0)[i]);
                        textViewcolumnName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));

                        Spinner spinner_clac=new Spinner(StaticSelectActivity.this);
                        spinner_clac.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.2f));
                        List<String> data_list = new ArrayList<String>();
                        data_list.add("计数");
                        data_list.add("均值");
                        data_list.add("求和");
                            //适配器
                        arr_adapter= new ArrayAdapter<String>(StaticSelectActivity.this, R.layout.my_spinner_item, data_list);
                            //设置样式
                        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //加载适配器
                        spinner_clac.setAdapter(arr_adapter);
                        spinner_clac.setId(i);
                            //spinner_icon.setSelection(0,true);
                        spinner_clac.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
                                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    // TODO Auto-generated method stub
                                    int sx=arg0.getId();
                                    String static_new=staticByColumn(resultListSelect,sx,arr_adapter.getItem(arg2).toString(),resultFieldsType);
                                    tvStaticResultList.get(sx).setText(static_new);
                                    //.get(arg0.getId()).userLayerIcon=arr_adapter.getItem(arg2).toString();//文本说明
                                }
                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub
                                }
                            });

                        TextView tvStaticResult= new TextView(StaticSelectActivity.this);
                        String static_result=staticByColumn(resultListSelect,i,"计数",resultFieldsType);
                        tvStaticResult.setText(static_result);
                        tvStaticResult.setTextSize(12);//sp
                        tvStaticResult.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
                        tvStaticResultList.add(tvStaticResult);
                        tableRow.addView(textViewcolumnName);
                        tableRow.addView(spinner_clac);
                        tableRow.addView(tvStaticResult);
                        linearLayoutStatic.addView(tableRow);
                    }

                }else if(msg.what==18){

                }
                super.handleMessage(msg);
            } catch(Exception e){
               e.printStackTrace();
            }
        }
    };

    class AreaSelectCalcThread implements Runnable {

        @Override
        public void run() {
            if(myApplication.userSelectPolygonList.size()==0||myApplication.userSelectPolyLayerName.equals("")){
                return;
            }
            try {
                resultListSelect.addAll(searchTableInPolygon(StaticSelectActivity.this,DBNAME1,myApplication.userSelectPolyLayerName,myApplication.userSelectPolygonList,resultFieldsType));

                if(resultListSelect.size()>0){
                    Message msg1 = new Message();
                    msg1.what = 1;   //发送开始导入消息
                    handler.sendMessage(msg1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //SpatialRelationUtil.isPolygonContainsPoint(myApplication.userSelectPolygonList.get(0),);

        }
    }

    public static List<String[]> searchTableInPolygon(Context mContext, String dbname, String tablename, List<List<LatLng>> polygonPointsList_bd,List<Integer> fieldsType) {

        List<List<LatLng>> polygonPointsList_Gps=new ArrayList<>();
        List<String[]> mSearchResult=new ArrayList<>();
        List<double[]> maxminGpsList=new ArrayList<>();
        int numsPolygon= polygonPointsList_bd.size();

        for(int s=0;s<numsPolygon;s++) {
            List<LatLng> polygonPoints_Gps = new ArrayList<>();
            double maxLong, minLong, maxLat, minLat;
            List<Double> longList = new ArrayList<>();
            List<Double> latList = new ArrayList<>();
            double[] maxminGps = new double[4];

            for (int i = 0; i < polygonPointsList_bd.get(s).size(); i++) {
                polygonPoints_Gps.add(PositionUtility.bd09_To_Gps84(polygonPointsList_bd.get(s).get(i).latitude, polygonPointsList_bd.get(s).get(i).longitude));
                longList.add(polygonPoints_Gps.get(i).longitude);
                latList.add(polygonPoints_Gps.get(i).latitude);
            }
            polygonPointsList_Gps.add(polygonPoints_Gps);
            maxLong = Collections.max(longList);
            minLong = Collections.min(longList);
            maxLat = Collections.max(latList);
            minLat = Collections.min(latList);
            maxminGps[0]=minLong;
            maxminGps[1]=maxLong;
            maxminGps[2]=minLat;
            maxminGps[3]=maxLat;
            maxminGpsList.add(maxminGps);
        }

        try{
            MyDatabaseHelper dbhelper=new MyDatabaseHelper(mContext,dbname,tablename,null,1);
            SQLiteDatabase db1 = dbhelper.getWritableDatabase();
            //String sqlstr="select * from "+tablename+" where "+ searchField +" = "+keyId;
            int columns=0;
            for(int k=0;k<numsPolygon;k++){
                String sqlstr = "select * from " + tablename + " where 经度>" + maxminGpsList.get(k)[0] + " and 经度<" + maxminGpsList.get(k)[1] + " and 纬度>" + maxminGpsList.get(k)[2] + " and 纬度<" + maxminGpsList.get(k)[3] + " order by id";
                Cursor cursor_res = db1.rawQuery(sqlstr, null);
                if(cursor_res.getCount()==0){
                    System.out.println("数据库表"+tablename+"查询结果条数为0");

                }else{
                    columns=cursor_res.getColumnCount();
                    String[] columnNames=new String[columns];
                    columnNames=cursor_res.getColumnNames();
                    if(k==0||mSearchResult.size()==0){
                        mSearchResult.add(columnNames);
                    }
                    while (cursor_res.moveToNext()) {

                        double entrylong=0.0,entrylat=0.0;
                        String[] searchRes=new String[columns];
                        for(int i=0;i<columns;i++){
                            if(columnNames[i].equals("经度")){
                                entrylong=cursor_res.getDouble(i);
                            }else if(columnNames[i].equals("纬度")){
                                entrylat=cursor_res.getDouble(i);
                            }
                            if(k==0||mSearchResult.size()<=1){
                                fieldsType.add(cursor_res.getType(i));
                            }

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
                        if(SpatialRelationUtil.isPolygonContainsPoint(polygonPointsList_Gps.get(k),new LatLng(entrylat,entrylong))) {
                            mSearchResult.add(searchRes);
                        }
                    }
                }
                if (cursor_res != null) {
                    try {
                        cursor_res.close();
                    } catch (Exception e) {
                    }
                }
            }
            db1.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("数据库表查询失败");
        }
        return mSearchResult;
    }

    public static String staticByColumn(List<String[]> staticArray,int columnIndex,String func,List<Integer> colunmType){
        String staticRes="";
        List<Integer> intList=new ArrayList<>();
        List<Float> floatList=new ArrayList<>();
        List<String> strList=new ArrayList<>();
        List<Boolean> booleanList=new ArrayList<>();
        switch (colunmType.get(columnIndex)){
            case FIELD_TYPE_NULL:
                break;
            case FIELD_TYPE_INTEGER:
                for(int i=1;i<staticArray.size();i++){
                    intList.add(Integer.parseInt(staticArray.get(i)[columnIndex]));
                }
                break;
            case FIELD_TYPE_FLOAT:
                for(int i=1;i<staticArray.size();i++){
                    floatList.add(Float.parseFloat(staticArray.get(i)[columnIndex]));
                }
                break;
            case FIELD_TYPE_STRING:
                for(int i=1;i<staticArray.size();i++){
                    strList.add(staticArray.get(i)[columnIndex]);
                }
                break;
            case FIELD_TYPE_BLOB:
                for(int i=1;i<staticArray.size();i++){
                    booleanList.add(Boolean.parseBoolean(staticArray.get(i)[columnIndex]));
                }
                break;
            default:
                break;
        }

        if(func.equals("计数")){
            if(intList.size()>0){
                staticRes=String.valueOf(intList.size());
            }else if(floatList.size()>0){
                staticRes=String.valueOf(floatList.size());
            }else if(strList.size()>0){
                staticRes=String.valueOf(strList.size());
            }else if(booleanList.size()>0){
                staticRes=String.valueOf(booleanList.size());
            }
        }else if(func.equals("均值")){
            if(intList.size()>0){
                int total = 0;
                for(int i=0;i<intList.size();i++){
                    total += intList.get(i);
                }
                staticRes=String.valueOf(total/intList.size());
            }else if(floatList.size()>0){
                float total = 0;
                for(int i=0;i<floatList.size();i++){
                    total += floatList.get(i);
                }
                staticRes=String.valueOf(total/floatList.size());
            }else if(strList.size()>0){
                try{
                    float total = 0;
                    for(int i=0;i<strList.size();i++){
                        total = total+ Float.parseFloat(strList.get(i));
                    }
                    staticRes=String.valueOf(total/strList.size());
                }catch (Exception e){
                    e.printStackTrace();
                    staticRes="类型不符，无法求均值";
                }
            }else if(booleanList.size()>0){
                staticRes="Boolean类型，无法求均值";
            }
        }else if(func.equals("求和")){
            if(intList.size()>0){
                int total = 0;
                for(int i=0;i<intList.size();i++){
                    total += intList.get(i);
                }
                staticRes=String.valueOf(total);
            }else if(floatList.size()>0){
                float total = 0;
                for(int i=0;i<floatList.size();i++){
                    total += floatList.get(i);
                }
                staticRes=String.valueOf(total);
            }else if(strList.size()>0){
                try{
                    float total = 0;
                    for(int i=0;i<strList.size();i++){
                        total = total+ Float.parseFloat(strList.get(i));
                    }
                    staticRes=String.valueOf(total);
                }catch (Exception e){
                    e.printStackTrace();
                    staticRes="类型不符，无法求均值";
                }
            }else if(booleanList.size()>0){
                staticRes="Boolean类型，无法求和";
            }
        }
        return staticRes;
    }
}
