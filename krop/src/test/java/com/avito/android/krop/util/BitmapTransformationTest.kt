package com.avito.android.krop.util

import android.graphics.Matrix
import android.os.Parcel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random


@RunWith(RobolectricTestRunner::class)
class BitmapTransformationTest {

    @Test
    fun parcelize() {

        val expected = BitmapTransformation(
                randomMatrix(),
                randomSize(),
                randomSize()
        )
        val parcel = Parcel.obtain()
        expected.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val result = BitmapTransformation.CREATOR.createFromParcel(parcel)
        println("got res $result")
        assertEquals(expected, result)
    }

    private fun randomMatrix() = Matrix().apply {
        postTranslate(Random.nextFloat(), Random.nextFloat())
        postScale(Random.nextFloat(), Random.nextFloat())
        postRotate(Random.nextFloat())
    }

    private fun randomSize() = BitmapTransformation.Size(width = Random.nextInt(), height = Random.nextInt())
}