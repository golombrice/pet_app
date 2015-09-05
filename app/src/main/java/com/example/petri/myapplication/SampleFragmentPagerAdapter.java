package com.example.petri.myapplication;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Haku", "Tallennetut" };
    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    private ArrayList< Fragment > fragments_ = new ArrayList<Fragment>(tabTitles.length);
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                fragments_.add(0, SearchFragment.newInstance("fds", "gfd"));
                return fragments_.get(0);
            case 1:
                fragments_.add(1, SavedUsersFragment.newInstance("fds", "gfd"));
                return fragments_.get(1);

        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    public Fragment getFragment(int position) {
        return fragments_.get(position);
    }
}
