package com.hansjin.mukja_android.Activity;

import android.content.Intent;
import android.media.Image;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Detail.DetailActivity_;
import com.hansjin.mukja_android.Model.Category;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.Recommand;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TabActivity.Tab1Recommand.AkinatorActivity;
import com.hansjin.mukja_android.TabActivity.Tab1Recommand.Tab1RecommandAdapter;
import com.hansjin.mukja_android.TabActivity.TabActivity;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class GroupRecommandActivity extends AppCompatActivity {

    GroupRecommandActivity activity;

    public GroupRecommandAdapter adapter;

    private RecyclerView recyclerView;
    public LinearLayout indicator;
    public int page = 1;
    public boolean endOfPage = false;

    private Toolbar cs_toolbar;
    SwipeRefreshLayout pullToRefresh;

    private RecyclerView.LayoutManager layoutManager;
    Category field = new Category();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_recommand);
        activity = this;

        Intent intent = getIntent();
        field.group = intent.getStringArrayListExtra("group");
        Log.i("ddddd","받음 : "+field.group);

        initView();


        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new GroupRecommandAdapter(new GroupRecommandAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(activity, DetailActivity_.class);
                    intent.putExtra("food", adapter.mDataset.get(position));
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }
            }, activity, this);
        }
        recyclerView.setAdapter(adapter);
        LoadingUtil.stopLoading(indicator);
        pullToRefresh.setRefreshing(false);

        connectRecommand(field);
    }

    private void initView() {
        indicator = (LinearLayout) findViewById(R.id.indicator);
        cs_toolbar = (Toolbar) findViewById(R.id.cs_toolbar);
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh);

        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("그룹 추천 음식");
    }


    void connectRecommand(Category field) {
        Log.i("ddddd","connectRecommand1 field.group : "+field.group);
        field.group.add(SharedManager.getInstance().getMe()._id);
        Log.i("ddddd","connectRecommand2 내 id더함 field.group : "+field.group);
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.recommendationResult(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Recommand>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        adapter.notifyDataSetChanged();
                        pullToRefresh.setRefreshing(false);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        Log.i("ddddd","connectRecommand5");
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Recommand response) {

                        Log.i("ddddd","connectRecommand6");
                        adapter.mDataset.clear();
                        if (response != null && response.listFood.size()>0) {

                            Log.i("ddddd","connectRecommand7");
                            for (Food food : response.listFood) {
                                adapter.addData(food);
                            }
                        } else {
                            Log.i("ddddd","connectRecommand8");
                            endOfPage = true;
                        }
                    }
                });
    }
}
