package com.example.beacontest.View;

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
    private int cx=200;//x轴一个单位长度
    private int cy=180;//y轴一个单位长度
    private float x;//x轴坐标
    private float y;//y轴坐标
    private int radius=10;//半径长度
    Paint paint;

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG_6, "onDraw: 刷新UI");
        setLayerType(LAYER_TYPE_HARDWARE,null);//关闭硬件加速
        canvas.drawColor(0,PorterDuff.Mode.CLEAR);//清空画布
        //设置画笔
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(0,200,1080,200,paint);
        canvas.drawLine(0,1600,1080,1600,paint);
        canvas.drawLine(0,200,0,1600,paint);
        canvas.drawLine(1080,200,1080,1600,paint);
        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        /*bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.outline);
        canvas.drawBitmap(bitmap,100,610,paint);//不限定图片大小  只指定左上角坐标
        rectF=new RectF(0,200,1080,1600);
        canvas.drawBitmap(bitmap,null,rectF,paint);//限定图片显示范围*/
        if(drawType){
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cy*y,cx*x+200,radius,paint);
        }
        if(drawType2){
            for(int i=1;i<=6;i++)
                canvas.drawLine(0,200+i*200,1080,200+i*200,paint);//直线
            for(int j=1;j<=5;j++)
                canvas.drawLine(180*j,200,180*j,1600,paint);//直线
        }
    }
}
