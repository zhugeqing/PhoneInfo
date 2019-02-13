package com.example.administrator.phoneinfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * Created by Administrator on 2017/8/13.
 */

public class JsonTools {

    /**
     *   将json 数组转换为Map 对象
     * @param jsonString
     * @return
     */
    public static Map<String, Object> getMap(String jsonString)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(jsonString);   @SuppressWarnings("unchecked")
        Iterator<String> keyIter = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext())
            {
                key = (String) keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把json 转换为ArrayList 形式
     * @return
     */
    public static List<Map<String, Object>> getList(String jsonString)
    {
        List<Map<String, Object>> list = null;
        try
        {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject;
            list = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < jsonArray.length(); i++)
            {
                jsonObject = jsonArray.getJSONObject(i);
                list.add(getMap(jsonObject.toString()));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 把json 转换为ArrayList 形式
     * @return
     */
    public static List<List<String>> jsonToList(JSONArray jsonArray)
    {
        List<List<String>> list=new ArrayList<>();
        try
        {
            JSONObject jsonObject;

            for (int i = 0; i < jsonArray.length(); i++)
            {
                jsonObject = jsonArray.getJSONObject(i);
                Iterator<String> keyIter = jsonObject.keys();
                List<String> titleList=new ArrayList<>();
                List<String> dataList=new ArrayList<>();

                while (keyIter.hasNext())
                {
                    String key = (String) keyIter.next();
                    if(i==0){
                        titleList.add(key);
                        dataList.add(jsonObject.get(key).toString());
                    }else{
                        dataList.add(jsonObject.get(key).toString());
                    }
                }
                if(i==0){
                    list.add(titleList);
                    list.add(dataList);
                }else{
                    list.add(dataList);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }


}

