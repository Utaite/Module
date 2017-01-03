package com.yuyu.module.utils;

import lombok.Data;

@Data
public class MapVO {

    private double lat, lon;
    private String description;

    public MapVO(double lat, double lon, String description) {
        this.lat = lat;
        this.lon = lon;
        this.description = description;
    }

}
