package org.desperu.independentnews.ui.showArticle.webClient

import android.webkit.JavascriptInterface
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.layout_fabs_menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface

/**
 * Constant value for decal the focused line in the web view from the top of the visible view.
 */
const val SCROLL_DECAL = 50

/**
 * Javascript interface to communicate with the web view page code.
 *
 * @constructor Instantiate a new JavaScriptInterface.
 *
 * @param articleInterface the interface of the show article activity to set.
 */
class JavaScriptInterface(articleInterface: ShowArticleInterface) {

    // FOR DATA
    private val activity = articleInterface.activity

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

            val isToNotes = y / contentHeight > 0.9
            val decal = if (isToNotes) 0 else SCROLL_DECAL
            val scrollY = y * realHeight / contentHeight + articleDataHeight + decal

            activity.article_scroll_view.smoothScrollTo(0, scrollY, 1000)
            if (isToNotes) activity.fabs_menu.hide()
        }
    }
}