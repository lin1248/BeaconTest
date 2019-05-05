package com.example.beacontest.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beacontest.Constant.HistoryPoint;
import com.example.beacontest.DM.DM;
import com.example.beacontest.R;
import com.example.beacontest.Adapter.DatabaseAdapter;

import static com.example.beacontest.DM.DM.DeleteDatabase;
import static com.example.beacontest.DM.DM.QuerryTime;

public class DatabaseActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static DatabaseAdapter adapter;
    private static Intent intent;
    private Button btn_back,btn_delete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_main);
        intent=this.getIntent();
        ListView lv=findViewById(R.id.database_listview);
        adapter = new DatabaseAdapter(this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        btn_back=findViewById(R.id.btn_back_history);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseActivity.this.finish();
            }
        });
        btn_delete=findViewById(R.id.btn_deleteData);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteDatabase();//删除数据
                Toast.makeText(DatabaseActivity.this,"数据已经删除",Toast.LENGTH_SHORT).show();
                DatabaseActivity.this.finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //通过view获取其内部的组件，进而进行操作
        String date = (String) ((TextView)view.findViewById(R.id.tv_time)).getText();
        HistoryPoint historyPoint=QuerryTime(date);
        if(historyPoint!=null){
            float[] oldX=historyPoint.getoldX();
            float[] oldY=historyPoint.getoldY();
            int num=historyPoint.getNum();
            Log.i("TAG", "onItemClick:数据 "+oldX+"//"+oldY+"//"+num);
            intent.putExtra("date",date);
            DatabaseActivity.this.setResult(Activity.RESULT_OK, intent);//返回页面1
            DatabaseActivity.this.finish();
        }
    }
}
