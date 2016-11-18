package com.hansjin.mukja_android.TasteAnalyst;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hansjin.mukja_android.Model.Analyst;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TasteAnalyst.ViewHolders.AnalystCountryViewHolder;
import com.hansjin.mukja_android.TasteAnalyst.ViewHolders.AnalystIngredientViewHolder;
import com.hansjin.mukja_android.TasteAnalyst.ViewHolders.AnalystKeywordViewHolder;
import com.hansjin.mukja_android.TasteAnalyst.ViewHolders.AnalystTasteViewHolder;
import com.hansjin.mukja_android.ViewHolder.ViewHolderParent;

/**
 * Created by kksd0900 on 2016. 11. 18..
 */

public class TasteAnalystAdapter extends RecyclerView.Adapter<ViewHolderParent> {
    private static final int TYPE_ITEM_KEYWORD = 0;
    private static final int TYPE_ITEM_TASTE = 1;
    private static final int TYPE_ITEM_COUNTRY = 2;
    private static final int TYPE_ITEM_INGREDIENT = 3;

    public Context context;
    public TasteAnalystActivity activity;
    public LinearLayout indicator;
    private OnItemClickListener mOnItemClickListener;

    Analyst analyst;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public TasteAnalystAdapter(OnItemClickListener onItemClickListener, Context mContext, TasteAnalystActivity mActivity, LinearLayout mIndicator) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        activity = mActivity;
        indicator = mIndicator;
    }

    @Override
    public ViewHolderParent onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM_KEYWORD) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_taste_analyst_item_keyword, parent, false);
            return new AnalystKeywordViewHolder(v);
        } else if (viewType == TYPE_ITEM_TASTE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_taste_analyst_item_taste, parent, false);
            return new AnalystTasteViewHolder(v);
        } else if (viewType == TYPE_ITEM_COUNTRY) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_taste_analyst_item_country, parent, false);
            return new AnalystCountryViewHolder(v);
        } else if (viewType == TYPE_ITEM_INGREDIENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_taste_analyst_item_ingredient, parent, false);
            return new AnalystIngredientViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolderParent holder, final int position) {
        if (holder instanceof AnalystKeywordViewHolder) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            AnalystKeywordViewHolder viewHolderParent = (AnalystKeywordViewHolder)holder;
            viewHolderParent.initViewHolder(analyst, context);
        }
        else if (holder instanceof AnalystTasteViewHolder) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            AnalystTasteViewHolder viewHolderParent = (AnalystTasteViewHolder)holder;
            viewHolderParent.initViewHolder(analyst, context);
        }
        else if (holder instanceof AnalystCountryViewHolder) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            AnalystCountryViewHolder viewHolderParent = (AnalystCountryViewHolder)holder;
        }
        else if (holder instanceof AnalystIngredientViewHolder) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            AnalystIngredientViewHolder viewHolderParent = (AnalystIngredientViewHolder)holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_ITEM_KEYWORD;
        else if (position == 1)
            return TYPE_ITEM_TASTE;
        else if (position == 2)
            return TYPE_ITEM_COUNTRY;
        else
            return TYPE_ITEM_INGREDIENT;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
