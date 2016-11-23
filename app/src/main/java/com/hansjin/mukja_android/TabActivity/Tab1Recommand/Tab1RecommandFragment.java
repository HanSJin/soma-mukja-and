package com.hansjin.mukja_android.TabActivity.Tab1Recommand;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hansjin.mukja_android.Activity.RegisterActivity_;
import com.hansjin.mukja_android.Detail.DetailActivity_;
import com.hansjin.mukja_android.Model.Category;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.Recommand;
import com.hansjin.mukja_android.Model.itemScores;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TabActivity.ParentFragment.TabParentFragment;
import com.hansjin.mukja_android.TabActivity.TabActivity;
import com.hansjin.mukja_android.Template.BaseAdapter;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.PredictionIO.PredictionIOLearnEvent;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab1RecommandFragment extends TabParentFragment {
    public static TabActivity activity;

    public Tab1RecommandAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public LinearLayout indicator;
    public int page = 1;
    public boolean endOfPage = false;
    SwipeRefreshLayout pullToRefresh;
    Button BT_akinator;
    View view;

    /**
     * Create a new instance of the fragment
     */
    public static Tab1RecommandFragment newInstance(int index) {
        Tab1RecommandFragment fragment = new Tab1RecommandFragment();
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
        connectCategory(view);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommand, container, false);
        connectCategory(view);
        return view;
    }

    private void initViewSetting(View view) {
        final TabActivity tabActivity = (TabActivity) getActivity();
        this.activity = tabActivity;

        Toolbar cs_toolbar = (Toolbar)view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("LOGO");

        BT_akinator = (Button) view.findViewById(R.id.BT_akinator);
        BT_akinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AkinatorActivity.class));
            }
        });


        if (recyclerView == null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new Tab1RecommandAdapter(new Tab1RecommandAdapter.OnItemClickListener() {
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
    }


    @Override
    public void refresh() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        connectRecommand(getField());
    }

    @Override
    public void reload() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        connectRecommand(getField());
    }

    private Category getField() {
        Category field = new Category();
        field.taste = new ArrayList<String>();
        field.country = new ArrayList<String>();
        field.cooking = new ArrayList<String>();
        return field;
    }

    void connectCategory(final View view) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getCategoryList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Category>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        initViewSetting(view);
                        pullToRefresh.setRefreshing(false);
                    }

                    @Override
                    public final void onError(Throwable e) {
                        Log.i("ddd","connetCategory onerror");
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public final void onNext(Category response) {
                        if (response != null) {
                            SharedManager.getInstance().setCategory(response);
                            connectRecommand(getField());
                        }else{
                            Log.i("ddd","connetCategory onNext Error");
                        }
                    }
                });
    }

    void connectRecommand(Category field) {
        field.group.add(SharedManager.getInstance().getMe()._id);
        Log.i("ddd","connetRecommand3");
        LoadingUtil.startLoading(indicator);
        Log.i("ddd","connetRecommand4");
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        Log.i("ddd","connetRecommand5");
        conn.recommendationResult(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Recommand>() {
                    @Override
                    public final void onCompleted() {
                        Log.i("ddd","connetRecommand Success");
                        LoadingUtil.stopLoading(indicator);
                        adapter.notifyDataSetChanged();
                        pullToRefresh.setRefreshing(false);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        Log.i("ddd","connetRecommand onError");
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Recommand response) {
                        Log.i("ddd","connetRecommand6");
                        adapter.mDataset.clear();
                        if (response != null && response.listFood.size()>0) {

                            Log.i("ddd","connetRecommand7");
                            for (Food food : response.listFood) {
                                adapter.addData(food);
                            }
                        } else {
                            Log.i("ddd","connetRecommand onNext Error");
                            endOfPage = true;
                        }
                    }
                });
    }
}
