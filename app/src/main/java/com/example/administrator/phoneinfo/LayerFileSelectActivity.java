package com.example.administrator.phoneinfo;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LayerFileSelectActivity extends AppCompatActivity {

    private MyApplication myApplication;
    private List<String> layerFileName=new ArrayList<>();
    private LinearLayout linearLayoutLayerFiles;
    private List<CheckBox> checkboxLayerFiles=new ArrayList<>();
    private List<CheckBox> checkboxFileFields_lon=new ArrayList<>();
    private List<CheckBox> checkboxFileFields_lat=new ArrayList<>();
    private List<CheckBox> checkboxFileFields_info=new ArrayList<>();
    private Button buttonLayerFileSelect;
    private Button buttonKmlSelect;
    private String[][] layerFileArray;
    private String[][] kmlArray;

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what==8) {
                for(int i=0;i<layerFileName.size();i++){
                    CheckBox checkBoxFile=new CheckBox(LayerFileSelectActivity.this);
                    checkBoxFile.setText(layerFileName.get(i));
                    checkBoxFile.setChecked(false);
                    checkboxLayerFiles.add(checkBoxFile);
                    linearLayoutLayerFiles.addView(checkBoxFile);
                }
            }else if(msg.what==3){
                Intent intent1 = new Intent(LayerFileSelectActivity.this, MapViewActivity.class);
                setResult(RESULT_OK,intent1); //这里有2个参数(int resultCode, Intent intent)
                finish();
            }else if(msg.what==4){
                Toast.makeText(LayerFileSelectActivity.this,"目前支持.kmz/.kml/.xls文件导入生成自定义图层，请选择正确的文件类型",Toast.LENGTH_LONG).show();
            }
            else if(msg.what==1){
                buttonKmlSelect.performClick();
            }
            else if(msg.what==2){
                ProgressDlgUtil.showProgressDlg("正在加载图层文件，请耐心等候...",LayerFileSelectActivity.this);
            }
            else if(msg.what==0){
                ProgressDlgUtil.stopProgressDlg();
                Toast.makeText(LayerFileSelectActivity.this, "成功导入图层！", Toast.LENGTH_LONG).show();
            }
            else if(msg.what==9){
                for(int i=0;i<checkboxLayerFiles.size();i++){
                    checkboxLayerFiles.get(i).setVisibility(View.GONE);
                }
                buttonLayerFileSelect.setVisibility(View.GONE);
                TableRow tbrow_lon = new TableRow(LayerFileSelectActivity.this);
                tbrow_lon.setBackgroundColor(0xFF00EE00);
                tbrow_lon.setPadding(10,10,10,10);
                TextView tx_lon = new TextView(LayerFileSelectActivity.this);
                tx_lon.setText("1.请选择经度字段:");
                tbrow_lon.addView(tx_lon);
                linearLayoutLayerFiles.addView(tbrow_lon);
                checkboxFileFields_lon=creatCheckBoxsInTableRow(layerFileArray[0],LayerFileSelectActivity.this,linearLayoutLayerFiles);

                TableRow tbrow_lat = new TableRow(LayerFileSelectActivity.this);
                tbrow_lat.setBackgroundColor(0xFF00EE00);
                tbrow_lat.setPadding(10,10,10,10);
                TextView tx_lat = new TextView(LayerFileSelectActivity.this);
                tx_lat.setText("2.请选择纬度字段:");
                tbrow_lat.addView(tx_lat);
                linearLayoutLayerFiles.addView(tbrow_lat);
                checkboxFileFields_lat=creatCheckBoxsInTableRow(layerFileArray[0],LayerFileSelectActivity.this,linearLayoutLayerFiles);

                TableRow tbrow_info = new TableRow(LayerFileSelectActivity.this);
                tbrow_info.setBackgroundColor(0xFF00EE00);
                tbrow_info.setPadding(10,10,10,10);
                TextView tx_info = new TextView(LayerFileSelectActivity.this);
                tx_info.setText("3.请选择信息显示字段:");
                tbrow_info.addView(tx_info);
                linearLayoutLayerFiles.addView(tbrow_info);
                checkboxFileFields_info=creatCheckBoxsInTableRow(layerFileArray[0],LayerFileSelectActivity.this,linearLayoutLayerFiles);
                TableRow tbrow_layername = new TableRow(LayerFileSelectActivity.this);

                tbrow_layername.setBackgroundColor(0xFF00EE00);
                tbrow_layername.setPadding(10,10,10,10);
                TextView tx_layername = new TextView(LayerFileSelectActivity.this);
                tx_layername.setText("4.请为您的图层取个名字:");
                tbrow_layername.addView(tx_layername);
                linearLayoutLayerFiles.addView(tbrow_layername);
                final EditText et_layername=new EditText(LayerFileSelectActivity.this);
                et_layername.setLayoutParams(new TextSwitcher.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                et_layername.setText("");
                et_layername.setSingleLine(true);
                et_layername.setHint("图层名称");
                et_layername.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayoutLayerFiles.addView(et_layername);

                Button buttonCreateLayer=new Button(LayerFileSelectActivity.this);
                buttonCreateLayer.setText("5.创建图层");
                buttonCreateLayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            //轮询经度字段
                            String field_lon = "", field_lat = "", layername = "";
                            int cindex_field_lon = -1, cindex_field_lat = -1;
                            List<Integer> colIndexList=new ArrayList<>();
                            List<String> keyFieldList = new ArrayList<>();
                            List<String> valFieldList = new ArrayList<>();
                            layername = et_layername.getText().toString().trim();
                            if (layername.equals("")) {
                                Toast.makeText(LayerFileSelectActivity.this, "图层名不能为空，请添加图层名称！", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(!GroupManagerActivity.userInputCheck(layername)){
                                Toast.makeText(LayerFileSelectActivity.this, "图层名含有特殊字符，请重新命名！", Toast.LENGTH_LONG).show();
                                return;
                            }

                            for (int i = 0; i < myApplication.userLayerNames.size(); i++) {
                                if (layername.equals(myApplication.userLayerNames.get(i))) {
                                    Toast.makeText(LayerFileSelectActivity.this, "存在相同的图层名:" + layername + "，请修改图层名称！", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            keyFieldList.add("userlayername_" + layername);
                            valFieldList.add(layername);
                            for (int i = 0; i < checkboxFileFields_lon.size(); i++) {
                                if (checkboxFileFields_lon.get(i).isChecked()) {
                                    field_lon = checkboxFileFields_lon.get(i).getText().toString();
                                    cindex_field_lon = i;
                                    colIndexList.add(cindex_field_lon);
                                    keyFieldList.add("userlayerlon_" + layername);
                                    valFieldList.add(field_lon);
                                    break;
                                }
                            }
                            for (int i = 0; i < checkboxFileFields_lat.size(); i++) {
                                if (checkboxFileFields_lat.get(i).isChecked()) {
                                    field_lat = checkboxFileFields_lat.get(i).getText().toString();
                                    cindex_field_lat = i;
                                    colIndexList.add(cindex_field_lat);
                                    keyFieldList.add("userlayerlat_" + layername);
                                    valFieldList.add(field_lat);
                                    break;
                                }
                            }
                            for (int i = 0; i < checkboxFileFields_info.size(); i++) {
                                if (checkboxFileFields_info.get(i).isChecked()) {
                                    keyFieldList.add("userlayerinfo_" + layername + "_" + i);
                                    valFieldList.add(checkboxFileFields_info.get(i).getText().toString());
                                    if(!(i==cindex_field_lon||i==cindex_field_lat)){
                                        colIndexList.add(i);
                                    }
                                }
                            }

                            keyFieldList.add("userlayertype_" + layername);
                            valFieldList.add("1");

                            if (field_lon.equals("") || field_lat.equals("")) {
                                Toast.makeText(LayerFileSelectActivity.this, "经度与纬度字段是必选字段，请选择", Toast.LENGTH_LONG).show();
                                return;
                            }
                            for(int i=0;i<valFieldList.size();i++){
                                if(!GroupManagerActivity.userInputCheck(valFieldList.get(i))){
                                    Toast.makeText(LayerFileSelectActivity.this, "您选择的字段名含有特殊字符，无法创建图层！", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            Double lon = Double.parseDouble(layerFileArray[1][cindex_field_lon]);
                            Double lat = Double.parseDouble(layerFileArray[1][cindex_field_lat]);
                            if (lon < 0 || lon > 180 || lat < 0 || lat > 90) {
                                Toast.makeText(LayerFileSelectActivity.this, "您所选的经度或纬度字段设置不符合规范，请确认字段选择是否正确？或者字段值是否正确？", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String[][] tpArray=new String[layerFileArray.length][colIndexList.size()];

                            for(int i=0;i<layerFileArray.length;i++){
                                for(int j=0;j<colIndexList.size();j++){
                                    tpArray[i][j]=layerFileArray[i][colIndexList.get(j)];
                                    //System.out.println("ZGQ:i="+i+" j="+j);
                                }
                            }

                            ProgressDlgUtil.showProgressDlg("正在创建图层，请耐心等候...",LayerFileSelectActivity.this);

                            if(CreateCellInfoDBActivity.createTable(LayerFileSelectActivity.this, tpArray, "tb_userlayer_" + layername)) {
                                AppSettingActivity.saveSettingInfo(getApplicationContext(), "userlayerconfig", keyFieldList, valFieldList);
                                ProgressDlgUtil.stopProgressDlg();

                                AlertDialog.Builder dlg = new AlertDialog.Builder(LayerFileSelectActivity.this);
                                dlg.setTitle("提示");
                                dlg.setMessage(layername + "图层创建成功，请在地图页面图层管理菜单中选择显示!");
                                dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent1 = new Intent(LayerFileSelectActivity.this, MainActivity.class);
                                        startActivity(intent1);
                                    }
                                });
                                dlg.show();
                            }else{
                                ProgressDlgUtil.stopProgressDlg();
                                Toast.makeText(LayerFileSelectActivity.this,"图层创建失败！请检查"+layername+"图层文件的字段与内容是否正确。", Toast.LENGTH_LONG).show();
                                Intent intent2 = new Intent(LayerFileSelectActivity.this, MapViewActivity.class);
                                startActivity(intent2);
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(LayerFileSelectActivity.this,"图层创建失败!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                linearLayoutLayerFiles.addView(buttonCreateLayer);
                /*
                for(int i=0;i<layerFileArray[0].length;i++){
                    CheckBox checkBoxfield=new CheckBox(LayerFileSelectActivity.this);
                    checkBoxfield.setText(layerFileArray[0][i]);
                    checkBoxfield.setChecked(false);
                    checkboxFileFields.add(checkBoxfield);
                    linearLayoutLayerFiles.addView(checkBoxfield);
                }
                */
                ProgressDlgUtil.stopProgressDlg();
            }
            else if(msg.what==10){
                for(int i=0;i<checkboxLayerFiles.size();i++){
                    checkboxLayerFiles.get(i).setVisibility(View.GONE);
                }
                buttonLayerFileSelect.setVisibility(View.GONE);
                TableRow tbrow_layername = new TableRow(LayerFileSelectActivity.this);
                tbrow_layername.setBackgroundColor(0xFF00EE00);
                tbrow_layername.setPadding(10,10,10,10);
                TextView tx_layername = new TextView(LayerFileSelectActivity.this);
                tx_layername.setText("1.请为您的图层取个名字:");
                tbrow_layername.addView(tx_layername);
                linearLayoutLayerFiles.addView(tbrow_layername);

                final EditText et_layername=new EditText(LayerFileSelectActivity.this);
                et_layername.setLayoutParams(new TextSwitcher.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                et_layername.setText("");
                et_layername.setSingleLine(true);
                et_layername.setHint("图层名称");
                et_layername.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayoutLayerFiles.addView(et_layername);

                Button buttonCreateLayer=new Button(LayerFileSelectActivity.this);
                buttonCreateLayer.setText("2.创建图层");
                buttonCreateLayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            //轮询经度字段
                            String field_lon = "经度", field_lat = "纬度", layername = "";
                            List<String> fieldlist_info = new ArrayList<>();
                            List<String> keyFieldList = new ArrayList<>();
                            List<String> valFieldList = new ArrayList<>();
                            layername = et_layername.getText().toString().trim();
                            if (layername.equals("")) {
                                Toast.makeText(LayerFileSelectActivity.this, "图层名不能为空，请添加图层名称！", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(!GroupManagerActivity.userInputCheck(layername)){
                                Toast.makeText(LayerFileSelectActivity.this, "图层名含有特殊字符，请重新命名！", Toast.LENGTH_LONG).show();
                                return;
                            }
                            for (int i = 0; i < myApplication.userLayerNames.size(); i++) {
                                if (layername.equals(myApplication.userLayerNames.get(i))) {
                                    Toast.makeText(LayerFileSelectActivity.this, "存在相同的图层名:" + layername + "，请修改图层名称！", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            keyFieldList.add("userlayername_" + layername);
                            valFieldList.add(layername);
                            keyFieldList.add("userlayerlon_" + layername);
                            valFieldList.add(field_lon);
                            keyFieldList.add("userlayerlat_" + layername);
                            valFieldList.add(field_lat);

                            for (int i = 0; i < kmlArray[0].length; i++) {
                                    keyFieldList.add("userlayerinfo_" + layername + "_" + i);
                                    valFieldList.add(kmlArray[0][i]);
                            }

                            keyFieldList.add("userlayertype_" + layername);
                            valFieldList.add("2");

                            if(CreateCellInfoDBActivity.createTable(LayerFileSelectActivity.this, kmlArray, "tb_userlayer_" + layername)){
                                AppSettingActivity.saveSettingInfo(getApplicationContext(), "userlayerconfig", keyFieldList, valFieldList);

                                AlertDialog.Builder dlg = new AlertDialog.Builder(LayerFileSelectActivity.this);
                                dlg.setTitle("提示");
                                dlg.setMessage(layername + "图层创建成功，请在地图页面图层管理菜单中选择显示!");
                                dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent1 = new Intent(LayerFileSelectActivity.this, MainActivity.class);
                                        startActivity(intent1);
                                    }
                                });
                                dlg.show();
                            }else{
                                Toast.makeText(LayerFileSelectActivity.this,"图层创建失败!", Toast.LENGTH_LONG).show();
                                Intent intent2 = new Intent(LayerFileSelectActivity.this, MapViewActivity.class);
                                startActivity(intent2);
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(LayerFileSelectActivity.this,"图层创建失败!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                linearLayoutLayerFiles.addView(buttonCreateLayer);
                /*
                for(int i=0;i<layerFileArray[0].length;i++){
                    CheckBox checkBoxfield=new CheckBox(LayerFileSelectActivity.this);
                    checkBoxfield.setText(layerFileArray[0][i]);
                    checkBoxfield.setChecked(false);
                    checkboxFileFields.add(checkBoxfield);
                    linearLayoutLayerFiles.addView(checkBoxfield);
                }
                */
                ProgressDlgUtil.stopProgressDlg();
            }
        super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_userdefinelayer);
        myApplication = (MyApplication) getApplicationContext();
        linearLayoutLayerFiles=(LinearLayout)findViewById(R.id.linearlayout_layerfiles);
        buttonKmlSelect=(Button)findViewById(R.id.button_kmlfileselect);
        buttonKmlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Message msg2 = new Message(); //发送取消读取进度条消息
                                msg2.what = 2;
                                handler.sendMessage(msg2);
                                MyKML myKml=new MyKML();
                                myKml.parseKml(myApplication.logFileSavePath,myApplication.ftpSelectLayerFileName);



                                //myKml.getCoordinateList();
                                //myApplication.kmlPolygonList.addAll(myKml.coordinatePolygonList);
                                int num_PolyPoints=myKml.numPolygonPoints;
                                int num_linePoints=myKml.numLinePoints;
                                int num_Points=myKml.numPoints;
                                kmlArray = new String[num_PolyPoints+num_linePoints+num_Points+1][10];
                                kmlArray[0][0]="shape_id";
                                kmlArray[0][1]="shape";
                                kmlArray[0][2]="经度";
                                kmlArray[0][3]="纬度";
                                kmlArray[0][4]="高度";
                                kmlArray[0][5]="document_name";
                                kmlArray[0][6]="folder_name";
                                kmlArray[0][7]="placemark_name";
                                kmlArray[0][8]="shape_color";
                                kmlArray[0][9]="shape_fillcolor";
                                int kmlArrayIndex=1;
                                int shapeid=1;
                                if(myKml.coordinatePolygonList.size()>0){
                                    for(int i=0;i<myKml.coordinatePolygonList.size();i++){
                                        for(int j=0;j<myKml.coordinatePolygonList.get(i).polygonPoints.size();j++){
                                            kmlArray[kmlArrayIndex][0]=String.valueOf(shapeid);
                                            kmlArray[kmlArrayIndex][1]="1";  //Polygon=1，Linestring=2,Point=3
                                            kmlArray[kmlArrayIndex][2]= String.valueOf(myKml.coordinatePolygonList.get(i).polygonPoints.get(j).longitude);
                                            kmlArray[kmlArrayIndex][3]= String.valueOf(myKml.coordinatePolygonList.get(i).polygonPoints.get(j).latitude);
                                            kmlArray[kmlArrayIndex][4]= String.valueOf(myKml.coordinatePolygonList.get(i).polygonPointsHeigh.get(j));
                                            kmlArray[kmlArrayIndex][5]= myKml.coordinatePolygonList.get(i).documentName;
                                            kmlArray[kmlArrayIndex][6]= myKml.coordinatePolygonList.get(i).folderName;
                                            kmlArray[kmlArrayIndex][7]= myKml.coordinatePolygonList.get(i).polygonName;
                                            kmlArray[kmlArrayIndex][8]= myKml.coordinatePolygonList.get(i).color;
                                            kmlArray[kmlArrayIndex][9]= myKml.coordinatePolygonList.get(i).fillcolor;
                                            kmlArrayIndex=kmlArrayIndex+1;
                                        }
                                        shapeid=shapeid+1;
                                    }
                                }

                                if(myKml.coordinateLineList.size()>0){
                                    for(int i=0;i<myKml.coordinateLineList.size();i++){
                                        for(int j=0;j<myKml.coordinateLineList.get(i).linePoints.size();j++){
                                            kmlArray[kmlArrayIndex][0]=String.valueOf(shapeid);
                                            kmlArray[kmlArrayIndex][1]="2";  //Polygon=1，Linestring=2,Point=3
                                            kmlArray[kmlArrayIndex][2]= String.valueOf(myKml.coordinateLineList.get(i).linePoints.get(j).longitude);
                                            kmlArray[kmlArrayIndex][3]= String.valueOf(myKml.coordinateLineList.get(i).linePoints.get(j).latitude);
                                            kmlArray[kmlArrayIndex][4]= String.valueOf(myKml.coordinateLineList.get(i).linePointsHeigh.get(j));
                                            kmlArray[kmlArrayIndex][5]= myKml.coordinateLineList.get(i).documentName;
                                            kmlArray[kmlArrayIndex][6]= myKml.coordinateLineList.get(i).folderName;
                                            kmlArray[kmlArrayIndex][7]= myKml.coordinateLineList.get(i).lineName;
                                            kmlArray[kmlArrayIndex][8]= myKml.coordinateLineList.get(i).color;
                                            kmlArray[kmlArrayIndex][9]= "";
                                            kmlArrayIndex=kmlArrayIndex+1;
                                        }
                                        shapeid=shapeid+1;
                                    }
                                }

                                if(myKml.coordinateList.size()>0){
                                    for(int i=0;i<myKml.coordinateList.size();i++){
                                            kmlArray[kmlArrayIndex][0]=String.valueOf(shapeid);
                                            kmlArray[kmlArrayIndex][1]="3";  //Polygon=1，Linestring=2,Point=3
                                            kmlArray[kmlArrayIndex][2]= String.valueOf(myKml.coordinateList.get(i).x);
                                            kmlArray[kmlArrayIndex][3]= String.valueOf(myKml.coordinateList.get(i).y);
                                            kmlArray[kmlArrayIndex][4]= String.valueOf(myKml.coordinateList.get(i).h);
                                            kmlArray[kmlArrayIndex][5]= myKml.coordinateList.get(i).documentName;
                                            kmlArray[kmlArrayIndex][6]= myKml.coordinateList.get(i).folderName;
                                            kmlArray[kmlArrayIndex][7]= myKml.coordinateList.get(i).name;
                                            kmlArray[kmlArrayIndex][8]= myKml.coordinateList.get(i).color;
                                            kmlArray[kmlArrayIndex][9]= "";

                                        kmlArrayIndex=kmlArrayIndex+1;
                                        shapeid=shapeid+1;
                                    }
                                }

                                Message msg10 = new Message(); //发送取消读取进度条消息
                                msg10.what = 10;
                                handler.sendMessage(msg10);

                                File tpFile=new File(myApplication.logFileSavePath+myApplication.ftpSelectLayerFileName);
                                if(tpFile.exists()){
                                    tpFile.delete();
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        buttonLayerFileSelect=(Button)findViewById(R.id.button_layerfileselect);
        buttonLayerFileSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myApplication.USER_AUTH_INFO.vipUser==0){
                    Toast.makeText(LayerFileSelectActivity.this,"非VIP用户不支持该功能，请联系13868808810开通VIP功能",Toast.LENGTH_LONG).show();
                    return;
                }

                if(checkboxLayerFiles.size()>0){
                    for(int i=0;i<checkboxLayerFiles.size();i++){
                        if(checkboxLayerFiles.get(i).isChecked()){
                            myApplication.ftpSelectLayerFileName=layerFileName.get(i);
                            break;
                        }
                    }
                    if(!myApplication.ftpSelectLayerFileName.equals("")){
                        //Toast.makeText(LayerFileSelectActivity.this,"正在下载图层文件:"+myApplication.ftpSelectLayerFileName,Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Message msg2 = new Message(); //发送读取进度条消息
                                    msg2.what = 2;
                                    handler.sendMessage(msg2);
                                    MyFTP myFTP=new MyFTP();
                                    String ftpdir=myApplication.layerFilesFtpDir;
                                    String savepath=myApplication.logFileSavePath;
                                    String filename=myApplication.ftpSelectLayerFileName;
                                    myFTP.downloadSingleFile(ftpdir,savepath,filename,new MyFTP.DownLoadProgressListener() {
                                        @Override
                                        public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                            //System.out.println("正在下载");
                                            //return;
                                        }
                                    });

                                    if(filename.endsWith(".xls")||filename.endsWith(".XLS")){
                                        layerFileArray=MyLogFile.readExcel(savepath+filename);

                                        if(layerFileArray!=null){
                                            Message msg9 = new Message(); //发送取消读取进度条消息
                                            msg9.what = 9;
                                            handler.sendMessage(msg9);
                                        }
                                        File tpFile=new File(savepath+filename);
                                        if(tpFile.exists()){
                                            tpFile.delete();
                                        }
                                    }else if(filename.endsWith(".xlsx")||filename.endsWith(".XLSX")) {
                                        layerFileArray=CreateCellInfoDBActivity.readXLSX(savepath+filename,myApplication,handler);
                                        if(layerFileArray!=null){
                                            Message msg9 = new Message(); //发送取消读取进度条消息
                                            msg9.what = 9;
                                            handler.sendMessage(msg9);
                                        }
                                        File tpFile=new File(savepath+filename);
                                        if(tpFile.exists()){
                                            tpFile.delete();
                                        }

                                    } else if(filename.endsWith(".kmz")||filename.endsWith(".kml")||filename.endsWith(".KMZ")||filename.endsWith(".KML")){
                                        Message msg1 = new Message(); //发送消息
                                        msg1.what = 1;
                                        handler.sendMessage(msg1);
                                    }else{
                                        Message msg4 = new Message(); //发送取消读取进度条消息
                                        msg4.what = 4;
                                        handler.sendMessage(msg4);
                                    }

                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                    System.out.println("图层文件下载失败！");
                                }
                            }
                        }).start();
                    }

                }else{
                    Toast.makeText(LayerFileSelectActivity.this,"当前服务器上没有图层文件，请先上传图层文件！",Toast.LENGTH_LONG).show();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    FTPFile[] layerFiles=new MyFTP().listFtpServerFiles(myApplication.layerFilesFtpDir);
                    for(int i=0;i<layerFiles.length;i++){
                        layerFileName.add(layerFiles[i].getName());
                    }
                    if(layerFileName.size()>0){
                        Message msg1 = new Message(); //发送开始显示消息
                        msg1.what = 8;
                        handler.sendMessage(msg1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    public List<CheckBox> creatCheckBoxsInTableRow(String[] myString,Context mContext,LinearLayout mLinLy){
        List<CheckBox> mCheckBoxList=new ArrayList<>();
        for(int i=0;i<myString.length;i++){
            CheckBox checkBoxfield=new CheckBox(mContext);
            checkBoxfield.setText(myString[i]);
            checkBoxfield.setChecked(false);
            mCheckBoxList.add(checkBoxfield);
            mLinLy.addView(checkBoxfield);
        }
        return mCheckBoxList;
    }


}

