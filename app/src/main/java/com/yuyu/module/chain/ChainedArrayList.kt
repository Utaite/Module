package com.yuyu.module.chain

import android.view.Menu

import java.util.ArrayList


@Suppress("UNCHECKED_CAST")
class ChainedArrayList<T> : ArrayList<T>() {

    fun addTo(obj: T): ChainedArrayList<T> {
        add(obj)
        return this
    }

    fun addMany(vararg obj: T): ChainedArrayList<T> {
        for (o in obj) add(o)
        return this
    }

    fun addMenu(menu: Menu, start: Int, end: Int): ChainedArrayList<T> {
        for (i in start..end - 1) add(menu.getItem(i).itemId as T)
        return this
    }

}

/*
package com.yuyu.module.chain;

import android.view.Menu;

import java.util.ArrayList;

public class ChainedArrayList extends ArrayList {

    public ChainedArrayList addTo(Object o) {
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
*/
