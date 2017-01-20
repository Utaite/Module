package com.yuyu.module.chain

import android.content.Context
import android.widget.Toast

class ChainedToast(context: Context) : Toast(context) {

    private var toast: Toast? = null

    fun setTextShow(s: CharSequence) {
        toast?.setText(s)
        toast?.show()
    }

    fun makeTextTo(context: Context, text: CharSequence, duration: Int): ChainedToast {
        toast = Toast.makeText(context, text, duration)
        return this
    }

}

/*
package com.yuyu.module.chain;

import android.content.Context;
import android.widget.Toast;

public class ChainedToast extends Toast {

    private Toast toast;

    public ChainedToast(Context context) {
        super(context);
    }

    public void setTextShow(CharSequence s) {
        toast.setText(s);
        toast.show();
    }

    public ChainedToast makeTextTo(Context context, CharSequence text, int duration) {
        toast = Toast.makeText(context, text, duration);
        return this;
    }

}
*/
