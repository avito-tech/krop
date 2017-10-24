package com.avito.android.krop

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.support.annotation.IntDef
import android.view.View

class OverlayView(context: Context) : View(context) {

    private var overlayColor: Int = Color.TRANSPARENT
    private var shape: Int = SHAPE_OVAL
    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var viewport = RectF()

    constructor(context: Context, @OverlayShape shape: Int) : this(context) {
        this.shape = shape
    }

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
        when (shape) {
            SHAPE_OVAL -> canvas.drawOval(viewport, clearPaint)
            else -> canvas.drawRect(viewport, clearPaint)
        }
    }

}

@IntDef(SHAPE_OVAL, SHAPE_RECT)
@Retention(AnnotationRetention.SOURCE)
annotation class OverlayShape

const val SHAPE_OVAL = 0
const val SHAPE_RECT = 1