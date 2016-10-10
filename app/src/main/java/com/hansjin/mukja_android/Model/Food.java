package com.hansjin.mukja_android.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by kksd0900 on 16. 9. 29..
 */
public class Food {
    public String _id;
    public String update_date;
    public String create_date;

    public String author;

    public String name;
    public String image;
    public int view_cnt;
    public int like_cnt;
    public int rate_cnt;

    public List<String> taste;
    public List<String> country;
    public List<String> cooking;
    public List<String> ingredient;

    public List<String> like_user;
    public List<String> rate_user;
    public List<Integer> rate_distribution;

    // DO NOT USE GET / SET METHOD

    public static Food mockFood(int index) {
        Food food = new Food();
        food.author = "승진이" + index;
        food.name = "해물 떡볶이 " + index;
        return food;
    }
}
