package com.example.beacontest.Function;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.beacontest.Constant.Coordinate;

import java.util.ArrayList;


import static com.example.beacontest.Constant.TAG.TAG_5;
import static com.example.beacontest.Constant.TAG.dbPath;
import static com.example.beacontest.Constant.TAG.querryName;

/**
 * KNN算法
 */
public class KNN {

    private ArrayList<Coordinate> LocationPointSet=new ArrayList<>();
    private Cursor cursor=null;
    /**
     * 获取定位点到各个指纹点的欧式距离的数据集(首次定位时使用)
     * @param RSSI_ADD7
     * @param RSSI_A7F3
     * @param RSSI_AD9F
     * @param RSSI_7D19
     * @return
     */
    private Coordinate[] getEulideanDistance(double RSSI_ADD7, double RSSI_A7F3, double RSSI_AD9F, double RSSI_7D19){
        Coordinate[] dataSet=new Coordinate[30];
        double result;
        double _ADD7,_A7F3,_AD9F,_7D19;
        int x,y;
        int i=0;
        //数据库操作
        SQLiteDatabase db=SQLiteDatabase.openDatabase(dbPath,null,SQLiteDatabase.OPEN_READONLY);
            cursor = db.rawQuery("select * from " + querryName, null);
            if (cursor.moveToFirst()) {
                do {
                    x = cursor.getInt(cursor.getColumnIndex("x"));
                    y = cursor.getInt(cursor.getColumnIndex("y"));
                    _ADD7 = cursor.getDouble(cursor.getColumnIndex("_ADD7"));
                    _A7F3 = cursor.getDouble(cursor.getColumnIndex("_A7F3"));
                    _AD9F = cursor.getDouble(cursor.getColumnIndex("_AD9F"));
                    _7D19 = cursor.getDouble(cursor.getColumnIndex("_7D19"));
                    result = Math.pow((_ADD7 - RSSI_ADD7), 2) + Math.pow((_A7F3 - RSSI_A7F3), 2) + Math.pow((_AD9F - RSSI_AD9F), 2)
                            + Math.pow((_7D19 - RSSI_7D19), 2);
                    result = Math.sqrt(result);
                    dataSet[i] = new Coordinate();
                    dataSet[i].setX(x);
                    dataSet[i].setY(y);
                    dataSet[i].setEulideanDistance(result);//距离各个坐标点的欧式距离按坐标点顺序存入数据集中
                    i++;
                    Log.d(TAG_5, "getEulideanDistance: 距离坐标为（" + x + "," + y + ")的欧式距离为" + result + "//id:" + i);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        return dataSet;
    }

    /**
     * 获取定位点到各个指纹点的欧式距离的数据集（非首次定位时使用）
     * @param RSSI_ADD7
     * @param RSSI_A7F3
     * @param RSSI_AD9F
     * @param RSSI_7D19
     * @param NearPoint
     * @return
     */
    private ArrayList<Coordinate> getEulideanDistance(double RSSI_ADD7, double RSSI_A7F3, double RSSI_AD9F, double RSSI_7D19, ArrayList<Coordinate> NearPoint){
        ArrayList<Coordinate> dataSet=new ArrayList<>();
        Coordinate usefulData;
        double result;
        double x,y;
        double _ADD7,_A7F3,_AD9F,_7D19;
        for(Coordinate a:NearPoint) {
            x = a.getX();
            y = a.getY();
            //Log.i(TAG_5, "getEulideanDistance: x="+x+"y="+y);
            String str1 = String.valueOf((int) x);
            String str2 = String.valueOf((int) y);
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
                cursor = db.rawQuery("select * from " + querryName + " where x=? and y=?", new String[]{str1, str2});
                if (cursor.moveToFirst()) {
                    _ADD7 = cursor.getDouble(cursor.getColumnIndex("_ADD7"));
                    _A7F3 = cursor.getDouble(cursor.getColumnIndex("_A7F3"));
                    _AD9F = cursor.getDouble(cursor.getColumnIndex("_AD9F"));
                    _7D19 = cursor.getDouble(cursor.getColumnIndex("_7D19"));
                    //Log.i(TAG_5, "getEulideanDistance: "+_ADD7+"//"+_A7F3+"//"+_AD9F+"//"+_7D19);
                    result = Math.pow((_ADD7 - RSSI_ADD7), 2) + Math.pow((_A7F3 - RSSI_A7F3), 2) + Math.pow((_AD9F - RSSI_AD9F), 2)
                            + Math.pow((_7D19 - RSSI_7D19), 2);
                    result = Math.sqrt(result);
                    usefulData=new Coordinate();
                    usefulData.setX(x);
                    usefulData.setY(y);
                    usefulData.setEulideanDistance(result);
                    dataSet.add(usefulData);
                }
                cursor.close();
                db.close();
        }
        return dataSet;
    }

    /**
     * 获取K个距离最近的点
     * @param dataSet_1  坐标和欧式距离集合
     * @param dataSet_2  坐标和欧式距离集合
     * @param K  用户设置的个数
     * @return
     */
    private Coordinate[] getKPoint(Coordinate[] dataSet_1 ,ArrayList<Coordinate> dataSet_2 ,int K){
        Coordinate[] pointSet=new Coordinate[K];

        if(dataSet_1!=null&&dataSet_2==null){//首次定位
            Coordinate buffer;
            //冒泡法排序 从大到小
            for(int i=0;i<29;i++){
                for(int j=0;j<29-i;j++){
                    if (dataSet_1[j].getEulideanDistance()>dataSet_1[j+1].getEulideanDistance())
                    {
                        buffer=dataSet_1[j];
                        dataSet_1[j]=dataSet_1[j+1];
                        dataSet_1[j+1]=buffer;
                    }
                }
            }
            for (int i=0;i<=K-1;i++) {
                pointSet[i]=dataSet_1[i];
                //Log.d(TAG_5, "getKPoint:首次定位 " + pointSet[i].getX() + "_" + pointSet[i].getY() + "///" + pointSet[i].getEulideanDistance());
            }
        }
        else if(dataSet_1==null&&dataSet_2!=null){//非首次定位
            int len=dataSet_2.size();
            Log.i(TAG_5, "getKPoint: len="+len);
            Coordinate[] dataSet_2_buffer=new Coordinate[len];
            int x=0;
            for(Coordinate a:dataSet_2){
                dataSet_2_buffer[x]=new Coordinate();
                dataSet_2_buffer[x++]=a;
                //Log.i(TAG_5, "getKPoint: "+a.getX()+a.getY());
            }
            Coordinate buffer;
            for(int i=0;i<len-1;i++){
                for(int j=0;j<len-1-i;j++){
                    if(dataSet_2_buffer[i].getEulideanDistance()>dataSet_2_buffer[i+1].getEulideanDistance())
                    {
                        buffer=dataSet_2_buffer[i];
                        dataSet_2_buffer[i]=dataSet_2_buffer[i+1];
                        dataSet_2_buffer[i+1]=buffer;
                    }
                }
            }
            if(len>=K) {
                for (int i = 0; i <= K - 1; i++) {
                    pointSet[i] = dataSet_2_buffer[i];
                    //Log.d(TAG_5, "getKPoint:非首次定位 个数大于K" + pointSet[i].getX() + "_" + pointSet[i].getY() + "///" + pointSet[i].getEulideanDistance());
                }
            }
            else
            {
                for (int i = 0; i <= len - 1; i++) {
                    pointSet[i] = dataSet_2_buffer[i];
                    //Log.d(TAG_5, "getKPoint:非首次定位 个数小于K" + pointSet[i].getX() + "_" + pointSet[i].getY() + "///" + pointSet[i].getEulideanDistance());
                }
            }
        }
        else {//错误
            return null;
        }
        return pointSet;
    }

    /**
     * 获取估计定位点
     * @param pointSet  K个点的集合
     * @return
     */
    private Coordinate getLocation(Coordinate[] pointSet){
        Coordinate pointLocation=new Coordinate();
        int len=pointSet.length;
        double totalInt=0;
        double x=0;
        double y=0;
        for(int i=0;i<=len-1;i++){
            totalInt+=pointSet[i].getEulideanDistance();
        }
        for(int i=0;i<=len-1;i++){
            x+=(pointSet[i].getEulideanDistance()/totalInt)*pointSet[i].getX();
            y+=(pointSet[i].getEulideanDistance()/totalInt)*pointSet[i].getY();
        }
        pointLocation.setX(x);
        pointLocation.setY(y);
        Log.d(TAG_5, "getLocation: 定位点为"+pointLocation.getX()+"_"+pointLocation.getY());
        //LocationPointSet.add(pointLocation);
        return pointLocation;
    }

    /**
     * 获取历史定位点附近半径为H以内的指纹点
     * @param pointLocationSet   定位点数据集
     * @param H  半径
     * @return
     */
    private ArrayList<Coordinate> getNearPoint(ArrayList<Coordinate> pointLocationSet, int H){
        ArrayList<Coordinate> NearPoint = new ArrayList<>();
        Coordinate[] coordinateData=getCoordinateData();
        int len=pointLocationSet.size();
        double x,y;
        //坐标点
        double distance;
        //距离
        double old_x=pointLocationSet.get(len-1).getX();
        double old_y=pointLocationSet.get(len-1).getY();
        for(Coordinate a:coordinateData){
            x=a.getX();
            y=a.getY();
            distance=Math.sqrt(Math.pow(x-old_x,2)+Math.pow(y-old_y,2));
            if(distance<=H){
                NearPoint.add(a);
                Log.i(TAG_5, "getNearPoint: x="+x+"y="+y);
            }
        }
        Log.i(TAG_5, "getNearPoint: end");
        return NearPoint;
    }

    /**
     * 坐标点的集合
     * @return
     */
     private Coordinate[] getCoordinateData(){
        Coordinate[] CoordinateData=new Coordinate[30];
        for(int i=1;i<=6;i++){
            for(int j=1;j<=5;j++){
                CoordinateData[(i-1)*5+j-1]=new Coordinate();//数组中每一个元素都需要实例
                CoordinateData[(i-1)*5+j-1].setX(i);
                CoordinateData[(i-1)*5+j-1].setY(j);
            }
        }
        return CoordinateData;
    }

    /**
     * 改进后KNN算法
     * @param RSSI_ADD7
     * @param RSSI_A7F3
     * @param RSSI_AD9F
     * @param RSSI_7D19
     * @param K
     * @param H
     * @return
     */
    public Coordinate KNN(double RSSI_ADD7, double RSSI_A7F3, double RSSI_AD9F, double RSSI_7D19,int K,int H){
        Coordinate LocationPoint;
        Log.i(TAG_5, "KNN: "+RSSI_ADD7+"//"+RSSI_A7F3+"//"+RSSI_AD9F+"//"+RSSI_7D19);
        Log.d(TAG_5, "KNN: LocationDataSet长度为"+LocationPointSet.size());
        if(LocationPointSet.size()>500){
            LocationPointSet=new ArrayList<>();
        }
        if(LocationPointSet.size()==0){
            Coordinate[] distanceDataSet;
            Coordinate[] KPoint;
            distanceDataSet=getEulideanDistance(RSSI_ADD7,RSSI_A7F3,RSSI_AD9F,RSSI_7D19);//获取欧式距离数据集
            KPoint=getKPoint(distanceDataSet,null,K);//获取K个欧式距离最小的数据集
            LocationPoint=getLocation(KPoint);//实现定位
            LocationPointSet.add(LocationPoint);//定位数据加入数据集
            //Log.d(TAG_5, "KNN: 首次定位distanceDataSet长度为"+distanceDataSet.length+"//KPoint长度为"+KPoint.length);
        }
        else
        {
            ArrayList<Coordinate> NearPoint;
            ArrayList<Coordinate> usefulDataSet;
            Coordinate[] KPoint;
            NearPoint=getNearPoint(LocationPointSet,H);//获取历史定位点在半径为H范围以内的所有点
            usefulDataSet=getEulideanDistance(RSSI_ADD7,RSSI_A7F3,RSSI_AD9F,RSSI_7D19,NearPoint);//获取在NearPoint中的欧式距离
            KPoint=getKPoint(null,usefulDataSet,K);//获取K个最近的指纹点 （可能少于K）
            LocationPoint=getLocation(KPoint);//实现定位
            LocationPointSet.add(LocationPoint);//定位数据加入数据集
            //Log.d(TAG_5, "KNN: 非首次定位distanceDataSet长度为"+usefulDataSet.size()+"//KPoint长度为"+KPoint.length);
         }
        return LocationPoint;
    }

}

