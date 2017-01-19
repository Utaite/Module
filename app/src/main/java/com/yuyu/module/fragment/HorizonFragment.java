package com.yuyu.module.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.adapter.HorizonAdapter;
import com.yuyu.module.chain.ChainedArrayList;
import com.yuyu.module.utils.HorizonVOK;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HorizonFragment extends RxFragment {

    private final String TAG = HorizonFragment.class.getSimpleName();

    private Context context;

    @BindView(R.id.horizon_view)
    HorizontalInfiniteCycleViewPager horizon_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horizon, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        initialize();
        return view;
    }

    public void initialize() {
        ArrayList<HorizonVOK> arrayList = new ChainedArrayList().addMany(
                new HorizonVOK(R.drawable.ic_menu_camera, getString(R.string.horizon_view_1)),
                new HorizonVOK(R.drawable.ic_menu_gallery, getString(R.string.horizon_view_2)),
                new HorizonVOK(R.drawable.ic_menu_manage, getString(R.string.horizon_view_3)),
                new HorizonVOK(R.drawable.ic_menu_send, getString(R.string.horizon_view_4)));

        horizon_view.setAdapter(new HorizonAdapter(context, arrayList));
    }
}
