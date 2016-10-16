package com.hansjin.mukja_android.Detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;

import java.util.ArrayList;

/**
 * Created by kksd0900 on 16. 10. 16..
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private static final int TYPE_IMAGE_HEADER = 0;
    private static final int TYPE_BODY_DESC = 1;
    private static final int TYPE_RANK_GRAPH = 2;
    private static final int TYPE_LIKE_FRIEND = 3;
    private static final int TYPE_TAIL_SIMILAR = 4;


    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public Food food;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public DetailAdapter(OnItemClickListener onItemClickListener, Context mContext, Food mFood) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        food = mFood;
    }

    @Override
    public DetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_image_header, parent, false);
            return new ImageHeaderViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof ImageHeaderViewHolder) {
            ImageHeaderViewHolder imageHolder = (ImageHeaderViewHolder) holder;
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
            return TYPE_LIKE_FRIEND;
        else if (position == 4)
            return TYPE_TAIL_SIMILAR;
        return TYPE_IMAGE_HEADER;
    }

    @Override
    public int getItemCount() {
        return 1;
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
        public TextView foodName, foodDesc;
        public ImageHeaderViewHolder(View v) {
            super(v);
        }
    }
}