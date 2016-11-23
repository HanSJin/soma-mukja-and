package com.hansjin.mukja_android.Utils.SharedManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by mijeong on 2016. 11. 22..
 */

public class PreferenceManager {
    private volatile static PreferenceManager single;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    public static PreferenceManager getInstance(Context ctx) {
        if (single == null) {
            synchronized(PreferenceManager.class) {
                if (single == null) {
                    single = new PreferenceManager(ctx);
                }
            }
        }

        return single;
    }
    public boolean set_id(String _id){
        try {
            editor.putString("_id", _id);
            editor.commit();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String get_id(){
        return prefs.getString("_id","");
    }

    public PreferenceManager(Context ctx){
        prefs = ctx.getSharedPreferences("mukja", ctx.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public Boolean getPush() {
        return prefs.getBoolean("push",true);
    }

    public boolean setPush(Boolean push) {
        try {
            editor.putBoolean("push",push);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
