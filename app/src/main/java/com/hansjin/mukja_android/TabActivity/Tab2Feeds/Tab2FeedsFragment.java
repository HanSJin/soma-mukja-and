package com.hansjin.mukja_android.TabActivity.Tab2Feeds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hansjin.mukja_android.Activity.RegisterActivity;
import com.hansjin.mukja_android.Activity.RegisterActivity_;
import com.hansjin.mukja_android.Detail.DetailActivity_;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Sign.SignActivity;
import com.hansjin.mukja_android.TabActivity.ParentFragment.TabParentFragment;
import com.hansjin.mukja_android.TabActivity.Tab1Recommand.Tab1RecommandAdapter;
import com.hansjin.mukja_android.TabActivity.TabActivity;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.hansjin.mukja_android.TabActivity.Tab5MyPage.Tab5MyPageFragment.activity;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab2FeedsFragment extends TabParentFragment {
    TabActivity activity;

    public Tab2FeedsAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public LinearLayout indicator;
    Button fab;
    public int page = 1;
    public boolean endOfPage = false;
    SwipeRefreshLayout pullToRefresh;

    /**
     * Create a new instance of the fragment
     */
    public static Tab2FeedsFragment newInstance(int index) {
        Tab2FeedsFragment fragment = new Tab2FeedsFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // just as usual
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        connectFeed(page);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        initViewSetting(view);

        return view;
    }

    private void initViewSetting(View view) {
        final TabActivity tabActivity = (TabActivity) getActivity();
        this.activity = tabActivity;

        if (recyclerView == null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new Tab2FeedsAdapter(new Tab2FeedsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getContext(), DetailActivity_.class);
                    intent.putExtra("food", adapter.getItem(position));
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

        fab = (Button) view.findViewById(R.id.add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 음식 업로드 페이지로 이동
                Intent intent = new Intent(activity, RegisterActivity_.class);
                //intent.putExtra("UserInfo", user); //user 정보가 들어있는 객체 전달
                startActivityForResult(intent, Constants.ACTIVITY_CODE_TAB2_REFRESH_REQUEST);
            }
        });

        connectFeed(1);
    }

    @Override
    public void refresh() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        connectFeed(page);
    }


    @Override
    public void reload() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        connectFeed(page);
    }


    void connectFeed(final int page_num) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getFeedList(SharedManager.getInstance().getMe()._id, page_num)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Food>>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        adapter.notifyDataSetChanged();
                        pullToRefresh.setRefreshing(false);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<Food> response) {
                        if (response != null && response.size()>0) {
                            for (Food food : response) {
                                adapter.addData(food);
                            }
                        } else {
                            endOfPage = true;
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("hansjin", "onActivityResult");
        if (requestCode == Constants.ACTIVITY_CODE_TAB2_REFRESH_REQUEST) {
            if (resultCode == Constants.ACTIVITY_CODE_TAB2_REFRESH_RESULT) {
                Log.d("hansjin", "refresh");
                page = 1;
                endOfPage = false;
                adapter.clear();
                adapter.notifyDataSetChanged();
                connectFeed(page);
            }
        }
    }
}