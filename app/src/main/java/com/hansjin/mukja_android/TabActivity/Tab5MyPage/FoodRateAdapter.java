package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.hansjin.mukja_android.Utils.Constants.Constants.API_BASE_URL;

/**
 * Created by kksd0900 on 16. 9. 30..
 */
public class FoodRateAdapter extends RecyclerView.Adapter<FoodRateAdapter.ViewHolder> {
    private static final int TYPE_ITEM = 0;

    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<Food> mDataset = new ArrayList<>();
    public FoodRate foodRate;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public FoodRateAdapter(OnItemClickListener onItemClickListener, Context mContext, FoodRate mFoodRate) {

        mOnItemClickListener = onItemClickListener;
        context = mContext;
        mDataset.clear();
        foodRate = mFoodRate;
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
    public FoodRateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_rate_food, parent, false);
            return new ItemViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final Food food = mDataset.get(position);

            String tasteStr = "";
            for (String taste : food.taste) {
                tasteStr += ("#" + taste + ", ");
            }
            String countryStr = "";
            for (String country : food.country) {
                countryStr  += ("#" + country + ", ");
            }
            String cookingStr = "";
            for (String cooking : food.cooking) {
                cookingStr += ("#" + cooking + ", ");
            }
            String imgStr = food.image_url;

            itemViewHolder.TV_food_name.setText(food.name);
            itemViewHolder.TV_category.setText(tasteStr + countryStr + cookingStr);
            itemViewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                public void onRatingChanged(RatingBar ratingBar, float rating,
                                            boolean fromUser) {

                    //txtRatingValue.setText(String.valueOf(rating));
                    food.rate_person.add(0,food.newrate(SharedManager.getInstance().getMe()._id,rating));
                    Log.i("zxczx",""+ rating);
                    food_rate(food, position);
                }
            });

            String image_url = API_BASE_URL + "/images/food/" + imgStr;
            //new DownloadImageTask(itemViewHolder.IV_food).execute(API_BASE_URL + "/images/food/" + imgStr);
            Glide.with(context).load(image_url).into(itemViewHolder.IV_food);

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
    public class ItemViewHolder extends ViewHolder {
        public TextView TV_food_name, TV_category;

        ImageView IV_food;
        RatingBar ratingBar;

        public ItemViewHolder(View v) {
            super(v);
            TV_food_name = (TextView) v.findViewById(R.id.TV_food_name);
            TV_category = (TextView) v.findViewById(R.id.TV_category);

            IV_food = (ImageView) v.findViewById(R.id.IV_food);
            ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        }
    }

    public void food_rate(Food food, final int index) {
        LoadingUtil.startLoading(foodRate.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.rateFood(food, SharedManager.getInstance().getMe()._id, food._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(foodRate.indicator);
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