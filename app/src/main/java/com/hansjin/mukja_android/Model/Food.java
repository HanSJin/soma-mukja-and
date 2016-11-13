package com.hansjin.mukja_android.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.RatingBar;

import com.hansjin.mukja_android.Utils.TimeFormatter.TimeFormmater;

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

    //public List<String> like_person = new ArrayList<>();
    public List<LikePerson> like_person = new ArrayList<>();
    public List<RatePerson> rate_person = new ArrayList<>();
    public List<Integer> rate_distribution = new ArrayList<>();

    public int comment_cnt;
    public List<CommentPerson> comment_person = new ArrayList<>();

    public class LikePerson implements Serializable {
        public String user_id;
        public String like_date_;

        public String getUser_id() {
            return user_id;
        }

        public String getLike_date() {
            return like_date_;
        }
    }

    public List<String> like_person_id(){
        List<String> result = new ArrayList<>();
        for (LikePerson user_id:like_person) {
            result.add(user_id.user_id);
        }
        return result;
    }

    public LikePerson newLike(String user_id,String like_date_){
        LikePerson lp = new LikePerson();
        lp.user_id = user_id;
        lp.like_date_ = like_date_;
        return lp;
    }

    public class Author implements Serializable {
        public String author_id;
        public String author_nickname;
        public String author_thumbnail_url;
        public String author_thumbnail_url_small;
    }


    public class RatePerson implements Serializable {
        public String user_id;
        public float rate_num;
        public String rate_date;

        public String getUser_id() {
            return user_id;
        }

        public float getRate_num() {
            return rate_num;
        }
        public String getRateDate() {
            return rate_date;
        }
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




    public class CommentPerson implements Serializable {
        public String user_id;
        public String user_name;
        public String comment;
        public String comment_date;
        public String thumbnail_url_small;
        public List<ReCommentPerson> re_comment_person;
        public String comment_id;

        public String getUser_id() {
            return user_id;
        }

        public String getComment() {
            return comment;
        }
        public String getCommentDate() {
            return comment_date;
        }
    }

    public List<String> comment_person_id(){
        List<String> result = new ArrayList<>();
        for (CommentPerson user_id:comment_person) {
            result.add(user_id.user_id);
        }
        return result;
    }

    public CommentPerson newComment(String user_id, String comment){
        CommentPerson cp = new CommentPerson();
        cp.comment = comment;
        cp.user_id = user_id;
        return cp;
    }


    public class ReCommentPerson implements Serializable {
        public String user_id;
        public String user_name;
        public String comment;
        public String comment_date;
        public String thumbnail_url_small;

        public String getUser_id() {
            return user_id;
        }

        public String getComment() {
            return comment;
        }
        public String getCommentDate() {
            return comment_date;
        }
    }

    public List<String> re_comment_person_id(){
        List<String> result = new ArrayList<>();
        for (CommentPerson user_id:comment_person) {
            result.add(user_id.user_id);
        }
        return result;
    }

    public CommentPerson newReComment(String user_id, String comment){
        CommentPerson cp = new CommentPerson();
        cp.comment = comment;
        cp.user_id = user_id;
        return cp;
    }

}
