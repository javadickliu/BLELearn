package com.example.blelearn.adapter;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.example.blelearn.R;

import java.util.List;

/**
 * rcview适配器
 */
public class RCAdapter extends RecyclerView.Adapter<RCAdapter.VH> {
    private static final String TAG = "RCAdapter";
    private List<BluetoothDevice> mDatas;
    private ItemClickListenr listenr;

    public List<BluetoothDevice> getmDatas() {
        return mDatas;
    }

    public void setmDatas(List<BluetoothDevice> mDatas) {
        this.mDatas = mDatas;
    }

    public RCAdapter(List<BluetoothDevice> data) {
        this.mDatas = data;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        String itemContent="设备名称:"+mDatas.get(position).getName()+"\n"+"mac地址:"+mDatas.get(position).getAddress();
        holder.title.setText(itemContent);
        holder.bg.setTag(position);
        Log.d(TAG, "onBindViewHolder: mDatas.size="+mDatas.size());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setItemClickListener(ItemClickListenr listener) {//定义方法传递监听对象
        this.listenr = listener;
    }

    public interface ItemClickListenr {//监听接口

        public void onClick(View view, int positon);
    }


    public class VH extends RecyclerView.ViewHolder {
        public final TextView title;
        public final LinearLayout bg;
        public VH(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.mainactivity_recyclerview_item_bluetooth_name);
            bg=(LinearLayout)v.findViewById(R.id.rc_item_bg);
            bg.setOnClickListener(new MyViewClickListener());
        }
    }

    private class MyViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (listenr != null) {
                listenr.onClick(v, (int) v.getTag());//通知监听者
            }
        }
    }

}
