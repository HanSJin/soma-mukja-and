package com.hansjin.mukja_android.Model;

import java.util.Date;
import java.text.SimpleDateFormat;

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
    public Integer age;//*
    public Boolean gender;//*
    public String job;//보류?
    public String location;//회원가입 후, 팝업으로 동의구하기
    public int rated_food_num;//회원가입 후, 팝업으로 동의구하기
    public String password;
}
