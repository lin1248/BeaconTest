package com.example.beacontest.Main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beacontest.Constant.Coordinate;
import com.example.beacontest.Function.GetOrientation;
import com.example.beacontest.Function.KNN;
import com.example.beacontest.R;
import com.example.beacontest.View.DrawView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.functions.Consumer;

import static com.example.beacontest.Constant.Address.mAdress_7D19;
import static com.example.beacontest.Constant.Address.mAdress_A7F3;
import static com.example.beacontest.Constant.Address.mAdress_AD9F;
import static com.example.beacontest.Constant.Address.mAdress_ADD7;
import static com.example.beacontest.Constant.TAG.TAG_1;
import static com.example.beacontest.Constant.TAG.TAG_2;
import static com.example.beacontest.Constant.TAG.dbName;


public class Main extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    private GetOrientation getOrientation =new GetOrientation();
    private static final int request_enabled = 1;//定义一个int resultCode
    MyHanlder myHanlder = new MyHanlder();
    private SensorManager sm=null;
    float[] accelerometerValues=new float[3];
    float[] magneticFieldValues=new float[3];
    float[] values=new float[3];
    float[] RValues=new float[9];
    PowerManager.WakeLock mWakeLock;
    double _ADD7,_A7F3,_AD9F,_7D19;
    KNN knn=new KNN();
    Coordinate LocationPoint;
    ConstraintLayout layout;
    RadioButton radio_btn,radio_btn2;
    RadioGroup radioGroup;

    /**
     * 提取蓝牙广播包数据
     **/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            /*
              发现A7F3
              */
            if (device.getAddress().equals(mAdress_A7F3)  ){
                        Log.i(TAG_1, "onLeScan: A7F3:getRSSI" + rssi);
                        _A7F3=rssi;
            }

            /*
             发现ADD7
              */
            else if(device.getAddress().equals(mAdress_ADD7)){
                        Log.i(TAG_1, "onLeScan: ADD7:getRSSI" + rssi);
                        _ADD7=rssi;
            }

            /*
             发现AD9F
              */
            else if(device.getAddress().equals(mAdress_AD9F)){
                        Log.i(TAG_1, "onLeScan: AD9F:getRSSI" + rssi);
                        _AD9F=rssi;
            }

            /*
             发现7D19
              */
            else if(device.getAddress().equals(mAdress_7D19)){
                        Log.i(TAG_1, "onLeScan: 7D19:getRSSI" + rssi);
                        _7D19=rssi;
            }
            if(_7D19!=0&&_AD9F!=0&&_ADD7!=0&&_A7F3!=0){
                LocationPoint=knn.KNN(_ADD7,_A7F3,_AD9F,_7D19,5,3);
                initUI((float) LocationPoint.getX(),(float) LocationPoint.getY(),true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_layout);
        RxPermissions rxPermissions=new RxPermissions(this);
        copyDB(dbName);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
        //获取权限
        rxPermissions.request(Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION ).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    Toast.makeText(Main.this,"允许了权限！",Toast.LENGTH_SHORT).show();
                    mBluetoothAdapter.startLeScan(mLeScanCallback);//获取gps权限后使用mLeScanCallback回调方法
                }else{
                    Toast.makeText(Main.this,"授权未成功！",Toast.LENGTH_SHORT).show();
                }

            }
        });
        initUI(0,0,false);//初始化UI
        initWakeLock();
        //初始化唤醒锁
        init_bluetooth();
        //初始化蓝牙要放在蓝牙适配器获取了默认适配器之后

    }

    /**
     * 初始化唤醒锁
     */
    @SuppressLint("InvalidWakeLockTag")
    private void initWakeLock() {
        if (null == mWakeLock) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                            PowerManager.ON_AFTER_RELEASE,
                    "mainLockService");
            if(null!=mWakeLock){
//                mWakeLock.acquire();
//                另一种方式
                mWakeLock.acquire(60 * 10 * 1000);
            }
        }
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
            String str=getOrientation.getStr((int)values[0]);
            Message msg5 = Message.obtain();
            msg5.what = 5;
            msg5.obj = str;
            myHanlder.sendMessage(msg5);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

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
     * 更新UI
     */
    @SuppressLint("HandlerLeak")
    class MyHanlder extends Handler{
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1://A7F3

                    Log.i(TAG_1, "handleMessage: get A7F3 Accuracy:" + msg.obj);
                    break;
                case 2://ADD7

                   //tv2.setText("ADD7::: txPower："+msg.arg1  + "dB\r\n" + "RSSI: " + msg.arg2 + "dB\r\n" + "距离： " + msg.obj + "m");
                    Log.i(TAG_1, "handleMessage: get ADD7 Accuracy:" + msg.obj);
                    break;
                case 3://AD9F

                    //tv3.setText("AD9F::: txPower：" + msg.arg1 + "dB\r\n" + "RSSI: " + msg.arg2 + "dB\r\n" + "距离： " + msg.obj + "m");
                    Log.i(TAG_1, "handleMessage: get AD9F Accuracy:" + msg.obj);
                    break;
                case 4://7D19

                    //tv4.setText("7D19::: txPower："+msg.arg1  + "dB\r\n" + "RSSI: " + msg.arg2 + "dB\r\n" + "距离： " + msg.obj + "m");
                    Log.i(TAG_1, "handleMessage: get 7D19 Accuracy:" + msg.obj);
                    break;
                case 5:

                    //tv5.setText("方向："+msg.obj);
                    Log.i(TAG_1, "Orientation is"+msg.obj);
                    break;

            }
        }
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

    /**
    拷贝数据库
     */
    public void copyDB(String dbName){
        try{
            File file=new File(getFilesDir(),dbName);
            if(file.exists()&&file.length()>0){
                //拷贝成功
                Log.d(TAG_1, "copyDB: 拷贝成功");
            }else {
                InputStream is=getAssets().open(dbName);

                FileOutputStream fos=new FileOutputStream(file);
                byte[] buffer =new byte[1024];
                int len;
                while((len=is.read(buffer))!=-1){
                    fos.write(buffer,0,len);
                }
                fos.close();
                is.close();
                Log.d(TAG_1, "copyDB: 流写入成功");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 刷新UI
     * @param x
     * @param y
     * @param drawType
     */
    private void initUI(float x,float y,boolean drawType) {
        layout =findViewById(R.id.final_layout);
        radio_btn=findViewById(R.id.radio);
        radio_btn2=findViewById(R.id.radioCancel);
        radioGroup=findViewById(R.id.radioGroup);

        final DrawView view = new DrawView(this);
        layout.addView(view);
        view.setDrawType(false);//初始化DrawType

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio:
                        view.setDrawType2(true);
                        view.invalidate();
                        break;
                    case R.id.radioCancel:
                        view.setDrawType2(false);
                        view.invalidate();
                        break;
                }
            }
        });
        view.setX(x);
        view.setY(y);
        view.setDrawType(drawType);
        view.invalidate();
    }
}
