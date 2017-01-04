package com.yuyu.module.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class HorizonVO {

    private final int img;
    private final String title;

}
