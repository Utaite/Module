package com.yuyu.module.utils;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import lombok.Data;
import lombok.experimental.Accessors;

@Parcel
@Data
@Accessors(chain = true)
public class MainParcel {

    private String name;
    private int age;

    @ParcelConstructor
    public MainParcel(String name, int age) {
        this.name = name;
        this.age = age;
    }

}
