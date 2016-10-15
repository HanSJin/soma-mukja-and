package com.hansjin.mukja_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.Result;
import com.hansjin.mukja_android.Model.itemScores;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_result)
public class ResultActivity extends AppCompatActivity {
    SharedPreferences sp;
    Food item_food;

    @ViewById(R.id.food)
    TextView food;
    @ViewById(R.id.taste)
    TextView taste;
    @ViewById(R.id.ingredient)
    TextView ingredient;
    @ViewById(R.id.cooking)
    TextView cooking;
    @ViewById(R.id.country)
    TextView country;
    @ViewById(R.id.Similar)
    TextView similar;

    @ViewById
    public LinearLayout indicator;

    @Click(R.id.similar_btn)
    void similar_btn(){
        //connectSimilarCall();
    }
    @Click(R.id.buy)
    void buy(){
        connectBuyCall();
    }

    @AfterViews
    void afterBindingView() {
        Intent intent = getIntent();
        Log.i("test","받음 : "+intent.getStringExtra("food_id"));
        connectTestCall(intent.getStringExtra("food_id"));
    }

    void connectTestCall(String id) {
        LoadingUtil.startLoading(indicator);
        sp = getSharedPreferences("user", MODE_PRIVATE);

        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getOneFood(id,sp.getString("user_id",null))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Food response) {
                        if (response != null) {
                            item_food = response;
//                            food.setText(response.getName());
//                            taste.setText("맛 : "+TextUtils.join(",",response.getTaste()));
//                            country.setText("국적 : "+TextUtils.join(",",response.getCountry()));
//                            cooking.setText("조리 : "+TextUtils.join(",",response.getCooking()));
//                            ingredient.setText("재료 : "+TextUtils.join(",",response.getIngredient()));

                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void connectBuyCall() {
        LoadingUtil.startLoading(indicator);
        sp = getSharedPreferences("user", MODE_PRIVATE);

        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.buyItem(sp.getString("user_id",null),item_food._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        Toast.makeText(getApplicationContext(), "PIO Buy Items Success", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Result response) {
                        if (response != null) {
                            Log.i("pio","response : "+response.getResult());
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
/*
    void connectSimilarCall() {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.similarResult(item_food._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<itemScores>>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        Toast.makeText(getApplicationContext(), "PIO Similar Items Success", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        Log.i("result","서버오류");
                        LoadingUtil.stopLoading(indicator);
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<itemScores> response) {
                        if (response != null) {
                            similar.setText(response.get(0).getItems()+" : "+response.get(0).getScore());
                        } else {
                            similar.setText("결과 값이 없습니다");
                        }
                    }
                });
    }
    */
}
