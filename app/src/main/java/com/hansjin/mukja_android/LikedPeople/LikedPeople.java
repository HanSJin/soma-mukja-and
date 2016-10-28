package com.hansjin.mukja_android.LikedPeople;

import android.app.Activity;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hansjin.mukja_android.LikedPeople.LikedPeopleAdapter;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_liked_people)
public class LikedPeople extends AppCompatActivity {
    LikedPeople activity;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    LikedPeopleAdapter adapter;

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

    private Food food;

    public static ActionBar actionBar;

    @AfterViews
    void afterBindingView() {
        this.activity = this;

        food = (Food) getIntent().getSerializableExtra("food");

        setSupportActionBar(cs_toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("<" + food.name + "> 좋아한 사람들");


        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new LikedPeopleAdapter(new LikedPeopleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            }, this, this, food);
        }
        recyclerView.setAdapter(adapter);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                connectTestCall(food._id);
            }
        });

        connectTestCall(food._id);

    }

    void refresh() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        pullToRefresh.setRefreshing(false);
    }

    @UiThread
    void uiThread(List<User> response) {
        List<User> response_friends = new ArrayList<>();
        List<User> response_noFriends = new ArrayList<>();

        for(User user : response){
            if(SharedManager.getInstance().getMe().friends.contains(user.social_id)){
                response_friends.add(0, user);
            }else{
                response_noFriends.add(0, user);
            }
        }

        for (User user : response_friends) {
            adapter.addData(user);
        }

        for (User user : response_noFriends) {
            adapter.addData(user);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.anim_exit_in, R.anim.anim_exit_out);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onBackPressed() {
        super.finish();
        overridePendingTransition(R.anim.anim_exit_in, R.anim.anim_exit_out);
    }

    @Override
    public void finish() {
        super.finish();
    }

    void connectTestCall(String food_id) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getLikedPerson(food_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<User>>() {
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
                    public final void onNext(List<User> response) {
                        if (response != null) {
                            uiThread(response);
                        } else {
                            Toast.makeText(getApplicationContext(), "좋아요한 사람이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

