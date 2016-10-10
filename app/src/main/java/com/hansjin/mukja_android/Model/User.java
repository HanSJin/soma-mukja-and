package com.hansjin.mukja_android.Model;

/**
 * Created by mijeong on 2016. 10. 4..
 */
public class User {
    String name;
    String _id;

    public User(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    // DO NOT USE GET / SET METHOD
}
