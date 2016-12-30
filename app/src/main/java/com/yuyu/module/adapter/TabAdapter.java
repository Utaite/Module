package com.yuyu.module.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {

    private final String TAG = TabAdapter.class.getSimpleName();

    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragmentsTitle = new ArrayList<>();

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentsTitle.get(position);
    }
    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        fragmentsTitle.add(title);
    }
}
