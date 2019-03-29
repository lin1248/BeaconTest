package com.example.beacontest.Main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.beacontest.Function.KNN;
import com.example.beacontest.R;
import com.example.beacontest.constant.Coordinate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.beacontest.constant.TAG.TAG_test;
import static com.example.beacontest.constant.TAG.dbName;

public class testMain extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        copyDB(dbName);
        KNN knn=new KNN();
        Coordinate location=new Coordinate();
        location=knn.KNN(-60,-71,-71,-71,5,3);
        /*Coordinate[] data;
        data=KNN.getCoordinateData();
        for(int i=0;i<=29;i++)
            Log.d(TAG_test, "getCoordinateData: "+(i+1)+"组x:"+data[i].getX()+"//y:"+data[i].getY()+"\\");*/
    }

    /**
     拷贝数据库
     */
    public void copyDB(String dbName){
        try{
            File file=new File(getFilesDir(),dbName);
            if(file.exists()&&file.length()>0){
                //拷贝成功
                Log.d(TAG_test, "copyDB: 拷贝成功");
            }else {
                InputStream is=getAssets().open(dbName);

                FileOutputStream fos=new FileOutputStream(file);
                byte[] buffer =new byte[1024];
                int len;
                while((len=is.read(buffer))!=-1){
                    fos.write(buffer,0,len);
                }
                fos.close();
                is.close();
                Log.d(TAG_test, "copyDB: 流写入成功");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
