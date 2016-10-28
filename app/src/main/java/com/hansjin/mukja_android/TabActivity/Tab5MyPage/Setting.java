package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Sign.SignActivity;
import com.hansjin.mukja_android.Sign.SignFragment;
import com.hansjin.mukja_android.TabActivity.TabActivity_;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.PopupNotCompleted;
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
                startActivity(new Intent(getApplicationContext(), SignActivity.class));
                finish();
            }
        });

        BT_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PopupWithdrawal.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isWithdrawal)
            withdrawal();
    }

    void withdrawal(){
        Map field = new HashMap();
        field.put("user_id", SharedManager.getInstance().getMe()._id);
        connectWithdrawalUser(field);
    }

    void connectWithdrawalUser(final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.withdrawalUser(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            editor.clear();
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(), SignActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
