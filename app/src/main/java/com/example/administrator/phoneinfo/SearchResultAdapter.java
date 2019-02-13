package com.example.administrator.phoneinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */

public class SearchResultAdapter extends ArrayAdapter<MapSearchActivity.ResultEntrySearch> {
    private int resourceId;

    public SearchResultAdapter(Context context, int textViewResourceId, List<MapSearchActivity.ResultEntrySearch> objects){
        super(context,textViewResourceId,objects);
        resourceId =textViewResourceId;
        //System.out.println("ZGQ:resourceID= "+resourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        try {

            MapSearchActivity.ResultEntrySearch itemX = getItem(position);
            //System.out.println("ZGQ:Now show the variable fruit is "+ fruit.getName() +" and the position is "+position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.vHolderTableName = (TextView) view.findViewById(R.id.lv_tvsearchtablename);
                viewHolder.vHolderCellName = (TextView) view.findViewById(R.id.lv_tvsearchcellname);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            //System.out.println("ZGQ:the variable view is: "+view);
            //ImageView fruitImage=(ImageView) view.findViewById(R.id.fruit_image);
            //TextView fruitName=(TextView) view.findViewById(R.id.fruit_name);
            viewHolder.vHolderTableName.setText(itemX.resultTableName);
            viewHolder.vHolderCellName.setText(itemX.resultContent);
            return view;
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        TextView vHolderTableName;
        TextView vHolderCellName;
    }
}

