package com.yuyu.module.utils;

import android.util.Log;
import android.view.Menu;

import java.util.ArrayList;

public class ChainedArrayList<E> extends ArrayList {

    public ChainedArrayList addOne(Object o) {
        add(o);
        return this;
    }

    public ChainedArrayList addMany(Object... o) {
        for (Object object : o) {
            add(object);
        }
        return this;
    }

    public ChainedArrayList addMenu(Menu menu, int start, int end) {
        for (int i = start; i < end; i++) {
            add(menu.getItem(i).getItemId());
        }
        return this;
    }

}
