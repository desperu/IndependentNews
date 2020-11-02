package org.desperu.independentnews.extension

import android.os.Build
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindDimen
import org.desperu.independentnews.ui.sources.SourcesActivity
import org.desperu.independentnews.ui.sources.fragment.sourceList.RecyclerViewAdapter
import org.desperu.independentnews.utils.BASTAMAG
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

/**
 * Set the adapter of the recycler view.
 * @param adapter the adapter to set for the recycler view.
 */
@BindingAdapter("adapter")
fun RecyclerView.myAdapter(adapter: RecyclerViewAdapter?) {
    this.adapter = adapter
}

/**
 * Update transition name with the given position of the item.
 */
@BindingAdapter("updateTransitionName")
fun View.updateTransitionName(position: Int?) { // TODO use string and set in view model with service/Ressources
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && position != null) {
        val transitionName = context.getString(R.string.animation_source_list_to_detail) + position
        setTransitionName(transitionName)
    }
//    (context as SourcesActivity)
}

/**
 * Set the background color depends of the source.
 * @param color the color to set for the background.
 */
@Suppress("Deprecation")
@BindingAdapter("myBackgroundColor")
fun ImageView.myBackgroundColor(color: Int?) {
    if (color != null && color != 0) {
        setBackgroundColor(resources.getColor(color))
    }
}

/**
 * Set the background resource of the view, depends of the enabled value.
 * @param enabled true if the source is enabled.
 */
@BindingAdapter("myBackground")
fun View.myBackground(enabled: Boolean?) {
    setBackgroundResource(
        if (enabled != null && !enabled) R.drawable.source_border_disabled
        else R.drawable.source_border_enabled
    )
}

/**
 * Set the padding of the view, depends of the source.
 * @param sourceName the name of the source.
 */
@BindingAdapter("myPadding")
fun View.myPadding(sourceName: String?) {
    setPadding(
        bindDimen(
            if (sourceName == BASTAMAG) R.dimen.default_margin
            else R.dimen.default_little_margin
        ).value.toInt()
    )
}

/**
 * Set the text and the background resource of the button, depends of the enabled value.
 * @param enabled true if the source is enabled.
 */
@BindingAdapter("myButton")
fun Button.myButton(enabled: Boolean?) {
    if (enabled != null && enabled) {
        text = resources.getString(R.string.fragment_source_detail_button_disable)
        setBackgroundResource(R.drawable.source_button_disabled)
    } else {
        text = resources.getString(R.string.fragment_source_detail_button_enable)
        setBackgroundResource(R.drawable.source_button_enabled)
    }
}