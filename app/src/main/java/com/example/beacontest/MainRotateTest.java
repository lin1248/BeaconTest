package com.example.beacontest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainRotateTest extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;
    private SensorManager sm=null;
    private Sensor aSensor=null;
    private Sensor mSensor=null;
    protected static final String TAG_3 = "RotateTest";
    private Message msg_rotate;
    private Message msg_btnTextChange;
    private NewHandler newHandler = new NewHandler();
    private GetOrientation getOrientation =new GetOrientation();
    RotateBitmap rotateBitmap = new RotateBitmap();
    private Bitmap originBM = null;
    private Bitmap rotateBM = null;
    private Button rotate_Btn;
    private String stopRotate = "停止旋转";
    private String startRotate = "开始旋转";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSION_CODE = 1;

    float[] accelerometerValues=new float[3];
    float[] magneticFieldValues=new float[3];
    float[] values=new float[3];
    float[] RValues=new float[9];
    private static int i = 0;
    private int angle = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotate_test);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }

        //originBM = BitmapFactory.decodeResource(getResources(),R.drawable.pika);
        textView = findViewById(R.id.Tv3);
        imageView = findViewById(R.id.IV_1);
        rotate_Btn = findViewById(R.id.rotate_Btn);
        rotate_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sm == null) {
                    msg_btnTextChange = Message.obtain();
                    msg_btnTextChange.what = 2;
                    msg_btnTextChange.obj = stopRotate;
                    newHandler.sendMessage(msg_btnTextChange);
                    sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                    sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
                    sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
                }
                else{
                    msg_btnTextChange = Message.obtain();
                    msg_btnTextChange.what = 3;
                    msg_btnTextChange.obj = startRotate;
                    newHandler.sendMessage(msg_btnTextChange);
                    sm.unregisterListener(myListener);
                    sm=null;
                }

            }
        });

    }


    /**
     * 申请权限的回调函数
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i(TAG_3, "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

    /**
     * 获取方向数据
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
            if(i++>5) {
                i=0;
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
        newHandler.sendMessage(msg_rotate);
    }
    /**
     * 结束的时候释放
     * 停止获取方向数据
     */
    @Override
    protected void onDestroy() {
        if(sm != null)
        sm.unregisterListener(myListener);
        super.onDestroy();

        Log.i(TAG_3, "onDestroy: Good Luck!");
    }

    /**
     * MyHandler更新UI
     */
    class NewHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:

                    Log.i(TAG_3, "handleMessage: 发生旋转");
                    imageView.setImageBitmap(rotateBM);
                    textView.setText((String)msg_rotate.obj);

                    break;
                case 2:
                    rotate_Btn.setText((String)msg.obj);
                    break;
                case 3:
                    rotate_Btn.setText((String)msg.obj);
                    break;
            }
        }
    }
}
