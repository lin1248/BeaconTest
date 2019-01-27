package com.example.beacontest;


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
    protected static double formula_1(int txPower , double rssi){
        if(rssi == 0){
            return -1;
        }

        double ratio = rssi * 1.0 / txPower;
        if(ratio < 1.0){
            return Math.pow(ratio , 10);
        }else{
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }


}
