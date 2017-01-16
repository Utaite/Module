package com.yuyu.module.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.chain.ChainedArrayList;
import com.yuyu.module.chain.ChainedTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SpinnerFragment extends RxFragment {

    private final String TAG = SpinnerFragment.class.getSimpleName();

    private final int HIDE_POSITION = 0;

    private Context context;

    @BindView(R.id.spinner_spinner)
    AppCompatSpinner spinner_spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spinner, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        initialize();
        return view;
    }

    public void initialize() {
        ArrayList<String> arrayList = new ChainedArrayList().addMany(
                getString(R.string.spinner_prompt),getString(R.string.spinner_view_1),
                getString(R.string.spinner_view_2), getString(R.string.spinner_view_3), getString(R.string.spinner_view_4));

        spinner_spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, arrayList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return position == HIDE_POSITION ?
                        new ChainedTextView(context).setHeightTo(0) : super.getDropDownView(position, null, parent);
            }
        });

        spinner_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != HIDE_POSITION) {
                    ((MainActivity) context).getToast().setTextShow(
                            getString(R.string.spinner_selected, position, spinner_spinner.getAdapter().getItem(position)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
