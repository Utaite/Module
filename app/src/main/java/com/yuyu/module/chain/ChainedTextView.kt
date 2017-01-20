package com.yuyu.module.chain

import android.content.Context
import android.widget.TextView

class ChainedTextView(context: Context) : TextView(context) {

    fun setHeightTo(pixels: Int): ChainedTextView {
        height = pixels
        return this
    }

}

/*
package com.yuyu.module.chain;

import android.content.Context;
import android.widget.TextView;

public class ChainedTextView extends TextView {

    public ChainedTextView(Context context) {
        super(context);
    }

    public ChainedTextView setHeightTo(int pixels) {
        setHeight(pixels);
        return this;
    }

}
*/
