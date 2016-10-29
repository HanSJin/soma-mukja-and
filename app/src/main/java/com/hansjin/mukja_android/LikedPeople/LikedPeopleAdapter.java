package com.hansjin.mukja_android.LikedPeople;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.Profile.YourProfileActivity;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.hansjin.mukja_android.Utils.Constants.Constants.API_BASE_URL;
import static com.hansjin.mukja_android.Utils.Constants.Constants.IMAGE_BASE_URL;

/**
 * Created by kksd0900 on 16. 9. 30..
 */
public class LikedPeopleAdapter extends RecyclerView.Adapter<LikedPeopleAdapter.ViewHolder> {
    private static final int TYPE_ITEM = 0;

    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<User> mDataset = new ArrayList<>();
    public LikedPeople likedPeople;

    public Food food;
    //List<String> friends = SharedManager.getInstance().getMe().friends;
    List<String> friends = SharedManager.getInstance().getMe().friends_id();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public LikedPeopleAdapter(OnItemClickListener onItemClickListener, Context mContext, LikedPeople mlikedPeople, Food mfood) {

        mOnItemClickListener = onItemClickListener;
        context = mContext;
        mDataset.clear();
        likedPeople = mlikedPeople;
        food = mfood;
    }

    public void addData(User user) {
        mDataset.add(user);
    }

    public User getItem(int position) {
        return mDataset.get(position);
    }

    public void clear() {
        mDataset.clear();
    }

    @Override
    public LikedPeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_liked_person, parent, false);
            return new LikedPeopleViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof LikedPeopleViewHolder) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            LikedPeopleViewHolder itemViewHolder = (LikedPeopleViewHolder) holder;
            final User user = mDataset.get(position);

            itemViewHolder.TV_user_name.setText(user.nickname);
            itemViewHolder.TV_date.setText("~" + "전");
            itemViewHolder.ratingBar1.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View view, MotionEvent event)
                {
                    // 터치 이벤트 제거
                    return true;
                };
            });


            List<String> rate_person_id = food.rate_person_id();

            if(rate_person_id.contains(user._id)) { //이미 rating 했었다면
                for(int i=0;i<food.rate_person.size();i++){
                    if(food.rate_person.get(i).getUser_id().equals(user._id)){
                        itemViewHolder.ratingBar1.setRating(food.rate_person.get(i).getRate_num());
                        break;
                    }
                }
            }else{
                //itemViewHolder.ratingBar1.setRating(0);
                itemViewHolder.ratingBar1.setVisibility(View.INVISIBLE);
            }

            if(user.thumbnail_url.contains("facebook")){
                Glide.with(context).
                        load(user.thumbnail_url).
                        thumbnail(0.1f).
                        bitmapTransform(new CropCircleTransformation(context)).into(itemViewHolder.IV_profile);
            }else{
                Glide.with(context).
                        load(Constants.IMAGE_BASE_URL + user.thumbnail_url).
                        thumbnail(0.1f).
                        bitmapTransform(new CropCircleTransformation(context)).into(itemViewHolder.IV_profile);
            }

            itemViewHolder.cell_liked_person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, YourProfileActivity.class);
                    intent.putExtra("user_id", user._id);
                    context.startActivity(intent);
                }
            });
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
    public class LikedPeopleViewHolder extends ViewHolder {
        public TextView TV_user_name, TV_date;
        public ImageView IV_profile;
        private RatingBar ratingBar1;
        public RelativeLayout cell_liked_person;

        public LikedPeopleViewHolder(View v) {
            super(v);
            TV_user_name = (TextView) v.findViewById(R.id.TV_user_name);
            TV_date = (TextView) v.findViewById(R.id.TV_date);
            IV_profile = (ImageView) v.findViewById(R.id.IV_profile);
            ratingBar1 = (RatingBar) v.findViewById(R.id.ratingBar);
            cell_liked_person = (RelativeLayout) v.findViewById(R.id.cell_liked_person);
        }
    }
}