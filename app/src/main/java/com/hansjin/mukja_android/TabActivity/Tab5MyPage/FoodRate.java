package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hansjin.mukja_android.R;

public class FoodRate extends AppCompatActivity {

    Button BT_X;

    int ratedFoodNumber = 0; // default 0
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_rate);

        Toolbar cs_toolbar = (Toolbar)findViewById(R.id.cs_toolbar);
        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle("음식 평가 - " + ratedFoodNumber + "개 완료");

        BT_X = (Button) findViewById(R.id.BT_X);



        BT_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
