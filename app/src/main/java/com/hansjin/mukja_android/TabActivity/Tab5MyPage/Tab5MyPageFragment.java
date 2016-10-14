package com.hansjin.mukja_android.TabActivity.Tab5MyPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.widget.ExploreByTouchHelper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TabActivity.ParentFragment.TabParentFragment;
import com.hansjin.mukja_android.TabActivity.Tab1Recommand.Tab1RecommandAdapter;
import com.hansjin.mukja_android.TabActivity.TabActivity;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.hansjin.mukja_android.Sign.SignActivity.context;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab5MyPageFragment extends TabParentFragment {
    TabActivity activity;

    public Tab5MyPageAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public LinearLayout indicator;
    public int page = 1;
    public boolean endOfPage = false;
    SwipeRefreshLayout pullToRefresh;
    Button BT_setting;
    Button BT_pref_anal;
    Button BT_food_rate;
    public static ImageView profile_image;

    TextView TV_name;
    public static TextView TV_about_me;

    SharedPreferences prefs;

    Bitmap bitmap;

    Button BT_edit_about_me;

    /**
     * Create a new instance of the fragment
     */
    public static Tab5MyPageFragment newInstance(int index) {
        Tab5MyPageFragment fragment = new Tab5MyPageFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        initViewSetting(view);
        return view;
    }

    private void initViewSetting(View view) {
        final TabActivity tabActivity = (TabActivity) getActivity();
        this.activity = tabActivity;

        prefs = getActivity().getSharedPreferences("TodayFood", Context.MODE_PRIVATE);

        BT_setting = (Button)view.findViewById(R.id.BT_setting);
        BT_pref_anal = (Button) view.findViewById(R.id.BT_pref_anal);
        BT_food_rate = (Button) view.findViewById(R.id.BT_food_rate);
        profile_image = (ImageView) view.findViewById(R.id.profile_image);

        BT_edit_about_me = (Button) view.findViewById(R.id.BT_edit_about_me);


        Toolbar cs_toolbar = (Toolbar)view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("내 정보");

        TV_name = (TextView) view.findViewById(R.id.TV_name);
        TV_about_me = (TextView) view.findViewById(R.id.TV_about_me);


        TV_name.setText(prefs.getString("user_name",""));
        TV_about_me.setText(prefs.getString("user_about_me",""));

        if (recyclerView == null) {
//            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//            recyclerView.setHasFixedSize(true);
//            layoutManager = new LinearLayoutManager(activity);
//            recyclerView.setLayoutManager(layoutManager);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);

            //layoutManager = new LinearLayoutManager(activity);
            //recyclerView.setLayoutManager(layoutManager);
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));

        }

        if (adapter == null) {
            adapter = new Tab5MyPageAdapter(new Tab5MyPageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            }, activity, this);
        }
        recyclerView.setAdapter(adapter);

        indicator = (LinearLayout)view.findViewById(R.id.indicator);
        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                refresh();
            }
        });



        //http://graph.facebook.com/fid값 입력/picture

        BT_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Setting.class));
            }
        });
        BT_pref_anal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
            }
        });
        BT_food_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FoodRate.class));
            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ThumbPopupActivity.class));
            }
        });
        BT_edit_about_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PopupEditAboutMe.class));
            }
        });

        //Picasso.with(getApplicationContext()).load("http://graph.facebook.com/" + prefs.getString("user_id","") + "/picture?width=78&height=78").into(profile_image);
        new DownloadImageTask(profile_image).execute("http://graph.facebook.com/" + prefs.getString("user_id","") + "/picture?width=78&height=78");
        //profile_image.setImageBitmap(getFacebookProfilePicture(prefs.getString("user_id","")));

        /*
        Thread mThread = new Thread(){
            @Override
            public void run() {
                try{
                    URL url = new URL("http://graph.facebook.com/" + prefs.getString("user_id","") + "/picture");
                    Log.i("asd", url.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        };

        mThread.start();

        try{
            mThread.join();
            profile_image.setImageBitmap(bitmap);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
*/
        connectTestCall();
    }

    @Override
    public void refresh() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        connectTestCall();
    }

    @Override
    public void reload() {

    }

    void connectTestCall() {

    }

//    private Bitmap loadImageFromNetwork(URL url){
//        try {
//            Log.i("asd", url.toString());
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoInput(true);
//            conn.connect();
//
//            InputStream is = conn.getInputStream();
//            bitmap = BitmapFactory.decodeStream(is);
//            Log.i("asd3", ""+bitmap);
//        } catch(IOException ex){
//            ex.printStackTrace();
//        }
//
//        return bitmap;
//    }
//
//    private class DownloadImageTask extends AsyncTask<URL, Void, Bitmap> {
//        /** The system calls this to perform work in a worker thread and
//         * delivers it the parameters given to AsyncTask.execute() */
//        protected Bitmap doInBackground(URL... urls) {
//
//            Log.i("asd2", ""+urls[0].toString());
//            return loadImageFromNetwork(urls[0]);
//        }
//
//        /** The system calls this to perform work in the UI thread and delivers
//         * the result from doInBackground() */
//        protected void onPostExecute(Bitmap result) {
//            profile_image.setImageBitmap(result);
//            Log.i("asd", ""+result);
//        }
//    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;


            try {
                URL url = new URL(urldisplay);
                InputStream in = url.openConnection().getInputStream();
                mIcon11 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Log.i("zxc", ""+ mIcon11);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }


            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public static Bitmap getFacebookProfilePicture(String userID){
        try {
            URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?width=78&height=78");
            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
