package com.hansjin.mukja_android.Splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.ResultActivity_;
import com.hansjin.mukja_android.Sign.SignActivity;
import com.hansjin.mukja_android.TabActivity.TabActivity_;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_activity)
public class SplashActivity extends AppCompatActivity {
    SplashActivity activity;

//    @ViewById
//    Toolbar cs_toolbar;

    @AfterViews
    void afterBindingView() {
        this.activity = this;



//        setSupportActionBar(cs_toolbar);
//        getSupportActionBar().setTitle("스플래쉬");

        // 로그인 성공 후 시작
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent = new Intent(activity, TabActivity_.class);
                Intent intent = new Intent(activity, SignActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    void refresh() {

    }

    @UiThread
    void uiThread() {

    }

    void connectTestCall() {

    }
}
