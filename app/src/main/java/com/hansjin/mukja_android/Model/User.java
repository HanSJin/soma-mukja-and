package com.hansjin.mukja_android.Model;

import java.sql.Date;

/**
 * Created by mijeong on 2016. 10. 4..
 */
public class User {
    //*->어느 정도 중요한 정보
    //**->가입시 반드시 입력받아야하는 정보
    Date update_date;
    Date create_date;
    String social_type;//normal or facebook
    String social_id;//**email
    String session_key;
    Date session_expire_date;
    String push_token;
    Boolean push_use;//회원가입 후, 팝업으로 동의구하기
    String device_type;
    String app_version;
    Date access_ip;//타입 Date?
    Date access_last_date;
    String login_last_date;
    Integer access_cnt;
    Integer login_cnt;
    Integer report_cnt;
    String thumbnail_url;//*
    String thumbnail_url_small;
    String nickname;//** nickname보다는 name이 낫지않나? 왜냐하면 페이스북 가입 유저들은 다 실명쓰는데 일반가입만 익명이면 형평성에 문제가될수도
    String about_me;//*
    Integer age;//*
    Boolean gender;//*
    String job;//보류?
    String location;//회원가입 후, 팝업으로 동의구하기
    //String password; //**일반가입인 경우만 해당

    public User() {
        this.gender = null;
        this.update_date = null;
        this.create_date = null;
        this.social_type = null;
        this.social_id = null;
        this.session_key = null;
        this.session_expire_date = null;
        this.push_token = null;
        this.push_use = null;
        this.device_type = null;
        this.app_version = null;
        this.access_ip = null;
        this.access_last_date = null;
        this.login_last_date = null;
        this.access_cnt = null;
        this.login_cnt = null;
        this.report_cnt = null;
        this.thumbnail_url = null;
        this.thumbnail_url_small = null;
        this.nickname = null;
        this.about_me = null;
        this.age = null;
        this.job = null;
        this.location = null;
    }

    public User(String social_id, String name, Boolean gender, String device_type, String app_version, String about_me, Integer age, String job, String location) {
        this.gender = gender;
        this.update_date = null;
        this.create_date = null;
        this.social_type = null;
        this.social_id = social_id;
        this.session_key = null;
        this.session_expire_date = null;
        this.push_token = null;
        this.push_use = null;
        this.device_type = device_type;
        this.app_version = app_version;
        this.access_ip = null;
        this.access_last_date = null;
        this.login_last_date = null;
        this.access_cnt = null;
        this.login_cnt = null;
        this.report_cnt = null;
        this.thumbnail_url = null;
        this.thumbnail_url_small = null;
        this.nickname = name;
        this.about_me = about_me;
        this.age = age;
        this.job = job;
        this.location = location;
    }


    public void setUser(User user){
        this.gender = user.gender;
        this.update_date = user.update_date;
        this.create_date = user.create_date;
        this.social_type = user.social_type;
        this.social_id = user.social_id;
        this.session_key = user.session_key;
        this.session_expire_date = user.session_expire_date;
        this.push_token = user.push_token;
        this.push_use = user.push_use;
        this.device_type = user.device_type;
        this.app_version = user.app_version;
        this.access_ip = user.access_ip;
        this.access_last_date = user.access_last_date;
        this.login_last_date = user.login_last_date;
        this.access_cnt = user.access_cnt;
        this.login_cnt = user.login_cnt;
        this.report_cnt = user.report_cnt;
        this.thumbnail_url = user.thumbnail_url;
        this.thumbnail_url_small = user.thumbnail_url_small;
        this.nickname = user.nickname;
        this.about_me = user.about_me;
        this.age = user.age;
        this.job = user.job;
        this.location = user.location;
    }

    public String getUser_social_id() {
        return social_id;
    }

    public String getUser_nickname() {
        return nickname;
    }

    public String getUser_about_me() {
        return about_me;
    }


}
