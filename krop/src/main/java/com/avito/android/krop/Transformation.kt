package com.avito.android.krop

import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable

class Transformation(var size: SizeF = SizeF(), var crop: RectF = RectF()) : Parcelable {

    val isEmpty: Boolean
        get() = crop.isEmpty

    fun forSize(size: SizeF): RectF {
        val factor = this.size.width / size.width
        return RectF(
                crop.left * factor,
                crop.top * factor,
                crop.right * factor,
                crop.bottom * factor
        )
    }

    constructor(parcel: Parcel) : this(
            size = parcel.readParcelable(SizeF::class.java.classLoader),
            crop = parcel.readParcelable(RectF::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(size, flags)
        parcel.writeParcelable(crop, flags)
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