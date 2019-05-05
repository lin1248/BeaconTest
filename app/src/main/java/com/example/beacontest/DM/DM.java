package com.example.beacontest.DM;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.beacontest.Constant.HistoryPoint;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.beacontest.Constant.TAG.TAG_6;
import static com.example.beacontest.Constant.TAG.TAG_DB;

public class DM {
    private static SQLiteDatabase db;
    private static Gson gson = new Gson();
    /**
     * 打开或者创建数据库
     */
    public static void openOrCreateDb(){
        db= SQLiteDatabase.openOrCreateDatabase("data/data/com.example.beacontest/files/history.db",null);
        try {
            //创建表SQL语句
            String stu_table = "create table historyPoint(_id integer primary key autoincrement,date TEXT,oldX TEXT,oldY TEXT,num INTEGER)";
            //执行SQL语句
            db.execSQL(stu_table);
        }catch (SQLException e){
            Log.i(TAG_DB, "openOrCreateDb: table已存在");
        }
        Log.i(TAG_6, "openOrCreateDb: 打开或者创建数据库");
    }

    /**
     * 保存数据
     * @param oldX
     * @param oldY
     */
    public static void storeDatabase(float[] oldX, float[] oldY, int num){
        Log.i("TAG", "storeDatabase: 保存一次");
        openOrCreateDb();
        //实例化常量值
        ContentValues cValue = new ContentValues();
        cValue.put("date", getDate());
        cValue.put("oldX", gson.toJson(oldX));
        cValue.put("oldY", gson.toJson(oldY));
        cValue.put("num",num);
        //调用insert()方法插入数据
        if(db.insert("historyPoint",null,cValue)==-1){
            Log.i(TAG_6, "storeDatabase: 插入数据失败");
        }else{
            Log.i(TAG_6, "storeDatabase: 插入数据");
        }

        db.close();
    }

    /**
     * 获取当前时间
     * @return
     */
    public static String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        Log.i(TAG_6, "getDate: 获取当前时间");
        return simpleDateFormat.format(date);
    }

    /**
     * 查询数据库
     * @return
     */
    public static ArrayList<HistoryPoint> QuerryDataBase(){
        openOrCreateDb();
        ArrayList<HistoryPoint> pointList=new ArrayList<HistoryPoint>();
        Cursor cursor = db.query("historyPoint", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String _id = cursor.getString(0);
            String time = cursor.getString(1);
            float[] oldX =gson.fromJson(cursor.getString(2),float[].class);
            float[] oldY =gson.fromJson(cursor.getString(3),float[].class);
            int num=cursor.getInt(4);
            HistoryPoint historyPoint=new HistoryPoint(_id,time,oldX,oldY,num);
            pointList.add(historyPoint);
            Log.i("TAG", "Querry: 查询到数据"+pointList);
        }
        cursor.close();
        db.close();
        return pointList;
    }

    /**
     * 查询时间
     * @return
     */
    public static HistoryPoint QuerryTime(String date){
        openOrCreateDb();
        HistoryPoint historyPoint;
        Cursor cursor = db.query("historyPoint",  new String[]{"_id,date,oldX,oldY,num"}, "date like ?", new String[]{date}, null, null, null);
        if (cursor.moveToNext()) {
            String _id = cursor.getString(0);
            String time = cursor.getString(1);
            float[] oldX =gson.fromJson(cursor.getString(2),float[].class);
            float[] oldY =gson.fromJson(cursor.getString(3),float[].class);
            int num=cursor.getInt(4);
            historyPoint=new HistoryPoint(_id,time,oldX,oldY,num);
            cursor.close();
            db.close();
            return historyPoint;
        }
        return null;
    }

    /**
     * 删除数据
     */
    public static void DeleteDatabase(){
        openOrCreateDb();
        db.execSQL("DELETE FROM "+"historyPoint");
    }
}
