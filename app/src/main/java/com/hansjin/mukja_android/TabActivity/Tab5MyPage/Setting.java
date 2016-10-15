package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hansjin.mukja_android.R;

public class Setting extends AppCompatActivity {

    Button BT_X;
    Button BT_food_rate;
    Button BT_pref_anal;
    Button BT_push_setting;
    Button BT_agreement;
    Button BT_privacy_rule;
    Button BT_logout;
    Button BT_withdrawal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initViewSetting();

    }

    private void initViewSetting() {
        Toolbar cs_toolbar = (Toolbar)findViewById(R.id.cs_toolbar);
        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle("내 정보");

        BT_X = (Button) findViewById(R.id.BT_X);
        BT_food_rate = (Button) findViewById(R.id.BT_food_rate);
        BT_pref_anal = (Button) findViewById(R.id.BT_pref_anal);
        BT_push_setting = (Button) findViewById(R.id.BT_push_setting);
        BT_agreement = (Button) findViewById(R.id.BT_agreement);
        BT_privacy_rule = (Button) findViewById(R.id.BT_privacy_rule);
        BT_logout = (Button) findViewById(R.id.BT_logout);
        BT_withdrawal = (Button) findViewById(R.id.BT_withdrawal);

        BT_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BT_food_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FoodRate.class));
            }
        });

        BT_pref_anal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //승진이형이 취향분석 다하면 여기에 넣기
            }
        });

        BT_push_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        BT_agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Agreement.class));
            }
        });

        BT_privacy_rule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PrivacyRule.class));
            }
        });

        BT_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        BT_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
