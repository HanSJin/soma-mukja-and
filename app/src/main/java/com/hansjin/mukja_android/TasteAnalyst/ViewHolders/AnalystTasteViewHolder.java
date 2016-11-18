package com.hansjin.mukja_android.TasteAnalyst.ViewHolders;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Model.Analyst;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;
import com.hansjin.mukja_android.ViewHolder.ViewHolderParent;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.communication.IOnItemFocusChangedListener;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by kksd0900 on 2016. 11. 18..
 */

public class AnalystTasteViewHolder extends ViewHolderParent {
    PieChart mPieChart;
    ImageView user_thumb;

    public AnalystTasteViewHolder(View v) {
        super(v);

        mPieChart = (PieChart) v.findViewById(R.id.piechart);
        user_thumb = (ImageView) v.findViewById(R.id.user_thumb);
    }

    public void initViewHolder(final Analyst analyst, final Context context) {
        if (analyst == null)
            return;

        String image_url = SharedManager.getInstance().getMe().thumbnail_url;
        if (image_url.contains("facebook")) {
            Glide.with(context).
                    load(image_url).
                    thumbnail(0.1f).
                    bitmapTransform(new CropCircleTransformation(context)).into(user_thumb);
        } else {
            Glide.with(context).
                    load(Constants.IMAGE_BASE_URL + image_url).
                    thumbnail(0.1f).
                    bitmapTransform(new CropCircleTransformation(context)).into(user_thumb);
        }
        
        // 42BCCD 블루
        // FA6E78 핑크
        // 54B454 그린
        // F5C35F 엘로
        // 966ED2 보라

        mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
        mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
        mPieChart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#FED70E")));

        mPieChart.setOnItemFocusChangedListener(new IOnItemFocusChangedListener() {
            @Override
            public void onItemFocusChanged(int _Position) {
//                Log.d("PieChart", "Position: " + _Position);
            }
        });
        mPieChart.startAnimation();



    }
}