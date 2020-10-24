package org.desperu.independentnews.views

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

/**
 * A custom [WebView] that does not allow to scroll.
 * To correct scroll jump at the start of the web view.
 *
 * This [NoScrollWebView] should be placed in a [ScrollView] to allow the user to scroll
 * and show the content of this [NoScrollWebView].
 * Set the [isScrollContainer] xml attribute to true to better performances.
 */
class NoScrollWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

//    override fun overScrollBy(
//        deltaX: Int,
//        deltaY: Int,
//        scrollX: Int,
//        scrollY: Int,
//        scrollRangeX: Int,
//        scrollRangeY: Int,
//        maxOverScrollX: Int,
//        maxOverScrollY: Int,
//        isTouchEvent: Boolean
//    ): Boolean {
//        return false
//    }
//
//    override fun scrollTo(x: Int, y: Int) {}
//
//    override fun computeScroll() {}
}