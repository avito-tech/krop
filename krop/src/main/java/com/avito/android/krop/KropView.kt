package com.avito.android.krop

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class KropView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val viewport = RectF()

    private var offset = 0
    private var aspectX = 1
    private var aspectY = 1
    private var overlayColor = Color.TRANSPARENT
    private var bitmap: Bitmap? = null

    private lateinit var imageView: ZoomableImageView
    private lateinit var overlayView: OverlayView

    var cropListener: CropListener? = null

    init {
        parseAttrs(attrs)
        initViews(context)
    }

    private fun parseAttrs(attrs: AttributeSet) {
        var arr: TypedArray? = null
        try {
            arr = context.obtainStyledAttributes(attrs, R.styleable.KropView)
            with(arr) {
                offset = getDimensionPixelOffset(R.styleable.KropView_krop_offset, offset)
                aspectX = getInteger(R.styleable.KropView_krop_aspectX, aspectX)
                aspectY = getInteger(R.styleable.KropView_krop_aspectY, aspectY)
                overlayColor = getColor(R.styleable.KropView_krop_overlayColor, overlayColor)
            }
        } finally {
            arr?.recycle()
        }
    }

    private fun initViews(context: Context) {
        imageView = ZoomableImageView(context)
        imageView.imageMoveListener = object : ZoomableImageView.ImageMoveListener {
            override fun onMove() {
                cropListener?.onCrop(getTransformation())
            }
        }
        addView(imageView)

        overlayView = OverlayView(context)
        overlayView.setBackgroundColor(overlayColor)
        addView(overlayView)
    }

    fun setZoom(scale: Float) {
        imageView.setZoom(scale)
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        imageView.setImageBitmap(bitmap)
    }

    fun getTransformation(): Transformation {
        val transformation = Transformation()
        val bitmap = bitmap ?: return transformation
        with(transformation) {
            size = SizeF(bitmap.width.toFloat(), bitmap.height.toFloat())
            crop = getCropRect()
        }
        return transformation
    }

    private fun getCropRect(): RectF {
        val rect = RectF()
        val bitmap = bitmap ?: return rect
        val bounds = imageView.getImageBounds()
        val multiplier = bounds.width() / bitmap.width.toFloat()
        with(rect) {
            left = (-bounds.left) / multiplier
            top = (-bounds.top) / multiplier
            right = (-bounds.left + viewport.width()) / multiplier
            bottom = (-bounds.top + viewport.height()) / multiplier
        }

        return rect
    }

    fun getCroppedBitmap(): Bitmap? {
        val rect = getCropRect()
        return Bitmap.createBitmap(
                bitmap,
                rect.left.toInt(),
                rect.top.toInt(),
                rect.width().toInt(),
                rect.height().toInt()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)

        calculateViewport(viewport, width, height, offset, aspectX, aspectY)

        imageView.viewport = viewport
        overlayView.viewport = viewport

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun applyOffset(offset: Int) {
        this.offset = offset
        imageView.requestLayout()
        imageView.resetZoom()
        invalidate()
    }

    fun applyAspectRatio(aspectX: Int, aspectY: Int) {
        this.aspectX = aspectX
        this.aspectY = aspectY
        imageView.requestLayout()
        imageView.resetZoom()
        invalidate()
    }

    fun applyOverlayColor(color: Int) {
        this.overlayColor = color
        overlayView.setBackgroundColor(overlayColor)
        invalidate()
    }

    override fun invalidate() {
        imageView.invalidate()
        overlayView.invalidate()
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(
                superState = superState,
                offset = offset,
                aspectX = aspectX,
                aspectY = aspectY,
                overlayColor = overlayColor,
                imageViewState = imageView.onSaveInstanceState()
        )
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            offset = state.offset
            aspectX = state.aspectX
            aspectY = state.aspectY
            overlayColor = state.overlayColor
            imageView.onRestoreInstanceState(state.imageViewState)
            overlayView.setBackgroundColor(overlayColor)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun calculateViewport(rect: RectF, width: Int, height: Int, offset: Int, aspectX: Int, aspectY: Int): RectF {
        val x: Float = width * 0.5f
        val y: Float = height * 0.5f

        val maxWidth: Float = width - offset * 2f
        val maxHeight: Float = height - offset * 2f

        val desiredWidth: Float
        val desiredHeight: Float

        when {
            maxWidth < maxHeight -> {
                desiredWidth = maxWidth
                desiredHeight = maxWidth * aspectY / aspectX
            }
            maxWidth > maxHeight -> {
                desiredWidth = maxHeight * aspectX / aspectY
                desiredHeight = maxHeight
            }
            else -> {
                desiredWidth = maxWidth
                desiredHeight = maxHeight
            }
        }

        var resultWidth = maxWidth
        var resultHeight = maxWidth * desiredHeight / desiredWidth
        if (resultHeight > maxHeight) {
            resultHeight = maxHeight
            resultWidth = desiredWidth * maxHeight / desiredHeight
        }

        with(rect) {
            left = x - resultWidth / 2
            top = y - resultHeight / 2
            right = x + resultWidth / 2
            bottom = y + resultHeight / 2
        }

        return rect
    }

    interface CropListener {

        fun onCrop(transformation: Transformation)

    }

    class SavedState : BaseSavedState {

        var offset: Int
        var aspectX: Int
        var aspectY: Int
        var overlayColor: Int
        val imageViewState: Parcelable

        constructor(superState: Parcelable,
                    offset: Int,
                    aspectX: Int,
                    aspectY: Int,
                    overlayColor: Int,
                    imageViewState: Parcelable) : super(superState) {
            this.offset = offset
            this.aspectX = aspectX
            this.aspectY = aspectY
            this.overlayColor = overlayColor
            this.imageViewState = imageViewState
        }

        constructor(source: Parcel) : super(source) {
            offset = source.readInt()
            aspectX = source.readInt()
            aspectY = source.readInt()
            overlayColor = source.readInt()
            imageViewState = source.readParcelable(Parcelable::class.java.classLoader)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls<SavedState?>(size)
            }
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            with(out) {
                writeInt(offset)
                writeInt(aspectX)
                writeInt(aspectY)
                writeInt(overlayColor)
                writeParcelable(imageViewState, flags)
            }
        }
    }
}
