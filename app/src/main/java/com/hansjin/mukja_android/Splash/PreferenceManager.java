package com.hansjin.mukja_android.Splash;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mijeong on 2016. 11. 22..
 */

public class PreferenceManager {
    private volatile static PreferenceManager single;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    private Boolean push;

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
