package com.example.administrator.phoneinfo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;
//import org.apache.http.entity.mime.content.StringBody;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


public class CreateCellInfoDBActivity extends AppCompatActivity {

    public static final String DBNAME1 = "CellInfo.db";//无线小区信息数据库
    public static final String TABLENAME1 = "CellInfo_LTE";//LTE小区信息表
    public static final String TABLENAME_GSM = "CellInfo_GSM";//GSM小区信息表
    public static final String TABLENAME_FDD = "CellInfo_FDD";//FDD小区信息表
    public static final String TABLENAME_NB = "CellInfo_NB";//NB小区信息表


    public static final String TABLENAME_MR = "MR_LTE";//LTE小区信息表

    private MyDatabaseHelper dbhelper;
    private SQLiteDatabase db1;
    private MyApplication myApplication;
    //private static int readProgress;
    private File file=null;
    private String[][] excelArray=new String[0][];
    private List<String> mrFileName=new ArrayList<>();
    private LinearLayout linearLayoutMrFiles;
    private LinearLayout linearLayoutCellFiles;
    private List<CheckBox> checkboxMrFiles=new ArrayList<>();
    private String tipShowFileName="";

    private List<String> cellFileList=new ArrayList<>();
    private List<CheckBox> checkboxCellFiles=new ArrayList<>();
    //private List<String> cellFileListChecked=new ArrayList<>();
    private List<String> cellFileUrlList=new ArrayList<>();

    private boolean flagCellinfoFromUrl=false;

    //private List<ZgqExcelRow> zgqExcelRowsList=new ArrayList<ZgqExcelRow>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.createcellinfodb);
        myApplication = (MyApplication) getApplicationContext();
        dbhelper = new MyDatabaseHelper(this, DBNAME1,TABLENAME1, null, 1);
        Button downExcelButton=(Button) findViewById(R.id.down_excel_button);
        Button downMRExcelButton=(Button) findViewById(R.id.read_MRexcel_button);
        linearLayoutMrFiles=(LinearLayout)findViewById(R.id.linearlayout_mrfiles);
        linearLayoutCellFiles=(LinearLayout)findViewById(R.id.linearlayout_cellfiles);

        cellFileUrlList.add(myApplication.CellInfoUrl_LTE);
        cellFileUrlList.add(myApplication.CellInfoUrl_GSM);
        cellFileUrlList.add(myApplication.CellInfoUrl_FDD);
        cellFileUrlList.add(myApplication.CellInfoUrl_NB);


        //显示小区信息表列表
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    FTPFile[] cellFiles=new MyFTP().listFtpServerFiles(myApplication.cellInfoFtpDir);
                    for(int i=0;i<cellFiles.length;i++){
                        cellFileList.add(cellFiles[i].getName());
                    }
                    if(cellFileList.size()>0){
                        Message msg9 = new Message(); //发送开始下载消息
                        msg9.what = 9;
                        handler.sendMessage(msg9);
                    }else if(cellFileList.size()==0&&myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo")){
                        flagCellinfoFromUrl=true;
                        for(int i=0;i<cellFileUrlList.size();i++){
                            if(!cellFileUrlList.get(i).equals("")){
                                String[] tparray=cellFileUrlList.get(i).split("/");
                                if(tparray.length>0){
                                    cellFileList.add(tparray[tparray.length-1]);
                                }
                            }
                        }

                        Message msg9 = new Message(); //发送开始下载消息
                        msg9.what = 9;
                        handler.sendMessage(msg9);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //显示MR文件列表
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    FTPFile[] mrFiles=new MyFTP().listFtpServerFiles(myApplication.mrFilesFtpDir);
                    for(int i=0;i<mrFiles.length;i++){
                        mrFileName.add(mrFiles[i].getName());
                    }
                    if(mrFileName.size()>0){
                        Message msg1 = new Message(); //发送开始下载消息
                        msg1.what = 8;
                        handler.sendMessage(msg1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        /*
        Button createDatabaseButton1 = (Button) findViewById(R.id.create_database);
        createDatabaseButton1.setVisibility(View.INVISIBLE);
        createDatabaseButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        */
        //*****************************************************************************
        //导入格式为 .xls .xlsx



        final Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
        intent1.setType("application/*");//设置类型
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        final Button readExcelButton1 = (Button) findViewById(R.id.read_excel_button);
        readExcelButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myApplication.USER_AUTH_INFO.vipUser==0){
                    Toast.makeText(CreateCellInfoDBActivity.this,"非VIP用户不支持该功能，请联系13868808810开通VIP功能",Toast.LENGTH_LONG).show();
                    return;
                }
                startActivityForResult(intent1, 1);
            }
        });
        //联网更新基站信息
        downExcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myApplication.USER_AUTH_INFO.vipUser==0){
                    Toast.makeText(CreateCellInfoDBActivity.this,"非VIP用户不支持该功能，请联系13868808810开通VIP功能",Toast.LENGTH_LONG).show();
                    return;
                }

                final List<String> cellFileCheckedList=new ArrayList<>();
                final List<String> cellUrlCheckedList=new ArrayList<>();
                for(int i=0;i<checkboxCellFiles.size();i++){
                    if(checkboxCellFiles.get(i).isChecked()){
                        cellFileCheckedList.add(String.valueOf(checkboxCellFiles.get(i).getText()));
                        if(myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo")){
                            cellUrlCheckedList.add(cellFileUrlList.get(i));
                        }
                    }
                }
                if(cellFileCheckedList.size()==0){
                    Toast.makeText(CreateCellInfoDBActivity.this,"请选择要导入的基站信息表",Toast.LENGTH_LONG).show();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<cellFileCheckedList.size();i++){
                            String cellFileName=cellFileCheckedList.get(i);
                            String cellInfoURL="";
                            if(myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo")){
                                cellInfoURL=cellUrlCheckedList.get(i);
                            }
                            tipShowFileName=cellFileName;

                            Message msg10 = new Message();
                            msg10.what = 10;   //发送开始导入消息
                            handler.sendMessage(msg10);

                            String newFilename=myApplication.logFileSavePath+cellFileName;
                            File filepath = new File(myApplication.logFileSavePath);
                            if (!filepath.exists()) {
                                filepath.mkdirs();
                            }

                            file = new File(newFilename);
                            //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
                            if(file.exists())
                            {
                                file.delete();
                            }

                            try {
                                // 构造URL
                                //    String cellInfoURL="http://10.60.104.109/wyxf_0228.xls";
                                if(myApplication.USER_AUTH_INFO.groupName.equals("wzmcc_cellinfo")){

                                    URL url = new URL(cellInfoURL);
                                    // 打开连接
                                    URLConnection con = url.openConnection();
                                    //获得文件的长度
                                    //int contentLength = con.getContentLength();
                                    //System.out.println("ZGQ:基站信息表文件长度 :"+contentLength);
                                    // 输入流
                                    InputStream is = con.getInputStream();
                                    // 1K的数据缓冲
                                    byte[] bs = new byte[1024];
                                    // 读取到的数据长度
                                    int len;
                                    // 输出的文件流
                                    OutputStream os = new FileOutputStream(newFilename);
                                    // 开始读取
                                    while ((len = is.read(bs)) != -1) {
                                        os.write(bs, 0, len);
                                    }
                                    // 完毕，关闭所有链接
                                    os.close();
                                    is.close();

                                }else {
                                    //cellInfoURL = "ftp://" + myApplication.mrFtpUser + ":" + myApplication.mrFtpPassword + "@" + myApplication.mrFtpServerIp + ":" + String.valueOf(myApplication.mrFtpServerPort) + myApplication.cellInfoFtpDir + cellFileName;
                                    //cellInfoURL= URLEncoder.encode(cellInfoURL, "UTF-8");
                                            MyFTP myFTP = new MyFTP();
                                            try {
                                                myFTP.downloadSingleFile(myApplication.cellInfoFtpDir, myApplication.logFileSavePath, cellFileName, new MyFTP.DownLoadProgressListener() {
                                                    @Override
                                                    public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                                        if(currentStep==FtpActivity.FTP_DISCONNECT_SUCCESS){

                                                        }else if(currentStep==FtpActivity.FTP_DOWN_FAIL){
                                                            Message msgDownFail = new Message(); //发送导入失败消息
                                                            msgDownFail.what = 4;
                                                            handler.sendMessage(msgDownFail);
                                                        }
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                }


                                    Message msgDownOk = new Message(); //发送下载完成消息
                                    //tipShowFileName="LTE基站";
                                    msgDownOk.what = 3;
                                    handler.sendMessage(msgDownOk);

                                    Message msg1 = new Message();
                                    msg1.what = 0;   //发送开始导入消息
                                    handler.sendMessage(msg1);

                                    String[][] excelArray;
                                    if(newFilename.endsWith(".xlsx")||newFilename.endsWith(".XLSX")){
                                        excelArray = readXLSX(newFilename,myApplication,handler);
                                    } else{
                                        excelArray = readExcel(newFilename);
                                    }

                                    String tableName="";
                                    if(cellFileName.contains("GSM")||cellFileName.contains("gsm")||cellFileName.contains("2g")||cellFileName.contains("2G")){
                                        tableName=TABLENAME_GSM;
                                    }else if(cellFileName.contains("FDD")){
                                        tableName=TABLENAME_FDD;
                                    }else if(cellFileName.contains("NB")){
                                        tableName=TABLENAME_NB;
                                    }else {
                                        tableName=TABLENAME1;
                                    }

                                    createTable(CreateCellInfoDBActivity.this,excelArray,tableName);

                                    myApplication.flagCellInfoLteExist=true;
                                    Message msgSuc = new Message(); //发送成功导入消息
                                    msgSuc.what = 1;
                                    handler.sendMessage(msgSuc);
                                Thread.sleep(1000);

                            } catch (Exception e) {
                                    e.printStackTrace();
                                    Message msgDownFail = new Message(); //发送导入失败消息
                                    msgDownFail.what = 4;
                                    handler.sendMessage(msgDownFail);
                                    if(file!=null){
                                        if(file.exists())
                                        {
                                            file.delete();
                                        }
                                    }
                            }

                        }
                    }
                }).start();

            }
        });
        //联网更新MR信息
        downMRExcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myApplication.USER_AUTH_INFO.vipUser==0){
                    Toast.makeText(CreateCellInfoDBActivity.this,"非VIP用户不支持该功能，请联系13868808810开通VIP功能",Toast.LENGTH_LONG).show();
                    return;
                }
                //ProgressDlgUtil.showProgressDlg("正在下载MR信息表，请稍等....",CreateCellInfoDBActivity.this);
                final List<String> mrAreaCheckedList=new ArrayList<>();
                for(int i=0;i<checkboxMrFiles.size();i++){
                    if(checkboxMrFiles.get(i).isChecked()){
                        mrAreaCheckedList.add(String.valueOf(checkboxMrFiles.get(i).getText()));
                    }
                }
                if(mrAreaCheckedList.size()==0){
                    Toast.makeText(CreateCellInfoDBActivity.this,"请选择导入区域",Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //String newFilename=myApplication.logFileSavePath+"MR.xls";
                        excelArray=new String[1000000][];
                        try {
                            boolean firstMrFile=true;
                            int excelIndex=0;
                            for(int i=0;i<mrAreaCheckedList.size();i++) {
                                tipShowFileName=mrAreaCheckedList.get(i);
                                Message msgStart = new Message(); //发送开始下载消息
                                msgStart.what = 6;
                                handler.sendMessage(msgStart);

                                String newFilename = mrAreaCheckedList.get(i);
                                MyFTP myFTP=new MyFTP();
                                String ftpdir=myApplication.mrFilesFtpDir;
                                String savepath=myApplication.logFileSavePath;
                                myFTP.downloadSingleFile(ftpdir,savepath,newFilename,new MyFTP.DownLoadProgressListener() {
                                    @Override
                                    public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                        //System.out.println("正在下载");
                                        //return;
                                    }
                                });
                                /*
                                file = new File(newFilename);
                                //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
                                if (file.exists()) {
                                    file.delete();
                                }
                                // 构造URL
                                //String cellInfoURL = "http://120.199.120.85:50080/wzgjgl_mobile/UploadFiles_201701/cellquery/MR_all/" + mrAreaCheckedList.get(i);
                                String cellInfoURL = "ftp://"+myApplication.mrFtpUser+":"+myApplication.mrFtpPassword+"@"+myApplication.mrFtpServerIp+":"+String.valueOf(myApplication.mrFtpServerPort)+myApplication.mrFilesFtpDir + mrAreaCheckedList.get(i);
                                URL url = new URL(cellInfoURL);
                                // 打开连接
                                URLConnection con = url.openConnection();
                                //获得文件的长度
                                int contentLength = con.getContentLength();
                                //System.out.println("ZGQ:MR信息表文件长度 :" + contentLength);
                                // 输入流
                                InputStream is = con.getInputStream();
                                // 1K的数据缓冲
                                byte[] bs = new byte[1024];
                                // 读取到的数据长度
                                int len;
                                // 输出的文件流
                                OutputStream os = new FileOutputStream(newFilename);
                                // 开始读取
                                while ((len = is.read(bs)) != -1) {
                                    os.write(bs, 0, len);
                                }
                                // 完毕，关闭所有链接
                                os.close();
                                is.close();
                                */

                                Message msgDownOk = new Message(); //发送下载完成消息
                                msgDownOk.what = 3;
                                handler.sendMessage(msgDownOk);

                                Message msg1 = new Message();
                                msg1.what = 0;   //发送开始导入消息
                                handler.sendMessage(msg1);

                                String[][] tempExcelArray;
                                if(newFilename.endsWith(".xlsx")||newFilename.endsWith(".XLSX")){
                                    tempExcelArray = readXLSX(savepath+newFilename,myApplication,handler);
                                } else{
                                    tempExcelArray=readExcel(savepath+newFilename);
                                }

                                if(firstMrFile){
                                    //excelArray=tempExcelArray;
                                    System.arraycopy(tempExcelArray,0,excelArray,excelIndex,tempExcelArray.length);
                                    excelIndex=tempExcelArray.length;
                                    firstMrFile=false;
                                    //try{
                                    //    file = new File(savepath+newFilename);
                                    //}catch(Exception e){
                                    //    e.printStackTrace();
                                    //}
                                }
                                else{
                                    System.arraycopy(tempExcelArray,1,excelArray,excelIndex,tempExcelArray.length-1);
                                    excelIndex=excelIndex+tempExcelArray.length-1;
                                    /*
                                    for(int k=1;k<tempExcelArray.length;k++){
                                        excelArray[excelIndex]=tempExcelArray[k];
                                        excelIndex=excelIndex+1;
                                    }*/
                                }
                                Message msg7 = new Message();
                                msg7.what = 7;   //发送导入完成消息
                                handler.sendMessage(msg7);

                                File tpFile=new File(savepath+newFilename);
                                if(tpFile.exists()){
                                    tpFile.delete();
                                }

                            }
                            //String[][] excelArray=readExcelAll(newFilename);
                            Message msg1 = new Message();
                            tipShowFileName="MR";
                            msg1.what = 0;   //发送开始导入消息
                            handler.sendMessage(msg1);
                            String[][] excelArrayResult=new String[excelIndex][];
                            System.arraycopy(excelArray,0,excelArrayResult,0,excelIndex);
                            createTable(CreateCellInfoDBActivity.this,excelArrayResult,TABLENAME_MR);
                            excelArray=null;
                            Message msgSuc = new Message(); //发送成功导入消息
                            msgSuc.what = 1;
                            handler.sendMessage(msgSuc);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Message msgDownFail = new Message(); //发送失败消息
                            msgDownFail.what = 4;
                            handler.sendMessage(msgDownFail);
                        }

                    }
                }).start();
            }
        });

    }

    private
    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            //ProgressDialog myProgressDialog=null;
            if (msg.what == 1) {
                ProgressDlgUtil.stopProgressDlg();
                AlertDialog.Builder dlg=new AlertDialog.Builder(CreateCellInfoDBActivity.this);
                dlg.setTitle("提示");
                dlg.setMessage("恭喜!信息表已成功导入!");
                dlg.setPositiveButton("确定",null);
                dlg.show();
                if(file!=null){
                    if(file.exists())
                    {
                        file.delete();
                        System.out.println("ZGQ:excel已经删除");
                    }
                }
            } else if (msg.what==0) {
                ProgressDlgUtil.showProgressDlg("正在导入"+tipShowFileName+"信息表，耗时较长，请耐心等待....", CreateCellInfoDBActivity.this);
            }else if(msg.what==2){
                ProgressDlgUtil.stopProgressDlg();
                Toast.makeText(CreateCellInfoDBActivity.this,"导入错误! 请检查信息表存储位置是否正确? 或者信息表中字段与数据是否异常?",Toast.LENGTH_LONG).show();
            }else if(msg.what==3){
                ProgressDlgUtil.stopProgressDlg();
                Toast.makeText(CreateCellInfoDBActivity.this,tipShowFileName+"信息表下载成功",Toast.LENGTH_LONG).show();
            } else if(msg.what==4){
                ProgressDlgUtil.stopProgressDlg();
                Toast.makeText(CreateCellInfoDBActivity.this,tipShowFileName+"信息表下载失败，请检查手机是否正常联网?"+tipShowFileName+"信息表是否正确存放于服务器",Toast.LENGTH_LONG).show();
            } else if(msg.what==531){
                ProgressDlgUtil.progressDlg.setMessage(tipShowFileName+"已经读取"+myApplication.readProgress+"条数据....");
                //myProgressDialog.setMessage("已经读取"+readProgress+"条基站数据");
            } else if (msg.what==6){
                ProgressDlgUtil.showProgressDlg("正在下载"+tipShowFileName+"信息表，请稍后....",CreateCellInfoDBActivity.this);
            }else if (msg.what==7) {
                ProgressDlgUtil.stopProgressDlg();
                Toast.makeText(CreateCellInfoDBActivity.this,tipShowFileName+"信息表已经导入",Toast.LENGTH_SHORT).show();
            }else if (msg.what==8) {
                for(int i=0;i<mrFileName.size();i++){
                    CheckBox checkBoxMr=new CheckBox(CreateCellInfoDBActivity.this);
                    checkBoxMr.setText(mrFileName.get(i));
                    checkBoxMr.setChecked(false);
                    checkboxMrFiles.add(checkBoxMr);
                    linearLayoutMrFiles.addView(checkBoxMr);
                    //checkboxMrFiles.get(i).infla(CreateCellInfoDBActivity.this);
                }
            }else if (msg.what==9) {
                for(int i=0;i<cellFileList.size();i++){
                    CheckBox checkBoxCellFile=new CheckBox(CreateCellInfoDBActivity.this);
                    checkBoxCellFile.setText(cellFileList.get(i));
                    checkBoxCellFile.setChecked(false);
                    checkboxCellFiles.add(checkBoxCellFile);
                    linearLayoutCellFiles.addView(checkBoxCellFile);
                    //checkboxMrFiles.get(i).infla(CreateCellInfoDBActivity.this);
                }
            }else if (msg.what==10) {
                ProgressDlgUtil.showProgressDlg("正在下载"+tipShowFileName+"信息表，请稍等....",CreateCellInfoDBActivity.this);
            }
        }
    };

    private String excelFilePath="";
    //然后进入系统的文件管理,选择文件后
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            System.out.println("ZGQ:选择的文件Uri = " + data.toString());
            //通过Uri获取真实路径
            //final String excelPath = getRealFilePath(this, data.getData());
            final String excelPath = GetPathFromUri4kitkat.getPath(this,data.getData());

            System.out.println("ZGQ:excelPath = " + excelPath);//    /storage/emulated/0/test.xls
            if (excelPath.contains(".xls") || excelPath.contains(".xlsx")|| excelPath.contains(".XLS")|| excelPath.contains(".XLSX")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("是否确定读取Excel文件?");
                builder1.setTitle("提示");
                builder1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //载入excel
                        Toast.makeText(CreateCellInfoDBActivity.this,"正在加载Excel中...",Toast.LENGTH_LONG).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Message msg1 = new Message();
                                    tipShowFileName="基站";
                                    msg1.what = 0;   //发送开始导入消息
                                    handler.sendMessage(msg1);
                                    String[][] excelArray1;
                                    String filetype=excelPath.substring(excelPath.length()-4,excelPath.length());
                                    if (filetype.contains(".xls") || filetype.contains(".XLS")) {
                                        excelArray1 = readExcel(excelPath);
                                    }else{
                                        excelArray1 = readXLSX(excelPath,myApplication,handler);
                                    }
                                    createTable(CreateCellInfoDBActivity.this,excelArray1,TABLENAME1);
                                    Message msgSuc = new Message(); //发送成功导入消息
                                    msgSuc.what = 1;
                                    handler.sendMessage(msgSuc);
                                } catch (Exception e){
                                    e.printStackTrace();
                                    Message msgErrer = new Message(); //发送成功导入消息
                                    msgErrer.what = 2;
                                    handler.sendMessage(msgErrer);
                                }

                            }
                        }).start();
                        dialog.dismiss();

                        //zgqExcelRowsList.clear();
                    }
                });
                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder1.create().show();
            } else {
                Toast.makeText(this, "此文件不是excel2003格式", Toast.LENGTH_LONG).show();
            }
        }
    }

    //读取Excel表，按行读取的内容存储在List<ZgqExcelRow>对象中
    public String[][] readExcel(String excelPath) {

        String[][] zgqExcelArray = new String[0][];
        InputStream input = null;
        try {
            input = new FileInputStream(new File(excelPath));
            POIFSFileSystem fs = new POIFSFileSystem(input);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            // Iterate over each row in the sheet
            Iterator<Row> rows = sheet.rowIterator();
            zgqExcelArray = new String[sheet.getLastRowNum()+1][];

            int i = 0,showProgress=0,numsCol=0;
            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow) rows.next();
                //int s = row.getPhysicalNumberOfCells();
                if(i==0){
                    numsCol=row.getPhysicalNumberOfCells();;
                }
                zgqExcelArray[i] = new String[numsCol];



                //Iterator<Cell> cells = row.cellIterator();
                //j=0;


                for(int j=0;j<numsCol;j++){

                //while (cells.hasNext()) {
                    //HSSFCell cell = (HSSFCell) cells.next();

                    HSSFCell cell = row.getCell(j);
                    if(cell==null){
                        zgqExcelArray[i][j]="";
                        continue;
                    }

                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            //自定操作
                            if (DateUtil.isCellDateFormatted(cell)) {
                                zgqExcelArray[i][j] = String.valueOf(cell.getDateCellValue());
                                } else {
                                cell.setCellType(Cell.CELL_TYPE_STRING);
                                zgqExcelArray[i][j]  = cell.getStringCellValue();
                                //System.out.println("ZGQ:numeric= " + cell.getStringCellValue());
                            }
                            break;
                        case HSSFCell.CELL_TYPE_STRING:
                            //System.out.println("ZGQ:string= " + cell.getStringCellValue());
                            //自定操作,我这里写入姓名
                            zgqExcelArray[i][j] = (String)cell.getStringCellValue();
                            break;
                        case HSSFCell.CELL_TYPE_BOOLEAN:
                            //System.out.println("ZGQ:boolean= " + cell.getBooleanCellValue());
                            zgqExcelArray[i][j] = String.valueOf(cell.getBooleanCellValue());
                            break;
                        case HSSFCell.CELL_TYPE_FORMULA:
                            //System.out.println("ZGQ:formula= " + cell.getCellFormula());
                            zgqExcelArray[i][j] = String.valueOf(cell.getCellFormula());
                            break;
                        default:
                            zgqExcelArray[i][j]="";
                            System.out.println("ZGQ:unsuported sell type");
                            break;
                    }

                    //j = j + 1;

                }
                i = i + 1;
                showProgress=showProgress+1;
                if(showProgress==1000){
                    showProgress=0;
                    myApplication.readProgress=i;
                    Message msgProgress = new Message(); //发送
                    msgProgress.what = 531;
                    handler.sendMessage(msgProgress);
                }

            }
            //System.out.println("ZGQ: " + stu.siteName + stu.cellName + stu.tacNum + " " + stu.enodeBId + " " + stu.cellId + " " + stu.pciNum + stu.coverArea + stu.siteLongitude + " " + stu.siteLatitude + " " + stu.antAzimuth + " " + stu.antBandWidth);
            //zgqExcelRowsList.add(stu);
        } catch (IOException ex) {
            ex.printStackTrace();
            if(zgqExcelArray!=null){zgqExcelArray = new String[0][0];}
        }finally {
            try {
                if (input!=null) {
                    input.close();
                    System.out.println("ZGQ:inputStream is closed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //刷新列表
        //getAllRows();
        return zgqExcelArray;
    }
/*
    public String[][] readExcelAll(String excelPath){
        String[][] zgqExcelArray = new String[0][];
        try {
            OPCPackage pkg = OPCPackage.open(new File(excelPath));
            XSSFWorkbook wb = new XSSFWorkbook(pkg);
            Sheet sheet = wb.getSheetAt(0);
            zgqExcelArray = new String[sheet.getLastRowNum()+1][];
            for (Row row : sheet) {
                for (Cell cell : row) {
                    String a =cell.getStringCellValue();// Do something here
                }
            }
            pkg.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return zgqExcelArray;
    }
*/
    /*
    private void createTable(String[][] excelContent,String mTableName) {

        int rowNums =excelContent.length;
        int colNums=excelContent[0].length;

        if (excelContent!= null) {
            db1 = dbhelper.getWritableDatabase();
            Cursor cursor_tbname = db1.rawQuery("select name from sqlite_master where type='table' order by name", null);
            List<String> array_tbName = new ArrayList<String>();
            String[] tablefields=new String[colNums];
            int i=0;
            for(int j=0;j<=colNums-1;j++){
                tablefields[j]=excelContent[i][j];
            }
            while (cursor_tbname.moveToNext()) {
                //遍历数据库中所有表的表名
                array_tbName.add(cursor_tbname.getString(0));
                System.out.println("ZGQ:tablename= " + cursor_tbname.getString(0));
            }
            db1.beginTransaction();
            if (array_tbName.contains(mTableName)) {
                db1.execSQL("drop table " + mTableName);
            }
            String str="";
            String insertstr="";
            String valueX="";
            int j;
            for(j=0;j<=(tablefields.length-2);j++) {
                    if (tablefields[j].contains("经度")||tablefields[j].contains("纬度")){
                        str= str + tablefields[j]+" real, ";
                    }else if(tablefields[j].contains("TAC")||tablefields[j].contains("ENBID")||tablefields[j].contains("CELLID")||tablefields[j].contains("PCI")||tablefields[j].contains("方位角")||tablefields[j].contains("天线水平波瓣角")||tablefields[j].contains("弱覆盖MR总条数"))
                    {
                        str= str + tablefields[j]+" integer, ";
                    }else{
                        str= str + tablefields[j]+" text, ";
                    }
                    insertstr=insertstr+tablefields[j]+",";
                    valueX=valueX+"?,";
            }

            if (tablefields[j].contains("经度")||tablefields[j].contains("纬度")){
                str= str + tablefields[j]+" real";
            }else if(tablefields[j].contains("TAC")||tablefields[j].contains("ENBID")||tablefields[j].contains("CELLID")||tablefields[j].contains("PCI")||tablefields[j].contains("方位角")||tablefields[j].contains("天线水平波瓣角")||tablefields[j].contains("弱覆盖MR总条数"))
            {
                str= str + tablefields[j]+" integer";
            }else{
                str= str + tablefields[j]+" text";
            }
            insertstr=insertstr+tablefields[j];
            valueX=valueX+"?";

            System.out.println("ZGQ:"+str);
            String sql_Create_Table="create table " + mTableName + " (id integer primary key autoincrement, "+str+")";
            System.out.println("ZGQ:"+sql_Create_Table);
            db1.execSQL(sql_Create_Table);
            array_tbName.clear();
            cursor_tbname.close();
            //读取Excel的第0行作为表字段

            for (i = 1; i <= (rowNums - 1); i++) {

                String sql_insert="insert into " + mTableName + "("+insertstr+") values("+valueX+")";

                db1.execSQL(sql_insert,excelContent[i]);



            }
            db1.setTransactionSuccessful();        //设置事务处理成功，不设置会自动回滚不提交
            db1.endTransaction();  //处理完成
            db1.close();
        }
    }
*/
    public static String[][] readXLSX(String path,MyApplication myApplication, Handler mHandler) {

    String[][] resultArray = new String[0][];
    List<List<String>> str=new ArrayList<List<String>>();
    String v = null;
    String tStr="";
    boolean flat = false;
    List<String> ls = new ArrayList<String>();
    myApplication.readProgress=0;
    try {
        ZipFile xlsxFile = new ZipFile(new File(path));
        ZipEntry sharedStringXML = xlsxFile
                .getEntry("xl/sharedStrings.xml");
        InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);
        XmlPullParser xmlParser = Xml.newPullParser();
        xmlParser.setInput(inputStream, "utf-8");
        int evtType = xmlParser.getEventType();
        Boolean flagNewSI=true;
        Boolean flagFirstSiTag=true;
        String tpx="";
        String tpxSum="";
        while (evtType != XmlPullParser.END_DOCUMENT) {
            switch (evtType) {
                case XmlPullParser.START_TAG:
                    String tag = xmlParser.getName();
                    if(tag.equalsIgnoreCase("si")){
                        flagNewSI=true;
                        if(flagFirstSiTag){
                            flagFirstSiTag=false;
                        }else{
                            ls.add(tpxSum); //检测到下一个SI(shareItem)，把上一个SI加到列表里面
                        }
                    }
                    if (tag.equalsIgnoreCase("t")) {
                        tpx=xmlParser.nextText().trim();
                        if(flagNewSI) {
                            tpxSum=tpx;
                            flagNewSI=false;
                        }else{
                            tpxSum=tpxSum+tpx;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            evtType = xmlParser.next();
        }
        ls.add(tpxSum);

        ZipEntry sheetXML = xlsxFile.getEntry("xl/worksheets/sheet1.xml");
        InputStream inputStreamsheet = xlsxFile.getInputStream(sheetXML);
        XmlPullParser xmlParsersheet = Xml.newPullParser();
        xmlParsersheet.setInput(inputStreamsheet, "utf-8");
        int evtTypesheet = xmlParsersheet.getEventType();

        int rowNums=0;
        int columNums=0;
        List<String> colNameList=new ArrayList<>();
        boolean flagFirstRow=true;
        boolean flagPreColHasV=true;
        int colindex=0;
        String rowName="";

        List<String> columList=new ArrayList<>();

        while (evtTypesheet != XmlPullParser.END_DOCUMENT) {
            switch (evtTypesheet) {
                case XmlPullParser.START_TAG:
                    String tag = xmlParsersheet.getName();
                    if (tag.equalsIgnoreCase("row")) {
                        rowNums=rowNums+1;
                        columList=new ArrayList<String>();
                        colindex=0;
                        rowName=xmlParsersheet.getAttributeValue(null, "r");
                        System.out.println(rowName);

                    } else if (tag.equalsIgnoreCase("c")) {
                        if(!flagPreColHasV) {
                            columList.add("");
                        }
                        flagPreColHasV=false;

                        if(flagFirstRow){
                            columNums=columNums+1;
                            String tp=xmlParsersheet.getAttributeValue(null, "r");
                            colNameList.add(tp.substring(0,tp.indexOf(rowName)));
                        }else{

                            String crName=xmlParsersheet.getAttributeValue(null, "r");
                            String crNamesub=crName.substring(0,crName.indexOf(rowName));
                            int columId=colNameList.indexOf(crNamesub);
                            if(columId>colindex){
                                int m=(columId-colindex);
                                for(int i=0;i<m;i++){
                                    columList.add("");
                                    colindex=colindex+1;
                                }
                                colindex=colindex+1;
                            }else{
                                colindex=colindex+1;
                            }
                        }

                        String t = xmlParsersheet.getAttributeValue(null, "t");
                        if (t != null && !t.equals("str")&& !t.equals("inlineStr")) {
                            flat = true;
                            //System.out.println(flat + "有");
                        } else {
                            //System.out.println(flat + "没有");
                            flat = false;
                        }
                    } else if (tag.equalsIgnoreCase("v")) {
                        flagPreColHasV=true;
                        v = xmlParsersheet.nextText();
                        if (v != null) {
                            if (flat) {
                                //str += ls.get(Integer.parseInt(v)) + "  ";
                                try{
                                    columList.add(ls.get(Integer.parseInt(v)));
                                } catch(Exception e){
                                    //String v1=v;
                                    columList.add("");
                                }

                            } else {
                                //str += v + "  ";
                                columList.add(v);
                            }
                        }
                    }else if (tag.equalsIgnoreCase("t")) {
                        flagPreColHasV=true;
                        tStr = xmlParsersheet.nextText();
                        if (tStr != null) {
                            columList.add(tStr);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (xmlParsersheet.getName().equalsIgnoreCase("row")
                            && v != null) {
                        //str += "\n";
                        if(columList.size()<columNums){
                            int cs=columNums-columList.size();
                            for(int i=0;i<cs;i++){
                                columList.add("");
                            }
                        }
                        str.add(columList);
                        myApplication.readProgress++;
                        if(flagFirstRow){
                            flagFirstRow=false;
                        }
                        if(myApplication.readProgress%1000==0){
                            Message msgProgress = new Message(); //发送
                            msgProgress.what = 531;
                            mHandler.sendMessage(msgProgress);
                        }

                    }
                    break;
            }
            evtTypesheet = xmlParsersheet.next();
        }

        resultArray=new String[rowNums][columNums];

        for(int a=0;a<rowNums;a++){
            for(int b=0;b<columNums;b++){
                resultArray[a][b]=str.get(a).get(b);
            }
        }


    } catch (ZipException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (XmlPullParserException e) {
        e.printStackTrace();
    }

        /*
        if (str == null) {
            //str = "解析文件出现问题";
        }else{

        }
        */

    return resultArray;
}

    public static Boolean createTable(Context mContext,String[][] excelContent,String mTableName) {
        SQLiteDatabase db1=null;
        try{
            int rowNums =excelContent.length;
            int colNums=excelContent[0].length;
            if (excelContent!= null) {
                MyDatabaseHelper dbhelper=new MyDatabaseHelper(mContext,CreateCellInfoDBActivity.DBNAME1,mTableName,null,1);
                db1 = dbhelper.getWritableDatabase();
                Cursor cursor_tbname = db1.rawQuery("select name from sqlite_master where type='table' order by name", null);
                List<String> array_tbName = new ArrayList<>();
                String[] tablefields=new String[colNums];
                int i=0;
                for(int j=0;j<=colNums-1;j++){
                    tablefields[j]=excelContent[i][j];
                }
                while (cursor_tbname.moveToNext()) {
                    //遍历数据库中所有表的表名
                    array_tbName.add(cursor_tbname.getString(0));
                    System.out.println("ZGQ:tablename= " + cursor_tbname.getString(0));
                }
                db1.beginTransaction();
                if (array_tbName.contains(mTableName)) {
                    db1.execSQL("drop table " + mTableName);
                }
                String str="";
                String insertstr="";
                String valueX="";
                int j;
                for(j=0;j<=(tablefields.length-2);j++) {
                    if (tablefields[j].contains("经度")||tablefields[j].contains("纬度")){
                        str= str + tablefields[j]+" real, ";
                    }else if(tablefields[j].contains("TAC")||tablefields[j].contains("ENBID")||tablefields[j].contains("CELLID")||tablefields[j].contains("PCI")||tablefields[j].contains("方位角")||tablefields[j].contains("天线水平波瓣角")||tablefields[j].contains("弱覆盖MR总条数")||tablefields[j].contains("弱覆盖MR数"))
                    {
                        str= str + tablefields[j]+" integer, ";
                    }else{
                        str= str + tablefields[j]+" text, ";
                    }
                    insertstr=insertstr+tablefields[j]+",";
                    valueX=valueX+"?,";
                }

                if (tablefields[j].contains("经度")||tablefields[j].contains("纬度")){
                    str= str + tablefields[j]+" real";
                }else if(tablefields[j].contains("TAC")||tablefields[j].contains("ENBID")||tablefields[j].contains("CELLID")||tablefields[j].contains("PCI")||tablefields[j].contains("方位角")||tablefields[j].contains("天线水平波瓣角")||tablefields[j].contains("弱覆盖MR总条数")||tablefields[j].contains("弱覆盖MR数"))
                {
                    str= str + tablefields[j]+" integer";
                }else{
                    str= str + tablefields[j]+" text";
                }
                insertstr=insertstr+tablefields[j];
                valueX=valueX+"?";

                System.out.println("ZGQ:"+str);
                String sql_Create_Table="create table " + mTableName + " (id integer primary key autoincrement, "+str+")";
                System.out.println("ZGQ:"+sql_Create_Table);
                db1.execSQL(sql_Create_Table);
                array_tbName.clear();
                cursor_tbname.close();
                //读取Excel的第0行作为表字段

                for (i = 1; i <= (rowNums - 1); i++) {
                         /*
                         String insertRowContent="";
                        int j;
                        for(j=0;j<=colNums-2;j++) {
                            insertRowContent = insertRowContent + excelContent[i][j] + ",";
                        }
                        insertRowContent= insertRowContent+excelContent[i][j];
                        */
                    String sql_insert="insert into " + mTableName + "("+insertstr+") values("+valueX+")";
                    //System.out.println(sql_insert);
                    db1.execSQL(sql_insert,excelContent[i]);

                }
                db1.setTransactionSuccessful();        //设置事务处理成功，不设置会自动回滚不提交
                db1.endTransaction();  //处理完成
                db1.close();
            }
            return true;
        }catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"信息表创建失败！请检查信息表字段名称与字段值设置是否符合规范。",Toast.LENGTH_LONG).show();
            if(db1!=null){
                db1.close();
            }
            return false;
        }
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI,
                new String[]{MediaStore.Images.ImageColumns.DATA},//
                null, null, null);
        if (cursor == null) result = contentURI.getPath();
        else {
            try {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(index);
            }catch (Exception e){
                e.printStackTrace();
                result = contentURI.getPath();
            }
            cursor.close();
        }
        return result;
    }

    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//
                projection, selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);
        if (cursor.moveToFirst()) filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    /**
     * 根据Uri获取真实图片路径
     * <p/>
     * 一个android文件的Uri地址一般如下：
     * content://media/external/images/media/62026
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        if(data == null){
            data= ContentUriUtil.getPath(context, uri);
            //Cursor cursor = context.getContentResolver().query(uri,null,null, null, null);
            //if (cursor != null){
            //    if (cursor.moveToFirst()){
                    //String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
            //        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    //int fileLength = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
            //        data=path;
            //    }
            //}
        }
        return data;
    }



    protected void onDestroy(){
        //卸载super的前后是没有却别的
        if(file!=null){
            if(file.exists())
            {
                file.delete();
            }
        }
        if(db1!=null) {
            db1.close();
        }
        super.onDestroy();

    }

}
