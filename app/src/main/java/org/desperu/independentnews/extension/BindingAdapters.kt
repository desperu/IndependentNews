package org.desperu.independentnews.extension

import android.os.Build
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import org.desperu.independentnews.R
import org.desperu.independentnews.utils.Utils.millisToString

/** TODO use text month ???
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

/**
 * Set the article html code to load in the web view.
 * @param article the article html code to load.
 */
@BindingAdapter("setArticle")
fun WebView.setArticle(article: String?) {
    article?.let { loadData(it, "text/html; charset=UTF-8", null) }
}

/**
 * Update transition name with the given position of the item.
 */
@BindingAdapter("updateTransitionName")
fun View.updateTransitionName(position: Int?) { // TODO use string and set in view model with service/Ressources
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && position != null) {
        val transitionName = context.getString(R.string.animation_main_to_show_article) + position
        setTransitionName(transitionName)
    }
}