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

    public String name;
    public int view_cnt;
    public int like_cnt;
    public int rate_cnt;
    public Author author = new Author();
    public String image_url;

    public List<String> taste = new ArrayList<>();
    public List<String> country = new ArrayList<>();
    public List<String> cooking = new ArrayList<>();
    public List<String> ingredient = new ArrayList<>();

    public List<String> like_person = new ArrayList<>();
    public List<String> rate_persion = new ArrayList<>();
    public List<Integer> rate_distribution = new ArrayList<>();

    public class Author {
        public String author_id;
        public String author_nickname;
        public String author_thumbnail_url;
        public String author_thumbnail_url_small;
    }
}
