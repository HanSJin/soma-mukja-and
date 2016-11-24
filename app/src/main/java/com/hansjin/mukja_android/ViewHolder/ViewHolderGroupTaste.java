package com.hansjin.mukja_android.ViewHolder;

import android.view.View;
import android.widget.LinearLayout;

import com.hansjin.mukja_android.R;
import com.zhy.view.flowlayout.TagFlowLayout;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class ViewHolderGroupTaste extends ViewHolderParent {
    public TagFlowLayout taste, country, cooking;
    public LinearLayout layout_category;

    public ViewHolderGroupTaste(View v) {
        super(v);
        taste = (TagFlowLayout) v.findViewById(R.id.taste_flowlayout);
        country = (TagFlowLayout) v.findViewById(R.id.country_flowlayout);
        cooking = (TagFlowLayout) v.findViewById(R.id.cooking_flowlayout);
        layout_category = (LinearLayout) v.findViewById(R.id.layout_category);
    }
}
