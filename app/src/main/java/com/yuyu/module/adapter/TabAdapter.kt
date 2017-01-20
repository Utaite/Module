package com.yuyu.module.adapter


import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter

import com.yuyu.module.utils.TabVO

import java.util.ArrayList

class TabAdapter(fm: FragmentManager, private val vo: ArrayList<TabVO>) : FragmentPagerAdapter(fm) {

    private val TAG: String = TabAdapter::class.java.simpleName

    override fun getItem(position: Int) = vo[position].fragment

    override fun getCount() = vo.size

    override fun getPageTitle(position: Int) = vo[position].title

}

/*
package com.yuyu.module.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.yuyu.module.utils.TabVO;

import java.util.ArrayList;

public class TabAdapter extends FragmentPagerAdapter {

    private final String TAG = TabAdapter.class.getSimpleName();

    private ArrayList<TabVO> vo;

    public TabAdapter(FragmentManager fm, ArrayList<TabVO> vo) {
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
*/
