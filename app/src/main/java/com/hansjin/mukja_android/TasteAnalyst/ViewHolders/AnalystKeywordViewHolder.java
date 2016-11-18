package com.hansjin.mukja_android.TasteAnalyst.ViewHolders;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hansjin.mukja_android.Model.Analyst;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.TasteAnalyst.AnalystManager;
import com.hansjin.mukja_android.ViewHolder.ViewHolderParent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kksd0900 on 2016. 11. 18..
 */

public class AnalystKeywordViewHolder extends ViewHolderParent {
    RelativeLayout layout_keyword;
    TextView keyword_1, keyword_2, keyword_3, keyword_4, keyword_5, keyword_6, keyword_7, keyword_8, keyword_9;
    List<TextView> keywordTexts;

    public AnalystKeywordViewHolder(View v) {
        super(v);

        layout_keyword = (RelativeLayout) v.findViewById(R.id.layout_keyword);
        keyword_1 = (TextView) v.findViewById(R.id.keyword_1);
        keyword_2 = (TextView) v.findViewById(R.id.keyword_2);
        keyword_3 = (TextView) v.findViewById(R.id.keyword_3);
        keyword_4 = (TextView) v.findViewById(R.id.keyword_4);
        keyword_5 = (TextView) v.findViewById(R.id.keyword_5);
        keyword_6 = (TextView) v.findViewById(R.id.keyword_6);
        keyword_7 = (TextView) v.findViewById(R.id.keyword_7);
        keyword_8 = (TextView) v.findViewById(R.id.keyword_8);
        keyword_9 = (TextView) v.findViewById(R.id.keyword_9);
        keywordTexts = new ArrayList();
        keywordTexts.add(keyword_1);
        keywordTexts.add(keyword_2);
        keywordTexts.add(keyword_3);
        keywordTexts.add(keyword_4);
        keywordTexts.add(keyword_5);
        keywordTexts.add(keyword_6);
        keywordTexts.add(keyword_7);
        keywordTexts.add(keyword_8);
        keywordTexts.add(keyword_9);
    }

    public void initViewHolder(final Analyst analyst, final Context context) {
        if (analyst == null)
            return ;
        List<String> keywords = AnalystManager.makeKeywordList(analyst);
        Map<String, Integer> containerDictionary = AnalystManager.makeContainerDictionary(keywords);
        Map<String, Integer> sortByValue = AnalystManager.sortByValue(containerDictionary);

        int index = 0;
        Iterator<String> keys = sortByValue.keySet().iterator();
        while(keys.hasNext() && index<9) {
            keywordTexts.get(index).setText(keys.next());
            index++;
        }
    }
}