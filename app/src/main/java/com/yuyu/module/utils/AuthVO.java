package com.yuyu.module.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthVO {

    private String email, displayName, uid, photoUrl, id, idToken;

}
