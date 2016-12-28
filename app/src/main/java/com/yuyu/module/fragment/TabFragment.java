package com.yuyu.module.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuyu.module.R;
import com.yuyu.module.adapter.TabAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabFragment extends Fragment {

    private final String TAG = TabFragment.class.getSimpleName();

    @BindView(R.id.tab_layout)
    TabLayout tab_layout;
    @BindView(R.id.view_pager)
    ViewPager view_pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.nav_tab));
        tabInitialize(new Fragment[2]);
        return view;
    }

    public void tabInitialize(Fragment[] arr) {
        Log.e(TAG, "호출");
        arr[0] = new TabFragment1();
        arr[1] = new TabFragment2();
        view_pager.setAdapter(new TabAdapter(getFragmentManager(), arr));
        tab_layout.setupWithViewPager(view_pager);
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}