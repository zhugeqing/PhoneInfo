package com.example.administrator.phoneinfo;

import com.baidu.mapapi.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/6.
 */

public class DeepCopy implements Serializable {

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    public static List<LatLng> deepCopyForLatLng(List<LatLng> mLatLnglist) {
        List<LatLng> resList=new ArrayList<>();
        for(int i=0;i<mLatLnglist.size();i++){
            LatLng sm=new LatLng(mLatLnglist.get(i).latitude, mLatLnglist.get(i).longitude);
            resList.add(sm);
        }
        return resList;
    }

}
