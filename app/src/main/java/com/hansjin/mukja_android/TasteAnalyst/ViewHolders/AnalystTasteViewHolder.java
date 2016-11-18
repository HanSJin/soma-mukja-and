package com.hansjin.mukja_android.TasteAnalyst.ViewHolders;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hansjin.mukja_android.Model.Analyst;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TasteAnalyst.AnalystManager;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.SharedManager.SharedManager;
import com.hansjin.mukja_android.ViewHolder.ViewHolderParent;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.communication.IOnItemFocusChangedListener;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by kksd0900 on 2016. 11. 18..
 */

public class AnalystTasteViewHolder extends ViewHolderParent {
    PieChart mPieChart;
    ImageView user_thumb;
    LinearLayout layout_taste_1, layout_taste_2, layout_taste_3, layout_taste_4, layout_taste_5;
    TextView taste_1, taste_2, taste_3, taste_4, taste_5;
    List<LinearLayout> layoutList;
    List<TextView> tasteList;

    public AnalystTasteViewHolder(View v) {
        super(v);

        mPieChart = (PieChart) v.findViewById(R.id.piechart);
        user_thumb = (ImageView) v.findViewById(R.id.user_thumb);
        layout_taste_1 = (LinearLayout) v.findViewById(R.id.layout_taste_1);
        layout_taste_2 = (LinearLayout) v.findViewById(R.id.layout_taste_2);
        layout_taste_3 = (LinearLayout) v.findViewById(R.id.layout_taste_3);
        layout_taste_4 = (LinearLayout) v.findViewById(R.id.layout_taste_4);
        layout_taste_5 = (LinearLayout) v.findViewById(R.id.layout_taste_5);
        taste_1 = (TextView) v.findViewById(R.id.taste_1);
        taste_2 = (TextView) v.findViewById(R.id.taste_2);
        taste_3 = (TextView) v.findViewById(R.id.taste_3);
        taste_4 = (TextView) v.findViewById(R.id.taste_4);
        taste_5 = (TextView) v.findViewById(R.id.taste_5);
        layoutList = new ArrayList();
        tasteList = new ArrayList();
        layoutList.add(layout_taste_1);
        layoutList.add(layout_taste_2);
        layoutList.add(layout_taste_3);
        layoutList.add(layout_taste_4);
        layoutList.add(layout_taste_5);
        tasteList.add(taste_1);
        tasteList.add(taste_2);
        tasteList.add(taste_3);
        tasteList.add(taste_4);
        tasteList.add(taste_5);
    }

    public void initViewHolder(final Analyst analyst, final Context context) {
        if (analyst == null)
            return;

        String image_url = SharedManager.getInstance().getMe().thumbnail_url;
        if (image_url.contains("facebook")) {
            Glide.with(context).load(image_url).thumbnail(0.1f).
                    bitmapTransform(new CropCircleTransformation(context)).into(user_thumb);
        } else {
            Glide.with(context).load(Constants.IMAGE_BASE_URL + image_url).thumbnail(0.1f).
                    bitmapTransform(new CropCircleTransformation(context)).into(user_thumb);
        }

        Map<String, Integer> containerDictionary = AnalystManager.makeContainerDictionary(analyst.tastes);
        Map<String, Integer> sortByValue = AnalystManager.sortByValue(containerDictionary);

        int index = 0;
        String[] colorSet = {"#52CCDD", "#FA6E78", "#64C464", "#F5C35F", "#966ED2"};
        Iterator<String> keys = sortByValue.keySet().iterator();
        while(keys.hasNext() && index<5) {
            String taste = keys.next();
            int tasteValue = sortByValue.get(taste);
            mPieChart.addPieSlice(new PieModel(taste, tasteValue+1, Color.parseColor(colorSet[index])));
            layoutList.get(index).setVisibility(View.VISIBLE);
            tasteList.get(index).setText(taste);
            index++;
        }
        mPieChart.setAnimationTime(1000);
        mPieChart.startAnimation();
    }
}