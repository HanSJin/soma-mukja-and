package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Sign.SignActivity;
import com.hansjin.mukja_android.Sign.SignFragment;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.PopupNotCompleted;
import com.hansjin.mukja_android.Utils.PopupRequest;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Setting extends AppCompatActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Button BT_X;
    Button BT_food_rate;
    Button BT_pref_anal;
    Button BT_push_setting;
    Button BT_agreement;
    Button BT_privacy_rule;
    Button BT_logout;
    Button BT_withdrawal;
    Switch ST_push;

    public static boolean isWithdrawal = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initViewSetting();

    }

    private void initViewSetting() {
        Toolbar cs_toolbar = (Toolbar)findViewById(R.id.cs_toolbar);
        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle("설정");

        prefs = getSharedPreferences("TodayFood", MODE_PRIVATE);
        editor = prefs.edit();

        BT_X = (Button) findViewById(R.id.BT_X);
        BT_food_rate = (Button) findViewById(R.id.BT_food_rate);
        BT_pref_anal = (Button) findViewById(R.id.BT_pref_anal);
        BT_push_setting = (Button) findViewById(R.id.BT_push_setting);
        BT_agreement = (Button) findViewById(R.id.BT_agreement);
        BT_privacy_rule = (Button) findViewById(R.id.BT_privacy_rule);
        BT_logout = (Button) findViewById(R.id.BT_logout);
        BT_withdrawal = (Button) findViewById(R.id.BT_withdrawal);
        ST_push = (Switch) findViewById(R.id.push_switch);

        BT_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BT_food_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FoodRate_.class));
            }
        });

        BT_pref_anal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //승진이형이 취향분석 다하면 여기에 넣기
                startActivity(new Intent(getApplicationContext(), PopupNotCompleted.class));
            }
        });

        BT_push_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PopupNotCompleted.class));
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
                editor.clear();
                editor.commit();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(getApplicationContext(), SignActivity.class));
                finish();
            }
        });

        BT_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PopupRequest.class));
                //탈퇴 기능 완성했지만 쓰지않는걸로(탈퇴를 완벽하게 구현하려면 서버단에서 data 조인해서 지워야할 것이 무진장 많음)
                //startActivity(new Intent(getApplicationContext(), PopupWithdrawal.class));
            }
        });

        if(SharedManager.getInstance().getPush())
            ST_push.setChecked(true);
        else
            ST_push.setChecked(false);

        ST_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    FirebaseMessaging.getInstance().subscribeToTopic("push_on");
                    SharedManager.getInstance().setPush(true);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("push_on");
                    SharedManager.getInstance().setPush(false);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
