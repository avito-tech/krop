package com.avito.android.krop.util

import org.junit.Assert.assertEquals
import org.junit.Test

class KLineTest {

    @Test
    fun `test intersection`() {
        val line1 = KLine(KPoint(1f, 1f), KPoint(3f, 2f))
        val line2 = KLine(KPoint(1f, 4f), KPoint(2f, -1f))
        val actual = line1.findIntersection(line2)
        val expected = KPoint(17f / 11, 14f / 11)

        assertEquals(expected, actual)
    }

    @Test
    fun `test normal`() {
        val line = KLine(KPoint(0f, 0f), KPoint(6f, 0f))
        val point = KPoint(3f, 3f)
        val actual = line.normalFrom(point)
        val expected = KLine(point, KPoint(3f, 0f))

        assertEquals(expected, actual)
    }

    @Test
    fun `test nearest point in front of segment`() {
        val line = KLine(KPoint(0f, 0f), KPoint(6f, 0f))
        val point = KPoint(3f, 3f)
        val actual = line.nearestPointFor(point)
        val expected = KPoint(3f, 0f)

        assertEquals(expected, actual)
    }

    @Test
    fun `test nearest point out of segment`() {
        val line = KLine(KPoint(0f, 0f), KPoint(6f, 0f))
        val point = KPoint(-3f, 3f)
        val actual = line.nearestPointFor(point)
        val expected = KPoint(0f, 0f)

        assertEquals(expected, actual)
    }
}