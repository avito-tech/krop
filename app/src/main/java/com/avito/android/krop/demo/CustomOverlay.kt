package com.avito.android.krop.demo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import com.avito.android.krop.OverlayView

class CustomOverlay(context: Context, attrs: AttributeSet? = null) : OverlayView(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 200f
        color = Color.GREEN
    }
    private val path = Path()

    override fun Canvas.drawViewportView(viewport: RectF, clearPaint: Paint) {
        // Make some area transparent
        drawOval(viewport, clearPaint)

        path.reset()
        path.addOval(viewport, Path.Direction.CW)
        drawTextOnPath("This is custom overlay for paint", path, 0f, 0f, paint)
    }
}