package com.example.administrator.phoneinfo;

/**
 * Created by Administrator on 2017/5/1.
 */

public class MyCoordinate {
    public double x;
    public double y;
    public double h;
    public String documentName="";
    public String folderName="";
    public String name="";
    public String color="";

    public MyCoordinate(double x, double y,double h,String name)
    {
        this.x = x;
        this.y = y;
        this.h = h;
        this.name = name;
    }
    public MyCoordinate()
    {
    }
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public double getH() {
        return h;
    }
    public void setH(double h) {
        this.h = h;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}

