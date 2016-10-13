package com.hansjin.mukja_android.TabActivity.Tab4Explore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TabActivity.ParentFragment.TabParentFragment;
import com.hansjin.mukja_android.TabActivity.Tab2Feeds.Tab2FeedsAdapter;
import com.hansjin.mukja_android.TabActivity.TabActivity;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab4ExploreFragment  extends TabParentFragment {
    TabActivity activity;

    public Tab4ExploreAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewSearch;

    private RecyclerView.LayoutManager layoutManager;
    public LinearLayout indicator;
    public int page = 1;
    public boolean endOfPage = false;
    SwipeRefreshLayout pullToRefresh;
    Button BT_search;
    Boolean BT_search_bool = false;

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

        LinearLayout searchView = (LinearLayout) view.findViewById(R.id.view_searchbar);
        LL_rank = (LinearLayout) view.findViewById(R.id.LL_rank);
        LL_search = (LinearLayout) view.findViewById(R.id.LL_search);

        LL_search.setVisibility(LinearLayout.GONE);


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

        BT_search = (Button)view.findViewById(R.id.BT_search);
        BT_search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!BT_search_bool) {
                    BT_search.setText("취소");
                    BT_search_bool = true;

                    //검색 결과 화면 나오면서 기존에 있던 키워드별 랭킹 뷰 invisible
                    LL_search.setVisibility(LinearLayout.VISIBLE);
                    LL_rank.setVisibility(LinearLayout.GONE);
                }else{
                    BT_search.setText("검색");
                    BT_search_bool = false;

                    //키워드별 랭킹 뷰 나오면서 기존에 있던 검색 결과 화면 invisible
                    LL_rank.setVisibility(LinearLayout.VISIBLE);
                    LL_search.setVisibility(LinearLayout.GONE);
                }
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
