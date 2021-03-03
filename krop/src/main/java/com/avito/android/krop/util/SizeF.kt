package com.avito.android.krop.util

import android.os.Parcel
import android.os.Parcelable

class SizeF(var width: Float = 0.0f, var height: Float = 0.0f) : Parcelable {

    val widthInt: Int
        get() = width.toInt()

    val heightInt: Int
        get() = height.toInt()

    constructor(parcel: Parcel) : this(
            width = parcel.readFloat(),
            height = parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(width)
        parcel.writeFloat(height)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "SizeF(width=$widthInt, height=$heightInt)"
    }


    companion object CREATOR : Parcelable.Creator<SizeF> {
        override fun createFromParcel(parcel: Parcel): SizeF {
            return SizeF(parcel)
        }

        override fun newArray(size: Int): Array<SizeF?> {
            return arrayOfNulls(size)
        }
    }

}