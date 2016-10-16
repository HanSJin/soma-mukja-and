package com.hansjin.mukja_android.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.RatingBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kksd0900 on 16. 9. 29..
 */
public class Food implements Serializable {
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
    public List<RatePerson> rate_person = new ArrayList<>();
    public List<Integer> rate_distribution = new ArrayList<>();

    public class Author implements Serializable {
        public String author_id;
        public String author_nickname;
        public String author_thumbnail_url;
        public String author_thumbnail_url_small;
    }

    public class RatePerson implements Serializable {
        public String user_id;
        public float rate_num;
    }

    public List<String> rate_person_id(){
        List<String> result = new ArrayList<>();
        for (RatePerson user_id:rate_person) {
            result.add(user_id.user_id);
        }
        return result;
    }

    public RatePerson newrate(String user_id,float rate_num){
        RatePerson rp = new RatePerson();
        rp.rate_num = rate_num;
        rp.user_id = user_id;
        return rp;
    }
}
