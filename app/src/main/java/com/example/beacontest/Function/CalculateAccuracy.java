package com.example.beacontest.Function;


import android.util.Log;

import static java.lang.Math.pow;
import static java.lang.StrictMath.abs;

/**
 * 计算beacon距离
 */
public class CalculateAccuracy {
    /**
     * formula_1
     * @param txPower
     * @param rssi
     * @return
     */
    private static final String TAG = "Accuracy";
    protected static double formula_1(int txPower , double rssi){
        if(rssi == 0){
            return -1;
        }

        double ratio = rssi * 1.0 / txPower;
        if(ratio < 1.0){
            return pow(ratio , 10);
        }else{
            double accuracy = (0.89976) * pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    /**
     * formula_2
     * @param rssi
     * @return
     */
     public static String formula_2(int rssi){
        int iRssi = abs(rssi);
        float power = (float) ((iRssi - 61)/(10*2.0));
        float acc_f = (float) pow(10 , power);
        String acc = String.format("%4.2f", acc_f);   //取两位
                Log.i(TAG, "formula_2: " + acc_f +"::" + acc);
        return acc; //保留两位小数
    }
}
