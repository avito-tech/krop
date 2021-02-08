package com.avito.android.krop.util

import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable

@Suppress("unused")
data class Transformation(
        val scale: Float,
        val focusOffset: PointF,
        val rotationAngle: Float = 0f
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readFloat(),
            parcel.readParcelable(PointF::class.java.classLoader),
            parcel.readFloat()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(scale)
        parcel.writeParcelable(focusOffset, flags)
        parcel.writeFloat(rotationAngle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Transformation> {
        override fun createFromParcel(parcel: Parcel): Transformation {
            return Transformation(parcel)
        }

        override fun newArray(size: Int): Array<Transformation?> {
            return arrayOfNulls(size)
        }
    }

}