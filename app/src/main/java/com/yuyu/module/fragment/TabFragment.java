package com.yuyu.module.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.yuyu.module.R;
import com.yuyu.module.adapter.TabAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabFragment extends Fragment {

    private final String TAG = TabFragment.class.getSimpleName();

    @BindView(R.id.tab_layout)
    PagerSlidingTabStrip tab_layout;
    @BindView(R.id.view_pager)
    ViewPager view_pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.nav_tab));
        tabInitialize();
        return view;
    }

    public void tabInitialize() {
        TabAdapter adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new TabFragment1(), getString(R.string.tab_1));
        adapter.addFragment(new TabFragment2(), getString(R.string.tab_2));
        view_pager.setAdapter(adapter);
        tab_layout.setViewPager(view_pager);
    }

}