package org.desperu.independentnews.extension

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import org.desperu.independentnews.R
import org.desperu.independentnews.utils.Utils.millisToString

/**
 * Set the published date in millis to string format.
 * @param millis the time in millis to set.
 */
@BindingAdapter("setDate")
fun TextView.setPublishedDate(millis: Long?) {
    text = if (millis != null) millisToString(millis) else "error"
}

/**
 * Load the image with the url into the image view.
 * @param imageUrl the image url to load.
 */
@BindingAdapter("setImage")
fun ImageView.setImage(imageUrl: String?){
    Glide.with(this)
         .load(if (!imageUrl.isNullOrBlank()) imageUrl else R.drawable.no_image)
         .into(this)
}