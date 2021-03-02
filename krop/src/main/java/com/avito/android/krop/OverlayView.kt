package com.avito.android.krop

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.IntDef
import android.view.View

abstract class OverlayView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var overlayColor: Int = Color.TRANSPARENT
    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    var viewport = RectF()

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

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
        canvas.drawViewportView(viewport, clearPaint)
    }

    /**
     * @param viewport focus window rectangle on canvas
     * @param clearPaint paint for removing color in custom area of canvas
     */
    protected abstract fun Canvas.drawViewportView(viewport: RectF, clearPaint: Paint)
}

class OvalOverlay(context: Context) : OverlayView(context) {

    override fun Canvas.drawViewportView(viewport: RectF, clearPaint: Paint) {
        drawOval(viewport, clearPaint)
    }
}

class RectOverlay(context: Context) : OverlayView(context) {

    override fun Canvas.drawViewportView(viewport: RectF, clearPaint: Paint) {
        drawRect(viewport, clearPaint)
    }
}

@IntDef(SHAPE_OVAL, SHAPE_RECT)
@Retention(AnnotationRetention.SOURCE)
annotation class OverlayShape

const val SHAPE_CUSTOM = -1
const val SHAPE_OVAL = 0
const val SHAPE_RECT = 1