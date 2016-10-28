package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.hansjin.mukja_android.TabActivity.Tab2Feeds.Tab2FeedsAdapter;
import com.hansjin.mukja_android.TabActivity.TabActivity;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.PopupNotCompleted;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import org.androidannotations.annotations.UiThread;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab5MyPageFragment extends TabParentFragment {
    public static TabActivity activity;

    public Tab5MyPageAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public LinearLayout indicator;
    public int page = 1;
    public boolean endOfPage = false;
    SwipeRefreshLayout pullToRefresh;
    Button BT_setting;
    Button BT_pref_anal;
    Button BT_food_rate;
    public static ImageView IV_profile;

    TextView TV_user_name;
    public static TextView TV_about_me;

    Bitmap bitmap;

    Button BT_edit_about_me;

    String image_url;

    /**
     * Create a new instance of the fragment
     */
    public static Tab5MyPageFragment newInstance(int index) {
        Tab5MyPageFragment fragment = new Tab5MyPageFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        initViewSetting(view);
        return view;
    }

    private void initViewSetting(View view) {
        final TabActivity tabActivity = (TabActivity) getActivity();
        this.activity = tabActivity;



        BT_edit_about_me = (Button) view.findViewById(R.id.BT_edit_about_me);
        BT_setting = (Button)view.findViewById(R.id.BT_setting);
        BT_pref_anal = (Button) view.findViewById(R.id.BT_pref_anal);
        BT_food_rate = (Button) view.findViewById(R.id.BT_food_rate);
        IV_profile = (ImageView) view.findViewById(R.id.IV_profile);

        connectTestCall();
        connectTestCall_UserInfo();

        Toolbar cs_toolbar = (Toolbar)view.findViewById(R.id.cs_toolbar);

        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("내 정보");

        TV_user_name = (TextView) view.findViewById(R.id.TV_user_name);
        TV_about_me = (TextView) view.findViewById(R.id.TV_about_me);



        if (recyclerView == null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        }
        if (adapter == null) {
            adapter = new Tab5MyPageAdapter(new Tab5MyPageAdapter.OnItemClickListener() {
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

        indicator = (LinearLayout)view.findViewById(R.id.indicator);
        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                refresh();
            }
        });

        BT_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Setting.class));
            }
        });
        BT_pref_anal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PopupNotCompleted.class));
            }
        });
        BT_food_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FoodRate_.class));
            }
        });
        IV_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ThumbPopupActivity.class));
            }
        });
        BT_edit_about_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PopupEditAboutMe.class));
            }
        });



    }

    @Override
    public void refresh() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        connectTestCall();
        connectTestCall_UserInfo();

    }

    @Override
    public void reload() {
        refresh();
    }

    void connectTestCall() {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getLikedFood(SharedManager.getInstance().getMe()._id)
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
                        Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<Food> response) {
                        if (response != null) {
                            for (Food food : response) {
                                adapter.addData(food);
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void connectTestCall_UserInfo() {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getUserInfo(SharedManager.getInstance().getMe()._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        TV_user_name.setText(SharedManager.getInstance().getMe().nickname);
                        TV_about_me.setText(SharedManager.getInstance().getMe().about_me);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            SharedManager.getInstance().setMe(response);
                            image_url = SharedManager.getInstance().getMe().thumbnail_url;
                            if(image_url.contains("facebook")){
                                Glide.with(getActivity()).
                                        load(image_url).
                                        thumbnail(0.1f).
                                        bitmapTransform(new CropCircleTransformation(getActivity())).into(IV_profile);
                            }else{
                                Glide.with(getActivity()).
                                        load(Constants.IMAGE_BASE_URL + image_url).
                                        thumbnail(0.1f).
                                        bitmapTransform(new CropCircleTransformation(getActivity())).into(IV_profile);
                            }
                        } else {
                            Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
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
