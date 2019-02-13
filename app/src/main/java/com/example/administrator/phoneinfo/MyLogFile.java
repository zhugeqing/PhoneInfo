package com.example.administrator.phoneinfo;

import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by Administrator on 2017/3/5.
 */

public class MyLogFile {

    public static Boolean createExcelfromArray(String filename,String filePath,List<String[]> logData) throws IllegalAccessException {
        int rowIndex=0;
        int colNums=0;
        int colIndex=0;
        if(logData==null)
        {System.out.println("ZGQ:保存的记录为空!");return false;}
        int rowNums=logData.size();
        List<String> logMemberName=new ArrayList<>();
        List<String> logMemberType=new ArrayList<>();

        try {
            String[] logData0=logData.get(0);
            colNums=logData0.length;
            File file = new File(filePath);
            if (!file.exists())
            {file.mkdirs();}
            // 创建新的Excel 工作簿
            HSSFWorkbook workbook = new HSSFWorkbook();
            workbook.createSheet();
            HSSFSheet sheet=workbook.getSheetAt(0);
            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 500);// 设定行的高度
            //创建excel表头

            for (int i = 0; i < logData0.length; i++) {
                    HSSFCell cell = row.createCell(i);                 // 创建一个Excel的单元格
                    cell.setCellValue(String.valueOf(logData0[i]));  //向单元格中输入值
            }
            HSSFCell celldata;

            for (rowIndex = 1; rowIndex < rowNums; rowIndex++) {
                HSSFRow rowdata = sheet.createRow(rowIndex);
                logData0 = logData.get(rowIndex);
                for (int i=0;i<colNums;i++) {
                        celldata = rowdata.createCell(i);                 // 创建一个Excel的单元格
                        celldata.setCellValue(String.valueOf(logData0[i]));  //向单元格中输入值

                }
            }

            //HSSFCellStyle style = workbook.createCellStyle(); //单元格的样式
            //cell.setCellStyle(style);
            FileOutputStream fOut = new FileOutputStream(
                    filePath + File.separator + filename );  // 设置文件输出流
            workbook.write(fOut);  //写入到sdcard
            fOut.flush();
            fOut.close();   // 操作结束，关闭文件
            return true;
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("ZGQ:保存excel出错!");
            return false;
        }
    }

    public void createExcel(String filename,String filePath,List<MyApplication.LogRecord> logData) throws IllegalAccessException {

        int rowIndex=0;
        int colNums=0;
        int colIndex=0;
        if(logData==null)
        {System.out.println("ZGQ:保存的记录为空!");return;}
        int rowNums=logData.size();
        List<String> logMemberName=new ArrayList<>();
        List<String> logMemberType=new ArrayList<>();

        try {
            MyApplication.LogRecord logData0=logData.get(0);
            //Field[] fields = logData0.getClass().getDeclaredFields();
            Field[] fields = logData0.getClass().getFields();
            File file = new File(filePath);
            if (!file.exists())
            {file.mkdirs();}
            // 创建新的Excel 工作簿
            HSSFWorkbook workbook = new HSSFWorkbook();
            workbook.createSheet();
            HSSFSheet sheet=workbook.getSheetAt(0);
            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 500);// 设定行的高度
            //创建excel表头


            for (int i = 0; i < fields.length; i++) {
                //System.out.println("成员变量" + i + "类型 : " + fields[i+3].getType().getName());
                //System.out.println("成员变量" + i + "变量名: " + fields[i+3].getName() + "\t");
                //System.out.println("成员变量" + i + "值: " + fields[i].get(logData0));
                String fname = fields[i].getName();
                if(fname.contains("sec")||fname.contains("log")) {
                    logMemberName.add(fields[i].getName());
                    logMemberType.add(fields[i].getType().getName());
                    HSSFCell cell = row.createCell(i);                 // 创建一个Excel的单元格
                    //HSSFCellStyle style = workbook.createCellStyle(); //单元格的样式
                    //cell.setCellStyle(style);
                    cell.setCellValue(logMemberName.get(i));  //向单元格中输入值
                }
                colNums=i+1;
            }
            HSSFCell celldata;

            for (rowIndex = 0; rowIndex < rowNums; rowIndex++) {
                HSSFRow rowdata = sheet.createRow(rowIndex+1);
                logData0 = logData.get(rowIndex);
                //row.setHeight((short) 500);// 设定行的高度
                //fields = logData.get(rowIndex).getClass().getDeclaredFields();
                //fields = logData.get(rowIndex).getClass().getFields();
                for (int i=0;i<colNums;i++) {
                    String fname = fields[i].getName();
                    if(fname.contains("sec")||fname.contains("log")) {
                    celldata = rowdata.createCell(i);                 // 创建一个Excel的单元格
                    celldata.setCellValue(String.valueOf(fields[i].get(logData0)));  //向单元格中输入值
                }
            }
            }

            //HSSFCellStyle style = workbook.createCellStyle(); //单元格的样式
            //cell.setCellStyle(style);
            FileOutputStream fOut = new FileOutputStream(
                    filePath + File.separator + filename );  // 设置文件输出流
            workbook.write(fOut);  //写入到sdcard
            fOut.flush();
            fOut.close();   // 操作结束，关闭文件
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("ZGQ:保存log出错!");return;
        }
    }

    public static String[][] readExcel(String excelPath) {

        String[][] zgqExcelArray = new String[0][];
        try {
            InputStream input = new FileInputStream(new File(excelPath));
            POIFSFileSystem fs = new POIFSFileSystem(input);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            // Iterate over each row in the sheet
            Iterator<Row> rows = sheet.rowIterator();
            zgqExcelArray = new String[sheet.getLastRowNum()+1][];
            //List<ZgqExcelRow> zgqExcelRowsList=new ArrayList<ZgqExcelRow>();
            int i = 0,j,showProgress=0;
            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow) rows.next();
                int s = row.getPhysicalNumberOfCells();
                zgqExcelArray[i] = new String[s];
                //System.out.println("Row #" + row.getRowNum());

                //每一行 = 新建一个excel行对象
                //ZgqExcelRow stu = new ZgqExcelRow();
                // Iterate over each cell in the row and print out the cell"s
                // content
                Iterator<Cell> cells = row.cellIterator();
                j=0;
                while (cells.hasNext()) {
                    HSSFCell cell = (HSSFCell) cells.next();
                    //System.out.println("ZGQ:ColumnIndex :" + cell.getColumnIndex());
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

                    j = j + 1;

                }
                i = i + 1;

            }

        } catch (IOException ex) {
            ex.printStackTrace();
            if(zgqExcelArray!=null){zgqExcelArray = new String[0][0];}
        }
        //刷新列表
        //getAllRows();
        return zgqExcelArray;
    }

}
