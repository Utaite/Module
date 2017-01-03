package com.yuyu.module.utils;

import android.app.Fragment;

import lombok.Data;

@Data
public class TabVO {

    private Fragment fragment;
    private String title;

    public TabVO(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

}
