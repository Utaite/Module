package com.yuyu.module.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChatVO {

    private String email, message, photoUrl;

    public ChatVO() {
    }

    public ChatVO(String email, String message, String photoUrl) {
        this.email = email;
        this.message = message;
        this.photoUrl = photoUrl;
    }

}
