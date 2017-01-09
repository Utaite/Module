package com.yuyu.module.chain;

import android.view.View;


public class Chained {

    public static void setVisibilityMany(int visibility, View... view) {
        for (View v : view) {
            v.setVisibility(visibility);
        }
    }

    public static void setAlpha(int alpha, View... view) {
        for(View v : view) {
            v.getBackground().setAlpha(alpha);
        }
    }

}
