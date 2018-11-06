package com.open.aqrlei.ipc

import android.os.Parcel
import android.os.Parcelable

/**
 * @author  aqrLei on 2018/8/1
 * @description 必须与相应的AIDL在同个包下
 */
data class Info(var data: String,
                var times: Int) : Parcelable {
    companion object CREATOR : Parcelable.Creator<Info> {
        override fun createFromParcel(source: Parcel): Info = Info(source)

        override fun newArray(size: Int): Array<Info?> = arrayOfNulls(size)
    }

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readInt()

    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeString(data)
            writeInt(times)
        }
    }

    override fun describeContents() = 0
}