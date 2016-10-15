package com.hansjin.mukja_android.Utils.Dialogs;

/**
 * Created by lmjin_000 on 2016-05-10.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hansjin.mukja_android.R;

import retrofit2.Retrofit;

public class CustomDialog extends Dialog  {

    private TextView txt_food_name;
    private RatingBar ratingbar;
    private String food_name;
    private float rate_num;

    public CustomDialog(Context context,String food) {
        super(context);
        food_name = food;
    }

    public float getRatenum(){
        return rate_num;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rate_dialog);
        init();

        txt_food_name.setText(food_name);
        set_rating();
    }

    private void set_rating() {
        ratingbar.setIsIndicator(false);
        ratingbar.setStepSize(0.1f);
        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            //v : 4개가 채워지면 0.5점*8만큼 해서 점수가 v=4로 넘어 온다.
            public void onRatingChanged(RatingBar ratingBar, float v, boolean fromUser) {
                //5점 만점
                float st = 5f / ratingBar.getNumStars();
                rate_num = st * v;
                dismiss();
            }
        });
    }

    private void init() {
        txt_food_name = (TextView) findViewById(R.id.food_name);
        ratingbar = (RatingBar) findViewById(R.id.ratingBar);
    }
}