package org.desperu.independentnews.views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.doOnNextLayout
import org.desperu.independentnews.extension.design.setScale
import kotlin.math.min

/**
 * A custom [GestureImageView] that scale image drawable to full screen on next layout request.
 * To properly work, it's needed to set specific xml attribute values :
 *
 * - layout_width="wrap_content"
 * - layout_height="wrap_content"
 * - layout_gravity="center"
 * - scaleType="centerInside"
 */
class GestureImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr) {

    // FOR DATA
    internal var scaleFactor = 1.0f

    /**
     * Scale the image drawable to full screen. Adapt the largest side to match the screen size,
     * by performing a scale and maintain the image's aspect ratio
     */
    internal fun scaleToFullScreen() {
        doOnNextLayout {
            // Get screen size
            val root = parent as View
            val screenWidth = (root.width).toFloat()
            val screenHeight = (root.bottom).toFloat()

            // Get Image Drawable size
            val rect = run { Rect().apply(::getHitRect) }
            val width = (rect.right - rect.left).toFloat()
            val height = (rect.bottom - rect.top).toFloat()

            // Scale the image so that the largest side match the screen size
            scaleFactor = min(screenWidth / width, screenHeight / height)
            setScale(scaleFactor)
        }
    }
}