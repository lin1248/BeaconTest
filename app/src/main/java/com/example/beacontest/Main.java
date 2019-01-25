package com.example.beacontest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


public class Main extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    private static final int request_enabled = 1;//定义一个int resultCode
    private static final String TAG_1 = "BtTest2";
    private static final int REQUEST_COARSE_LOCATION = 3;
    private static final String mAdress_ADD7 = "30:45:11:5D:AD:D7";
    private static final String mAdress_A7F3 = "30:45:11:5D:A7:F3";

    /**
     * 获取蓝牙广播包
     **/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String str = str2HexStr(scanRecord);
            String str2 = str.substring(0,8);
            Log.d(TAG_1, "onLeScan: "+str2+":::");
            if (device.getAddress().equals(mAdress_A7F3)  ){

                Log.i(TAG_1, "onLeScan: 发现A7F3");
                Log.i(TAG_1, "onLeScan: device:" + device.getName() + " RSSI:" + rssi + " Address:" + device.getAddress() );
                Log.i(TAG_1, "onLeScan: " + ":::" + str);

            }

            if(device.getAddress().equals(mAdress_ADD7)){

                Log.i(TAG_1, "onLeScan: 发现ADD7");
                Log.i(TAG_1, "onLeScan: device:" + device.getName() + " RSSI:" + rssi + " Address:" + device.getAddress() );
                Log.i(TAG_1, "onLeScan: " + ":::" + str);

            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 6.0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_COARSE_LOCATION);
            Log.i(TAG_1, "onCreate: 获取运行时权限1");

        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        //开始扫描，扫描到之后使用mLeScanCallback回调方法
        init_bluetooth();
        //初始化蓝牙要放在蓝牙适配器获取了默认适配器之后

    }

    //获取运行时权限
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case REQUEST_COARSE_LOCATION: {
                //Log.i(TAG_1, "onRequestPermissionsResult: gogo");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG_1, "onRequestPermissionsResult: 已获得定位权限");
                    //permission granted!
                } else {
                    Log.i(TAG_1, "onRequestPermissionsResult: 获得定位权限失败");
                }
                return;
            }
        }
    }

    //初始化蓝牙设备
    private void init_bluetooth() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            Log.i(TAG_1, "init_bluetooth: 设备不支持蓝牙");
            finish();
        } else if (!mBluetoothAdapter.isEnabled()) {
            //弹出对话框提示用户是后打开
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, request_enabled);
            Log.i(TAG_1, "init_bluetooth: 打开蓝牙");
        } else {
            Log.i(TAG_1, "init_bluetooth: 蓝牙正常工作");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_enabled) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "打开蓝牙成功！", Toast.LENGTH_SHORT).show();
                Log.i(TAG_1, "打开蓝牙成功! ");
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "放弃打开蓝牙!", Toast.LENGTH_SHORT).show();
                Log.i(TAG_1, "放弃打开蓝牙!");
            } else {
                Toast.makeText(this, "打开蓝牙异常！", Toast.LENGTH_SHORT).show();
                Log.i(TAG_1, "打开蓝牙异常！");
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        Log.i(TAG_1, "onDestroy: Good Luck!");
    }

    /**
     * 字节数组转换成十六进制字符串
     *
     * @param /String str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(byte[] bs) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        /*byte[] bs = str.getBytes();*/
        int bit;

        for (int i = 0; i < bs.length; i++) {
            String str;
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

}
