package com.example.beacontest.View;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.example.beacontest.R;

import static com.example.beacontest.Constant.TAG.TAG_6;

public class DrawView extends View {
    private boolean drawType=false;
    private boolean drawType2=false;
    private float x;//x轴坐标
    private float y;//y轴坐标
    private static float[] oldX=new float[1024];//旧的x轴坐标
    private static float[] oldY=new float[1024];//旧的y轴坐标
    private static int num=0;
    private boolean pathEnable=false;//路径开关
    Paint paint;

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

        //设置画笔
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        if(drawType2){
            for(int i=1;i<=6;i++)
                canvas.drawLine(0,200+i*200,1080,200+i*200,paint);//直线
            for(int j=1;j<=5;j++)
                canvas.drawLine(180*j,200,180*j,1600,paint);//直线
        }

        if(!pathEnable) {
            //setLayerType(LAYER_TYPE_HARDWARE, null);//关闭硬件加速
            //canvas.drawColor(0, PorterDuff.Mode.CLEAR);//清空画布
            canvas.drawLine(0, 200, 1080, 200, paint);
            canvas.drawLine(0, 1600, 1080, 1600, paint);
            canvas.drawLine(0, 200, 0, 1600, paint);
            canvas.drawLine(1080, 200, 1080, 1600, paint);
            if(drawType){
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.FILL);
                //x轴一个单位长度
                int cx = 200;
                //y轴一个单位长度
                int cy = 180;
                //半径长度
                int radius = 10;
                canvas.drawCircle(cy * y, cx * x + 200, radius, paint);
            }
        }else {
            if(drawType) {
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.FILL);
                //x轴一个单位长度
                int cx = 200;
                //y轴一个单位长度
                int cy = 180;
                //半径长度
                int radius = 10;
                if (oldX[0]== 0 || oldY [0]== 0) {

                    oldX [num]= x;
                    oldY [num]= y;
                    //num++;
                    canvas.drawCircle(cy * y, cx * x + 200, radius, paint);
                } else {
                    num++;
                    canvas.drawCircle(cy * oldY[0], cx * oldX[0] + 200, radius, paint);
                    for(int i=0;i<=num-2;i++)
                        canvas.drawLine(oldY[i] * cy, oldX[i] * cx + 200, cy * oldY[i+1], cx * oldX[i+1] + 200, paint);
                    canvas.drawLine(oldY[num-1] * cy, oldX[num-1] * cx + 200, cy * y, cx * x + 200, paint);

                    oldX[num] = x;
                    oldY[num] = y;
                }
            }
            else{
                clear();
            }
        }

        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        /*bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.outline);
        canvas.drawBitmap(bitmap,100,610,paint);//不限定图片大小  只指定左上角坐标
        rectF=new RectF(0,200,1080,1600);
        canvas.drawBitmap(bitmap,null,rectF,paint);//限定图片显示范围*/


    }
}
