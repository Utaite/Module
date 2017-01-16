package com.yuyu.module.utils;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import lombok.Data;
import lombok.experimental.Accessors;

@Parcel
@Data
@Accessors(chain = true)
public class MainParcel {

    String id;
    String pw;

    @ParcelConstructor
    public MainParcel(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

}
