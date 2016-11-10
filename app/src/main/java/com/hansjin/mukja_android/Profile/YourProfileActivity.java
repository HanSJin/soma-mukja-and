package com.hansjin.mukja_android.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.hansjin.mukja_android.TabActivity.Tab5MyPage.FoodRate_;
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
    String url;

    Button BT_acceptYou;

    public static int FRIEND_NO = 0;
    public static int FRIEND_YES = 1;
    public static int FRIEND_REQUESTED = 2; //나 -> 친구 요청 한 상태
    public static int FRIEND_WAITING = 3; //친구 -> 나 요청 건 상태



    public int FRIEND_STATE = FRIEND_NO;

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

        Toolbar cs_toolbar = (Toolbar) findViewById(R.id.cs_toolbar);

        setSupportActionBar(cs_toolbar);


        TV_user_name = (TextView) findViewById(R.id.TV_user_name);
        TV_about_me = (TextView) findViewById(R.id.TV_about_me);

        BT_acceptYou = (Button) findViewById(R.id.BT_acceptYou);



        for(int i=0;i<SharedManager.getInstance().getMe().friends_NonFacebook_Waiting.size();i++) {
            Log.i("zxc", SharedManager.getInstance().getMe().friends_NonFacebook_Waiting.get(i).user_id);
            Log.i("zxc", user_id);

            if (SharedManager.getInstance().getMe().friends_NonFacebook_Waiting.get(i).user_id.equals(user_id)) {
                BT_acceptYou.setText("친구 요청 수락");
                BT_acceptYou.setBackgroundResource(R.drawable.category_btn_selected_blue);
                FRIEND_STATE = FRIEND_WAITING;
            }
        }

        for(int i=0;i<SharedManager.getInstance().getMe().friends_NonFacebook_Requested.size();i++) {
            Log.i("zxc", SharedManager.getInstance().getMe().friends_NonFacebook_Requested.get(i).user_id);
            Log.i("zxc", user_id);
            if (SharedManager.getInstance().getMe().friends_NonFacebook_Requested.get(i).user_id.equals(user_id)) {
                BT_acceptYou.setText("친구 요청 중");
                BT_acceptYou.setBackgroundResource(R.drawable.category_btn_selected_green);
                FRIEND_STATE = FRIEND_REQUESTED;
                BT_acceptYou.setEnabled(false);
            }
        }

        for(int i=0;i<SharedManager.getInstance().getMe().friends_NonFacebook.size();i++) {
            Log.i("zxc", SharedManager.getInstance().getMe().friends_NonFacebook.get(i).user_id);
            Log.i("zxc", user_id);
            if (SharedManager.getInstance().getMe().friends_NonFacebook.get(i).user_id.equals(user_id)) {
                BT_acceptYou.setText("이미 친구");
                BT_acceptYou.setBackgroundResource(R.drawable.category_btn_selected_red);
                FRIEND_STATE = FRIEND_YES;
                BT_acceptYou.setEnabled(false);
            }
        }


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
                Intent intent = new Intent(getApplicationContext(), FoodRate_.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });
        BT_acceptYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_accept(SharedManager.getInstance().getYou());
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
                        if(SharedManager.getInstance().getYou().social_type.equals("facebook")) {
                            IV_profile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    url = "http://facebook.com/" + SharedManager.getInstance().getYou().social_id;

                                    /*
                                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                                        url = "http://" + url;
                                    */
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(browserIntent);
                                }
                            });
                        }
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


    public void user_accept(User you) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.acceptYou(you, SharedManager.getInstance().getMe()._id, you._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
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
                    public final void onNext(User response) {
                        if (response != null) {
                            if(FRIEND_STATE == FRIEND_WAITING) {
                                BT_acceptYou.setText("이미 친구");
                                BT_acceptYou.setBackgroundResource(R.drawable.category_btn_selected_red);
                                BT_acceptYou.setEnabled(false);
                            }else if(FRIEND_STATE == FRIEND_NO) {
                                BT_acceptYou.setText("친구 요청 중");
                                BT_acceptYou.setBackgroundResource(R.drawable.category_btn_selected_green);
                                BT_acceptYou.setEnabled(false);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
