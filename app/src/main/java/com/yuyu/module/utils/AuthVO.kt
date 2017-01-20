package com.yuyu.module.utils

data class AuthVO(var email: String? = null,
                  var displayName: String? = null,
                  var uid: String? = null,
                  var photoUrl: String? = null,
                  var id: String? = null,
                  var idToken: String? = null)

/*
package com.yuyu.module.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthVO {

    private String email, displayName, uid, photoUrl, id, idToken;

}
*/
