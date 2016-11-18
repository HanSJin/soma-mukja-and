package com.hansjin.mukja_android.TasteAnalyst.ViewHolders;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hansjin.mukja_android.Model.Analyst;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TasteAnalyst.AnalystManager;
import com.hansjin.mukja_android.ViewHolder.ViewHolderParent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by kksd0900 on 2016. 11. 18..
 */

public class AnalystCountryViewHolder extends ViewHolderParent {
    LinearLayout layout_country_1, layout_country_2, layout_country_3, layout_country_4;
    TextView county_1, county_2, county_3, county_4;
    TextView county_percent_1, county_percent_2, county_percent_3, county_percent_4;

    List<LinearLayout> layoutList;
    List<TextView> countTxtList;
    List<TextView> percentList;

    public AnalystCountryViewHolder(View v) {
        super(v);

        layout_country_1 = (LinearLayout) v.findViewById(R.id.layout_country_1);
        layout_country_2 = (LinearLayout) v.findViewById(R.id.layout_country_2);
        layout_country_3 = (LinearLayout) v.findViewById(R.id.layout_country_3);
        layout_country_4 = (LinearLayout) v.findViewById(R.id.layout_country_4);
        county_1 = (TextView) v.findViewById(R.id.county_1);
        county_2 = (TextView) v.findViewById(R.id.county_2);
        county_3 = (TextView) v.findViewById(R.id.county_3);
        county_4 = (TextView) v.findViewById(R.id.county_4);
        county_percent_1 = (TextView) v.findViewById(R.id.county_percent_1);
        county_percent_2 = (TextView) v.findViewById(R.id.county_percent_2);
        county_percent_3 = (TextView) v.findViewById(R.id.county_percent_3);
        county_percent_4 = (TextView) v.findViewById(R.id.county_percent_4);

        layoutList = new ArrayList();
        countTxtList = new ArrayList();
        percentList = new ArrayList();

        layoutList.add(layout_country_1);
        layoutList.add(layout_country_2);
        layoutList.add(layout_country_3);
        layoutList.add(layout_country_4);
        countTxtList.add(county_1);
        countTxtList.add(county_2);
        countTxtList.add(county_3);
        countTxtList.add(county_4);
        percentList.add(county_percent_1);
        percentList.add(county_percent_2);
        percentList.add(county_percent_3);
        percentList.add(county_percent_4);
    }

    public void initViewHolder(final Analyst analyst, final Context context) {
        if (analyst == null)
            return;

        Map<String, Integer> containerDictionary = AnalystManager.makeContainerDictionary(analyst.countries);
        Map<String, Integer> sortByValue = AnalystManager.sortByValue(containerDictionary);

        Log.d("hansjin", sortByValue.toString());
        int index = 0;
        Iterator<String> keys = sortByValue.keySet().iterator();
        while(keys.hasNext() && index<4) {
            String country = keys.next();
            layoutList.get(index).setVisibility(View.VISIBLE);
            countTxtList.get(index).setText(country);
            if (country.equals("한식")) {
                countTxtList.get(index).setTextColor(Color.parseColor(AnalystManager.colorSet[1]));
            } else if (country.equals("중식")) {
                countTxtList.get(index).setTextColor(Color.parseColor(AnalystManager.colorSet[2]));
            } else if (country.equals("일식")) {
                countTxtList.get(index).setTextColor(Color.parseColor(AnalystManager.colorSet[3]));
            } else if (country.equals("양식")) {
                countTxtList.get(index).setTextColor(Color.parseColor(AnalystManager.colorSet[4]));
            }
            index++;
        }


    }
}
