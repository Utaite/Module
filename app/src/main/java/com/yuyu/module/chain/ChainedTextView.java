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