package com.example.beacontest;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索附近蓝牙
 */
public class MainBleTest extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    private static final int request_enabled = 1;//定义一个int resultCode
    private static final String TAG_1 = "BtTest";
    private List<BluetoothDevice> mBlueList = new ArrayList<>();
    private ListView lisetView;
    private TextView textView1;
    private Button btn;
    private final int MY_PERMISSION_REQUEST_CONSTANT = 2;
    private static final int REQUEST_COARSE_LOCATION = 0;
    private IntentFilter filter,filter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        if (Build.VERSION.SDK_INT >= 6.0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_CONSTANT);
            Log.i(TAG_1, "onCreate: 获取运行时权限");

        }
        lisetView =  findViewById(R.id.Lv);
        textView1 =  findViewById(R.id.Tv);
        btn = findViewById(R.id.btn);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        init_bluetooth();
        //初始化蓝牙要放在蓝牙适配器获取了默认适配器之后
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG_1, "onClick: 搜索按键按下");
                startScanBluth();
                //开始搜索蓝牙
            }
        });
    }

    //获取运行时权限
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //permission granted!
                }
                return;
            }
        }
    }

    //申请位置定位运行时权限
/*    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this, "动态请求权限", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                return;
            } else {

            }
        } else {

        }
    }*/


    //初始化蓝牙设备
    private void init_bluetooth(){
        if(mBluetoothAdapter == null){
            Toast.makeText(this,"设备不支持蓝牙",Toast.LENGTH_SHORT).show();
            Log.i(TAG_1, "init_bluetooth: 设备不支持蓝牙");
            finish();
        }
        else if(!mBluetoothAdapter.isEnabled()){
            //弹出对话框提示用户是后打开
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,request_enabled);
            Log.i(TAG_1, "init_bluetooth: 打开蓝牙");
        }
        else{
            Log.i(TAG_1, "init_bluetooth: 蓝牙正常工作");
        }
        Log.i(TAG_1, "init_bluetooth: 注册异步搜索蓝牙设备的广播");
        startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == request_enabled){
            if(resultCode == RESULT_OK){
                Toast.makeText(this,"打开蓝牙成功！",Toast.LENGTH_SHORT).show();
                Log.i(TAG_1, "打开蓝牙成功! ");
            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this,"放弃打开蓝牙!",Toast.LENGTH_SHORT).show();
                Log.i(TAG_1, "放弃打开蓝牙!");
            }
            else{
                Toast.makeText(this, "打开蓝牙异常！", Toast.LENGTH_SHORT).show();
                Log.i(TAG_1, "打开蓝牙异常！");
            }
        }
    }

    /**
     * 注册异步搜索蓝牙设备的广播
     */
    private void startDiscovery() {
        // 找到设备的广播
         filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // 注册广播
        registerReceiver(receiver, filter);
        Log.i(TAG_1, "startDiscovery: 注册 找到设备的广播");
        // 搜索完成的广播
         filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 注册广播
        registerReceiver(receiver, filter1);
        Log.i(TAG_1, "startDiscovery: 注册 搜索完成的广播");
    }

    /**
     * 广播接收器
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 收到的广播类型
            String action = intent.getAction();
            // 发现设备的广播
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 从intent中获取设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 没否配对
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (!mBlueList.contains(device)) {
                        mBlueList.add(device);
                    }
                    textView1.setText("附近设备：" + mBlueList.size() + "个\u3000\u3000" );
                    ListViewAdapter adapter = new ListViewAdapter(MainBleTest.this, mBlueList);
                    lisetView.setAdapter(adapter);

                    Log.i(TAG_1, "onReceive: " + mBlueList.size());
                    Log.i(TAG_1, "onReceive: " + (device.getName() + ":" + device.getAddress() +  "m" + "\n"));
                }
                // 搜索完成
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 关闭进度条
                progressDialog.dismiss();
                Log.i(TAG_1, "onReceive: 搜索完成");
            }
        }
    };

    private ProgressDialog progressDialog;
    /**
     * 搜索蓝牙的方法
     */
    private void startScanBluth() {
        // 判断是否在搜索,如果在搜索，就取消搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.i(TAG_1, "startScanBluth: 取消搜索蓝牙！");
        }
        // 开始搜索
        mBluetoothAdapter.startDiscovery();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("正在搜索，请稍后！");
        progressDialog.show();
        Log.i(TAG_1, "startScanBluth: 开始搜索");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.i(TAG_1, "onDestroy: 取消注册广播接收器");
    }

    /**
        获取蓝牙广播包
     **/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i(TAG_1, "onLeScan: device:"+device.getName()+" RSSI:"+rssi+"");
        }
    };
}
