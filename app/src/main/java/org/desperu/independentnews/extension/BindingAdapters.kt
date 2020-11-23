package org.desperu.independentnews.extension

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindDimen
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.SourceWithData
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceListAdapter
import org.desperu.independentnews.utils.BASTAMAG
import org.desperu.independentnews.utils.SourcesUtils.getButtonLinkColor
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl
import org.desperu.independentnews.utils.Utils.millisToString
import org.desperu.independentnews.views.GestureImageView

/**
 * Show or hide view, depends of toShow value.
 * @param toShow true to show, false to hide view.
 */
@BindingAdapter("toShow")
fun View.toShow(toShow: Boolean?) {
    toShow?.let {
        visibility = if (toShow) View.VISIBLE else View.GONE
    }
}

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
    text = value.mToString()
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setSectionTheme")
fun TextView.setSectionTheme(article: Article?) {
    if (!article?.section.isNullOrBlank() || !article?.theme.isNullOrBlank())
        text = "${article?.section} > ${article?.theme}"
}

/**
 * Load the image with the url into the image view.
 * @param imageUrl the image url to load.
 */
@BindingAdapter("setImage")
fun ImageView.setImage(imageUrl: String?) {
    val isItem = tag == "item_article"
    val isFragImage = tag == "frag_image"
    val isNotNull = !imageUrl.isNullOrBlank() && getPageNameFromUrl(imageUrl) != "null"
    val image: Any? = if (isNotNull) imageUrl else R.drawable.no_image

    if (isItem || isNotNull)
        Glide.with(this)
            .load(image)
            .listener(getRequestListener(this, isFragImage))
            .override(Target.SIZE_ORIGINAL)
            .encodeQuality(100)
            .into(this)
    else
        visibility = View.GONE
}

/**
 * Returns the Glide Request Listener for the Glide request. Used to hide loading bar,
 * show no_image drawable on load failed, and resize image for fragment image.
 *
 * @param imageView the image view to set the image.
 * @param isFragImage true if is image of Show Image Fragment, false otherwise.
 *
 * @return the Glide Request Listener for the Glide request.
 */
private fun getRequestListener(imageView: ImageView, isFragImage: Boolean): RequestListener<Drawable> {
    val loadingBar = (imageView.parent as View).findViewById<ContentLoadingProgressBar>(R.id.progress_loading_bar)

    // TODO to put in another class / file
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
 * Set the html code to load in the web view.
 * @param html the html code to show in the web view.
 */
@BindingAdapter("setHtml")
fun WebView.setHtml(html: String?) {
    html?.let { loadData(it, "text/html; charset=UTF-8", null) }
}

/**
 * Set the article html code to load in the web view.
 * @param article the article to show in the web view.
 */
@BindingAdapter("setArticle")
fun WebView.setArticle(article: Article?) {
    article?.let {
        if (it.article.isNotBlank())
            loadData(it.article, "text/html; charset=UTF-8", null)
        else
            loadUrl(it.url)
    }
}

/**
 * Set the adapter of the recycler view.
 * @param adapter the adapter to set for the recycler view.
 */
@BindingAdapter("adapter")
fun RecyclerView.myAdapter(adapter: SourceListAdapter?) {
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
@BindingAdapter("myBackgroundColor")
fun ImageView.myBackgroundColor(color: Int?) {
    if (color != null && color != 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            backgroundTintList =
                ColorStateList.valueOf(ResourcesCompat.getColor(resources, color, null))
    }
}

/**
 * Set the background color depends of the source name and the single source page position.
 * @param sourceWithData the source with data, single page, to set for the background.
 */
@Suppress("Deprecation")
@BindingAdapter("myBackgroundColor")
fun Button.myBackgroundColor(sourceWithData: SourceWithData?) {
    sourceWithData?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            backgroundTintList = ColorStateList.valueOf(resources.getColor(getButtonLinkColor(it)))
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
            if (sourceName == BASTAMAG) R.dimen.default_large_margin
            else R.dimen.default_margin
        ).value.toInt()
    )
}

/**
 * Set the source drawable and the background color of the fab, depends of the enabled value.
 * @param enabled true if the source is enabled.
 */
@BindingAdapter("disableFab")
@Suppress("Deprecation")
fun FloatingActionButton.disableFab(enabled: Boolean?) {
    if (enabled != null && enabled) {
        setImageResource(R.drawable.ic_baseline_check_white_24)
        backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_green_light))
    } else {
        setImageResource(R.drawable.ic_close)
        supportImageTintList = ColorStateList.valueOf(resources.getColor(android.R.color.white))
        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
    }
}