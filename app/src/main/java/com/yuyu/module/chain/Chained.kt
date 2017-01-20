package com.yuyu.module.chain

import android.view.View


class Chained {

    companion object {
        @JvmStatic
        fun setVisibilityMany(visibility: Int, vararg view: View) {
            for (v in view) v.visibility = visibility
        }

        @JvmStatic
        fun setAlpha(alpha: Int, vararg view: View) {
            for (v in view) v.background.alpha = alpha
        }

    }
}

/*
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
*/
