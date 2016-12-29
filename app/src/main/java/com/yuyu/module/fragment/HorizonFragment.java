package com.yuyu.module.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuyu.module.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HorizonFragment extends Fragment {

    private final String TAG = HorizonFragment.class.getSimpleName();

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horizon, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.nav_horizon));
        return view;
    }
}
