package com.example.administrator.phoneinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class WebTestPlanActivity extends AppCompatActivity {

    private MyApplication myApplication;
    private TextView tvWebTestPlan1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webtest_plan);
        myApplication = (MyApplication) getApplicationContext();
        tvWebTestPlan1=(TextView) findViewById(R.id.tv_webtest_plan1);


        tvWebTestPlan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WebTestPlanActivity.this,WebTestActivity.class));
            }
        });

    }


}

