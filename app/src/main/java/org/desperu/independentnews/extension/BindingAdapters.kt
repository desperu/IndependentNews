package org.desperu.independentnews.extension

import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import org.desperu.independentnews.R
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl
import org.desperu.independentnews.utils.Utils.millisToString

/**
 * Set the published date in millis to string format.
 * @param millis the time in millis to set.
 */
@BindingAdapter("setDate")
fun TextView.setPublishedDate(millis: Long?) {
    text = if (millis != null && millis != 0L) millisToString(millis) else ""
}

@BindingAdapter("setInt")
fun TextView.setInt(value: Int?) {
    text = value?.toString() ?: ""
}

/**
 * Load the image with the url into the image view.
 * @param imageUrl the image url to load.
 */
@BindingAdapter("setImage")
fun ImageView.setImage(imageUrl: String?) {
    val isItem = tag == "item_article"
    val isNotNull = !imageUrl.isNullOrBlank() && getPageNameFromUrl(imageUrl) != "null"
    val image: Any? = if (isNotNull) imageUrl else R.drawable.no_image

    if (isItem || isNotNull)
        Glide.with(this).load(image).into(this)
    else
        visibility = View.GONE
}

/**
 * Load the image with the url into the image view.
 * @param imageId the unique identifier of the image to load.
 */
@BindingAdapter("setImage")
fun ImageView.setImage(imageId: Int?) {
    Glide.with(this)
        .load(if (imageId != null && imageId != 0) imageId else R.drawable.no_image)
        .into(this)
}

/**
 * Set the article html code to load in the web view.
 * @param article the article html code to load.
 */
@BindingAdapter("setArticle")
fun WebView.setArticle(article: String?) {
    article?.let { loadData(it, "text/html; charset=UTF-8", null) }
}