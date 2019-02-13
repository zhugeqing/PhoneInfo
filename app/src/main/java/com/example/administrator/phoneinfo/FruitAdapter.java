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

public class FruitAdapter extends ArrayAdapter<LogPlayEntry> {
    private int resourceId;

    public FruitAdapter(Context context, int textViewResourceId, List<LogPlayEntry> objects){
        super(context,textViewResourceId,objects);
        resourceId =textViewResourceId;
        //System.out.println("ZGQ:resourceID= "+resourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        try {

            LogPlayEntry itemX = getItem(position);
            //System.out.println("ZGQ:Now show the variable fruit is "+ fruit.getName() +" and the position is "+position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.vHolderTime = (TextView) view.findViewById(R.id.listv_tvCellTime);
                viewHolder.vHolderCellName = (TextView) view.findViewById(R.id.listv_tvCellName);
                viewHolder.vHolderCellId = (TextView) view.findViewById(R.id.listv_tvCellId);
                viewHolder.vHolderPCI = (TextView) view.findViewById(R.id.listv_tvPCI);
                viewHolder.vHolderTac = (TextView) view.findViewById(R.id.listv_tvTac);
                viewHolder.vHolderdBm = (TextView) view.findViewById(R.id.listv_tvdBm);
                viewHolder.vHolderRsrq = (TextView) view.findViewById(R.id.listv_tvRsrq);
                viewHolder.vHolderSinr = (TextView) view.findViewById(R.id.listv_tvSinr);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            //System.out.println("ZGQ:the variable view is: "+view);
            //ImageView fruitImage=(ImageView) view.findViewById(R.id.fruit_image);
            //TextView fruitName=(TextView) view.findViewById(R.id.fruit_name);
            viewHolder.vHolderTime.setText(itemX.logEntryTime);
            viewHolder.vHolderCellName.setText(itemX.logEntryCellName);
            viewHolder.vHolderCellId.setText(String.valueOf(itemX.logEntryCId));
            viewHolder.vHolderPCI.setText(String.valueOf(itemX.logEntryPci));
            viewHolder.vHolderTac.setText(String.valueOf(itemX.logEntryTac));
            viewHolder.vHolderdBm.setText(String.valueOf(itemX.logEntrySignalStrength));
            viewHolder.vHolderRsrq.setText(itemX.logEntryRsrq);
            viewHolder.vHolderSinr.setText(String.valueOf(itemX.logEntrySINR));
            return view;
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        TextView vHolderTime;
        TextView vHolderCellName;
        TextView vHolderCellId;
        TextView vHolderPCI;
        TextView vHolderTac;
        TextView vHolderdBm;
        TextView vHolderRsrq;
        TextView vHolderSinr;

    }


}
