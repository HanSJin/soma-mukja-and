package com.hansjin.mukja_android.Detail;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.GlobalResponse;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Template.BaseAdapter;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_detail)
public class DetailActivity extends AppCompatActivity {
    DetailActivity activity;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    DetailAdapter adapter;
    private Food food;

    @ViewById
    SwipeRefreshLayout pullToRefresh;

    @ViewById
    Toolbar cs_toolbar;

    @ViewById
    public LinearLayout indicator;

    @ViewById
    public EditText ET_comment;

    @Click
    void BT_comment() {
        connCommentFood(ET_comment.getText().toString());
        ET_comment.setText("");
    }

    @AfterViews
    void afterBindingView() {
        this.activity = this;

        food = (Food) getIntent().getSerializableExtra("food");

        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle(food.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new DetailAdapter(new DetailAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            }, this, this, food);
        }
        recyclerView.setAdapter(adapter);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadingUtil.startLoading(indicator);
                refresh();
            }
        });

        connFoodView();
        connLikedPerson();
    }

    void refresh() {
        adapter.notifyDataSetChanged();
        LoadingUtil.stopLoading(indicator);
        pullToRefresh.setRefreshing(false);
        connFoodView();
        connLikedPerson();
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

    @Background
    void connFoodView() {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.foodView(SharedManager.getInstance().getMe()._id, food._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GlobalResponse>() {
                    @Override
                    public final void onCompleted() {
                    }

                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public final void onNext(GlobalResponse response) {
                        if (response != null && response.code == 0) {
                            food.view_cnt++;
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Background
    void connLikedPerson() {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getLikedPerson(food._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<User>>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public final void onNext(List<User> response) {
                        if (response != null && response.size()>0) {
                            adapter.personList = response;
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    @Background
    void connCommentFood(String comment){
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);

        Map field = new HashMap();
        field.put("me_id", SharedManager.getInstance().getMe()._id);
        field.put("comment", comment);// commenter_name
        field.put("me_name", SharedManager.getInstance().getMe().nickname);
        field.put("me_pic_small", SharedManager.getInstance().getMe().thumbnail_url_small);
        conn.commentFood(food._id, field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GlobalResponse>() {
                    @Override
                    public final void onCompleted() {
                        setResult(Constants.ACTIVITY_CODE_TAB2_REFRESH_RESULT);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public final void onNext(GlobalResponse response) {
                        if (response.code == 0) {
                            Toast.makeText(activity, "댓글이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Background
    void connGetCommentFood(){
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getCommentFood(food._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Food.CommentPerson>>() {
                    @Override
                    public final void onCompleted() {

                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public final void onNext(List<Food.CommentPerson> response) {
                        if (response.size() != 0) {
                            Toast.makeText(activity, "댓글이 정상적으로 리프레쉬되었습니다.", Toast.LENGTH_SHORT).show();
                            food.comment_person = response;
                        } else {
                            Toast.makeText(activity, "댓글이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
