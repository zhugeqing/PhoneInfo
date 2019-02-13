package com.example.administrator.phoneinfo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/6.
 */

public class CellGeneralInfo implements Serializable{

        public int type;
        public String cellName="";
        public int CId;
        public int lac;
        public int tac;
        public int psc;
        public int pci;
        public int signalStrength;
        public String rsrq;
        public int SINR=99;
        public int ERFCN=-2;
}

