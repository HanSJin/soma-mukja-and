package com.hansjin.mukja_android.TabActivity.Tab2Feeds;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Utils.Dialogs.CustomDialog;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.Result;
import com.hansjin.mukja_android.Utils.PredictionIO.PredictionIOLearnEvent;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.ViewHolder.ViewHolderFood;
import com.hansjin.mukja_android.ViewHolder.ViewHolderParent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab2FeedsAdapter extends RecyclerView.Adapter<ViewHolderParent> {
    private static final int TYPE_ITEM = 0;

    private PredictionIOLearnEvent pio;
    public Context context;
    public Tab2FeedsFragment fragment;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<Food> mDataset = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public Tab2FeedsAdapter(OnItemClickListener onItemClickListener, Context mContext, Tab2FeedsFragment mFragment) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        fragment = mFragment;
        mDataset.clear();
        pio = new PredictionIOLearnEvent(context);
    }

    public void addData(Food item) {
        mDataset.add(item);
    }

    public Food getItem(int position) {
        return mDataset.get(position);
    }

    public void clear() {
        mDataset.clear();
    }

    @Override
    public ViewHolderParent onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_food, parent, false);
            return new ViewHolderFood(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolderParent holder, final int position) {
        if (holder instanceof ViewHolderFood) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            final ViewHolderFood itemViewHolder = (ViewHolderFood) holder;
            final Food food = mDataset.get(position);

            itemViewHolder.authorName.setText(food.author);
            itemViewHolder.foodName.setText(food.name);
            //TODO: 정보 띄워주기 서버와 연동 후 화면 테스트해보기
            Glide.with(context).load(food.image).into(itemViewHolder.food_img);
            itemViewHolder.rate_num.setText(cal_rate(food));
            itemViewHolder.category_tag.setText(combine_tag(food));
            itemViewHolder.people_like.setText(food.like_cnt+"명의 사람들이 좋아해요");
            //itemViewHolder.friend_like.setText(cal_friend(food));
            //헤더 부분
            /*
            Glide.with(context)
                    .load("https://graph.facebook.com/" + <facebook_id> + "/picture?type=normal")
                    .into(itemViewHolder.author_image);
            */
            itemViewHolder.write_time.setText(cal_time(food));

            itemViewHolder.eat_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: if 먹고싶어요 눌렀을 경우
                    pio.food_like(food._id,itemViewHolder.heart,fragment.getResources().getDrawable(R.drawable.heart_red));
                    //TODO: if 먹고싶어요 취소
                    //pio.food_like_cancle(<event_id>,itemViewHolder.heart,fragment.getResources().getDrawable(R.drawable.heart_gray));
                }
            });

            itemViewHolder.rate_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if 하트 누른 상태라면(db에서 상태가져오거나 확인해서
                    final CustomDialog customDialog = new CustomDialog(context,food.name);
                    customDialog.show();
                    customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //TODO: pio rate 점수 전송
                            if(pio.food_rate(food._id)==true)
                                itemViewHolder.star.setImageDrawable(fragment.getResources().getDrawable(R.drawable.star_yellow));
                        }
                    });
                }
            });

            itemViewHolder.report_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popup = new PopupMenu(context, itemViewHolder.report_btn);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.popup, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //TODO: 신고하기 테스트 하기
                            report_food(food._id);
                            return true;
                        }
                    });

                    popup.show();
                }
            });



            if (position == mDataset.size()-1 && !fragment.endOfPage)
                fragment.connectFeed(++fragment.page);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private String cal_time(Food food) {
        //food.update 형식 : 2011-10-05T14:48:00.000Z

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current_time = sdfNow.format(new Date(System.currentTimeMillis()));

        int su_arr[] = {0,4,5,7,8,10,11,13,14,16};
        String str_arr[] = {"년 전","개월 전","일 전","시간 전","분 전"};
        int update, now;

        for(int i=0;i<5;i++){
            if(i==5)
                return "방금 전";
            else {
                update = Integer.valueOf(food.update_date.substring(su_arr[i*2],su_arr[i*2+1]));
                now = Integer.valueOf(current_time.substring(su_arr[i*2],su_arr[i*2+1]));
                if (now-update > 0)
                    return now-update + str_arr[i];
            }
        }

        return null;
    }

    private String cal_friend(Food food) {
        //TODO: 페이스북 친구 연동 부분 보고 할 것
        String result="좋아하는 친구가 없어요";
        /*
        친구 목록 받아와서 food.likeuser와 비교한 후
        총 cnt알아내고 대표 친구 2명 알아 내서 string 완성할 것
         */
        return result;
    }

    private String combine_tag(Food food) {
        List<List<String>> category = new ArrayList<>();
        category.add(food.taste);
        category.add(food.country);
        category.add(food.cooking);

        String result ="";
        int cnt = 1;
        for(int i=0;i<3;i++){
            for (String str:category.get(i)) {
                if(cnt>7){
                    result+="…";
                    return result;
                }
                result+=("#"+str+" ");
                cnt++;
            }
        }
        return result;
    }

    private String cal_rate(Food food) {
        //TODO: 분포 0.5부터로 해놨는데 아니면 바꿀 것
        float i =0.5f;
        float total = 0;
        int cnt = 0;
        for (int dis:food.rate_distribution) {
            cnt += dis;
            total+=i*dis;
            i+=0.5f;
        }
        return String.valueOf(total/cnt);
    }

    public void report_food(String food_id) {
        SharedPreferences sp = context.getSharedPreferences("TodayFood", context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", null);

        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.reportFood(user_id,food_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    @Override
                    public final void onCompleted() {
                        Toast.makeText(context, "신고 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Result response) {
                        if (response != null) {
                            Log.i("report",response.toString());
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
