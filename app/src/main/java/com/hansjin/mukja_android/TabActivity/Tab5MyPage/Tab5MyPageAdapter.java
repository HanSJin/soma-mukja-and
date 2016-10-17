package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TabActivity.Tab1Recommand.Tab1RecommandFragment;

import java.util.ArrayList;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab5MyPageAdapter extends RecyclerView.Adapter<Tab5MyPageAdapter.ViewHolder> {
    private static final int TYPE_ITEM = 0;

    public Context context;
    public Tab5MyPageFragment fragment;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<Food> mDataset = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public Tab5MyPageAdapter(OnItemClickListener onItemClickListener, Context mContext, Tab5MyPageFragment mFragment) {
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
    public Tab5MyPageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_liked_food, parent, false);
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

            itemViewHolder.TV_food_name.setText(food.name);
            //itemViewHolder.IV_food.setBackground();
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
        public TextView TV_food_name;
        public ImageView IV_food;

        public ItemViewHolder(View v) {
            super(v);
            TV_food_name = (TextView) v.findViewById(R.id.TV_food_name);
            IV_food = (ImageView) v.findViewById(R.id.IV_food);

        }
    }

}
