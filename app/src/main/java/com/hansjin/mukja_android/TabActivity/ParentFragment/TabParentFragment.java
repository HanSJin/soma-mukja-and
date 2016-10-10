package com.hansjin.mukja_android.TabActivity.ParentFragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.hansjin.mukja_android.R;

/**
 * Created by kksd0900 on 16. 8. 29..
 */
public abstract class TabParentFragment extends Fragment {
    public abstract void reload();
    public abstract void refresh();

    public void willBeDisplayed() {
        fadeInAnim(this.getView());
    }

    void fadeInAnim(final View view) {
        final Animation animationFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animationFadeOut);
    }
}
