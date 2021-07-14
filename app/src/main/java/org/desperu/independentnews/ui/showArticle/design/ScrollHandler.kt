package org.desperu.independentnews.ui.showArticle.design

import android.os.Build
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.core.view.doOnLayout
import androidx.core.widget.NestedScrollView
import kotlinx.android.synthetic.main.activity_show_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.fragment.ArticleFragment
import org.desperu.independentnews.views.webview.NestedWebView
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Scroll handler, that help to handle scrollable for the fragments of this activity.
 *
 * @constructor Instantiate a new ScrollHandler.
 */
class ScrollHandler : ScrollHandlerInterface, KoinComponent {

    // FOR COMMUNICATION
    private val showArticleInterface = get<ShowArticleInterface>()
    private val activity = showArticleInterface.activity
    private val articleDesign: ArticleDesignInterface = get()

    // FOR DESIGN
    private val sv: NestedScrollView get() = activity.findViewById(R.id.article_scroll_view)
    private val nestedWebView: NestedWebView get() = activity.findViewById(R.id.web_view)
    private val isArticleFrag get() = showArticleInterface.getCurrentFragment() is ArticleFragment
    override val scrollable: ViewGroup get() = if (isArticleFrag) sv else nestedWebView

    // FOR DATA
    private val scrollHeight get() =
        if (isArticleFrag) sv.run { getChildAt(0).bottom - measuredHeight }
        else activity.webView.run { getFormattedContentHeight() - measuredHeight }
    override var scrollPosition = -1
    override var hasScroll = false

    // --------------
    // METHODS
    // --------------

    /**
     * Setup the scroll listener on the nested scroll view.
     */
    override fun setupScrollListener() {
        // TODO on scroll video should pause ...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            scrollable.setOnScrollChangeListener { _, _, scrollY, _, _ -> onScrollChanged(scrollY) }
        else
            scrollable.viewTreeObserver.addOnScrollChangedListener { onScrollChanged(null) }
    }

    /**
     * Scroll Changed Listener, update scroll progress bar,
     * handle paused article reach bottom,
     * and handle swipe refresh listener.
     *
     * @param scrollY the given scroll Y position.
     */
    private fun onScrollChanged(scrollY: Int?) {
        val newScrollY = scrollY ?: scrollable.scrollY

        articleDesign.updateScrollProgress(newScrollY)

        val isPaused = activity.viewModel.isPaused.get()
        if (isPaused && newScrollY == scrollHeight) articleDesign.showRemovePausedDialog()

        // Handle swipe refresh state
        activity.article_swipe_refresh.isEnabled = newScrollY == 0
    }

    /**
     * Returns the current scroll y value in percent.
     *
     * @param svScrollY the scroll y value.
     *
     * @return the current scroll y value in percent.
     */
    override fun getScrollYPercent(svScrollY: Int): Float {
        val scrollY = if (svScrollY != 0) svScrollY else scrollable.scrollY
        return (scrollY.toFloat() / scrollHeight.toFloat())
    }

    /**
     * Save the scroll position of the scroll view.
     */
    override fun saveScrollPosition() { scrollPosition = scrollable.scrollY }

    /**
     * Scroll vertically to the y position value, if null restore scroll position.
     *
     * @param y the y value, vertical axe, to scroll to.
     */
    override fun scrollTo(y: Int?) {
        Log.e(javaClass.enclosingMethod?.name, "Scroll To Scrollable : $scrollable")
        scrollable.doOnLayout {
            val scrollY = when {
                y != null -> y
                scrollPosition > -1 -> scrollPosition
                else -> 0
            }

            scrollable.scrollTo(scrollable.scrollX, scrollY)
            scrollPosition = -1
            hasScroll = true
            Log.e(javaClass.enclosingMethod?.name, "has scroll : $hasScroll")
        }
    }

    /**
     * Scroll vertically to the y percent value.
     *
     * @param yPercent the y percent value, vertical axe, to scroll to.
     */
    override fun scrollTo(@FloatRange(from = 0.0, to = 1.0) yPercent: Float) {
        scrollable.scrollTo(scrollable.scrollX, (scrollHeight * yPercent).toInt())
    }

    /**
     * Smooth scroll vertically to the scroll percent value.
     *
     * @param scrollPercent the scroll position to restore.
     */
    override fun smoothScrollTo(@FloatRange(from = 0.0, to = 1.0) scrollPercent: Float) {
        // Restore scroll position
        val yPercent = scrollPercent * activity.webView.getTextRatio()
        val y = (scrollHeight * yPercent).toInt()
        sv.smoothScrollTo(scrollable.scrollX, y, 1000)
    }
}