package org.desperu.independentnews.ui.showArticle.design

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.os.postDelayed
import androidx.core.transition.doOnEnd
import androidx.core.widget.ContentLoadingProgressBar
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.layout_fabs_menu.*
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.helpers.DialogHelper
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.utils.AUTO_REMOVE_PAUSE
import org.desperu.independentnews.utils.AUTO_REMOVE_PAUSE_DEFAULT
import org.desperu.independentnews.utils.REMOVE_PAUSED
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

/**
 * Article Design class used to handle User Interface.
 *
 * @property activity       the activity for which handle ui.
 * @property actualUrl      the actual url of the web view.
 * @property dialogHelper   the dialog helper interface access.
 * @property prefs          the shared preferences service access.
 *
 * @constructor Instantiate a new ArticleDesign.
 */
class ArticleDesign : ArticleDesignInterface, KoinComponent {

    // FOR COMMUNICATION
    private val showArticleInterface = get<ShowArticleInterface>()
    private val activity = showArticleInterface.activity
    private val actualUrl get() = showArticleInterface.fragmentInterface.mWebViewClient?.actualUrl
    private val dialogHelper: DialogHelper = get()
    private val prefs: SharedPrefService = get()

    private val scrollHandler: ScrollHandlerInterface by inject()
    private val articleAnimations: ArticleAnimations by inject()

    // FOR DESIGN
    private val scrollBar: ProgressBar by bindView(activity, R.id.article_scroll_progress_bar)
    private val loadingAnimBar: ContentLoadingProgressBar by bindView(activity, R.id.content_loading_bar)
    private val loadingProgressBar: ProgressBar by bindView(activity, R.id.appbar_loading_progress_bar)

    // FOR DATA
    override var isFirstPage = true
//    private var hasStarted = false
//    private var isLayoutDesigned = false
    override var isRefresh = false
    private var dialogCount = 0

    init {
        configureAppBar()
        configureSwipeRefresh()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure the app bar for special use, with full collapse and hide status bar.
     */
    private fun configureAppBar() {
        activity.appbar_container.tag = activity::class.java.simpleName
    }

    /**
     * Configure swipe refresh listener, re-set article or web page on refresh.
     */
    private fun configureSwipeRefresh() {
        activity.run {
            article_swipe_refresh.setOnRefreshListener {
                val article = viewModel.article.get()

                isRefresh = true // To handle navigation history

                if (article != null)
                    viewModel.article.apply { set(article); notifyChange() }
                else
                    webView.loadUrl(actualUrl ?: "")
            }
        }
    }

    // --------------
    // UI
    // --------------

    /**
     * Handle layout design, used between page navigation to hide or show ui elements.
     *
     * @param progress the loading progress of the page.
     */
    override fun handleDesign(progress: Int) {

//        if (progress == 0 && hasStarted) return
//        Log.w("Lock start", "is same ${(progress == 0 && hasStarted) == (progress == 0 && !isLayoutDesigned)}")
//        if (progress > 0 && !hasStarted && !isFirstPage) return
//        Log.w("Lock finish", "is same ${(progress > 0 && !hasStarted && !isFirstPage) == (progress > 0 && isLayoutDesigned)}")


        Log.e(javaClass.enclosingMethod?.name, "progress : $progress") // TODO to remove

        when(progress) {
            0 -> { // TODO hasStart hasFinished ????
//                hasStarted = true
//                isLayoutDesigned = false
                scrollHandler.hasScroll = false
//                sv.visibility = View.INVISIBLE
                scrollHandler.scrollable.alpha = 0f

                scrollBar.visibility = View.INVISIBLE
                loadingAnimBar.show()
                loadingProgressBar.visibility = View.VISIBLE
            }
            in 80..100 -> {
                if (!isFirstPage) {
//                if (!isLayoutDesigned && !isFirstPage) {
//                    isLayoutDesigned = true
//                    Log.e(javaClass.enclosingMethod?.name, "is Layout designed : $isLayoutDesigned") // TODO to remove
                    activity.article_swipe_refresh.isRefreshing = false
                    Log.e(javaClass.enclosingMethod?.name, "ActualUrl : $actualUrl")
                    if (!isHtmlData(actualUrl.mToString())) scrollHandler.scrollTo(null)
                    articleAnimations.showScrollView()
                }
            }
            101 -> {
//                hasStarted = false
//                isLayoutDesigned = true
                isRefresh = false
                isFirstPage = false

                scrollBar.apply { visibility = View.VISIBLE; this.progress = 0 }
                loadingProgressBar.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * Hide article data container, depends of toHide value.
     *
     * @param toHide true to hide data container, false to show.
     */
    override fun hideArticleDataContainer(toHide: Boolean) { // TODO to remove??? useless ???
        activity.article_data_container?.visibility = if (toHide) View.GONE else View.VISIBLE
    }

    /**
     * Update loading progress bar, in the app bar, with the new progress value.
     *
     * @param newProgress the new progress value.
     */
    override fun updateLoadingProgress(newProgress: Int) {
//        if (newProgress <= 80 && !isLayoutDesigned || newProgress > 80 && isLayoutDesigned)
            updateProgressBar(loadingProgressBar, newProgress)
    }

    /**
     * Update scroll progress bar, below the app bar, with the new scroll Y value.
     * If the given scroll Y is null, direct get the current value in the nested scroll view.
     *
     * @param scrollY the new scroll Y value.
     */
    override fun updateScrollProgress(scrollY: Int) {
        val newProgress = scrollHandler.getScrollYPercent(scrollY) * 100
        updateProgressBar(scrollBar, newProgress.toInt())
    }

    /**
     * Update progress bar, with the new progress value.
     * Use animation only for API >= NOUGAT.
     *
     * @param progressBar   the progress bar for which update progress.
     * @param newProgress   the new progress value.
     */
    private fun updateProgressBar(progressBar: ProgressBar, newProgress: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            progressBar.setProgress(newProgress, true)
        else
            progressBar.progress = newProgress
    }

    /**
     * Show or hide fabs menu, depends of to show value.
     *
     * @param toShow    true to show the fabs menu, false to hide.
     * @param toDelay   true to delay show fabs menu, false to do on transition end.
     */
    override fun showFabsMenu(toShow: Boolean, toDelay: Boolean) {
        val delay = activity.resources.getInteger(
            if (toDelay)
                android.R.integer.config_longAnimTime
            else
                android.R.integer.config_shortAnimTime
        )
        val show = {
            Handler(Looper.getMainLooper()).postDelayed(delay.toLong()) {
                activity.fabs_menu.show()
            }
        }

        if (toShow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (toDelay) show()
                else activity.window.enterTransition.doOnEnd { show() }
            } else
                show()
        } else
            activity.fabs_menu.hide()
    }

    /**
     * Show the remove pause dialog, when reach the bottom of a paused article.
     */
    override  fun showRemovePausedDialog() {
        // To prevent flood
        if (dialogCount >= 2) return
        dialogCount += 1

        val isAuto = prefs.getPrefs().getBoolean(AUTO_REMOVE_PAUSE, AUTO_REMOVE_PAUSE_DEFAULT)

        if (!isAuto)
            dialogHelper.showDialog(REMOVE_PAUSED)
        else
            activity.viewModel.updatePaused(0f)
    }
}