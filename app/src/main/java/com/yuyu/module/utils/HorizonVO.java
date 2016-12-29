package com.yuyu.module.utils;

import lombok.Data;

@Data
public class HorizonVO {

    private int img;
    private String title;

    public HorizonVO(int img, String title) {
        this.img = img;
        this.title = title;
    }

}