package org.desperu.independentnews.ui.showArticle.webClient

import android.webkit.JavascriptInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.fragment_source_detail.*
import kotlinx.android.synthetic.main.layout_fabs_menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The javascript interface name, to call android function.
 */
const val JS_INTERFACE_NAME = "AndroidFunction"

/**
 * Constant value for decal the focused line in the web view from the top of the visible view.
 */
const val SCROLL_DECAL = 100

/**
 * Javascript interface to communicate with the web view page code.
 *
 * @property activity the activity that owns the web view.
 *
 * @constructor Instantiate a new JavaScriptInterface.
 *
 * @param activity the activity that owns the web view to set.
 */
class JavaScriptInterface(private val activity: AppCompatActivity) {

    /**
     * Web scroll to, called from javascript, through the javascript interface.
     * Convert the web view element Y value to real Y value,
     * to properly scroll the nested scroll view that contains the web view,
     * to the element Y position.
     *
     * @param y the web view element top Y value.
     */
    @JavascriptInterface
    fun webScrollTo(y: Int) {
        activity.lifecycleScope.launch(Dispatchers.Main) {
            val contentHeight = activity.web_view.contentHeight
            val realHeight = activity.web_view.height
            val articleDataHeight = activity.article_data_container.measuredHeight

            val ratio = y.toFloat() / contentHeight.toFloat()
            val isToNotes = ratio > 0.9f
            val scrollY = y * realHeight / contentHeight + articleDataHeight - SCROLL_DECAL

            activity.article_scroll_view.smoothScrollTo(0, scrollY, 1000)
            if (isToNotes) activity.fabs_menu.hide()
        }
    }

    /**
     * On page show, web view event, called after page content finish loading.
     */
    @JavascriptInterface
    fun onPageShow() {
        val webView = activity.web_view ?: activity.source_detail_web_view
        webView.onPageShow = true
    }
}