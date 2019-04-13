package com.example.beacontest.Main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
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
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.functions.Consumer;

import static com.example.beacontest.Constant.Address.mAdress_7D19;
import static com.example.beacontest.Constant.Address.mAdress_A7F3;
import static com.example.beacontest.Constant.Address.mAdress_AD9F;
import static com.example.beacontest.Constant.Address.mAdress_ADD7;
import static com.example.beacontest.Constant.TAG.TAG_1;
import static com.example.beacontest.Constant.TAG.TAG_3;
import static com.example.beacontest.Constant.TAG.dbName;
import static com.example.beacontest.Constant.TAG.url;


public class Main extends AppCompatActivity {
    private static float x = -1;
    private static float y = -1;
    private static int K = 5;
    private static int H = 3;
    private static int netServerSwitch = 1;
    private static boolean locationEnable = false;
    private static int times = 0;
    private static int t=50;
    private static int num = 0;
    private static int netTimes=0;
    private static Coordinate[] LocationSet=new Coordinate[t];

    private Message msg_timer,msg_showXY;
    private BluetoothAdapter mBluetoothAdapter;
    private GetOrientation getOrientation = new GetOrientation();
    private static final int request_enabled = 1;//定义一个int resultCode
    private MyHanlder myHanlder = new MyHanlder();
    private SensorManager sm = null;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private float[] values = new float[3];
    private float[] RValues = new float[9];

    private PowerManager.WakeLock mWakeLock;
    private double _ADD7, _A7F3, _AD9F, _7D19;
    private KNN knn = new KNN();
    private DrawView view;
    private RotateBitmap rotateBitmap = new RotateBitmap();
    private Bitmap originBM = null;
    private Bitmap rotateBM = null;
    private Timer timer;
    private TimerTask task;

    private MenuItem mMenuItem;
    private ImageView imageView_ori;
    private TextView tv_x,tv_y,tv_K,tv_H;
    private TextView tv_statue;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting, menu);
        mMenuItem = menu.findItem(R.id.action_start);//获取item实例
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting://监听菜单按钮
                Intent intent = new Intent(Main.this, SettingActivity.class);
                intent.putExtra("K", K + "");
                intent.putExtra("H", H + "");
                intent.putExtra("netServerSwitch", netServerSwitch + "");
                startActivityForResult(intent, 0);
                break;
            case R.id.action_start:
                if (locationEnable) {
                    stopTimer();
                    String start = "开始定位";
                    mMenuItem.setTitle(start);
                    locationEnable = false;
                    initUI(0, 0, false);
                } else {
                    String stop = "停止定位";
                    mMenuItem.setTitle(stop);
                    startTimer();
                    Log.i(TAG_1, "onClick: timer start!");
                    locationEnable = true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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

            if (_ADD7 != 0 && _A7F3 != 0 && _AD9F != 0 && _7D19 != 0  && locationEnable) {

                if (timer != null) {
                    Main.this.timer.cancel();
                }
                stopTimer();
                startTimer();
                if (netServerSwitch == 1) {//服务器计算
                    if(netTimes>=20){
                        KnnRequest(_ADD7, _A7F3, _AD9F, _7D19, K, H);
                        netTimes=0;
                    }

                    else
                        netTimes++;
                } else if (netServerSwitch == 0) {//本地计算
                    Coordinate locationPoint = knn.KNN(_ADD7, _A7F3, _AD9F, _7D19, K, H);
                    Log.i(TAG_1, "onResponse: 本地返回值+"+num);
                    LocationSet[num]=new Coordinate();
                    LocationSet[num++]=locationPoint;
                }
            }
            if(num>=t){
                Coordinate locationPoint = KNN.getAverageLocation(t,LocationSet);
                LocationSet=new Coordinate[t];
                num=0;

                x = (float) Math.round(locationPoint.getX()*100)/100;
                y = (float) Math.round(locationPoint.getY()*100)/100;

                msg_showXY=Message.obtain();
                msg_showXY.what=3;
                myHanlder.sendMessage(msg_showXY);

                initUI((float) locationPoint.getX(), (float) locationPoint.getY(), true);
            }
        }

    };

    /**
     * 关闭计时器
     */
    private void stopTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    /**
     * 开启计时器
     */
    private void startTimer(){
        if (timer == null) {
            timer = new Timer();
        }

        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    msg_timer=Message.obtain();
                    msg_timer.what=1;
                    mHandler.sendMessage(msg_timer);
                    Log.i(TAG_1, "run: timer done!");
                }
            };
        }
        if(timer != null && task != null )
            timer.schedule(task,10000);
    }

    /**
     * 处理其他线程发送的消息
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("ShowToast")
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case 1:
                    Toast.makeText(Main.this,"搜索Beacon超时",Toast.LENGTH_LONG).show();
                    Log.i(TAG_1, "handleMessage: timer done!");
                    break;
            }
        }
    };

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_layout);

        copyDB(dbName);//复制数据库
        //获取蓝牙设配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //获取方向传感器
        sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //注册监听
        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_GAME);

        tv_x=findViewById(R.id.tv_x);
        tv_y=findViewById(R.id.tv_y);
        tv_K=findViewById(R.id.tv_K);
        tv_H=findViewById(R.id.tv_H);
        ImageView image_statue = findViewById(R.id.image_status);
        image_statue.setImageResource(R.drawable.greenpoint);
        ViewGroup.LayoutParams params = image_statue.getLayoutParams();
        params.height=60;
        params.width=60;
        image_statue.setLayoutParams(params);

        tv_statue=findViewById(R.id.tv_statue);
        ImageView imageView = findViewById(R.id.image);
        imageView.setImageResource(R.drawable.outline);
        params =imageView.getLayoutParams();
        params.height=1400;
        params.width=1080;
        imageView.setLayoutParams(params);

        imageView_ori=findViewById(R.id.image_ori);
        imageView_ori.setImageResource(R.drawable.pika);
        params =imageView_ori.getLayoutParams();
        params.height=200;
        params.width=200;
        imageView_ori.setLayoutParams(params);

        //获取权限
        RxPermissions rxPermissions=new RxPermissions(this);
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

        RadioButton radio_btn2 = findViewById(R.id.radioCancel);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        ConstraintLayout layout = findViewById(R.id.final_layout);
        radio_btn2.setChecked(true);

        view = new DrawView(this);
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

        Message msg_init = Message.obtain();
        msg_init.what=2;
        myHanlder.sendMessage(msg_init);

        initUI(0,0,false);//初始化UI
        //initWakeLock();
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
     * 用于发送Msg旋转箭头
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
        Message msg_rotate = Message.obtain();
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
     * 广播的回调函数
     * 获取蓝牙打开的状态
     * 更新界面信息
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
        if(requestCode == 0){
            if (resultCode == Activity.RESULT_OK) {
                K= Integer.valueOf(data.getStringExtra("K"));
                H= Integer.valueOf(data.getStringExtra("H"));
                Message msg_showKH=Message.obtain();
                msg_showKH.what=2;
                myHanlder.sendMessage(msg_showKH);
                netServerSwitch=Integer.valueOf(data.getStringExtra("netServerSwitch"));
                if(netServerSwitch==1){
                    Message msg_showStatus=Message.obtain();
                    msg_showStatus.what=4;
                    myHanlder.sendMessage(msg_showStatus);
                    Log.i(TAG_1, "onActivityResult: 4 "+netServerSwitch);
                }else{
                    Message msg_showStatus=Message.obtain();
                    msg_showStatus.what=6;
                    myHanlder.sendMessage(msg_showStatus);
                    Log.i(TAG_1, "onActivityResult: 6 " +netServerSwitch);
                }
                Log.d(TAG_1, "onActivityReenter: "+K+H+netServerSwitch);
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
                case 2:
                    tv_K.setText("   "+K);
                    tv_H.setText("   "+H);
                    break;
                case 3:
                    tv_x.setText(" "+x);
                    tv_y.setText(" "+y);
                    break;
                case 4:
                    tv_statue.setText("联网");
                    break;
                case 5:
                    //tv5.setText("方向："+msg.obj);
                    Log.i(TAG_1, "Orientation is"+msg.obj);
                    break;
                case 6:
                    tv_statue.setText("本地");
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
        final double[] _x = new double[1];
        final double[] _y = new double[1];

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
                            _x[0] =Double.valueOf(result);
                            result=jsonObject.getString("Y");
                            _y[0]=Double.valueOf(result);

                            Log.i(TAG_1, "onResponse: 网络返回值+"+num);
                            x = (float) Math.round(_x[0]*100)/100;
                            y = (float) Math.round(_y[0]*100)/100;

                            msg_showXY=Message.obtain();
                            msg_showXY.what=3;
                            myHanlder.sendMessage(msg_showXY);

                            initUI((float) _x[0], (float) _y[0], true);
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
