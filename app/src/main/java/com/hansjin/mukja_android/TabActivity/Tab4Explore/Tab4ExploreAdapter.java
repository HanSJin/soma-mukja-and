package com.hansjin.mukja_android.TabActivity.Tab4Explore;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Model.Explore;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TabActivity.Tab1Recommand.Tab1RecommandFragment;
import com.hansjin.mukja_android.Utils.Constants.Constants;

import java.util.ArrayList;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab4ExploreAdapter extends RecyclerView.Adapter<Tab4ExploreAdapter.ViewHolder> {
    private static final int TYPE_ITEM = 0;

    public Context context;
    public Tab4ExploreFragment fragment;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<Explore> mDataset = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public Tab4ExploreAdapter(OnItemClickListener onItemClickListener, Context mContext, Tab4ExploreFragment mFragment) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        fragment = mFragment;
        mDataset.clear();
    }

    public void addData(Explore item) {
        mDataset.add(item);
    }

    public Explore getItem(int position) {
        return mDataset.get(position);
    }

    public void clear() {
        mDataset.clear();
    }

    @Override
    public Tab4ExploreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_ranking_keyword, parent, false);
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
            Explore keyword = mDataset.get(position);

            int tempPosition = position+1;
            itemViewHolder.TV_ranking_keyword_name.setText("선호키워드 " + tempPosition + " - '" +keyword.show_title+"'");
            Glide.with(context).load(Constants.IMAGE_BASE_URL+keyword.image).into(itemViewHolder.IV_food);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
        }
    }
    public class ItemViewHolder extends ViewHolder {
        public TextView TV_ranking_keyword_name;
        ImageView IV_food;

        public ItemViewHolder(View v) {
            super(v);
            TV_ranking_keyword_name = (TextView) v.findViewById(R.id.TV_ranking_keyword_name);
            IV_food = (ImageView) v.findViewById(R.id.IV_food);
        }
    }

}
