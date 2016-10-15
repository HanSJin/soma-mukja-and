package com.hansjin.mukja_android.TabActivity.Tab4Explore;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.DownloadImageTask;

import java.util.ArrayList;

import static com.hansjin.mukja_android.Utils.Constants.Constants.API_BASE_URL;


/**
 * Created by kksd0900 on 16. 9. 30..
 */
public class Tab4ExploreAdapterSearch extends RecyclerView.Adapter<Tab4ExploreAdapterSearch.ViewHolder> {
    private static final int TYPE_ITEM = 0;

    public Context context;
    public Tab4ExploreFragment fragment;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<Food> mDataset = new ArrayList<>();


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public Tab4ExploreAdapterSearch(OnItemClickListener onItemClickListener, Context mContext, Tab4ExploreFragment mFragment) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        fragment = mFragment;
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
    public Tab4ExploreAdapterSearch.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_search, parent, false);
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


            new DownloadImageTask(itemViewHolder.IV_food).execute(API_BASE_URL + "/images/food/" + food.image_url);

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