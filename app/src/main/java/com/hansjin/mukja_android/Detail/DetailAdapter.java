package com.hansjin.mukja_android.Detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.LikedPeople.LikedPeople;
import com.hansjin.mukja_android.LikedPeople.LikedPeople_;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.NearbyRestaurant.NearByRestaurant;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Dialogs.CustomDialog;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import org.androidannotations.annotations.UiThread;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 10. 16..
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private static final int TYPE_IMAGE_HEADER = 0;
    private static final int TYPE_BODY_DESC = 1;
    private static final int TYPE_RANK_GRAPH = 2;
    private static final int TYPE_LIKE_PERSON = 3;
    private static final int TYPE_TAIL_SIMILAR = 4;

    public DetailActivity activity;
    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public Food food;
    public List<User> personList = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public DetailAdapter(OnItemClickListener onItemClickListener, Context mContext, DetailActivity mActivity, Food mFood) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        activity = mActivity;
        food = mFood;
    }

    @Override
    public DetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_image_header, parent, false);
            return new ImageHeaderViewHolder(v);
        } else if (viewType == TYPE_BODY_DESC) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_desc_body, parent, false);
            return new DescBodyViewHolder(v);
        } else if (viewType == TYPE_RANK_GRAPH) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_graph_body, parent, false);
            return new GraphBodyViewHolder(v);
        } else if (viewType == TYPE_LIKE_PERSON) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_person_body, parent, false);
            return new PersonBodyViewHolder(v);
        } else if (viewType == TYPE_TAIL_SIMILAR) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_simiral_tail, parent, false);
            return new SimiralTailViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof ImageHeaderViewHolder) {
            ImageHeaderViewHolder imageHolder = (ImageHeaderViewHolder) holder;
            Glide.with(context).
                    load(Constants.IMAGE_BASE_URL+food.image_url).
                    thumbnail(0.1f).
                    into(imageHolder.foodImage);
            imageHolder.foodName.setText(food.name);
            imageHolder.viewCnt.setText("View "+food.view_cnt);
            imageHolder.foodRateNum.setText(cal_rate(food)+"");
            imageHolder.heartImg.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_gray));
            for (String uid : food.like_person) {
                if (uid.equals(SharedManager.getInstance().getMe()._id)) {
                    imageHolder.heartImg.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_red));
                }
            }
            imageHolder.eatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    food_like(food);
                }
            });

            imageHolder.starImg.setImageDrawable(context.getResources().getDrawable(R.drawable.star_gray));
            for (String uid : food.rate_person_id()) {
                if (uid.equals(SharedManager.getInstance().getMe()._id)) {
                    imageHolder.starImg.setImageDrawable(context.getResources().getDrawable(R.drawable.star_yellow));
                }
            }
            imageHolder.rateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if 하트 누른 상태라면(db에서 상태가져오거나 확인해서
                    final CustomDialog customDialog = new CustomDialog(context,food.name);
                    customDialog.show();
                    customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            food.rate_person.add(0, food.newrate(SharedManager.getInstance().getMe()._id, customDialog.getRatenum()));
                            food_rate(food);
                        }
                    });
                }
            });
            imageHolder.layoutNearby.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, NearByRestaurant.class);
                    intent.putExtra("food_name",food.name);
                    activity.startActivity(intent);
                }
            });

        } else if (holder instanceof DescBodyViewHolder) {
            DescBodyViewHolder descBodyViewHolder = (DescBodyViewHolder) holder;
            descBodyViewHolder.txt_category.setText(combine_tag(food)+"");
            descBodyViewHolder.txt_ingredient.setText(combine_ingredient_tag(food)+"");
            if (food.like_cnt==0)
                descBodyViewHolder.txt_people_like.setText("가장 먼저 좋아요를 눌러주세요!");
            else
                descBodyViewHolder.txt_people_like.setText(food.like_cnt+"명의 사람들이 좋아해요");
            String friend = Constants.mockMyFriendText(position);
            if (friend.equals(""))
                descBodyViewHolder.txt_friend_like.setText("아직 이 음식을 좋아한 친구가 없어요.");
            else
                descBodyViewHolder.txt_friend_like.setText("회원님의 친구 "+friend+" 님이 좋아해요.");


            descBodyViewHolder.txt_people_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LikedPeople_.class);
                    intent.putExtra("food", food);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }
            });
        } else if (holder instanceof GraphBodyViewHolder) {
            GraphBodyViewHolder graphBodyViewHolder = (GraphBodyViewHolder) holder;

            ValueLineSeries series = new ValueLineSeries();
            series.setColor(0xFFF5C25A);
            series.addPoint(new ValueLinePoint(0));
            for (float rank : food.rate_distribution) {
                series.addPoint(new ValueLinePoint(rank));
            }
            series.addPoint(new ValueLinePoint(0));
            graphBodyViewHolder.lineChart.addSeries(series);

            int cnt = 1;
            int total_cnt = 0; // 평가자 수
            float total_point = 0; // 총 별점
            int max_index = 0; // 최대 평점 인덱스
            float max = 0;
            for (float rank : food.rate_distribution) {
                total_cnt += rank;
                total_point += (rank*((float)cnt/2.0));
                if (max < rank) {
                    max = rank;
                    max_index = cnt;
                }
                cnt++;
            }
            graphBodyViewHolder.rank_average.setText(String.format("%.2f",total_point/(float)total_cnt)+"");
            graphBodyViewHolder.rank_cnt.setText(total_cnt+"");
            graphBodyViewHolder.rank_max.setText((float)max_index/2+"");
        } else if (holder instanceof PersonBodyViewHolder) {
            PersonBodyViewHolder personBodyViewHolder = (PersonBodyViewHolder) holder;
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, dm);
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm);

            int cnt = 0;
            if (personList.size() > 0) {
                personBodyViewHolder.layout_person.removeAllViews();
            }
            for (User person : personList) {
                ImageView iv = new ImageView(context);
                iv.setImageResource(R.drawable.icon_cart);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setPadding(padding, padding, padding, padding);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
                iv.setLayoutParams(layoutParams);

                personBodyViewHolder.layout_person.addView(iv);
                Glide.with(context).
                        load(Constants.IMAGE_BASE_URL + person.thumbnail_url + ".png").
                        thumbnail(0.1f).
                        bitmapTransform(new CropCircleTransformation(context)).into(iv);

                if (cnt > 10)
                    break;
                cnt++;
            }
            if (personList.size() == 0) {
                personBodyViewHolder.no_person.setVisibility(View.VISIBLE);
            } else {
                personBodyViewHolder.no_person.setVisibility(View.GONE);
            }
        } else if (holder instanceof SimiralTailViewHolder) {
            SimiralTailViewHolder simiralTailViewHolder = (SimiralTailViewHolder) holder;
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_IMAGE_HEADER;
        else if (position == 1)
            return TYPE_BODY_DESC;
        else if (position == 2)
            return TYPE_RANK_GRAPH;
        else if (position == 3)
            return TYPE_LIKE_PERSON;
        else if (position == 4)
            return TYPE_TAIL_SIMILAR;
        return TYPE_IMAGE_HEADER;
    }

    @Override
    public int getItemCount() {
        return 4;
    }


    /*
        ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
        }
    }

    public class ImageHeaderViewHolder extends ViewHolder {
        public TextView foodName, viewCnt, foodRateNum;
        public ImageView foodImage, heartImg, starImg;
        public LinearLayout eatBtn, rateBtn, layoutNearby;
        public ImageHeaderViewHolder(View v) {
            super(v);
            foodImage = (ImageView) v.findViewById(R.id.food_image);
            heartImg = (ImageView) v.findViewById(R.id.heart_img);
            starImg = (ImageView) v.findViewById(R.id.star_img);
            foodName = (TextView) v.findViewById(R.id.food_name);
            foodRateNum = (TextView) v.findViewById(R.id.food_rate_num);
            viewCnt = (TextView) v.findViewById(R.id.view_cnt);
            eatBtn = (LinearLayout) v.findViewById(R.id.eat_btn);
            rateBtn = (LinearLayout) v.findViewById(R.id.rate_btn);
            layoutNearby = (LinearLayout) v.findViewById(R.id.layout_nearby);
        }
    }

    public class DescBodyViewHolder extends ViewHolder {
        public TextView txt_category, txt_ingredient, txt_people_like, txt_friend_like;
        public DescBodyViewHolder(View v) {
            super(v);
            txt_category = (TextView) v.findViewById(R.id.txt_category);
            txt_ingredient = (TextView) v.findViewById(R.id.txt_ingredient);
            txt_people_like = (TextView) v.findViewById(R.id.txt_people_like);
            txt_friend_like = (TextView) v.findViewById(R.id.txt_friend_like);
        }
    }

    public class GraphBodyViewHolder extends ViewHolder {
        public ValueLineChart lineChart;
        public TextView rank_average, rank_cnt, rank_max;
        public GraphBodyViewHolder(View v) {
            super(v);
            lineChart = (ValueLineChart) v.findViewById(R.id.linechart);
            lineChart.setShowIndicator(false);
            rank_average = (TextView) v.findViewById(R.id.rank_average);
            rank_cnt = (TextView) v.findViewById(R.id.rank_cnt);
            rank_max = (TextView) v.findViewById(R.id.rank_max);
        }
    }

    public class PersonBodyViewHolder extends ViewHolder {
        public LinearLayout layout_person;
        public TextView no_person;
        public PersonBodyViewHolder(View v) {
            super(v);
            layout_person = (LinearLayout) v.findViewById(R.id.layout_person);
            no_person = (TextView) v.findViewById(R.id.no_person);
        }
    }

    public class SimiralTailViewHolder extends ViewHolder {
        public TextView foodName, foodDesc;
        public SimiralTailViewHolder(View v) {
            super(v);
        }
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

    private String combine_ingredient_tag(Food food) {
        String result ="";
        for (String str:food.ingredient) {
            result+=(""+str+", ");
        }
        if (result.length()>2)
            result = result.substring(0, result.length()-2);
        return result;
    }

    public void food_like(Food mFood) {
        LoadingUtil.startLoading(activity.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.likeFood(SharedManager.getInstance().getMe()._id, mFood._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(activity.indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Food response) {
                        if (response != null) {
                            food = response;
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void food_rate(final Food mFood) {
        LoadingUtil.startLoading(activity.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.rateFood(mFood, SharedManager.getInstance().getMe()._id, mFood._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(activity.indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Food response) {
                        if (response != null) {
                            food = response;
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}