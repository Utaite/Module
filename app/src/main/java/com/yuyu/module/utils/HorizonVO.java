package com.yuyu.module.utils;

import lombok.Data;

@Data
public class HorizonVO {

    private int res;
    private String title;

    public HorizonVO(int res, String title) {
        this.res = res;
        this.title = title;
    }

}