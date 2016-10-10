package com.hansjin.mukja_android.ViewHolder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hansjin.mukja_android.R;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class ViewHolderFood extends ViewHolderParent {
    public LinearLayout cellFoodHeader, cellFoodBodyImage, cellFoodBodyDesc, cellFoodTail;
    public TextView foodName, authorName;
    public ViewHolderFood(View v) {
        super(v);
        cellFoodHeader = (LinearLayout) v.findViewById(R.id.cell_food_header);
        cellFoodBodyImage = (LinearLayout) v.findViewById(R.id.cell_food_body_image);
        cellFoodBodyDesc = (LinearLayout) v.findViewById(R.id.cell_food_body_desc);
        cellFoodTail = (LinearLayout) v.findViewById(R.id.cell_food_tail);

        foodName = (TextView) v.findViewById(R.id.food_name);
        authorName = (TextView) v.findViewById(R.id.author_name);
    }
}