package com.hansjin.mukja_android.TabActivity.Tab2Feeds;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Detail.DetailActivity_;
import com.hansjin.mukja_android.LikedPeople.LikedPeople_;
import com.hansjin.mukja_android.Model.GlobalResponse;
import com.hansjin.mukja_android.Model.User;
import com.hansjin.mukja_android.Profile.YourProfileActivity;
import com.hansjin.mukja_android.TabActivity.Tab5MyPage.Tab5MyPageFragment;
import com.hansjin.mukja_android.Utils.Dialogs.CustomDialog;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.Utils.PredictionIO.PredictionIOLearnEvent;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;
import com.hansjin.mukja_android.ViewHolder.ViewHolderFood;
import com.hansjin.mukja_android.ViewHolder.ViewHolderParent;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.hansjin.mukja_android.TabActivity.Tab5MyPage.Tab5MyPageFragment.activity;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab2FeedsAdapter extends RecyclerView.Adapter<ViewHolderParent> {
    private static final int TYPE_ITEM = 0;

    private PredictionIOLearnEvent pio;
    public Context context;
    public Tab2FeedsFragment fragment;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<Food> mDataset = new ArrayList<>();

    String image_url;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public Tab2FeedsAdapter(OnItemClickListener onItemClickListener, Context mContext, Tab2FeedsFragment mFragment) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        fragment = mFragment;
        mDataset.clear();
        pio = new PredictionIOLearnEvent(context);
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
    public ViewHolderParent onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("zxc", "onCreateViewHolder_feed : " + viewType);
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_food, parent, false);
            return new ViewHolderFood(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolderParent holder, final int position) {
        if (holder instanceof ViewHolderFood) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            final ViewHolderFood itemViewHolder = (ViewHolderFood) holder;
            final Food food = mDataset.get(position);

            itemViewHolder.authorName.setText(food.author.author_nickname);
            itemViewHolder.authorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, YourProfileActivity.class);
                    intent.putExtra("user_id", food.author.author_id);
                    context.startActivity(intent);
                }
            });
            image_url = food.author.author_thumbnail_url;
            if(image_url.contains("facebook")){
                Glide.with(context).
                        load(image_url).
                        into(itemViewHolder.author_image);
            }else{
                Glide.with(context).
                        load(Constants.IMAGE_BASE_URL + image_url).
                        into(itemViewHolder.author_image);
            }

            itemViewHolder.author_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, YourProfileActivity.class);
                    intent.putExtra("user_id", food.author.author_id);
                    context.startActivity(intent);
                }
            });
            itemViewHolder.foodName.setText(food.name);

            Glide.with(context).
                    load(Constants.IMAGE_BASE_URL + food.image_url).
                    thumbnail(0.1f).
                    into(itemViewHolder.food_img);
            itemViewHolder.rate_num.setText(cal_rate(food));
            itemViewHolder.category_tag.setText(combine_tag(food));
            if (food.like_person.size()==0)
                itemViewHolder.people_like.setText("가장 먼저 좋아요를 눌러주세요!");
            else {
                User me = SharedManager.getInstance().getMe();
                List<String> food_like_list = food.like_person_id();
                List<String> friend_list = me.friends_id();
                List<String> friend_NonFacebook_list = me.friends_NonFacebook_id();

                String tempFriend = "";
                String tempMe = "";
                boolean isYou = false;
                boolean isMe = false;

                int like_person_size = food.like_person.size();

                for(int i=0;i<food_like_list.size();i++){
                    if(isMe && isYou) break;
                    if(food_like_list.get(i).equals(me._id)){
                        isMe = true;
                    }

                    if(friend_list!=null) {
                        if(!isYou) {
                            for (int j = 0; j < friend_list.size(); j++) {
                                if (food_like_list.get(i).equals(friend_list.get(j))) {
                                    tempFriend = me.friends.get(j).getUser_name();
                                    isYou = true;
                                    break;
                                }
                            }
                        }
                    }

                    if(friend_NonFacebook_list!=null) {
                        if (!isYou) {
                            for (int j = 0; j < friend_NonFacebook_list.size(); j++) {
                                Log.i("zxcxccxcxc", food_like_list.get(i));
                                Log.i("zxcxccxcxc", friend_NonFacebook_list.get(j));


                                if (food_like_list.get(i).equals(friend_NonFacebook_list.get(j))) {
                                    tempFriend = me.friends_NonFacebook.get(j).getUser_name();
                                    isYou = true;
                                    break;
                                }
                            }
                        }
                    }

                }

                String txt = "";
                if(!isYou){
                    if(isMe) //me
                        if(like_person_size==1)
                            txt = "회원님이 좋아해요";
                        else
                            txt = "회원님 외 " + (like_person_size-1) + "명의 사람들이 좋아해요";
                    else{ // x
                        if(like_person_size==1)
                            txt = "1명의 사람이 좋아해요";
                        else
                            txt = like_person_size + "명의 사람들이 좋아해요";
                    }
                }else{//좋아요한 내 친구가 1명 이상일 때
                    if(isMe) { //you me
                        if(like_person_size==2)
                            txt = "회원님, " + tempFriend + "님이 좋아해요";
                        else
                            txt = "회원님, " + tempFriend + "님 외 " + (like_person_size - 2) + "명의 사람들이 좋아해요";
                    }
                    else //you
                        if(like_person_size==1)
                            txt = tempFriend+"님이 좋아해요";
                        else
                            txt = tempFriend+ "님 외 " + (like_person_size-1) + "명의 사람들이 좋아해요";
                }

                itemViewHolder.people_like.setText(txt);

            }

            String distance= ", ";
            User.LocationPoint location_point = food.author.author_location_point;
            if(location_point.lat!=0 && location_point.lon!=0) {
                distance += String.valueOf(CalculationByDistance(SharedManager.getInstance().getMe().location_point, location_point));
                distance += "km";
            }
            else
                distance = "";

            itemViewHolder.user_info.setText(cal_time(food) + distance);

            itemViewHolder.heart.setImageDrawable(fragment.getResources().getDrawable(R.drawable.heart_gray));
            itemViewHolder.star.setImageDrawable(fragment.getResources().getDrawable(R.drawable.star_gray));
            setImamge(food.like_person_id(),itemViewHolder.heart,R.drawable.heart_red);
            setImamge(food.rate_person_id(),itemViewHolder.star,R.drawable.star_yellow);
            itemViewHolder.eat_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    food_like(food, position);
                    setImamge(food.like_person_id(),itemViewHolder.heart,R.drawable.heart_red);
                }
            });

            itemViewHolder.rate_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if 하트 누른 상태라면(db에서 상태가져오거나 확인해서
                    final CustomDialog customDialog = new CustomDialog(context,food.name);
                    customDialog.show();
                    customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            food.rate_person.add(0,food.newrate(SharedManager.getInstance().getMe()._id,customDialog.getRatenum()));
                            food_rate(food, position);
                            setImamge(food.rate_person_id(),itemViewHolder.star,R.drawable.star_yellow);
                        }
                    });
                }
            });

            itemViewHolder.report_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popup = new PopupMenu(context, itemViewHolder.report_btn);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.popup, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //TODO: 신고하기 테스트 하기
                            report_food(food._id);
                            return true;
                        }
                    });

                    popup.show();
                }
            });

            if (position == mDataset.size()-1 && !fragment.endOfPage)
                fragment.connectFeed(++fragment.page);

            itemViewHolder.food_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity_.class);
                    intent.putExtra("food", food);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }
            });

            itemViewHolder.people_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(food.like_cnt>0)
                        return;

                    Intent intent = new Intent(context, LikedPeople_.class);
                    intent.putExtra("food", food);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

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

    public double CalculationByDistance(User.LocationPoint StartP, User.LocationPoint EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.lat;
        double lat2 = EndP.lat;
        double lon1 = StartP.lon;
        double lon2 = EndP.lon;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        double distance = (Radius * c);
        return Math.round(distance*100d) / 100d;
    }

    private String cal_time(Food food) {
        //food.update 형식 : 2011-10-05T14:48:00.000Z
        Log.i("aa","time : "+food.create_date);

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current_time = sdfNow.format(new Date(System.currentTimeMillis()));

        int su_arr[] = {0,4,5,7,8,10,11,13,14,16};
        String str_arr[] = {"년 전","개월 전","일 전","시간 전","분 전"};
        int update, now;

        for(int i=0;i<6;i++){
            if(i==5)
                return "방금 전";
            else {
                update = Integer.valueOf(food.update_date.substring(su_arr[i*2],su_arr[i*2+1]));
                now = Integer.valueOf(current_time.substring(su_arr[i*2],su_arr[i*2+1]));
                if (now-update > 0)
                    return now-update + str_arr[i];
            }
        }

        return null;
    }

    private String cal_friend(Food food) {
        //TODO: 페이스북 친구 연동 부분 보고 할 것
        String result="좋아하는 친구가 없어요";
        /*
        친구 목록 받아와서 food.likeuser와 비교한 후
        총 cnt알아내고 대표 친구 2명 알아 내서 string 완성할 것
         */
        return result;
    }

    private String combine_tag(Food food) {
        List<List<String>> category = new ArrayList<>();
        category.add(food.taste);
        category.add(food.country);
        category.add(food.cooking);

        String result ="";
        int cnt = 1;
        for(int i=0;i<3;i++){
            for (String str:category.get(i)) {
                if(cnt>7){
                    result+="…";
                    return result;
                }
                result+=("#"+str+" ");
                cnt++;
            }
        }
        return result;
    }

    private String cal_rate(Food food) {
        //TODO: 분포 0.5부터로 해놨는데 아니면 바꿀 것
        float i =0.5f;
        float total = 0;
        int cnt = 0;
        for (int dis:food.rate_distribution) {
            cnt += dis;
            total+=i*dis;
            i+=0.5f;
        }

        String str = String.format("%.2f", total/cnt);
        return str;
    }

    public void report_food(String food_id) {
        LoadingUtil.startLoading(fragment.indicator);
        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.reportFood(SharedManager.getInstance().getMe()._id, food_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GlobalResponse>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(fragment.indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(GlobalResponse response) {
                        if (response != null && response.code == 0 && response.message.equals("success")) {
                            Toast.makeText(context, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void food_like(Food food, final int index) {
        LoadingUtil.startLoading(fragment.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.likeFood(SharedManager.getInstance().getMe()._id, food._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(fragment.indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Food response) {
                        if (response != null) {
                            mDataset.get(index).like_cnt = response.like_cnt;
                            mDataset.get(index).like_person = response.like_person;
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void food_rate(Food food, final int index) {
        LoadingUtil.startLoading(fragment.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.rateFood(food, SharedManager.getInstance().getMe()._id, food._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Food>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(fragment.indicator);
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

    private void setImamge(List<String> array, ImageView imageview, int image) {
        for (String uid : array) {
            if (uid.equals(SharedManager.getInstance().getMe()._id)) {
                imageview.setImageDrawable(fragment.getResources().getDrawable(image));
            }
        }
    }
}