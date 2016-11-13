package com.hansjin.mukja_android.Model;

import com.hansjin.mukja_android.Utils.Constants.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by mijeong on 2016. 10. 4..
 */
public class User {
    //*->어느 정도 중요한 정보
    //**->가입시 반드시 입력받아야하는 정보
    public String _id;
    public String update_date;
    public String create_date;
    public String social_type;//normal or facebook
    public String social_id;//**email
    public String session_key;
    public String session_expire_date;
    public String push_token;
    public Boolean push_use;//회원가입 후, 팝업으로 동의구하기
    public String device_type;
    public String app_version;
    public String access_ip;//타입 Date?
    public String access_last_date;
    public String login_last_date;
    public Integer access_cnt;
    public Integer login_cnt;
    public Integer report_cnt;
    public String thumbnail_url;//*
    public String thumbnail_url_small;
    public String nickname;//** nickname보다는 name이 낫지않나? 왜냐하면 페이스북 가입 유저들은 다 실명쓰는데 일반가입만 익명이면 형평성에 문제가될수도
    public String about_me;//*
    public String birthday;//*
    public Boolean gender;//*
    public String job;//보류?
    public String location;//회원가입 후, 팝업으로 동의구하기
    public int rated_food_num;//회원가입 후, 팝업으로 동의구하기
    public String password;
    public List<Friends> friends = new ArrayList<>();
    public List<Friends_NonFacebook> friends_NonFacebook = new ArrayList<>();
    public List<Friends_NonFacebook_Waiting> friends_NonFacebook_Waiting = new ArrayList<>();
    public List<Friends_NonFacebook> friends_NonFacebook_Rejected = new ArrayList<>();
    public List<Friends_NonFacebook> friends_NonFacebook_Requested = new ArrayList<>();
    public int choice_cnt;
    public String choice_last_date;
    public LocationPoint location_point;

    public String getPic(){
        if(social_type.equals("facebook"))
            return thumbnail_url;
        else
            return Constants.IMAGE_BASE_URL + thumbnail_url;
    }

    public String getPic_small(){
        if(social_type.equals("facebook"))
            return thumbnail_url_small;
        else
            return Constants.IMAGE_BASE_URL + thumbnail_url_small;
    }

    public static class Friends_NonFacebook_Waiting implements Serializable {
        public String user_id;
        public String date;
        public String me_view_date;

        public String getUser_id() {
            return user_id;
        }
        public String getUser_view_date() {
            String temp_me_view_date = me_view_date;
            if(temp_me_view_date==null)
                return null;
            temp_me_view_date = temp_me_view_date.replace("T","\n");;
            String rest = temp_me_view_date.substring(temp_me_view_date.indexOf('.'), temp_me_view_date.length());
            temp_me_view_date = temp_me_view_date.replace(rest, "");

            return temp_me_view_date;
        }
    }




    public static class Friends_NonFacebook implements Serializable {
        public String user_id;
        public String user_name;
        public String date;
        public String user_pic_small;

        public String getUser_id() {
            return user_id;
        }
        public String getUser_name() {
            return user_name;
        }
        public String getUser_pic_small() {
            return user_pic_small;
        }
    }
    public List<String> friends_NonFacebook_id(){
        if(friends_NonFacebook==null)
            return null;

        List<String> result = new ArrayList<>();
        try {
            for (Friends_NonFacebook user : friends_NonFacebook) {
                result.add(user.user_id);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public static class Friends implements Serializable {
        public String user_id;
        public String user_name;
        public String user_pic_small;

        public String getUser_id() {
            return user_id;
        }
        public String getUser_name() {
            return user_name;
        }
        public String getUser_pic_small() {
            return user_pic_small;
        }

    }
    public List<String> friends_id(){
        if(friends==null)
            return null;

        List<String> result = new ArrayList<>();
        try {
            for (Friends user : friends) {
                result.add(user.user_id);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static Friends newFriend(String user_id, String user_name, String user_pic_small){
        Friends f = new Friends();
        f.user_id = user_id;
        f.user_name = user_name;
        f.user_pic_small = user_pic_small;
        return f;
    }


    public static class LocationPoint{
        public double lat;
        public double lon;

        public LocationPoint(double latitude, double longitude){
            lat = latitude;
            lon = longitude;
        }

    }

}
