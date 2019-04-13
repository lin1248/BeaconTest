package com.example.beacontest.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


import com.example.beacontest.R;

public class SettingActivity extends AppCompatActivity {
    private EditText edit_K,edit_H;
    private RadioButton radio_net,radio_local;
    private Intent intent;
    private String mod_K,mod_H,mod_netServerSwicth;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    //监听返回按钮操作事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                SettingActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("设置");
        intent = this.getIntent();
        String k = intent.getStringExtra("K");
        String h = intent.getStringExtra("H");
        String netServerSwitch = intent.getStringExtra("netServerSwitch");
        edit_K=findViewById(R.id.edit_K);
        edit_H=findViewById(R.id.edit_H);
        radio_net=findViewById(R.id.radio_net);
        radio_local=findViewById(R.id.radio_local);
        Button btn_save = findViewById(R.id.btn_save);
        edit_K.setText(k);
        edit_H.setText(h);
        if (netServerSwitch.equals("1")){
            radio_net.setChecked(true);
        }
        else {
            radio_local.setChecked(true);
        }
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_H!=null&&edit_K!=null) {
                    mod_K = edit_K.getText().toString();
                    mod_H = edit_H.getText().toString();
                    if(radio_local.isChecked()){
                        mod_netServerSwicth="0";
                    }
                    else {
                        mod_netServerSwicth="1";
                    }
                    Bundle bundle = intent.getExtras();
                    bundle.putString("K", mod_K);//添加要返回给页面1的数据
                    bundle.putString("H",mod_H);
                    bundle.putString("netServerSwitch",mod_netServerSwicth);
                    intent.putExtras(bundle);
                    SettingActivity.this.setResult(Activity.RESULT_OK, intent);//返回页面1
                }
                else{
                    Toast.makeText(SettingActivity.this,"值不能为空",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
