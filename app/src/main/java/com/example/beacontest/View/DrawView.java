package com.example.beacontest.View;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.beacontest.DM.DM;
import com.example.beacontest.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.beacontest.Constant.TAG.TAG_6;
import static com.example.beacontest.DM.DM.storeDatabase;

public class DrawView extends View {
    private boolean drawType=false;
    private boolean drawType2=false;
    private boolean mod_history=false;
    private float x;//x轴坐标
    private float y;//y轴坐标
    private static float[] oldX=new float[1024];//旧的x轴坐标
    private static float[] oldY=new float[1024];//旧的y轴坐标
    private static int num=0;
    private static int history_num=0;
    private boolean pathEnable=false;//路径开关
    private static int maxNum=1024;
    private SQLiteDatabase db;
    Paint paint;

    public void setHistory_num(int history_num){
        DrawView.history_num =history_num;
    }

    public void storeData(){
        storeDatabase(oldX,oldY,num);
    }

    public void setMod_history(boolean mod_history){
        this.mod_history=mod_history;
    }

    public void setNum(int num){
        DrawView.num =num;
    }

    public void setOldX(float[] oldX){
        DrawView.oldX =oldX;
    }

    public void setOldY(float[] oldY){
        DrawView.oldY=oldY;
    }

    public void clear(){
        oldX=new float[1024];
        oldY=new float[1024];
        num=0;
    }

    public void setPathEnable(boolean pathEnable){
        this.pathEnable=pathEnable;
    }

    public void setDrawType2(boolean type){
        this.drawType2=type;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setDrawType(boolean drawType) {
        this.drawType = drawType;
    }

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG_6, "onDraw: 刷新UI"+pathEnable);
        //x轴一个单位长度
        int cx = 200;
        //y轴一个单位长度
        int cy = 180;
        //半径长度
        int radius = 10;
        //设置画笔
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        if(drawType2){//是否画出网格线
           drawGridLine(canvas,paint);//画出网格线
        }

        Log.i("TAG", "onDraw: "+mod_history);
        if(!mod_history) {
            if (!pathEnable) {//是否路径显示
                Log.i("TAG", "onDraw: 不显示路径" + drawType + mod_history);
                if (drawType) {//开始定位
                    drawFrame(canvas, paint);//画出框架
                    paint.setColor(Color.GREEN);
                    paint.setStyle(Paint.Style.FILL);
                    storeArray(x, y);//存入数组
                    canvas.drawCircle(cy * y, cx * x + 200, radius, paint);
                } else {
                    num = 0;
                }
            } else {
                Log.i("TAG", "onDraw: 显示路径" + drawType + mod_history);
                if (drawType) {//开始定位
                    paint.setColor(Color.GREEN);
                    paint.setStyle(Paint.Style.FILL);
                    if (oldX[0] == 0 || oldY[0] == 0) {
                        Log.i("TAG", "onDraw: 第一次定位" + mod_history);
                        oldX[0] = x;
                        oldY[0] = y;
                        canvas.drawCircle(cy * y, cx * x + 200, radius, paint);
                    } else {
                        storeArray(x, y);//存入数组
                        canvas.drawCircle(cy * oldY[0], cx * oldX[0] + 200, radius, paint);
                        for (int i = 0; i <= num - 2; i++) {
                            canvas.drawLine(oldY[i] * cy, oldX[i] * cx + 200, cy * oldY[i + 1], cx * oldX[i + 1] + 200, paint);
                            //画出历史定位点
                        }
                        canvas.drawLine(oldY[num - 1] * cy, oldX[num - 1] * cx + 200, cy * y, cx * x + 200, paint);
                    }
                } else {//停止定位时清零num
                    num = 0;
                    clear();
                }
            }
        }else{
            Log.i("TAG", "onDraw: 数量"+history_num);
            if(!pathEnable){
                paint.setStyle(Paint.Style.FILL);
                for (int i = 0; i <= history_num - 1; i++) {
                    canvas.drawCircle(oldY[i]*cy,oldX[i]*cx+200,radius, paint);
                    //画出历史定位点
                }
            }else {
                paint.setStyle(Paint.Style.FILL);
                for (int i = 0; i <= history_num - 2; i++) {
                    canvas.drawLine(oldY[i] * cy, oldX[i] * cx + 200, cy * oldY[i + 1], cx * oldX[i + 1] + 200, paint);
                    //画出历史定位点
                }
                canvas.drawLine(oldY[history_num - 1] * cy, oldX[history_num - 1] * cx + 200, cy * y, cx * x + 200, paint);
            }
        }
    }

    /**
     * 存入数组
     * @param x
     * @param y
     */
    private void storeArray(float x,float y){

        if(num==maxNum){
            num=0;
        }
        oldX[num] = x;
        oldY[num] = y;
        num++;
        Log.i(TAG_6, "storeArray: 存入数组");
    }

    /**
     * 画出框架
     * @param canvas
     * @param paint
     */
    private void drawFrame(Canvas canvas,Paint paint){
        canvas.drawLine(0, 200, 1080, 200, paint);
        canvas.drawLine(0, 1600, 1080, 1600, paint);
        canvas.drawLine(0, 200, 0, 1600, paint);
        canvas.drawLine(1080, 200, 1080, 1600, paint);
        Log.i(TAG_6, "drawFrame: 画出框架");
    }

    /**
     * 画出网格线
     * @param canvas
     * @param paint
     */
    private void drawGridLine(Canvas canvas,Paint paint) {
        for(int i=1;i<=6;i++)
            canvas.drawLine(0,200+i*200,1080,200+i*200,paint);//直线
        for(int j=1;j<=5;j++)
            canvas.drawLine(180*j,200,180*j,1600,paint);//直线
        Log.i(TAG_6, "drawGridLine: 画出网格线");
    }
}
