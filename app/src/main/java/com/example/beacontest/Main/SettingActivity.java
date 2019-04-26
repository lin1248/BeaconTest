package com.example.beacontest.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


import com.example.beacontest.R;

public class SettingActivity extends AppCompatActivity {
    private EditText edit_K,edit_H,edit_t;
    private RadioButton radio_net,radio_local,radio_show,radio_noShow;
    private Intent intent;
    private String mod_K,mod_H,mod_t,mod_netServerSwicth,mod_isShow;
    private Button btn_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_main);
        //以下为接收Main传过来的信息并且处理
        intent = this.getIntent();
        String k = intent.getStringExtra("K");
        String h = intent.getStringExtra("H");
        String t = intent.getStringExtra("t");
        String pathEnable = intent.getStringExtra("pathEnable");
        Log.d("TAG", "onCreate: K"+k+"H"+h+"t"+t+"pathEnable"+pathEnable);
        String netServerSwitch = intent.getStringExtra("netServerSwitch");
        edit_K=findViewById(R.id.edit_K);
        edit_H=findViewById(R.id.edit_H);
        edit_t=findViewById(R.id.edit_t);
        radio_net=findViewById(R.id.radio_net);
        radio_local=findViewById(R.id.radio_local);
        radio_show=findViewById(R.id.radio_show);
        radio_noShow=findViewById(R.id.radio_noShow);
        Button btn_save = findViewById(R.id.btn_save);
        edit_K.setText(k);
        edit_H.setText(h);
        edit_t.setText(t);
        if (netServerSwitch.equals("1")){
            radio_net.setChecked(true);
        }
        else {
            radio_local.setChecked(true);
        }
        if(pathEnable.equals("1")){
            radio_show.setChecked(true);
        }else {
            radio_noShow.setChecked(true);
        }
        btn_save.setOnClickListener(new View.OnClickListener() {//保存按钮
            @Override
            public void onClick(View v) {
                if(edit_H!=null&&edit_K!=null) {
                    mod_K = edit_K.getText().toString();
                    mod_H = edit_H.getText().toString();
                    mod_t = edit_t.getText().toString();
                    if(radio_local.isChecked()){
                        mod_netServerSwicth="0";
                    }
                    else {
                        mod_netServerSwicth="1";
                    }
                    if(radio_show.isChecked()){
                        mod_isShow="1";
                    }
                    else {
                        mod_isShow="0";
                    }
                    Bundle bundle = intent.getExtras();
                    bundle.putString("K", mod_K);//添加要返回给页面1的数据
                    bundle.putString("H",mod_H);
                    bundle.putString("t",mod_t);
                    bundle.putString("pathEnable",mod_isShow);
                    bundle.putString("netServerSwitch",mod_netServerSwicth);
                    intent.putExtras(bundle);
                    SettingActivity.this.setResult(Activity.RESULT_OK, intent);//返回页面1
                }
                else{
                    Toast.makeText(SettingActivity.this,"值不能为空",Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {//返回按钮
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });
    }
}
