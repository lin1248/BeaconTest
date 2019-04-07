package com.example.beacontest.Main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.beacontest.Constant.Coordinate;
import com.example.beacontest.Function.GetOrientation;
import com.example.beacontest.Function.KNN;
import com.example.beacontest.Function.RotateBitmap;
import com.example.beacontest.R;
import com.example.beacontest.View.DrawView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

import static android.view.View.LAYER_TYPE_HARDWARE;
import static com.example.beacontest.Constant.Address.mAdress_7D19;
import static com.example.beacontest.Constant.Address.mAdress_A7F3;
import static com.example.beacontest.Constant.Address.mAdress_AD9F;
import static com.example.beacontest.Constant.Address.mAdress_ADD7;
import static com.example.beacontest.Constant.TAG.H;
import static com.example.beacontest.Constant.TAG.K;
import static com.example.beacontest.Constant.TAG.TAG_1;
import static com.example.beacontest.Constant.TAG.TAG_2;
import static com.example.beacontest.Constant.TAG.TAG_3;
import static com.example.beacontest.Constant.TAG.TAG_request;
import static com.example.beacontest.Constant.TAG.buttonColor;
import static com.example.beacontest.Constant.TAG.dbName;
import static com.example.beacontest.Constant.TAG.url;


public class Main extends AppCompatActivity {
    private static boolean locationEnable=false;
    private static int times=0;
    private Message msg_rotate;
    private BluetoothAdapter mBluetoothAdapter;
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
    Button btn_start,btn_stop;
    int num=0;
    ImageView imageView,imageView_ori;
    ViewGroup.LayoutParams params;
    DrawView view;
    RotateBitmap rotateBitmap = new RotateBitmap();
    private Bitmap originBM = null;
    private Bitmap rotateBM = null;

    /**
     * 提取蓝牙广播包数据
     **/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            /*
              发现A7F3
              */
                if (device.getAddress().equals(mAdress_A7F3)) {
                    Log.i(TAG_1, "onLeScan: A7F3:getRSSI" + rssi);
                    _A7F3 = rssi;
                }

            /*
             发现ADD7
              */
                else if (device.getAddress().equals(mAdress_ADD7)) {
                    Log.i(TAG_1, "onLeScan: ADD7:getRSSI" + rssi);
                    _ADD7 = rssi;
                }

            /*
             发现AD9F
              */
                else if (device.getAddress().equals(mAdress_AD9F)) {
                    Log.i(TAG_1, "onLeScan: AD9F:getRSSI" + rssi);
                    _AD9F = rssi;
                }

            /*
             发现7D19
              */
                else if (device.getAddress().equals(mAdress_7D19)) {
                    Log.i(TAG_1, "onLeScan: 7D19:getRSSI" + rssi);
                    _7D19 = rssi;
                }

                if(_ADD7!=0&&_A7F3!=0&&_AD9F!=0&&_7D19!=0&&num==50&&locationEnable) {
                    num=0;
                    //LocationPoint = knn.KNN(_ADD7, _A7F3, _AD9F, _7D19, 5, 3);
                    //initUI((float) LocationPoint.getX(), (float) LocationPoint.getY(), true);
                    KnnRequest(_ADD7, _A7F3, _AD9F, _7D19, K, H);
                }
                else if(locationEnable){
                    num++;
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
        imageView=findViewById(R.id.image);
        imageView.setImageResource(R.drawable.outline);
        params=imageView.getLayoutParams();
        params.height=1400;
        params.width=1080;
        imageView.setLayoutParams(params);
        imageView_ori=findViewById(R.id.image_ori);
        imageView_ori.setImageResource(R.drawable.pika);
        params=imageView_ori.getLayoutParams();
        params.height=200;
        params.width=200;
        imageView_ori.setLayoutParams(params);
        //获取权限
        rxPermissions.request(Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET).subscribe(new Consumer<Boolean>() {
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

        radio_btn=findViewById(R.id.radio);
        radio_btn2=findViewById(R.id.radioCancel);
        radioGroup=findViewById(R.id.radioGroup);
        layout =findViewById(R.id.final_layout);
        btn_start=findViewById(R.id.btn_start);
        btn_stop=findViewById(R.id.btn_stop);

        btn_stop.setBackgroundColor(0);
        btn_start.setBackgroundColor(Color.parseColor(buttonColor));
        radio_btn2.setChecked(true);

        view = new DrawView(this);
        layout.addView(view);
        view.setDrawType(false);//初始化DrawType

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_stop.setClickable(true);
                btn_start.setClickable(false);
                btn_start.setBackgroundColor(0);
                btn_stop.setBackgroundColor(Color.parseColor(buttonColor));
                locationEnable=true;
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_stop.setClickable(false);
                btn_start.setClickable(true);
                btn_stop.setBackgroundColor(0);
                btn_start.setBackgroundColor(Color.parseColor(buttonColor));
                locationEnable=false;
                initUI(0,0,false);
            }
        });


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
            Log.i(TAG_1, "onSensorChanged: 方向：" + values[0]);
            if(times++>5) {
                times=0;
                sendMsg(values[0]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    /**
     * 用于发送Msg
     * @param alpha
     */
    private void sendMsg(float alpha){
        if(rotateBM != null){
            Log.i(TAG_3, "sendMsg: rotateBM不为空");
            rotateBM = null;
        }
        if (originBM == null ){
            Log.i(TAG_3, "sendMsg: originBM为空");
            originBM = BitmapFactory.decodeResource(getResources(),R.drawable.pika);
        }else{
            Log.i(TAG_3, "sendMsg: originBM不为空");
            originBM = null;
            originBM = BitmapFactory.decodeResource(getResources(),R.drawable.pika);
        }
        rotateBM = rotateBitmap.rotateBitmapFun(originBM,alpha);
        Log.i(TAG_3, "sendMsg: 方向：" + alpha);
        if(alpha<0)
            alpha=alpha+360;
        String str=getOrientation.getStr((int)alpha);
        msg_rotate = Message.obtain();
        msg_rotate.what = 1;
        msg_rotate.obj = str+(int) alpha;
        myHanlder.sendMessage(msg_rotate);
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
     * 更新UI
     */
    @SuppressLint("HandlerLeak")
    class MyHanlder extends Handler{
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:

                    Log.i(TAG_3, "handleMessage: 发生旋转");
                    imageView_ori.setImageBitmap(rotateBM);

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
     * 刷新定位点
     * @param x
     * @param y
     * @param drawType
     */
    private void initUI(float x,float y,boolean drawType) {
        view.setX(x);
        view.setY(y);
        view.setDrawType(drawType);
        view.invalidate();
    }


    /**
     *
     * @param RSSI_ADD7
     * @param RSSI_A7F3
     * @param RSSI_AD9F
     * @param RSSI_7D19
     * @param K
     * @param H
     */
    public void KnnRequest(final double RSSI_ADD7, final double RSSI_A7F3, final double RSSI_AD9F, final double RSSI_7D19, final int K, final int H) {
        //请求地址
        String Url=url;
        String tag = "KnnRequest";    //注②
        final double[] x = new double[1];
        final double[] y = new double[1];

        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = (JSONObject) new JSONObject(response).get("params");
                            String result = jsonObject.getString("X");
                            x[0] =Double.valueOf(result);
                            result=jsonObject.getString("Y");
                            y[0]=Double.valueOf(result);
                            initUI((float) x[0], (float) y[0], true);
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.e(TAG_1, e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Main.this, "请检查网络连接设置", Toast.LENGTH_SHORT).show();
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e(TAG_1, error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ADD7", RSSI_ADD7+"");  //注⑥
                params.put("A7F3", RSSI_A7F3+"");
                params.put("AD9F", RSSI_AD9F+"");
                params.put("7D19", RSSI_7D19+"");
                params.put("K", K+"");
                params.put("H", H+"");
                return params;
            }
        };

        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }
}
