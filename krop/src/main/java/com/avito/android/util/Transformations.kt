package com.avito.android.util

import android.graphics.RectF
import com.avito.android.krop.SizeF
import com.avito.android.krop.Transformation

fun Transformation.rotate(angle: Int) {
    when (angle) {
        0 -> {
        }
        90 -> {
            size = SizeF(width = size.height, height = size.width)
            crop = RectF(
                    crop.top,
                    size.height - crop.right,
                    crop.bottom,
                    size.height - crop.left
            )
        }
        180 -> {
            crop = RectF(
                    size.width - crop.right,
                    size.height - crop.bottom,
                    size.width - crop.left,
                    size.height - crop.top
            )
        }
        270 -> {
            size = SizeF(width = size.height, height = size.width)
            crop = RectF(
                    size.width - crop.bottom,
                    crop.left,
                    size.width - crop.top,
                    crop.right
            )
        }
        else -> {
            throw IllegalArgumentException("Only right angles are allowed")
        }
    }
}