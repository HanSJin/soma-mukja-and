package com.hansjin.mukja_android.ViewHolder;

import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hansjin.mukja_android.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class ViewHolderFoodCategory extends ViewHolderParent {
    public TagFlowLayout taste, country, cooking;
    public ImageView expand_btn;
    public LinearLayout layout_category;

    public ViewHolderFoodCategory(View v) {
        super(v);
        taste = (TagFlowLayout) v.findViewById(R.id.taste_flowlayout);
        country = (TagFlowLayout) v.findViewById(R.id.country_flowlayout);
        cooking = (TagFlowLayout) v.findViewById(R.id.cooking_flowlayout);
        layout_category = (LinearLayout) v.findViewById(R.id.layout_category);
        expand_btn = (ImageView) v.findViewById(R.id.expand_btn);
    }
}
