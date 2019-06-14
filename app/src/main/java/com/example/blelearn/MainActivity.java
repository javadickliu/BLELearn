package com.example.blelearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BluetoothLeScanner bluetoothLeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//如果该权限没有获得权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {//打开蓝牙失败
            Log.d(TAG, "onCreate: 打开蓝牙失败");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {//已经打开蓝牙
            Log.d(TAG, "onCreate: 蓝牙已经打开成功 bluetoothAdapter=" + bluetoothAdapter);
            boolean ifStartLeScan = bluetoothAdapter.startLeScan(callback);
            if (ifStartLeScan) {
                Log.d(TAG, "onCreate: 开始扫描");
            } else {
                Log.d(TAG, "onCreate: 禁止扫描");
            }
//            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//            bluetoothLeScanner.startScan(scanCallback);


        }
//        SearchRequest request = new SearchRequest.Builder()
//                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
//               // .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
//             //   .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
//                .build();
//        BluetoothClient mClient = new BluetoothClient(this);
//        mClient.search(request, new SearchResponse() {
//            @Override
//            public void onSearchStarted() {
//                Log.d(TAG, "onSearchStarted: ");
//            }
//
//            @Override
//            public void onDeviceFounded(SearchResult device) {
//                Beacon beacon = new Beacon(device.scanRecord);
//                BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
//                Log.d(TAG, "onDeviceFounded: scanning");
//            }
//
//            @Override
//            public void onSearchStopped() {
//                Log.d(TAG, "onSearchStopped: ");
//            }
//
//            @Override
//            public void onSearchCanceled() {
//                Log.d(TAG, "onSearchCanceled: ");
//            }
//        });

    }

    final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            String deviceName = device.getName();
            String deviceUuid = device.getName();
            if (deviceName != null) {
                //todo 去重
                if(deviceName.equals("111")){
                       device.connectGatt(MainActivity.this, true, new BluetoothGattCallback() {
                           @Override
                           public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                               super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                           }

                           @Override
                           public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                               super.onConnectionStateChange(gatt, status, newState);
                               gatt.discoverServices();
                               Log.d(TAG, "onConnectionStateChange: status="+status);
                           }

                           @Override
                           public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                               super.onServicesDiscovered(gatt, status);
                               Log.d(TAG, "onServicesDiscovered: 连接成功");
                           }
                       });
                }
                Log.d(TAG, "run: scanning...name=" + deviceName + " mac=" + deviceUuid);
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
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }
}
