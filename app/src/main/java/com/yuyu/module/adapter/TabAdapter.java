package com.yuyu.module.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

public class TabAdapter extends FragmentPagerAdapter {

    private final String TAG = TabAdapter.class.getSimpleName();
    private Fragment[] arr;

    public TabAdapter(FragmentManager fm, Fragment[] arr) {
        super(fm);
        this.arr = arr;
        Log.e(TAG, "호출");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "TAB 1";
            case 1:
                return "TAB 2";
            default:
                return "";

        }
    }

    @Override
    public Fragment getItem(int position) {
        return arr[position];
    }

    @Override
    public int getCount() {
        return arr.length;
    }
}
