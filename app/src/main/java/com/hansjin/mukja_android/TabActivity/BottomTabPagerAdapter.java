package com.hansjin.mukja_android.TabActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.hansjin.mukja_android.TabActivity.ParentFragment.TabParentFragment;
import com.hansjin.mukja_android.TabActivity.Tab1Recommand.Tab1RecommandFragment;
import com.hansjin.mukja_android.TabActivity.Tab2Feeds.Tab2FeedsFragment;
import com.hansjin.mukja_android.TabActivity.Tab3List.Tab3ListFragment;
import com.hansjin.mukja_android.TabActivity.Tab4Explore.Tab4ExploreFragment;
import com.hansjin.mukja_android.TabActivity.Tab5MyPage.Tab5MyPageFragment;

import java.util.ArrayList;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class BottomTabPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private TabParentFragment currentFragment;

    public BottomTabPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(Tab1RecommandFragment.newInstance(0));
        fragments.add(Tab2FeedsFragment.newInstance(1));
        fragments.add(Tab3ListFragment.newInstance(2));
        fragments.add(Tab4ExploreFragment.newInstance(3));
        fragments.add(Tab5MyPageFragment.newInstance(4));
    }

    @Override
    public TabParentFragment getItem(int position) {
        return (TabParentFragment) fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((TabParentFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    /**
     * Get the current fragment
     */
    public TabParentFragment getCurrentFragment() {
        return currentFragment;
    }
}
