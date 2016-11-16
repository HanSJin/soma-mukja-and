package com.hansjin.mukja_android.TabActivity.Tab1Recommand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hansjin.mukja_android.Detail.DetailActivity_;
import com.hansjin.mukja_android.Model.Category;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class AkinatorActivity extends AppCompatActivity {

    AkinatorActivity activity;

    public AkinatorAdapter adapter;

    private RecyclerView recyclerView;
    public LinearLayout indicator;
    public int page = 1;

    private RelativeLayout activity_akinator;
    private LinearLayout LL_search;

    private RelativeLayout RL_no;
    private RelativeLayout RL_replay;
    private RelativeLayout RL_yes;


    private RecyclerView.LayoutManager layoutManager;


    TextView TV_question;
    Button BT_yes;
    Button BT_replay;
    Button BT_no;

    List<String> question_cooking = new ArrayList<>();
    List<String> question_country = new ArrayList<>();
    List<String> question_taste = new ArrayList<>();


    Category category_db;

    List<String> category_cooking = new ArrayList<>();
    List<String> category_country = new ArrayList<>();
    List<String> category_taste = new ArrayList<>();


    Category category_query = new Category();


    int i =0;
    public final static int QUESTION_TYPE_COOKING = 0;
    public final static int QUESTION_TYPE_COUNTRY = 1;
    public final static int QUESTION_TYPE_TASTE = 2;
    public final static int QUESTION_TYPE_FINISH = 3;
    public static int NEXT_QUESTION_TYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akinator);
        activity = this;
        NEXT_QUESTION_TYPE = QUESTION_TYPE_COOKING;

        LL_search = (LinearLayout) findViewById(R.id.LL_search);
        activity_akinator = (RelativeLayout) findViewById(R.id.activity_akinator);

        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);
        }

        indicator = (LinearLayout) findViewById(R.id.indicator);

        LL_search.setVisibility(LinearLayout.GONE);
        activity_akinator.setVisibility(RelativeLayout.VISIBLE);

        //category_query 초기화
        category_query.taste = new ArrayList<String>();
        category_query.country = new ArrayList<String>();
        category_query.cooking = new ArrayList<String>();


        List<String> taste = new ArrayList<>();
        List<String> country= new ArrayList<>();
        List<String> cooking= new ArrayList<>();
        cooking.add("밥");
        cooking.add("면");
        cooking.add("국물");
        cooking.add("볶음");
        cooking.add("조림");
        cooking.add("찜");
        cooking.add("구이");
        cooking.add("튀김");
        cooking.add("즉석");
        cooking.add("생");

        country.add("한식");
        country.add("중식");
        country.add("일식");
        country.add("양식");
        country.add("기타");

        taste.add("달콤");
        taste.add("짭짤");
        taste.add("감칠");
        taste.add("매움");
        taste.add("시원");
        taste.add("뜨거움");
        taste.add("얼큰");
        taste.add("새콤");
        taste.add("신선");
        taste.add("부드러움");
        taste.add("향료");
        taste.add("차가움");

        category_db = new Category(taste, country, cooking);

        for(int i=0;i<3;i++)
           Log.i("zxc", "category_db.taste : " + category_db.taste.get(i));

        TV_question = (TextView) findViewById(R.id.TV_question);
        BT_yes = (Button) findViewById(R.id.BT_yes);
        BT_no = (Button) findViewById(R.id.BT_no);
        BT_replay = (Button) findViewById(R.id.BT_replay);
        RL_no = (RelativeLayout) findViewById(R.id.RL_no);
        RL_replay = (RelativeLayout) findViewById(R.id.RL_replay);
        RL_yes = (RelativeLayout) findViewById(R.id.RL_yes);

        RL_replay.setVisibility(View.INVISIBLE);

        question_cooking.add(0, "밥인가요?");
        question_cooking.add(1, "면인가요?");
        question_cooking.add(2, "국물이 있나요?");
        question_cooking.add(3, "볶음 요리인가요?");
        question_cooking.add(4, "조림 요리인가요?");
        question_cooking.add(5, "찜 요리인가요?");
        question_cooking.add(6, "구이인가요?");
        question_cooking.add(7, "튀긴 요리인가요?");
        question_cooking.add(8, "즉석 요리인가요?");
        question_cooking.add(9, "익히지 않은(생) 요리인가요?");

        question_country.add(0, "한식인가요?");
        question_country.add(1, "중식인가요?");
        question_country.add(2, "일식인가요?");
        question_country.add(3, "양식인가요?");
        question_country.add(4, "기타인가요?");

        question_taste.add(0, "달콤한가요?");
        question_taste.add(1, "짭짤한가요?");
        question_taste.add(2, "감칠맛이 나나요?");
        question_taste.add(3, "매운가요?");
        question_taste.add(4, "시원한가요?");
        question_taste.add(5, "뜨거운가요?");
        question_taste.add(6, "얼큰한가요?");
        question_taste.add(7, "새콤한가요?");
        question_taste.add(8, "신선한가요?");
        question_taste.add(9, "부드러운가요?");
        question_taste.add(10, "향료가 있나요?");
        question_taste.add(11, "차가운가요?");
        question_taste.add(12, "고소한가요?");
        question_taste.add(13, "담백한가요?");
        question_taste.add(14, "느끼한가요?");

        TV_question.setText(question_cooking.get(i));

        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TV_question.setText(question_c.get(i)); //아키네이터는, 각각의 질문에 답할 때마다 질문이 달라져야함(DB음식의 해당 카테고리에 속하는 음식들을 기준으로).  고로, yes,no 할때마다 쿼리 보내야함.

                switch (NEXT_QUESTION_TYPE){
                    case QUESTION_TYPE_COOKING :
                        category_query.cooking.add(category_db.cooking.get(i));
                        i=0;
                        TV_question.setText(question_country.get(i));
                        NEXT_QUESTION_TYPE = QUESTION_TYPE_COUNTRY;
                        break;
                    case QUESTION_TYPE_COUNTRY :
                        category_query.country.add(category_db.country.get(i));
                        i=0;
                        TV_question.setText(question_taste.get(i));
                        NEXT_QUESTION_TYPE = QUESTION_TYPE_TASTE;
                        break;
                    case QUESTION_TYPE_TASTE :
                        category_query.taste.add(category_db.taste.get(i));
                        i=0;
                        connectRecommand(category_query);
                        NEXT_QUESTION_TYPE = QUESTION_TYPE_FINISH;
                        break;
                    case QUESTION_TYPE_FINISH :
                        //
                        break;
                    default:
                        break;
                }


            }
        });

        BT_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (NEXT_QUESTION_TYPE){
                    case QUESTION_TYPE_COOKING :
                        TV_question.setText(question_cooking.get(++i));
                        break;
                    case QUESTION_TYPE_COUNTRY :
                        TV_question.setText(question_country.get(++i));
                        break;
                    case QUESTION_TYPE_TASTE :
                        TV_question.setText(question_taste.get(++i));
                        break;
                    case QUESTION_TYPE_FINISH :
                        //
                        break;
                    default:
                        break;
                }
            }
        });

        BT_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RL_no.setVisibility(View.VISIBLE);
                RL_yes.setVisibility(View.VISIBLE);
                RL_replay.setVisibility(View.INVISIBLE);
                NEXT_QUESTION_TYPE = QUESTION_TYPE_COOKING;
                TV_question.setText(question_cooking.get(0));
            }
        });

        if (adapter == null) {
            adapter = new AkinatorAdapter(new AkinatorAdapter.OnItemClickListener() {
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
    }

    void connectRecommand(Category field) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.recommendationResult(SharedManager.getInstance().getMe()._id, field)
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
                        Toast.makeText(getApplicationContext().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<Food> response) {
                        adapter.clear();
                        if (response != null && response.size()>0) {
                            //new
                            for (Food food : response) {
                                adapter.addData(food);
                            }
                            adapter.notifyDataSetChanged();
                            //검색 결과 화면 나오면서 기존에 있던 키워드별 랭킹 뷰 invisible
                            LL_search.setVisibility(LinearLayout.VISIBLE);
                            activity_akinator.setVisibility(RelativeLayout.GONE);
                        } else {
                            TV_question.setText("음식 못 찾음");
                            RL_no.setVisibility(View.INVISIBLE);
                            RL_yes.setVisibility(View.INVISIBLE);
                            RL_replay.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}
