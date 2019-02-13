package com.example.administrator.phoneinfo;

/**
 * Created by Administrator on 2017/2/21.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class CellnfoRecycleViewAdapter extends RecyclerView.Adapter<CellnfoRecycleViewAdapter.MyViewHolder>
{
    private List<CellGeneralInfo> itemsData;
    private Context mContext;
    private LayoutInflater inflater;

    public CellnfoRecycleViewAdapter(Context context,List<CellGeneralInfo> itemsData)
    {
        this.itemsData = itemsData;
        this.mContext=context;
        inflater=LayoutInflater.from(mContext);
    }
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        try{
        View view  = inflater.inflate(R.layout.listitem, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("ZGQ:ViewHolder return null");
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        //holder.tvType.setText(String.valueOf(itemsData.get(position).type));
        try {
            int position_new=holder.getAdapterPosition();
            if(position_new<itemsData.size()&&itemsData.size()>0){
                holder.tvCellName.setText(String.valueOf(itemsData.get(position_new).cellName));
                holder.tvTac.setText(String.valueOf(itemsData.get(position_new).tac));
                if(itemsData.get(position_new).ERFCN==-2){
                    holder.tvCId.setText(String.valueOf(itemsData.get(position_new).CId));
                }
                else {
                    holder.tvCId.setText(String.valueOf(itemsData.get(position_new).CId) + "\n" + String.valueOf(itemsData.get(position_new).ERFCN));
                }
                holder.tvPCI.setText(String.valueOf(itemsData.get(position_new).pci));
                holder.tvdBm.setText(String.valueOf(itemsData.get(position_new).signalStrength));
                holder.tvRsrq.setText(String.valueOf(itemsData.get(position_new).rsrq));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public int getItemCount()
    {
        return itemsData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        //public TextView tvType;
        public TextView tvCellName;
        public TextView tvTac;
        public TextView tvCId;
        public TextView tvPCI;
        public TextView tvdBm;
        public TextView tvRsrq;

        public MyViewHolder(View view)
        {
            super(view);
            try{
            //tvType = (TextView)view.findViewById(R.id.tvCellType);
            tvCellName = (TextView)view.findViewById(R.id.tvCellName);
            tvCId = (TextView)view.findViewById(R.id.tvCellId);
            tvPCI = (TextView)view.findViewById(R.id.tvPCI);
            tvTac = (TextView) view.findViewById(R.id.tvTac);
            tvdBm = (TextView) view.findViewById(R.id.tvdBm);
            tvRsrq = (TextView) view.findViewById(R.id.tvRsrq);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

