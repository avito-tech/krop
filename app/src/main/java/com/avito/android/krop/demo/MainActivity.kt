package com.avito.android.krop.demo

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.ViewFlipper
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.avito.android.krop.KropView
import com.avito.android.krop.OverlayView
import com.avito.android.krop.util.ScaleAfterRotationStyle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var cropContainer: ViewFlipper
    private lateinit var kropView: KropView
    private lateinit var customOverlay: OverlayView
    private lateinit var resultImage: ImageView
    private lateinit var inputRotationAngle: EditText
    private lateinit var inputOffset: SeekBar
    private lateinit var inputAspectX: SeekBar
    private lateinit var inputAspectY: SeekBar
    private lateinit var pickColorButton: Button
    private lateinit var inputOverlayColor: EditText
    private lateinit var overlayShape: RadioGroup

    private var uri: Uri = Uri.EMPTY

    private var target: KropTarget? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        tintStatusBarIcons(this, true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigation = findViewById(R.id.navigation)
        viewFlipper = findViewById(R.id.view_flipper)
        cropContainer = findViewById(R.id.crop_container)
        resultImage = findViewById(R.id.result_image)

        inputOffset = findViewById(R.id.input_offset)
        inputAspectX = findViewById(R.id.input_aspect_x)
        inputAspectY = findViewById(R.id.input_aspect_y)
        pickColorButton = findViewById(R.id.pick_color_button)
        inputOverlayColor = findViewById(R.id.input_overlay_color)
        inputRotationAngle = findViewById(R.id.input_rotation_angle)
        overlayShape = findViewById(R.id.overlay_shape)
        customOverlay = findViewById(R.id.custom_overlay)

        kropView = findViewById(R.id.krop_view)

        pickColorButton.setOnClickListener {
            ColorPickerDialog()
                    .withColor(Color.parseColor(inputOverlayColor.text.toString()))
                    .withListener { _, color -> setInputOverlayColor(color) }
                    .show(supportFragmentManager, getString(R.string.select_color))
        }

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_crop -> showCrop()
                R.id.action_settings -> showSettings()
                R.id.action_result -> showPreview()
            }
            true
        }

        uri = savedInstanceState?.getParcelable(KEY_URI) ?: Uri.EMPTY

        if (savedInstanceState == null) {
            inputAspectX.progress = 1
            inputAspectY.progress = 1

            setInputOverlayColor(resources.getColor(R.color.default_overlay_color))

            kropView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val offset = resources.getDimension(R.dimen.default_offset)
                    inputOffset.progress = (offset / (kropView.measuredWidth / 2) * 100).toInt()
                    kropView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        } else {
            navigation.post {
                when (navigation.selectedItemId) {
                    R.id.action_crop -> showCrop()
                    R.id.action_settings -> showSettings()
                    R.id.action_result -> showPreview()
                }
            }
        }

        if (uri == Uri.EMPTY) {
            target = KropTarget(cropContainer, kropView, false)
            Picasso
                    .with(this)
                    .load("file:///android_asset/default_image.jpg")
                    .error(R.drawable.image)
                    .centerInside()
                    .resize(4000, 4000)
                    .config(Bitmap.Config.RGB_565)
                    .into(target)
        } else {
            loadUri(resetZoom = false)
        }
    }

    fun getBitmapFromAsset(context: Context, filePath: String): Bitmap {
        val assetManager = context.assets
        var input: InputStream? = null
        val bitmap: Bitmap
        try {
            input = assetManager.open(filePath)
            bitmap = BitmapFactory.decodeStream(input)
        } finally {
            input?.close()
        }
        return bitmap
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(KEY_URI, uri)
    }

    private fun setInputOverlayColor(color: Int) {
        val hexColor = colorToHex(color)
        inputOverlayColor.setText(hexColor)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        val menuRes = when (navigation.selectedItemId) {
            R.id.action_crop -> R.menu.main_crop
            R.id.action_settings -> R.menu.menu_settings
            else -> R.menu.main
        }
        inflater.inflate(menuRes, menu)
        for (c in 0 until menu.size()) {
            val item = menu.getItem(c)
            var drawable = item.icon
            drawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.color_accent))
            item.icon = drawable
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_select -> {
                selectPicture()
                return true
            }
            R.id.rotate -> {
                val angle = inputRotationAngle.text.toString().toFloatOrNull()
                if (angle != null) rotate(angle)
            }
            R.id.menu_apply -> {
                applySettings()
                showCrop()
                navigation.selectedItemId = R.id.action_crop
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectPicture() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), REQUEST_PICK_IMAGE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data?.data ?: return
                loadUri(resetZoom = true)
            }
        }
    }

    private fun loadUri(resetZoom: Boolean) {
        target = KropTarget(cropContainer, kropView, resetZoom)
        Picasso
                .with(this)
                .load(uri)
                .centerInside()
                .error(R.drawable.image)
                .resize(1024, 1024)
                .config(Bitmap.Config.RGB_565)
                .noFade()
                .into(target)
    }

    private fun applySettings() {
        try {
            val offset = (inputOffset.progress * (kropView.width / 2)) / 100
            val aspectX = inputAspectX.progress
            val aspectY = inputAspectY.progress
            val overlayColor = Color.parseColor(inputOverlayColor.text.toString())

            when(overlayShape.checkedRadioButtonId) {
                R.id.shape_oval -> kropView.applyOverlayShape(0)
                R.id.shape_rect -> kropView.applyOverlayShape(1)
                else -> kropView.applyOverlay(CustomOverlay(this))
            }
            kropView.apply {
                applyAspectRatio(aspectX, aspectY)
                applyOffset(offset)
                applyOverlayColor(overlayColor)
            }
        } catch (ignored: Throwable) {
            Snackbar.make(kropView, R.string.unable_to_apply_settings, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun rotate(angle: Float) {

        var fromAngle = 0f
        ValueAnimator.ofFloat(fromAngle, angle).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = ROTATION_DURATION_MS
            addUpdateListener { updatedAnimation ->
                val value = updatedAnimation.animatedValue as Float
                val diff = value - fromAngle
                fromAngle = value
                val animation = if (value == angle) ScaleAfterRotationStyle.ANIMATE else ScaleAfterRotationStyle.NONE
                kropView.rotateBy(diff, animation)
            }
            start()
        }
    }

    private fun showCrop() {
        viewFlipper.displayedChild = 0
        invalidateOptionsMenu()
    }

    private fun showSettings() {
        viewFlipper.displayedChild = 1
        invalidateOptionsMenu()
    }

    private fun showPreview() {
        val bitmap = kropView.getCroppedBitmap()
        resultImage.setImageBitmap(bitmap)
        viewFlipper.displayedChild = 2
        invalidateOptionsMenu()
    }

    private fun colorToHex(@ColorInt colorInt: Int) = "#" + Integer.toHexString(colorInt)

}

private const val REQUEST_PICK_IMAGE: Int = 42
private const val KEY_URI = "key_uri"
private const val ROTATION_DURATION_MS = 300L