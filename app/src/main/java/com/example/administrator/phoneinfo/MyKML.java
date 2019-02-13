package com.example.administrator.phoneinfo;

import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
//import org.jsoup.Jsoup;
//import org.jsoup.select.Elements;

//import com.soil.soilsampling.MainActivity;

//import android.util.Log;

public class MyKML {
    public static boolean addSampleSuccess = false; //判断读取KML是否成功
    private MyCoordinate coordinate = null; //存储从KML文件中读取出来的坐标值和name
    private String documentName="";
    private String folderName="";
    private String mColor="";
    private String mFillColor="";
    public List<MyCoordinate> coordinateList = new ArrayList();//存储每次实例化的Coordinate对象，每个Coordinate都保存着不同的x,y,name
    public List<MyCoordinatePolygon> coordinatePolygonList = new ArrayList();
    public List<MyCoordinateLine> coordinateLineList = new ArrayList();
    public int numPolygonPoints=0;
    public int numLinePoints=0;
    public int numPoints=0;

    public void parseKml(String pathName,String fileName) throws Exception
    {

        File file = new File(pathName+fileName);//pathName为KML文件的路径
        try {

            if(fileName.endsWith("kml")){
                InputStream inputStream = null;
                inputStream= new FileInputStream(file);
                parseXmlWithDom4j(inputStream);
                inputStream.close();
            }else
            {

            ZipFile zipFile = new ZipFile(file);
            ZipInputStream zipInputStream = null;
            InputStream inputStream = null;
            ZipEntry entry = null;
            zipInputStream = new ZipInputStream(new FileInputStream(file));
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String zipEntryName = entry.getName();
                if (zipEntryName.endsWith("kml") || zipEntryName.endsWith("kmz")) {
                    inputStream = zipFile.getInputStream(entry);
                    parseXmlWithDom4j(inputStream);
                }else if (zipEntryName.endsWith("png")) {

                }
            }
            zipInputStream.close();
            inputStream.close();
            }
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Boolean parseXmlWithDom4j(InputStream input) throws Exception
    {
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(input);
            Element root = document.getRootElement();//获取doc.kml文件的根结点
            listNodes(root);
            addSampleSuccess = true;
            //选择sd卡中的kml文件，解析成功后即调用MainActivity中的添加marker的方法向地图上添加样点marker
            //MainActivity mainActivity = new MainActivity();
            //mainActivity.addSampleMarker();//调用MainActivity中的方法
        } catch (DocumentException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return addSampleSuccess;
    }
    //遍历当前节点下的所有节点
    public void listNodes(Element node){
        String docName="";//Document节点中的name属性
        String folName="";//folder节点中的name属性
        String name = "";//Placemark节点中的name属性
        String x = "";//坐标x
        String y = "";//坐标y
        String h= "";
        double d_x = 200.0;//对x作string to double
        double d_y = 200.0;
        double d_h = 0.0;
        try {
            if("Document".equals(node.getName())){
                List<Element> docSons = node.elements();//得到Placemark节点所有的子节点
                for (Element element : docSons) { //遍历所有的子节点
                    if ("name".equals(element.getName())) {
                        documentName = element.getText();
                        //break;
                    }else if("Style".equals(element.getName())){
                        List<String> elementText=new ArrayList<>();
                        getAllSubNodes(element,"color",elementText);
                        if(elementText.size()>0){
                            mColor=elementText.get(0).trim();
                            mFillColor=elementText.get(0).trim();
                        }
                    }

                }
            } else if("Folder".equals(node.getName())){
                List<Element> foldSons = node.elements();//得到Placemark节点所有的子节点
                for (Element element : foldSons) { //遍历所有的子节点
                    if ("name".equals(element.getName())) {
                        folderName = element.getText();
                        break;
                    }
                }
            } else if ("Placemark".equals(node.getName())) {//如果当前节点是Placemark就解析其子节点
                List<Element> placemarkSons = node.elements();//得到Placemark节点所有的子节点
                for (Element element : placemarkSons) { //遍历所有的子节点
                    if ("name".equals(element.getName())) {
                        name = element.getText();
                    }else if("Polygon".equals(element.getName())) {
                        Element polygonSon;
                        MyCoordinatePolygon myCoordinatePolygon = new MyCoordinatePolygon();
                        List<String> nodeContent = new ArrayList<>();
                        getAllSubNodes(element, "coordinates", nodeContent);
                        //myCoordinatePolygon = new MyCoordinatePolygon();
                        myCoordinatePolygon.polygonName = name;
                        myCoordinatePolygon.documentName=String.copyValueOf(documentName.toCharArray());
                        myCoordinatePolygon.folderName=String.copyValueOf(folderName.toCharArray());
                        myCoordinatePolygon.color=String.copyValueOf(mColor.toCharArray());
                        myCoordinatePolygon.fillcolor=String.copyValueOf(mFillColor.toCharArray());
                        String nodeContentSplit[] = null;
                        nodeContentSplit = nodeContent.get(0).split(" ");
                        for (int m = 0; m < nodeContentSplit.length; m++){
                            String pointSplit[] = null;
                            pointSplit = nodeContentSplit[m].split(",");
                            double tplong = Math.round(Double.valueOf(pointSplit[0])*1000000)/1000000.0;
                            double tplat = Math.round(Double.valueOf(pointSplit[1])*1000000)/1000000.0;
                            double tph = Double.valueOf(pointSplit[2]);
                            LatLng tpLatLng = new LatLng(tplat, tplong);
                            myCoordinatePolygon.polygonPoints.add(tpLatLng);
                            myCoordinatePolygon.polygonPointsHeigh.add(tph);
                            numPolygonPoints=numPolygonPoints+1;
                        }
                        coordinatePolygonList.add(myCoordinatePolygon);//将每一个实例化的对象存储在list中

                    }else if("LineString".equals(element.getName())){
                        Element lineSon;
                        MyCoordinateLine myCoordinateLine = new MyCoordinateLine();
                        List<String> nodeContent = new ArrayList<>();
                        getAllSubNodes(element, "coordinates", nodeContent);
                        myCoordinateLine.lineName = name;
                        myCoordinateLine.documentName=String.copyValueOf(documentName.toCharArray());
                        myCoordinateLine.folderName=String.copyValueOf(folderName.toCharArray());
                        myCoordinateLine.color=String.copyValueOf(mColor.toCharArray());
                        String nodeContentSplit[] = null;
                        nodeContentSplit = nodeContent.get(0).split(" ");
                        for (int m = 0; m < nodeContentSplit.length; m++){
                            String pointSplit[] = null;
                            pointSplit = nodeContentSplit[m].split(",");
                            double tplong = Math.round(Double.valueOf(pointSplit[0])*1000000)/1000000.0;
                            double tplat = Math.round(Double.valueOf(pointSplit[1])*1000000)/1000000.0;
                            double tph = Double.valueOf(pointSplit[2]);
                            LatLng tpLatLng = new LatLng(tplat, tplong);
                            myCoordinateLine.linePoints.add(tpLatLng);
                            myCoordinateLine.linePointsHeigh.add(tph);
                            numLinePoints=numLinePoints+1;
                        }
                        coordinateLineList.add(myCoordinateLine);//将每一个实例化的对象存储在list中
                    }else if("Point".equals(element.getName())){
                        Element pointSon;
                        MyCoordinate myCoordinatePoint = new MyCoordinate();
                        List<String> nodeContent = new ArrayList<>();
                        getAllSubNodes(element, "coordinates", nodeContent);
                        myCoordinatePoint.name = name;
                        myCoordinatePoint.documentName=String.copyValueOf(documentName.toCharArray());
                        myCoordinatePoint.folderName=String.copyValueOf(folderName.toCharArray());
                        myCoordinatePoint.color=String.copyValueOf(mColor.toCharArray());
                        String nodeContentSplit[] = null;
                        nodeContentSplit = nodeContent.get(0).split(",");
                        double tplong = Math.round(Double.valueOf(nodeContentSplit[0])*1000000)/1000000.0;
                        double tplat = Math.round(Double.valueOf(nodeContentSplit[1])*1000000)/1000000.0;
                        double tph = Double.valueOf(nodeContentSplit[2]);
                        myCoordinatePoint.x=tplong;
                        myCoordinatePoint.y=tplat;
                        myCoordinatePoint.h=tph;
                        numPoints=numPoints+1;
                        coordinateList.add(myCoordinatePoint);//将每一个实例化的对象存储在list中
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //同时迭代当前节点下面的所有子节点
        //使用递归
        Iterator<Element> iterator = node.elementIterator();
        while(iterator.hasNext()){
            Element e = iterator.next();
            listNodes(e);
        }
    }
    public List<MyCoordinate> getCoordinateList()
    {
        return this.coordinateList;
    }

    /**
     * 从指定节点开始,递归遍历所有子节点
     * @author chenleixing
     */
    public void getAllSubNodes(Element node,String elementName,List<String> elementText){

        try{
            System.out.println("--------------------");
            //当前节点的名称、文本内容和属性
            System.out.println("当前节点名称："+node.getName());//当前节点名称
            System.out.println("当前节点的内容："+node.getTextTrim());//当前节点名称
            if(elementName.equals(node.getName())){
                elementText.add(node.getTextTrim());
                //throw new RuntimeException();  //中断递归函数
            }

            List<Attribute> listAttr=node.attributes();//当前节点的所有属性的list
            for(Attribute attr:listAttr){//遍历当前节点的所有属性
                String name=attr.getName();//属性名称
                String value=attr.getValue();//属性的值
                System.out.println("属性名称："+name+"属性值："+value);
            }

            //递归遍历当前节点所有的子节点
            List<Element> listElement=node.elements();//所有一级子节点的list
            for(Element e:listElement){//遍历所有一级子节点
                getAllSubNodes(e,elementName,elementText);//递归
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }




}


