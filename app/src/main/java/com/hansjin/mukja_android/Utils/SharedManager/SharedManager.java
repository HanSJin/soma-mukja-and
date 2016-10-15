package com.hansjin.mukja_android.Utils.SharedManager;

import com.hansjin.mukja_android.Model.User;

/**
 * Created by kksd0900 on 16. 10. 16..
 */
public class SharedManager {
    private volatile static SharedManager single;
    private User me;

    public static SharedManager getInstance() {
        if (single == null) {
            synchronized(SharedManager.class) {
                if (single == null) {
                    single = new SharedManager();
                }
            }
        }
        return single;
    }

    private SharedManager() {

    }

    public boolean setMe(User response) {
        try {
            this.me = response;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public User getMe() {
        return this.me;
    }

}
