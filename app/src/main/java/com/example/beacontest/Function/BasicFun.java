package com.example.beacontest.Function;

import android.util.Log;

import static com.example.beacontest.Constant.TAG.TAG_1;

public class BasicFun {
    /**
     * 字节数组转换成十六进制字符串
     *
     * @param /String str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(byte[] bs) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 发射强度的补码转化为原码
     */
    public static Integer txPowerTransfer (String txPower , String id){
        int num = Integer.parseInt(txPower,16);
        int symbol = num >> 7;
        if(symbol == 1) {
            num = (num - 1) ^ 0xff;
            num = 0 - num;
            Log.i(TAG_1, "txPowerTransfer: " + id + ":" + num);
        }
        else {
            num = (num - 1) ^ 0xff;
            Log.e(TAG_1, "txPowerTransfer: " + "ID: " + id + "原始数据： " + txPower + "转换后数据: " + num );
        }
        return num;
    }
}
