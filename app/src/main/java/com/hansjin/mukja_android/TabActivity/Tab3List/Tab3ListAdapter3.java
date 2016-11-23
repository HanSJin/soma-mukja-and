package com.hansjin.mukja_android.TabActivity.Tab3List;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.hansjin.mukja_android.Activity.GroupRecommandActivity;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.Profile.YourProfileActivity;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 9. 30..
 */
public class Tab3ListAdapter3 extends RecyclerView.Adapter<Tab3ListAdapter3.ViewHolder> {
    private static final int TYPE_ITEM = 0;

    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public static ArrayList<User> mDataset = new ArrayList<>();
    public Tab3ListFragment Tab3ListFragment;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public Tab3ListAdapter3(OnItemClickListener onItemClickListener, Context mContext, Tab3ListFragment mTab3ListFragment) {

        mOnItemClickListener = onItemClickListener;
        context = mContext;
        mDataset.clear();
        Tab3ListFragment = mTab3ListFragment;
    }

    public void addData(User item) {
        mDataset.add(item);
    }

    public User getItem(int position) {
        return mDataset.get(position);
    }

    public void clear() {
        mDataset.clear();
    }

    @Override
    public Tab3ListAdapter3.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_request_user, parent, false);
            return new RequestViewHolder3(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof RequestViewHolder3) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                    Intent intent = new Intent(context, YourProfileActivity.class);
                    intent.putExtra("user_id", mDataset.get(position)._id);
                    context.startActivity(intent);
                }
            });
            RequestViewHolder3 itemViewHolder = (RequestViewHolder3) holder;
            final User user = mDataset.get(position);

            itemViewHolder.TV_user_name.setText(user.nickname);
            //itemViewHolder.TV_category.setText(user.location + user.language + user.activity);
            itemViewHolder.TV_about_me.setText(user.about_me);
            itemViewHolder.BT_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user_reject(user, position);
                }
            });
            itemViewHolder.BT_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GroupRecommandActivity.class);
                    //수정
                    //intent.putExtra("group",)
                    context.startActivity(intent);
                }
            });

            Glide.with(context).load(user.getPic()).into(itemViewHolder.IV_user);

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
    public class RequestViewHolder3 extends ViewHolder {
        public TextView TV_user_name, TV_category, TV_about_me;
        ImageView IV_user;
        Button BT_no, BT_group;


        public RequestViewHolder3(View v) {
            super(v);
            TV_user_name = (TextView) v.findViewById(R.id.TV_user_name);
            TV_category = (TextView) v.findViewById(R.id.TV_category);
            TV_about_me = (TextView) v.findViewById(R.id.TV_about_me);
            IV_user = (ImageView) v.findViewById(R.id.IV_user);

            BT_no = (Button) v.findViewById(R.id.BT_no);
            BT_group = (Button) v.findViewById(R.id.group_button);
        }
    }


    public void user_reject(User you, final int index) {
        LoadingUtil.startLoading(Tab3ListFragment.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.rejectYou(you, SharedManager.getInstance().getMe()._id, you._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(Tab3ListFragment.indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            mDataset.remove(index);
                            //Tab3ListFragment.actionBar.setTitle("친구 요청 - " + mDataset.size() + "명"); // requestingFriendCnt = index?
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}