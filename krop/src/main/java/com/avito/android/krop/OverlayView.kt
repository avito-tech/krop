package com.avito.android.krop

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.view.View

class OverlayView(context: Context) : View(context) {

    private var overlayColor: Int = Color.TRANSPARENT
    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var viewport = RectF()

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        clearPaint.color = Color.BLACK
        clearPaint.style = Paint.Style.FILL
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)

        if (viewport.isEmpty) {
            with(viewport) {
                left = 0f
                top = 0f
                right = width.toFloat()
                bottom = height.toFloat()
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setOverlayColor(color: Int) {
        overlayColor = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(overlayColor)
        canvas.drawOval(viewport, clearPaint)
    }

}