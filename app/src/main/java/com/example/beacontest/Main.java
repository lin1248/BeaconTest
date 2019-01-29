package com.example.beacontest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class Main extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    private static final int request_enabled = 1;//定义一个int resultCode
    private static final String TAG_1 = "BtTest";
    private static final String TAG_2 = "Orientation";
    private static final int REQUEST_COARSE_LOCATION = 3;
    private static final String mAdress_ADD7 = "30:45:11:5D:AD:D7";
    private static final String mAdress_A7F3 = "30:45:11:5D:A7:F3";
    private TextView tv1,tv2;
    MyHanlder myHanlder = new MyHanlder();
    private static int i = 0;
    private static int j = 0;
    private SensorManager sm=null;
    private Sensor aSensor=null;
    private Sensor mSensor=null;

    float[] accelerometerValues=new float[3];
    float[] magneticFieldValues=new float[3];
    float[] values=new float[3];
    float[] RValues=new float[9];

    /**
     * 提取蓝牙广播包数据
     **/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String dataPacket = str2HexStr(scanRecord);
            //String str2 = str.substring(0,8);
            String txPower;
            String  accuracy;
            Message msg1 = Message.obtain();
            Message msg2 = Message.obtain();
            int intTxPower;
           // Log.d(TAG_1, "onLeScan: "+str2+":::");
            if (device.getAddress().equals(mAdress_A7F3)  ){
                txPower = dataPacket.substring(87,89);
                intTxPower = txPowerTransfer(txPower , "A7F3");
                if( intTxPower < 0) {
                   if(i++ < 20)
                        Log.i(TAG_1, "onLeScan: A7F3:getRSSI" + rssi);
                    Log.i(TAG_1, "onLeScan: 发现A7F3\r\n"
                            +"device:" + device.getName() + " Address:" + device.getAddress()+"\r\n"
                            +"广播包: " + dataPacket + "\r\n"
                            +"txPower: " + intTxPower + "dB");

                    //accuracy = CalculateAccuracy.formula_1(intTxPower, rssi);
                    // formula_1
                    accuracy = CalculateAccuracy.formula_2(rssi);
                    //formula_2
                    msg1.what = 1;
                    msg1.arg1 = intTxPower;
                    msg1.arg2 = rssi;
                    msg1.obj = accuracy;
                    myHanlder.sendMessage(msg1);
                }
                else{
                    Log.e(TAG_1, "onLeScan: 发现A7F3，无效数据包:" + dataPacket);
                }
            }

            if(device.getAddress().equals(mAdress_ADD7)){
                txPower = dataPacket.substring(87,89);
                intTxPower = txPowerTransfer(txPower , "ADD7");
                if( intTxPower < 0 ) {
                    if(j++ < 20)
                        Log.i(TAG_1, "onLeScan: ADD7:getRSSI" + rssi);
                    Log.i(TAG_1, "onLeScan: 发现ADD7\r\n"
                            + "device:" + device.getName() + " Address:" + device.getAddress() + "\r\n"
                            + "广播包: " + dataPacket + "\r\n"
                            + "txPower: " + intTxPower + "dB");

                    //accuracy = CalculateAccuracy.formula_1(intTxPower, rssi);
                    // formula_1
                    accuracy = CalculateAccuracy.formula_2(rssi);
                    //formula_2

                    msg2.what = 2;
                    msg2.arg1 = intTxPower;
                    msg2.arg2 = rssi;
                    msg2.obj = accuracy;
                    myHanlder.sendMessage(msg2);
                }
                else{
                    Log.e(TAG_1, "onLeScan: 发现ADD7，无效数据包:" + dataPacket);
                }
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = findViewById(R.id.Tv);
        tv2 = findViewById(R.id.Tv2);
        if (Build.VERSION.SDK_INT >= 6.0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_COARSE_LOCATION);
            Log.i(TAG_1, "onCreate: 获取运行时权限1");

        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        aSensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor=sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
        //mBluetoothAdapter.startLeScan(mLeScanCallback);
        //开始扫描，扫描到之后使用mLeScanCallback回调方法
        init_bluetooth();
        //初始化蓝牙要放在蓝牙适配器获取了默认适配器之后

    }

    /**
     * 获取方向
     */
    final SensorEventListener myListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                accelerometerValues=event.values;
            }
            if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                magneticFieldValues=event.values;
            }
            //调用getRotaionMatrix获得变换矩阵R[]
            SensorManager.getRotationMatrix(RValues, null, accelerometerValues, magneticFieldValues);
            SensorManager.getOrientation(RValues, values);
            //经过SensorManager.getOrientation(R, values);得到的values值为弧度
            //转换为角度
            values[0]=(float)Math.toDegrees(values[0]);
            Log.i(TAG_2, "onSensorChanged: 方向：" + values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    //获取运行时权限
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case REQUEST_COARSE_LOCATION: {
                //Log.i(TAG_1, "onRequestPermissionsResult: gogo");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG_1, "onRequestPermissionsResult: 已获得定位权限");
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                    //获取gps权限后使用mLeScanCallback回调方法
                    //permission granted!
                } else {
                    Log.i(TAG_1, "onRequestPermissionsResult: 获得定位权限失败");
                }
                return;
            }
        }
    }

    /**
     * 初始化蓝牙设备
     */
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

    /**
     * 获取蓝牙权限的响应
     * @param requestCode
     * @param resultCode
     * @param data
     */
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




    /**
     * 字节数组转换成十六进制字符串
     *
     * @param /String str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(byte[] bs) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 更新UI
     */
    class MyHanlder extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1://A7F3

                    tv1.setText("A7F3::: txPower：" + msg.arg1 + "dB\r\n" + "RSSI: " + msg.arg2 + "dB\r\n" + "距离： " + msg.obj + "m");
                    Log.i(TAG_1, "handleMessage: get A7F3 Accuracy:" + msg.obj);
                    break;
                case 2://ADD7

                    tv2.setText("ADD7::: txPower："+msg.arg1  + "dB\r\n" + "RSSI: " + msg.arg2 + "dB\r\n" + "距离： " + msg.obj + "m");
                    Log.i(TAG_1, "handleMessage: get ADD7 Accuracy:" + msg.obj);
                    break;

            }
        }
    }

    /**
     * 发射强度的补码转化为原码
     */
    public static Integer txPowerTransfer (String txPower , String id){
        int num = Integer.parseInt(txPower,16);
        int symbol = num >> 7;
        if(symbol == 1) {
            num = (num - 1) ^ 0xff;
            num = 0 - num;
            Log.i(TAG_1, "txPowerTransfer: " + id + ":" + num);
        }
        else {
            num = (num - 1) ^ 0xff;
            Log.e(TAG_1, "txPowerTransfer: " + "ID: " + id + "原始数据： " + txPower + "转换后数据: " + num );
        }
        return num;
    }

    /**
     * 结束的时候释放
     * 停止获取方向数据
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(myListener);
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        Log.i(TAG_1, "onDestroy: Good Luck!");
    }
}
