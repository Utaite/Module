package com.yuyu.module.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChatVO {

    private String userName;
    private String message;

    public ChatVO() {
    }

    public ChatVO(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }

}
