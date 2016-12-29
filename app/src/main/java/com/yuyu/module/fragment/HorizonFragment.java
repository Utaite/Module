package com.yuyu.module.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.yuyu.module.R;
import com.yuyu.module.adapter.HorizonAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HorizonFragment extends Fragment {

    private final String TAG = HorizonFragment.class.getSimpleName();

    @BindView(R.id.horizon_view)
    HorizontalInfiniteCycleViewPager horizon_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horizon, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.nav_horizon));
        horizon_view.setAdapter(new HorizonAdapter(getActivity().getApplicationContext()));
        return view;
    }

}
