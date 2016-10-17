package com.hansjin.mukja_android.TabActivity.Tab4Explore;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hansjin.mukja_android.Detail.DetailActivity_;
import com.hansjin.mukja_android.Model.Explore;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TabActivity.ParentFragment.TabParentFragment;
import com.hansjin.mukja_android.TabActivity.Tab2Feeds.Tab2FeedsAdapter;
import com.hansjin.mukja_android.TabActivity.TabActivity;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;

import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab4ExploreFragment  extends TabParentFragment {
    TabActivity activity;

    public Tab4ExploreAdapter adapter;
    public Tab4ExploreAdapterSearch adapterSearch;

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewSearch;

    private RecyclerView.LayoutManager layoutManager;
    public LinearLayout indicator;
    public int page = 1;
    public boolean endOfPage = false;
    SwipeRefreshLayout pullToRefresh;
    Button BT_search;
    Boolean BT_search_bool = false;
    EditText ET_search;

    private LinearLayout LL_rank;
    private LinearLayout LL_search;

    /**
     * Create a new instance of the fragment
     */
    public static Tab4ExploreFragment newInstance(int index) {
        Tab4ExploreFragment fragment = new Tab4ExploreFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();

        Drawable drawable = getResources().getDrawable(R.drawable.search2);
        BT_search.setText("");
        BT_search.setBackground(drawable);
        BT_search_bool = false;

        refresh();


        //키워드별 랭킹 뷰 나오면서 기존에 있던 검색 결과 화면 invisible
        LL_rank.setVisibility(LinearLayout.VISIBLE);
        LL_search.setVisibility(LinearLayout.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        initViewSetting(view);
        return view;
    }

    private void initViewSetting(View view) {
        final TabActivity tabActivity = (TabActivity) getActivity();
        this.activity = tabActivity;

        LL_rank = (LinearLayout) view.findViewById(R.id.LL_rank);
        LL_search = (LinearLayout) view.findViewById(R.id.LL_search);

        LL_search.setVisibility(LinearLayout.GONE);

        ET_search = (EditText) view.findViewById(R.id.view_searchbar).findViewById(R.id.ET_searchbar);
        ET_search.setHint("음식 혹은 음식점을 검색해보세요 !");

        if (recyclerView == null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (recyclerViewSearch == null) {
            recyclerViewSearch = (RecyclerView) view.findViewById(R.id.recycler_view_search);
            recyclerViewSearch.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
            recyclerViewSearch.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new Tab4ExploreAdapter(new Tab4ExploreAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Drawable drawable = getResources().getDrawable(R.drawable.tumyeong);
                    BT_search.setText("취소");
                    BT_search.setBackground(drawable);
                    BT_search_bool = true;

                    connectTestCall_Search(adapter.mDataset.get(position).title);
                    //검색 결과 화면 나오면서 기존에 있던 키워드별 랭킹 뷰 invisible
                    LL_search.setVisibility(LinearLayout.VISIBLE);
                    LL_rank.setVisibility(LinearLayout.GONE);
                }
            }, activity, this);
        }
        recyclerView.setAdapter(adapter);


        if (adapterSearch == null) {
            adapterSearch = new Tab4ExploreAdapterSearch(new Tab4ExploreAdapterSearch.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(activity, DetailActivity_.class);
                    intent.putExtra("food", adapterSearch.mDataset.get(position));
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }
            }, activity, this);
        }
        recyclerViewSearch.setAdapter(adapterSearch);

        indicator = (LinearLayout)view.findViewById(R.id.indicator);
        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                refresh();
                setRankingMainList();
            }
        });

        BT_search = (Button)view.findViewById(R.id.BT_search);
        BT_search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!BT_search_bool) {
                    Drawable drawable = getResources().getDrawable(R.drawable.tumyeong);
                    BT_search.setText("취소");
                    BT_search.setBackground(drawable);
                    BT_search_bool = true;

                    connectTestCall_Search(ET_search.getText().toString());
                    //검색 결과 화면 나오면서 기존에 있던 키워드별 랭킹 뷰 invisible
                    LL_search.setVisibility(LinearLayout.VISIBLE);
                    LL_rank.setVisibility(LinearLayout.GONE);
                }else{
                    Drawable drawable = getResources().getDrawable(R.drawable.search2);
                    BT_search.setText("");
                    BT_search.setBackground(drawable);
                    BT_search_bool = false;

                    refresh();


                    //키워드별 랭킹 뷰 나오면서 기존에 있던 검색 결과 화면 invisible
                    LL_rank.setVisibility(LinearLayout.VISIBLE);
                    LL_search.setVisibility(LinearLayout.GONE);
                }
            }
        });
        setRankingMainList();
    }

    @Override
    public void refresh() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        adapterSearch.clear();
        adapterSearch.notifyDataSetChanged();

        setRankingMainList();
    }

    @Override
    public void reload() {

    }

    //@UiThread
    void uiThread(List<Explore> response) {
        for (Explore food : response) {
            adapter.addData(food);
        }
        adapter.notifyDataSetChanged();
    }

    private void setRankingMainList() {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getExploreRanking()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Explore>>() {
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
                    public final void onNext(List<Explore> response) {
                        if (response != null) {
                            uiThread(response);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void uiThread_Search(List<Food> response) {
        for (Food food : response) {
            adapterSearch.addData(food);
            Log.i("test",food.name);
        }
        adapterSearch.notifyDataSetChanged();
        Log.i("test","total : "+adapterSearch.mDataset.size());
        Log.i("keyword", ""+adapterSearch);
    }

    void connectTestCall_Search(String keyword) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getSearchResult(keyword)
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
                        Toast.makeText(getActivity(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<Food> response) {
                        if (response != null) {
                            refresh();
                            uiThread_Search(response);
                        } else {
                            Toast.makeText(getActivity(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
