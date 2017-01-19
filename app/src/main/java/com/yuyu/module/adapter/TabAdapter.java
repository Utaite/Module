package com.yuyu.module.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.yuyu.module.utils.TabVOK;

import java.util.ArrayList;

public class TabAdapter extends FragmentPagerAdapter {

    private final String TAG = TabAdapter.class.getSimpleName();

    private ArrayList<TabVOK> vo;

    public TabAdapter(FragmentManager fm, ArrayList<TabVOK> vo) {
        super(fm);
        this.vo = vo;
    }

    @Override
    public Fragment getItem(int position) {
        return vo.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return vo.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return vo.get(position).getTitle();
    }

}
