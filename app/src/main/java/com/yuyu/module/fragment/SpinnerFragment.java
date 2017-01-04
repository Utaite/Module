package com.yuyu.module.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuyu.module.R;

import butterknife.ButterKnife;


public class SpinnerFragment extends Fragment {

    private final String TAG = SpinnerFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spinner, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
