package com.hansjin.mukja_android.Splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Sign.SignActivity;
import com.hansjin.mukja_android.TabActivity.TabActivity_;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.UiThread;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class SplashTest2 extends AppCompatActivity {

    ImageView IV_logo, IV_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_test2);
        final SplashTest2 activity = this;


        IV_logo=(ImageView) findViewById(R.id.IV_logo);
        IV_user=(ImageView) findViewById(R.id.IV_user);

        IV_logo.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.logo_todayfood)));



        final int[] slide_id = new int[8];
        int i=0;
        slide_id[i++] = R.anim.slide_in_2;
        slide_id[i++] = R.anim.slide_in_5;
        slide_id[i++] = R.anim.slide_in_8;
        slide_id[i++] = R.anim.slide_in_11;
        slide_id[i++] = R.anim.slide_in_up;
        slide_id[i++] = R.anim.slide_in_down;
        slide_id[i++] = R.anim.slide_in_left;
        slide_id[i++] = R.anim.slide_in_right;

        final String[] image_url = new String[4];
        image_url[0] = Constants.IMAGE_BASE_URL + "sample_food_1.jpg";
        image_url[1] = Constants.IMAGE_BASE_URL + "sample_food_2.jpg";
        image_url[2] = Constants.IMAGE_BASE_URL + "sample_food_3.jpg";
        image_url[3] = Constants.IMAGE_BASE_URL + "sample_food_4.jpg";

        Glide.with(getApplicationContext()).
            load(image_url[0]).
                //animate(R.anim.slide_in_2).
                animate(slide_id[(int) (Math.random() * 8)]).
            into(IV_user);

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                Glide.with(getApplicationContext()).
                        load(image_url[1]).
                        //animate(R.anim.slide_in_5).
                        animate(slide_id[(int) (Math.random() * 8)]).
                        into(IV_user);
            }
        }, 3000);

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                Glide.with(getApplicationContext()).
                        load(image_url[2]).
                        //animate(R.anim.slide_in_8).
                        animate(slide_id[(int) (Math.random() * 8)]).
                        into(IV_user);
            }
        }, 6000);

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                Glide.with(getApplicationContext()).
                        load(image_url[3]).
                        //animate(R.anim.slide_in_11).
                        animate(slide_id[(int) (Math.random() * 8)]).
                        into(IV_user);
            }
        }, 9000);


        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("TodayFood", Context.MODE_PRIVATE);

                if(prefs.getString("social_id","").equals("")){
                    Intent intent = new Intent(activity, SignActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Map field = new HashMap();
                    field.put("social_id", prefs.getString("social_id", ""));

                    connectSigninUser(field);
                }

            }
        }, 10000);

        Timer timer = new Timer();
        timer.schedule(this.spashScreenFinished, 12000);
    }

    private final TimerTask spashScreenFinished = new TimerTask() {
        @Override
        public void run() {
            finish();
//            Intent splash = new Intent(SplashTest2.this, Setting.class);
//            // We set these flags so the user cannot return to the SplashScreen
//            splash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(splash);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleView(findViewById(R.id.layout));
    }

    private void recycleView(View view) {
        if(view != null) {
            Drawable bg = view.getBackground();
            if(bg != null) {
                bg.setCallback(null);
                ((BitmapDrawable)bg).getBitmap().recycle();
                view.setBackground(null);
            }
        }
    }


    void connectSigninUser(final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.signinUser(field)
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
                            SharedManager.getInstance().setMe(response);
                            Intent intent = new Intent(getApplicationContext(), TabActivity_.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
