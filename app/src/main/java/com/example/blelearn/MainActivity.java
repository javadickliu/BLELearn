package com.example.blelearn;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.blelearn.adapter.RCAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BluetoothLeScanner bluetoothLeScanner;
    private static final java.util.UUID UUID_SERVICE = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口的通用UUID,UUID是什么东西
    private static final java.util.UUID UUID_CHARACTERISTIC_READ = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FC");
    private static final java.util.UUID UUID_CHARACTERISTIC_WRITE = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FD");
    private static final java.util.UUID UUID_DESCRIPTOR = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FE");
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private RCAdapter rcAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //==位置权限校验==
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//如果该权限没有获得权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        Button startScanBtn = (Button) findViewById(R.id.mainactivity_startscan_btn);
        startScanBtn.setOnClickListener(clickListener);

        recyclerView = (RecyclerView) findViewById(R.id.mainactivity_bledevice_rc);
        rcAdapter = new RCAdapter(deviceList);
        rcAdapter.setItemClickListener(itemClickListenr);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(rcAdapter);

    }


    /**
     * 点击监听
     */
    private RCAdapter.ItemClickListenr itemClickListenr = new RCAdapter.ItemClickListenr() {
        @Override
        public void onClick(View view, int positon) {
            if (bluetoothAdapter != null) {
                Log.d(TAG, "onClick: 开始连接BLE设备停止扫描");
                bluetoothAdapter.stopLeScan(callback);
            }
            Intent intent=new Intent(MainActivity.this, DataControlActivity.class);
            intent.putExtra("key_bluetoothdevice",rcAdapter.getmDatas().get(positon));
            startActivity(intent);
//            RCAdapter rcAdapter = (RCAdapter) recyclerView.getAdapter();
         //   BluetoothGatt bluetoothGatt = rcAdapter.getmDatas().get(positon).connectGatt(MainActivity.this, false, new MyBluetoothGattCallback());
            //设置自动重连反而没有MyBluetoothGattCallback回调
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mainactivity_startscan_btn:
                    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    bluetoothAdapter = bluetoothManager.getAdapter();
                    if (bluetoothAdapter == null) {
                        Toast.makeText(MainActivity.this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onCreate: 设备不支持蓝牙");
                    } else {
                        if (!bluetoothAdapter.isEnabled()) {//蓝牙未打开
                            Toast.makeText(MainActivity.this, "蓝牙未打开,请打开蓝牙", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onCreate: 蓝牙未打开,请打开蓝牙");
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 1);
                        } else {//已经打开蓝牙
                            Log.d(TAG, "onCreate: 蓝牙已经打开成功 bluetoothAdapter=" + bluetoothAdapter);
                            //     bluetoothAdapter.
                            boolean ifStartLeScan = bluetoothAdapter.startLeScan(callback);
                            if (ifStartLeScan) {
                                Toast.makeText(MainActivity.this, "BLE已经打开成功,开始扫描", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onCreate: 开始扫描");
                            } else {
                                Log.d(TAG, "onCreate: 禁止扫描");
                            }
                        }
                    }
                    break;
            }
        }
    };

    /**
     *搜索BLE设备结果
     */
    final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress();
            //      Log.d(TAG, "onLeScan: CuurentThread=" + (Looper.myLooper() == Looper.getMainLooper()));
            if (deviceName != null) {
                //判断是否该设备已经搜索到
                boolean ifFindDevie = false;
                if (deviceList.size() == 0) {
                    Log.d(TAG, "onReceive: 初次添加蓝牙设备到集合1");
                    deviceList.add(device);
                    //   RCAdapter adapter = (RCAdapter) recyclerView.getAdapter();
                    if (rcAdapter != null) {
                        rcAdapter.setmDatas(deviceList);
                        rcAdapter.notifyDataSetChanged();//刷新list
                    }
                }
                for (int i = 0; i < deviceList.size(); i++) {//去重
                    if (deviceList.get(i).getAddress().equals(deviceHardwareAddress)) {
                        ifFindDevie = true;
                    }
                    if (i == deviceList.size() - 1 && !ifFindDevie) {//没有发现过的设备添加到list
                        deviceList.add(device);
                        Log.d(TAG, "onReceive: 新设备添加到list devicename=" + deviceName + " size=" + deviceList.size());
                        //  RCAdapter adapter = (RCAdapter) recyclerView.getAdapter();
                        rcAdapter.setmDatas(deviceList);
                        rcAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };




//    private ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//            Log.d(TAG, "onLeScan: 搜索到BLE设备");
////            if (bluetoothLeScanner != null)
////                bluetoothLeScanner.stopScan(this);
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            super.onScanFailed(errorCode);
////            if (bluetoothLeScanner != null)
////                bluetoothLeScanner.stopScan(this);
//            Log.d(TAG, "onLeScan: 搜索BLE设备失败");
//        }
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            super.onBatchScanResults(results);
//            Log.d(TAG, "onLeScan: 搜索BLE list");
//        }
//    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {//请求开启蓝牙的回调结果
            if (resultCode == Activity.RESULT_OK) {//用户允许打开蓝牙
                Toast.makeText(MainActivity.this, "允许打开蓝牙", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult:用户允许打开蓝牙 ");
            } else if (resultCode == Activity.RESULT_CANCELED) {//打开蓝牙失败或者用户拒绝打开蓝牙
                Toast.makeText(MainActivity.this, "拒绝打开蓝牙", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: 2222222222");
            }
        }
    }
}
