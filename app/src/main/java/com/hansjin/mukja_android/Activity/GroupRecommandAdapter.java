package com.hansjin.mukja_android.Activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.ViewHolder.ViewHolderFood;
import com.hansjin.mukja_android.ViewHolder.ViewHolderFoodCategory;

import java.util.ArrayList;


/**
 * Created by kksd0900 on 16. 9. 30..
 */
public class GroupRecommandAdapter extends RecyclerView.Adapter<GroupRecommandAdapter.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    public Context context;
    public GroupRecommandActivity activity;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<Food> mDataset = new ArrayList<>();


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public GroupRecommandAdapter(OnItemClickListener onItemClickListener, Context mContext, GroupRecommandActivity mActivity) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        activity = mActivity;
        mDataset.clear();
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
    public GroupRecommandAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
            return new ItemViewHolder(v);
        }
        return null;

        /*
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
            return new ItemViewHolder(v);
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_food_category, parent, false);
            return new ViewHolderFoodCategory(v);
        }
        return null;
        */
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

            Food food = mDataset.get(position);
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

            //itemViewHolder.TV_food_name.setText(food.name);
            itemViewHolder.food_name.setText(food.name);
            itemViewHolder.food_desc.setText(tasteStr + countryStr + cookingStr);

            Glide.with(context).
                    load(Constants.IMAGE_BASE_URL+food.image_url).
                    thumbnail(0.1f).
                    into(itemViewHolder.IV_food);
            //new DownloadImageTask(itemViewHolder.IV_food).execute(API_BASE_URL + "/images/food/" + food.image_url);
            //Glide.with(context).load(image_url).into(itemViewHolder.IV_food);
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
        public TextView food_name, food_desc;
        ImageView IV_food;


        public ItemViewHolder(View v) {
            super(v);
            food_name = (TextView) v.findViewById(R.id.food_name);
            food_desc = (TextView) v.findViewById(R.id.food_desc);


            IV_food = (ImageView) v.findViewById(R.id.IV_food);


        }
    }
}