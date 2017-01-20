package com.yuyu.module.utils

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

@Parcel(Parcel.Serialization.BEAN)
data class MainParcel @ParcelConstructor constructor(
        @ParcelProperty("id") val id: String,
        @ParcelProperty("pw") val pw: String
)

/*
package com.yuyu.module.utils;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import lombok.Data;
import lombok.experimental.Accessors;

@Parcel
@Data
@Accessors(chain = true)
public class MainParcel {

        String id, pw;

        @ParcelConstructor
        public MainParcel(String id, String pw) {
                this.id = id;
                this.pw = pw;
        }

}
*/
