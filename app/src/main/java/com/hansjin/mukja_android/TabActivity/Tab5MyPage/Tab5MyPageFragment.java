package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TabActivity.ParentFragment.TabParentFragment;
import com.hansjin.mukja_android.TabActivity.Tab1Recommand.Tab1RecommandAdapter;
import com.hansjin.mukja_android.TabActivity.TabActivity;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab5MyPageFragment extends TabParentFragment {
    TabActivity activity;

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
    public static ImageView profile_image;

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

        Toolbar cs_toolbar = (Toolbar)view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("내 정보");

        if (recyclerView == null) {
//            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//            recyclerView.setHasFixedSize(true);
//            layoutManager = new LinearLayoutManager(activity);
//            recyclerView.setLayoutManager(layoutManager);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);

            //layoutManager = new LinearLayoutManager(activity);
            //recyclerView.setLayoutManager(layoutManager);
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));

        }

        if (adapter == null) {
            adapter = new Tab5MyPageAdapter(new Tab5MyPageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

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

        BT_setting = (Button)view.findViewById(R.id.BT_setting);
        BT_pref_anal = (Button) view.findViewById(R.id.BT_pref_anal);
        BT_food_rate = (Button) view.findViewById(R.id.BT_food_rate);
        profile_image = (ImageView) view.findViewById(R.id.profile_image);

        BT_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Setting.class));
            }
        });
        BT_pref_anal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
            }
        });
        BT_food_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FoodRate.class));
            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ThumbPopupActivity.class));
            }
        });


        connectTestCall();
    }

    @Override
    public void refresh() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        connectTestCall();
    }

    @Override
    public void reload() {

    }

    void connectTestCall() {

    }
}