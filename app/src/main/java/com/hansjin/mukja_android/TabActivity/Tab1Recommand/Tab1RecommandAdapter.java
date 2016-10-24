package com.hansjin.mukja_android.TabActivity.Tab1Recommand;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Model.Category;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Dialogs.CustomDialog;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.PredictionIO.PredictionIOLearnEvent;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;
import com.hansjin.mukja_android.ViewHolder.ViewHolderFood;
import com.hansjin.mukja_android.ViewHolder.ViewHolderFoodCategory;
import com.hansjin.mukja_android.ViewHolder.ViewHolderParent;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab1RecommandAdapter extends RecyclerView.Adapter<ViewHolderParent> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private PredictionIOLearnEvent pio;
    public Context context;
    public Tab1RecommandFragment fragment;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<Food> mDataset = new ArrayList<>();
    List<String> push_tag = new ArrayList<>();

    //카테고리 띄워주기 위한 리스트들
    private String taste_list[] = new String[]{};
    private String country_list[] = new String[]{};
    private String cooking_list[] = new String[]{};
    //getRecommand에 실어보낼 Map
    private Category category_food = new Category();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public Tab1RecommandAdapter(OnItemClickListener onItemClickListener, Context mContext, Tab1RecommandFragment mFragment) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        fragment = mFragment;
        mDataset.clear();
        pio = new PredictionIOLearnEvent(context);

        taste_list = SharedManager.getInstance().getCategory().taste.toArray(
                new String[SharedManager.getInstance().getCategory().taste.size()]);
        country_list = SharedManager.getInstance().getCategory().country.toArray(
                new String[SharedManager.getInstance().getCategory().country.size()]);
        cooking_list = SharedManager.getInstance().getCategory().cooking.toArray(
                new String[SharedManager.getInstance().getCategory().cooking.size()]);

        category_food.taste = new ArrayList<String>();
        category_food.country = new ArrayList<String>();
        category_food.cooking = new ArrayList<String>();
    }

    public void addData(Food item) {
        mDataset.add(item);
    }

    public Food getItem(int position) {
        return mDataset.get(position);
    }

    public void clear() {
        mDataset.clear();
        push_tag.clear();
    }

    @Override
    public ViewHolderParent onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_food, parent, false);
            return new ViewHolderFood(v);
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_food_category, parent, false);
            return new ViewHolderFoodCategory(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolderParent holder, final int position) {
        if (holder instanceof ViewHolderFood) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position-1);
                }
            });
            final ViewHolderFood itemViewHolder = (ViewHolderFood) holder;
            final Food food = mDataset.get(position-1);

            itemViewHolder.cellFoodHeader.setVisibility(View.GONE);
            itemViewHolder.foodName.setText(food.name);
            //TODO: 정보 띄워주기 서버와 연동 후 화면 테스트해보기
            Glide.with(context).
                    load(Constants.IMAGE_BASE_URL+food.image_url).
                    thumbnail(0.1f).
                    into(itemViewHolder.food_img);
            Log.i("zxczxc", Constants.IMAGE_BASE_URL+food.image_url);
            itemViewHolder.rate_num.setText(cal_rate(food));
            itemViewHolder.category_tag.setText(combine_tag(food));
            if (food.like_cnt==0)
                itemViewHolder.people_like.setText("가장 먼저 좋아요를 눌러주세요!");
            else
                itemViewHolder.people_like.setText(food.like_cnt+"명의 사람들이 좋아해요");
            String friend = Constants.mockMyFriendText(position);
            if (friend.equals(""))
                itemViewHolder.friend_like.setText("아직 이 음식을 좋아한 친구가 없어요.");
            else
                itemViewHolder.friend_like.setText("회원님의 친구 "+friend+" 님이 좋아해요.");

            itemViewHolder.heart.setImageDrawable(fragment.getResources().getDrawable(R.drawable.heart_gray));
            itemViewHolder.star.setImageDrawable(fragment.getResources().getDrawable(R.drawable.star_gray));
            setImamge(food.like_person, itemViewHolder.heart, R.drawable.heart_red);
            setImamge(food.rate_person_id(), itemViewHolder.star, R.drawable.star_yellow);
            itemViewHolder.eat_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    food_like(food, position-1);
                    setImamge(food.like_person,itemViewHolder.heart,R.drawable.heart_red);
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
                            food.rate_person.add(0,food.newrate(SharedManager.getInstance().getMe()._id,customDialog.getRatenum()));
                            food_rate(food, position-1);
                            setImamge(food.rate_person_id(),itemViewHolder.star,R.drawable.star_yellow);
                        }
                    });
                }
            });


        } else if (holder instanceof ViewHolderFoodCategory) {

            final ViewHolderFoodCategory itemViewHolder = (ViewHolderFoodCategory) holder;
            Map<String[], TagFlowLayout> tagFlowLayouts = new HashMap<String[], TagFlowLayout>();
            tagFlowLayouts.put(taste_list,itemViewHolder.taste);
            tagFlowLayouts.put(country_list,itemViewHolder.country);
            tagFlowLayouts.put(cooking_list,itemViewHolder.cooking);
            for(final Map.Entry<String[], TagFlowLayout> entry : tagFlowLayouts.entrySet()) {
                entry.getValue().setAdapter(new TagAdapter<String>(entry.getKey()) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.category_btn, parent, false);
                        tv.setText(s);
                        if(push_tag.contains(s)){
                            Drawable r = fragment.getResources().getDrawable(R.drawable.category_btn_selected);
                            tv.setBackground(r);
                            tv.setTextColor(0xFFFFFFFF);
                        }
                        return tv;
                    }
                });
                entry.getValue().setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
                {
                    @Override
                    public boolean onTagClick(View view, int position, FlowLayout parent)
                    {
                        String s = entry.getKey()[position];
                        if(push_tag.contains(s)) {
                            if(entry.getKey()==taste_list)
                                category_food.taste.remove(s);
                            else if(entry.getKey()==country_list)
                                category_food.country.remove(s);
                            else if(entry.getKey()==cooking_list)
                                category_food.cooking.remove(s);

                            fragment.connectRecommand(category_food);

                            push_tag.remove(s);

                        }else {
                            if(entry.getKey()==taste_list)
                                category_food.taste.add(s);
                            else if(entry.getKey()==country_list)
                                category_food.country.add(s);
                            else if(entry.getKey()==cooking_list)
                                category_food.cooking.add(s);

                            fragment.connectRecommand(category_food);
                            push_tag.add(s);
                        }
                        return true;
                    }
                });
            }
        }
    }

    private void setImamge(List<String> array, ImageView imageview, int image) {
        for (String uid : array) {
            if (uid.equals(SharedManager.getInstance().getMe()._id)) {
                imageview.setImageDrawable(fragment.getResources().getDrawable(image));
            }
        }
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

        String result="";
        int cnt = 1;
        if (category != null) {
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
        }
        return result;
    }

    private String cal_rate(Food food) {
        //TODO: 분포 0.5부터로 해놨는데 아니면 바꿀 것
        float i =0.5f;
        float total = 0;
        int cnt = 0;
        if (food.rate_person != null) {
            for (int dis:food.rate_distribution) {
                cnt += dis;
                total+=i*dis;
                i+=0.5f;
            }
        }

        String str = String.format("%.2f", total/cnt);
        return str;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mDataset.size() + 1; //+1 is for the footer as it's an extra item
    }


    public void food_like(Food food, final int index) {
        LoadingUtil.startLoading(fragment.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.likeFood(SharedManager.getInstance().getMe()._id, food._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(fragment.indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Food response) {
                        if (response != null) {
                            mDataset.get(index).like_cnt = response.like_cnt;
                            mDataset.get(index).like_person = response.like_person;
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void food_rate(Food food, final int index) {
        LoadingUtil.startLoading(fragment.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.rateFood(food, SharedManager.getInstance().getMe()._id, food._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(fragment.indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Food response) {
                        if (response != null) {
                            mDataset.get(index).rate_cnt = response.rate_cnt;
                            mDataset.get(index).rate_person = response.rate_person;
                            mDataset.get(index).rate_distribution = response.rate_distribution;
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
