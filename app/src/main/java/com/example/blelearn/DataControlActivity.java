package com.example.blelearn;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
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
    private static final java.util.UUID UUID_DESCRIPTOR_NOTIFY = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FF");
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
        expandableListView.setAdapter(myExpandListViewAdapter);
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
      //  myExpandListViewAdapter.setChildren(childList);

      //  myExpandListViewAdapter.updataMyData(groupList,childList);
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
    private List<List<BluetoothGattCharacteristic>> temp2;
    private Handler updataUIHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: updataui !!!!!!!!!! thread2222="+(Looper.getMainLooper()==Looper.myLooper()));
              if(msg.what==0){//刷新service
                  myExpandListViewAdapter.updataMyData(temp1,temp2);
              }else if(msg.what==1){
                  deviceStatusTv.setText("Status:" + "已连接");
              }else if(msg.what==2){
                  deviceStatusTv.setText("Status:" + "断开连接");
              }


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
           //     deviceStatusTv.setText("Status:" + "已连接");
                updataUIHandler.sendEmptyMessageDelayed(1,0);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        //        deviceStatusTv.setText("Status:" + "未连接");
                updataUIHandler.sendEmptyMessageDelayed(2,0);
                Log.d(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {//BLE设备的Service连接成功,gatt.discoverServices()
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {


                //=================已经蓝牙设备UUID的基础上往蓝牙设备写数据===
                byte[] byter = new byte[1];
                byter[0] = 0x11;
                BluetoothGattCharacteristic characteristic = gatt.getService(UUID_SERVICE).getCharacteristic(UUID_CHARACTERISTIC_WRITE);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_DESCRIPTOR_NOTIFY);
//                // descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);//和通知类似,但服务端不主动发数据,只指示客户端读取数据
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.setCharacteristicNotification(characteristic,true);

                characteristic.setValue(byter);
                gatt.writeDescriptor(descriptor);//todo 两个同时调用只能生效一个？
            //    gatt.writeCharacteristic(characteristic);

                //====================

                Log.d(TAG, "onLeScan211: CuurentThread=" + (Looper.myLooper() == Looper.getMainLooper()));
                Log.d(TAG, "onServicesDiscovered: myExpandListViewAdapter=" + myExpandListViewAdapter);
                List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
                List<String> gorupList = new ArrayList<>();
                List<List<BluetoothGattCharacteristic>> childList = new ArrayList<>();
                for (int i = 0; i < bluetoothGattServices.size(); i++) {
                    gorupList.add("未知的服务");
                }
                for (int i = 0; i < bluetoothGattServices.size(); i++) {
                    List<BluetoothGattCharacteristic> childTemp = new ArrayList<>();
                    for (int j = 0; j < bluetoothGattServices.get(i).getCharacteristics().size(); j++) {
                        childTemp.add(bluetoothGattServices.get(i).getCharacteristics().get(j));
                        if (j == bluetoothGattServices.get(i).getCharacteristics().size() - 1) {
                            Log.d(TAG, "onServicesDiscovered: 1122111");
                            childList.add(childTemp);
                        }
                    }
                }
//                for (int i = 0; i < childList.size(); i++) {
//                    Log.d(TAG, "onServicesDiscovered: 22222");
//                    for (int j = 0; j < childList.get(i).size(); j++) {
//                        Log.d(TAG, "onServicesDiscovered: 33333  value=" + childList.get(i).get(j));
//                    }
//                }
                temp1=gorupList;
                temp2=childList;
                updataUIHandler.sendEmptyMessageDelayed(0,0);
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

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {//BLE服务端数据读取成功会回调;
            super.onCharacteristicChanged(gatt, characteristic);
            String valueStr = new String(characteristic.getValue());
            Log.d(TAG, "onCharacteristicChanged: characteristic111111111111111111111111111="+valueStr);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorRead: ");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorWrite: ");
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
