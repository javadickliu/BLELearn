package com.example.blelearn.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.blelearn.MainActivity;
import com.example.blelearn.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyExpandListViewAdapter extends BaseExpandableListAdapter {
    //    public String[] groups = {"A", "B", "C", "D"};
//    public String[][] children = {
//            {"A1", "A2"},
//            {"B1"},
//            {"C1", "C2", "C3"},
//            {"D1", "D2", "D3", "D4", "D5", "D6"}
//    };
    private static final String TAG = "MyExpandListViewAdapter";
    private List<String> groups = new ArrayList<>();

    private List<List<BluetoothGattCharacteristic>> children = new ArrayList<>();

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<List<BluetoothGattCharacteristic>> getChildren() {
        return children;
    }

    public void setChildren(List<List<BluetoothGattCharacteristic>> children) {
        this.children = children;
    }

    // List<List<BluetoothGattCharacteristic>> lists;
    //   HashMap<List<String>,List<BluetoothGattCharacteristic>> hashMap;
//    public List<List<BluetoothGattCharacteristic>> getLists() {
//        return lists;
//    }
//
//    public void setLists(List<List<BluetoothGattCharacteristic>> lists) {
//        this.lists = lists;
//    }
//
//    public String[] getGroups() {
//        return groups;
//    }
//
//    public void setGroups(String[] groups) {
//        this.groups = groups;
//    }
//
//    public String[][] getChildren() {
//        return children;
//    }
//
//    public void setChildren(String[][] children) {
//        this.children = children;
//    }
    public void updataMyData(List<String> groups, List<List<BluetoothGattCharacteristic>> children) {
        this.groups.clear();
        this.children.clear();
        notifyDataSetChanged();
        this.groups.addAll(groups);
        this.children.addAll(children);
        notifyDataSetChanged();
     //   notifyDataSetInvalidated();
    }

    @Override
    public int getGroupCount() {
        Log.d(TAG, "getGroupCount: ");
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Log.d(TAG, "getChildrenCount: ");
        return children.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        Log.d(TAG, "getGroup: ");
        return groups.get(groupPosition);
        //  return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.d(TAG, "getChild: ");
        return children.get(groupPosition).get(childPosition);
        //   return children[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        Log.d(TAG, "getGroupId: ");
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Log.d(TAG, "getChildId: ");
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        Log.d(TAG, "hasStableIds: ");
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.d(TAG, "getGroupView: ");
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_group, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_expand_group);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(groups.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        Log.d(TAG, "getChildView: size=" + groups.size());
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_expand_child);
            childViewHolder.bleDeviceProperties = (TextView) convertView.findViewById(R.id.label_expand_child_bleDeviceProperties);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tvTitle.setText("UUID:"+children.get(groupPosition).get(childPosition).getUuid());
        childViewHolder.bleDeviceProperties.setText("Properties:"+children.get(groupPosition).get(childPosition).getUuid());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        Log.d(TAG, "isChildSelectable: ");
        return true;
    }

    static class GroupViewHolder {
        TextView tvTitle;
    }

    static class ChildViewHolder {
        TextView tvTitle;
        TextView bleDeviceProperties;
    }
}
