package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.DownloadImageTask;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import static com.hansjin.mukja_android.Utils.Constants.Constants.API_BASE_URL;

/**
 * Created by kksd0900 on 16. 9. 30..
 */
public class FoodRateAdapter extends RecyclerView.Adapter<FoodRateAdapter.ViewHolder> {
    private static final int TYPE_ITEM = 0;

    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<Food> mDataset = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public FoodRateAdapter(OnItemClickListener onItemClickListener, Context mContext) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
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
            String imgStr = food.image_url;

            itemViewHolder.TV_food_name.setText(food.name);
            itemViewHolder.TV_category.setText(tasteStr + countryStr + cookingStr);

            new DownloadImageTask(itemViewHolder.IV_food).execute(API_BASE_URL + "/images/food/" + imgStr);

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
        Button BT_star_1;
        Button BT_star_2;
        Button BT_star_3;
        Button BT_star_4;
        Button BT_star_5;
        ImageView IV_food;

        public ItemViewHolder(View v) {
            super(v);
            TV_food_name = (TextView) v.findViewById(R.id.cell_rate_food_name).findViewById(R.id.TV_food_name);
            TV_category = (TextView) v.findViewById(R.id.cell_rate_food_category).findViewById(R.id.TV_category);

            BT_star_1 = (Button) v.findViewById(R.id.BT_star_1);
            BT_star_2 = (Button) v.findViewById(R.id.BT_star_2);
            BT_star_3 = (Button) v.findViewById(R.id.BT_star_3);
            BT_star_4 = (Button) v.findViewById(R.id.BT_star_4);
            BT_star_5 = (Button) v.findViewById(R.id.BT_star_5);
            IV_food = (ImageView) v.findViewById(R.id.IV_food);
        }
    }
}