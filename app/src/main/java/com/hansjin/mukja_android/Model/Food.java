package com.hansjin.mukja_android.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
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
        food.like_cnt = 5;
        food.view_cnt = 10;
        food.rate_cnt = 2;
        food.ingredient = new ArrayList<>();
        food.ingredient.add("떡");
        food.ingredient.add("해물");
        food.ingredient.add("고추장");
        food.taste = new ArrayList<>();
        food.taste.add("매콤");
        food.country = new ArrayList<>();
        food.country.add("한식");
        food.cooking = new ArrayList<>();
        food.cooking.add("볶음");
        food.update_date = "2011-10-05T14:48:00.000Z";
        food.image = "http://scontent.cdninstagram.com/t51.2885-15/s480x480/e15/11242295_1412719429052545_1061947082_n.jpg?ig_cache_key=OTk0MTUzNTkzNzI1OTI4Nzg4.2";
        food.rate_distribution = new ArrayList<>();
        for(int i=1;i<=10;i++){
            food.rate_distribution.add(i);
        }
        return food;
    }
    
    public Food(){
        //for post
        taste = new ArrayList<>();
        country = new ArrayList<>();
        cooking = new ArrayList<>();
        ingredient = new ArrayList<>();
    }
}
