package com.hansjin.mukja_android.ViewHolder;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hansjin.mukja_android.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class ViewHolderFood extends ViewHolderParent {
    public LinearLayout cellFoodHeader, cellFoodBodyImage, cellFoodBodyDesc, cellFoodTail, eat_btn, rate_btn, layout_like_people;
    public TextView foodName, authorName, rate_num, category_tag, people_like, friend_like, write_time;
    public ImageView heart,star,food_img, report_btn;
    public CircleImageView author_image;

    public ViewHolderFood(View v) {
        super(v);
        cellFoodHeader = (LinearLayout) v.findViewById(R.id.cell_food_header);
        cellFoodBodyImage = (LinearLayout) v.findViewById(R.id.cell_food_body_image);
        cellFoodBodyDesc = (LinearLayout) v.findViewById(R.id.cell_food_body_desc);
        cellFoodTail = (LinearLayout) v.findViewById(R.id.cell_food_tail);
        eat_btn = (LinearLayout) v.findViewById(R.id.eat_btn);
        rate_btn = (LinearLayout) v.findViewById(R.id.rate_btn);

        layout_like_people = (LinearLayout) v.findViewById(R.id.layout_like_people);
        foodName = (TextView) v.findViewById(R.id.food_name);
        authorName = (TextView) v.findViewById(R.id.author_name);
        rate_num = (TextView) v.findViewById(R.id.food_rate_num);
        category_tag = (TextView) v.findViewById(R.id.txt_category);
        people_like = (TextView) v.findViewById(R.id.txt_people_like);
        write_time = (TextView) v.findViewById(R.id.write_time);

        heart = (ImageView) v.findViewById(R.id.heart_img);
        star = (ImageView) v.findViewById(R.id.star_img);
        food_img = (ImageView) v.findViewById(R.id.food_image);
        report_btn = (ImageView) v.findViewById(R.id.report_btn);

        author_image = (CircleImageView) v.findViewById(R.id.author_image);
    }
}