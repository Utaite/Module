package com.yuyu.module.chain;

import android.view.View;


public class Chained {

    public static View setVisibilityTo(View view, int visibility) {
        view.setVisibility(visibility);
        return view;
    }

    public static void setVisibilityMany(int visibility, View... view) {
        for (View v : view) {
            v.setVisibility(visibility);
        }
    }

}
