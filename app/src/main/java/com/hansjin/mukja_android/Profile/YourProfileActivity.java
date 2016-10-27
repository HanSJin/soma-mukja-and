package com.hansjin.mukja_android.Profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Detail.DetailActivity_;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TabActivity.ParentFragment.TabParentFragment;
import com.hansjin.mukja_android.TabActivity.Tab5MyPage.FoodRate_;
import com.hansjin.mukja_android.TabActivity.Tab5MyPage.PopupEditAboutMe;
import com.hansjin.mukja_android.TabActivity.Tab5MyPage.Setting;
import com.hansjin.mukja_android.TabActivity.Tab5MyPage.Tab5MyPageAdapter;
import com.hansjin.mukja_android.TabActivity.Tab5MyPage.ThumbPopupActivity;
import com.hansjin.mukja_android.TabActivity.TabActivity;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class YourProfileActivity extends AppCompatActivity {
    public static YourProfileActivity activity;

    public YourProfileAdapter adapter;
    private RecyclerView recyclerView;
    public LinearLayout indicator;
    public int page = 1;
    public boolean endOfPage = false;
    SwipeRefreshLayout pullToRefresh;
    Button BT_pref_anal;
    Button BT_food_rate;
    public static ImageView IV_profile;

    TextView TV_user_name;
    public static TextView TV_about_me;

    String image_url;
    String user_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);
        initViewSetting();
    }


    private void initViewSetting() {

        user_id = getIntent().getStringExtra("user_id");

        BT_pref_anal = (Button) findViewById(R.id.BT_pref_anal);
        BT_food_rate = (Button) findViewById(R.id.BT_food_rate);
        IV_profile = (ImageView) findViewById(R.id.IV_profile);

        connectTestCall(user_id);
        connectTestCall_UserInfo(user_id);

        Toolbar cs_toolbar = (Toolbar) findViewById(R.id.cs_toolbar);

        setSupportActionBar(cs_toolbar);


        TV_user_name = (TextView) findViewById(R.id.TV_user_name);
        TV_about_me = (TextView) findViewById(R.id.TV_about_me);



        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        }
        if (adapter == null) {
            adapter = new YourProfileAdapter(new YourProfileAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getApplicationContext(), DetailActivity_.class);
                    intent.putExtra("food", adapter.mDataset.get(position));
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }
            }, activity, this);
        }
        recyclerView.setAdapter(adapter);

        indicator = (LinearLayout) findViewById(R.id.indicator);
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                refresh();
            }
        });

        BT_pref_anal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        BT_food_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FoodRate_.class));
            }
        });

    }


    public void refresh() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        connectTestCall(user_id);
        connectTestCall_UserInfo(user_id);

    }


    public void reload() {
        refresh();
    }

    void connectTestCall(String user_id) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getLikedFood(user_id)
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
                            for (Food food : response) {
                                adapter.addData(food);
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void connectTestCall_UserInfo(String user_id) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getUserInfo(user_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        getSupportActionBar().setTitle("<" + SharedManager.getInstance().getYou().nickname + "> 프로필");
                        TV_user_name.setText(SharedManager.getInstance().getYou().nickname);
                        TV_about_me.setText(SharedManager.getInstance().getYou().about_me);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            Log.i("makejin","response.social_id : " + response.social_id);
                            SharedManager.getInstance().setYou(response);
                            image_url = SharedManager.getInstance().getYou().thumbnail_url;
                            if(image_url.contains("facebook")){
                                Glide.with(getApplicationContext()).
                                        load(image_url).
                                        thumbnail(0.1f).
                                        bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(IV_profile);
                            }else{
                                Glide.with(getApplicationContext()).
                                        load(Constants.IMAGE_BASE_URL + image_url).
                                        thumbnail(0.1f).
                                        bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(IV_profile);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
