package com.example.blelearn;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.blelearn.R;
import com.example.blelearn.adapter.MyExpandListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class DataControlActivity extends AppCompatActivity {
    private static final String TAG = "DataControlActivity";
    private static final java.util.UUID UUID_SERVICE = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口的通用UUID,UUID是什么东西
    private static final java.util.UUID UUID_CHARACTERISTIC_READ = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FC");
    private static final java.util.UUID UUID_CHARACTERISTIC_WRITE = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FD");
    private static final java.util.UUID UUID_DESCRIPTOR = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FE");
    private TextView deviceInfoTv;
    private TextView deviceStatusTv;
    private MyExpandListViewAdapter myExpandListViewAdapter;
    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_control);
        BluetoothDevice bluetoothDevice = (BluetoothDevice) getIntent().getParcelableExtra("key_bluetoothdevice");
        deviceInfoTv = (TextView) findViewById(R.id.datacontrolactivity_deviceinfo_tv);
        deviceStatusTv = (TextView) findViewById(R.id.datacontrolactivity_devicestatus_tv);
        String deviceInfo = "Address:" + bluetoothDevice.getAddress() + "\n" +
                "Name:" + bluetoothDevice.getName();
        deviceInfoTv.setText(deviceInfo);
        deviceStatusTv.setText("Status:" + "未连接");


        expandableListView = (ExpandableListView) findViewById(R.id.datacontrolactivity_bleservice_expandlv);
        myExpandListViewAdapter = new MyExpandListViewAdapter();
//        List<String> groupList=new ArrayList<>();
//        groupList.add("aaa");
//        groupList.add("bbb");
//        groupList.add("ccc");
//        myExpandListViewAdapter.setGroups(groupList);
//        List<List<String>> childList=new ArrayList<>();
//        List<String> groupList1=new ArrayList<>();
//        groupList1.add("111");
//        List<String> groupList2=new ArrayList<>();
//        groupList2.add("111");
//        groupList2.add("222");
//        List<String> groupList3=new ArrayList<>();
//        groupList3.add("111");
//        groupList3.add("222");
//        groupList3.add("333");
//        childList.add(groupList1);
//        childList.add(groupList2);
//        childList.add(groupList3);
//        myExpandListViewAdapter.setChildren(childList);
        expandableListView.setAdapter(myExpandListViewAdapter);
        Log.d(TAG, "onCreate: myExpandListViewAdapter=" + myExpandListViewAdapter);
        BluetoothGatt bluetoothGatt = bluetoothDevice.connectGatt(DataControlActivity.this, false, new MyBluetoothGattCallback());
    }


    private String parseBondState(int state) {
        String result = "错误";
        if (state == BluetoothDevice.BOND_NONE) {
            result = "绑定none";
        } else if (state == BluetoothDevice.BOND_BONDING) {
            result = "绑定中";
        } else if (state == BluetoothDevice.BOND_BONDED) {
            result = "已经绑定";
        }
        return result;
    }
    private List<String> temp1;
    private List<List<String>> temp2;
    private Handler updataUIHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myExpandListViewAdapter.updataMyData(temp1,temp2);
        }
    };
    /**
     * 连接BLE设备结果
     */
    private class MyBluetoothGattCallback extends BluetoothGattCallback {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            Log.d(TAG, "onPhyUpdate: ");
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {//连接BLE设备GATT结果回调
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Attempting to start service discovery: 连接GATT成功" + gatt.discoverServices());
                deviceStatusTv.setText("Status:" + "已连接");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                deviceStatusTv.setText("Status:" + "未连接");
                Log.d(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {//BLE设备的Service连接成功,gatt.discoverServices()
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] byter = new byte[1];
                byter[0] = 0x11;
//                BluetoothGattCharacteristic characteristic = gatt.getService(UUID_SERVICE).getCharacteristic(UUID_CHARACTERISTIC_WRITE);
//                characteristic.setValue(byter);
//                gatt.writeCharacteristic(characteristic);
                Log.d(TAG, "onLeScan211: CuurentThread=" + (Looper.myLooper() == Looper.getMainLooper()));
                Log.d(TAG, "onServicesDiscovered: myExpandListViewAdapter=" + myExpandListViewAdapter);
                List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
                List<String> gorupList = new ArrayList<>();
                List<List<String>> childList = new ArrayList<>();
                for (int i = 0; i < bluetoothGattServices.size(); i++) {
                    gorupList.add("未知的服务");
                }
                for (int i = 0; i < bluetoothGattServices.size(); i++) {
                    List<String> childTemp = new ArrayList<>();
                    for (int j = 0; j < bluetoothGattServices.get(i).getCharacteristics().size(); j++) {
                        childTemp.add(bluetoothGattServices.get(i).getCharacteristics().get(j).getUuid() + "");
                        if (j == bluetoothGattServices.get(i).getCharacteristics().size() - 1) {
                            Log.d(TAG, "onServicesDiscovered: 1122111");
                            childList.add(childTemp);
                        }
                    }
                }
                for (int i = 0; i < childList.size(); i++) {
                    Log.d(TAG, "onServicesDiscovered: 22222");
                    for (int j = 0; j < childList.get(i).size(); j++) {
                        Log.d(TAG, "onServicesDiscovered: 33333  value=" + childList.get(i).get(j));
                    }
                }
                //  MyExpandListViewAdapter myExpandListViewAdapter=(MyExpandListViewAdapter) expandableListView.getAdapter();

//                myExpandListViewAdapter.getGroups().addAll(gorupList);
//                myExpandListViewAdapter.getChildren().addAll(childList);
//                myExpandListViewAdapter.setGroups(gorupList);
//                myExpandListViewAdapter.setChildren(childList);
//                myExpandListViewAdapter.notifyDataSetChanged();
              //  myExpandListViewAdapter.updataMyData(gorupList,childList);
                temp1=gorupList;
                temp2=childList;
                updataUIHandler.sendEmptyMessage(0);


//                Message message=Message.obtain();
//                Bundle bundle=new Bundle();
//                bundle.put
//                message.setData();
//                updataUIHandler.sendMessage()

                for (int j = 0; j < myExpandListViewAdapter.getGroupCount(); ++j) {
                    if (expandableListView.isGroupExpanded(j)) {//如果是原来展开的，就关闭再展开
                        expandableListView.collapseGroup(j);
                        expandableListView.expandGroup(j);
                    }
                }

//                expandableListView.collapseGroup(0);
//                expandableListView.expandGroup(0);
//                List<BluetoothGattService> bluetoothGattSer;vices = gatt.getServices();
//                String[] services=new String[bluetoothGattServices.size()];
//                for(int i=0;i<services.length;i++){
//                    services[i]="未知Service";
//                    Log.d(TAG, "onServicesDiscovered: 1111111111");
//                }
//                myExpandListViewAdapter.setGroups(services);
//                myExpandListViewAdapter.notifyDataSetChanged();

//                String[][] bluetoothGattCharacteristics=new String[][];
//                for(int i=0;i<bluetoothGattServices.size();i++){
//                    Log.d(TAG, "onServicesDiscovered: 2222");
//                  for(int j=0;j<bluetoothGattServices.get(i).getCharacteristics().size();j++){
//                      Log.d(TAG, "onServicesDiscovered: 333");
//                      bluetoothGattCharacteristics[i][j]=bluetoothGattServices.get(i).getCharacteristics().get(j).getUuid()+"";
//                  }
//                }
//                myExpandListViewAdapter.setGroups(services);
//                myExpandListViewAdapter.setChildren(bluetoothGattCharacteristics);
//                myExpandListViewAdapter.notifyDataSetChanged();
                //       Log.d(TAG, "onServicesDiscovered: size="+bluetoothGattCharacteristics.length);
                for (BluetoothGattService index : bluetoothGattServices) {
                    Log.d(TAG, "onServicesDiscovered: 服务连接成功 index=");
                }
                Log.d(TAG, "onServicesDiscovered: GATT 服务连接成功 status=" + status);
            } else {
                Log.d(TAG, "onServicesDiscovered: GATT 服务连接失败 status=" + status);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {//往Service写数据的结果回调
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicRead: 收到BLE设备发送的数据 characteristic=" + characteristic.getValue().length);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {//从Service读数据的结果回调
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicWrite: ");
        }
    }
}
