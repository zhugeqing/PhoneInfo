package com.example.administrator.phoneinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AppUpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);
        Button buttonUpdate=(Button) findViewById(R.id.btn_updateversion);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AppUpdateActivity.this, "开始下载云图软件...", Toast.LENGTH_LONG).show();
                Intent intent_update = new Intent(AppUpdateActivity.this,MyAppUpdateService.class);
                startService(intent_update);
            }
        });
    }
}
