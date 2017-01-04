package com.yuyu.module.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MapVO {

    private final double lat, lon;
    private final String description;

}
