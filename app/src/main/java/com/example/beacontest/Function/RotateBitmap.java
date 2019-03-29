package com.example.beacontest.Function;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import static com.example.beacontest.constant.TAG.TAG_3;


public class RotateBitmap {
    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public Bitmap rotateBitmapFun(Bitmap origin, float alpha) {
        alpha = 0 - alpha;
        if (origin == null) {
            Log.i(TAG_3, "rotateBitmapFun: 原图片为空");
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
}
