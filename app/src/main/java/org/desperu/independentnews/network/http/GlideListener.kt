package org.desperu.independentnews.network.http

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ContentLoadingProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import org.desperu.independentnews.R
import org.desperu.independentnews.views.GestureImageView

/**
 * Returns the Glide Request Listener for the Glide request. Used to hide loading bar,
 * show no_image drawable on load failed, and resize image for fragment image.
 *
 * @param imageView the image view to set the image.
 * @param isFragImage true if is image of Show Image Fragment, false otherwise.
 *
 * @return the Glide Request Listener for the Glide request.
 */
fun getRequestListener(imageView: ImageView, isFragImage: Boolean): RequestListener<Drawable> {
    val loadingBar = (imageView.parent as View).findViewById<ContentLoadingProgressBar>(R.id.content_loading_bar)

    return object : RequestListener<Drawable> {

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {

            loadingBar?.hide()
            imageView.background = ResourcesCompat
                .getDrawable(imageView.context.resources, R.drawable.no_image, null)

            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {

            if (isFragImage)
                target?.getSize { _, _ ->
                    (imageView as GestureImageView).scaleToFullScreen()
                    imageView.requestLayout()
                }
            loadingBar?.hide()

            return false
        }
    }
}