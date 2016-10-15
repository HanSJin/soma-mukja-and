package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_food_rate)
public class FoodRate extends AppCompatActivity {
    com.hansjin.mukja_android.TabActivity.Tab5MyPage.FoodRate activity;



    int ratedFoodNumber = 0; // default 0

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    FoodRateAdapter adapter;

    public static double lat = 0.0;
    public static double lon = 0.0;

    @ViewById
    SwipeRefreshLayout pullToRefresh;

    @ViewById
    Toolbar cs_toolbar;

    @ViewById
    public LinearLayout indicator;

    @ViewById
    Button BT_X;

    //currrent location start
    private LocationManager locationManager = null; // 위치 정보 프로바이더
    private LocationListener locationListener = null; //위치 정보가 업데이트시 동작

    private static final String TAG = "debug";
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    //currrent location end

    @AfterViews
    void afterBindingView() {
        this.activity = this;

        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle("음식 평가 - " + ratedFoodNumber + "개 완료");

        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new FoodRateAdapter(new FoodRateAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            }, this);
        }
        recyclerView.setAdapter(adapter);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                connectTestCall();
            }
        });

        connectTestCall();

        BT_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void refresh() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        pullToRefresh.setRefreshing(false);
    }

    @UiThread
    void uiThread(List<Food> response) {
        for (Food food : response) {
            adapter.addData(food);
        }
        adapter.notifyDataSetChanged();
    }

    void connectTestCall() {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getAllFood()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Food>>() {
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
                    public final void onNext(List<Food> response) {
                        if (response != null) {
                            uiThread(response);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

