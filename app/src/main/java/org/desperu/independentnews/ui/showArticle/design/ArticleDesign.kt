package org.desperu.independentnews.ui.showArticle.design

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.View
import android.view.ViewAnimationUtils.createCircularReveal
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.os.postDelayed
import androidx.core.transition.doOnEnd
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.layout_fabs_menu.*
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.design.getValueAnimator
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.helpers.AsyncHelper.waitCondition
import org.desperu.independentnews.helpers.DialogHelper
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.ShowArticleTransition
import org.desperu.independentnews.ui.showArticle.fabsMenu.IconAnim
import org.desperu.independentnews.ui.showArticle.fragment.ArticleFragment
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClientInterface
import org.desperu.independentnews.utils.AUTO_REMOVE_PAUSE
import org.desperu.independentnews.utils.AUTO_REMOVE_PAUSE_DEFAULT
import org.desperu.independentnews.utils.REMOVE_PAUSED
import org.desperu.independentnews.utils.TEXT_SIZE_DEFAULT
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.desperu.independentnews.views.webview.NestedWebView
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import kotlin.math.sqrt

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
    private val actualUrl get() = getKoin().getOrNull<MyWebViewClientInterface>()?.actualUrl
    private val dialogHelper: DialogHelper = get()
    private val prefs: SharedPrefService = get()

    // FOR DESIGN
    private val sv: NestedScrollView by bindView(activity, R.id.article_scroll_view)
    private val nestedWebView: NestedWebView by bindView(activity, R.id.web_view)
    private val isArticleFrag = showArticleInterface.getCurrentFragment() is ArticleFragment
    private val scrollable: ViewGroup = if (isArticleFrag) sv else nestedWebView
    private val scrollBar: ProgressBar by bindView(activity, R.id.article_scroll_progress_bar)
    private val loadingAnimBar: ContentLoadingProgressBar by bindView(activity, R.id.content_loading_bar)
    private val loadingProgressBar: ProgressBar by bindView(activity, R.id.appbar_loading_progress_bar)

    // FOR DATA
    override var isFirstPage = true
    private var isLayoutDesigned = false
    override var isRefresh = false
    private val scrollHeight get() =
        if (isArticleFrag) sv.run { getChildAt(0).bottom - measuredHeight }
        else activity.webView.run { getFormattedContentHeight() - measuredHeight }
    override var scrollPosition = -1
    private var hasScroll = false
    private var dialogCount = 0

    init {
        configureKoinDependency()
        setupScrollListener()
        configureAppBar()
        configureSwipeRefresh()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure koin dependency for article design.
     */
    private fun configureKoinDependency() {
        get<ArticleDesignInterface> { parametersOf(this) }
    }

    /**
     * Setup the scroll listener on the nested scroll view.
     */
    private fun setupScrollListener() {
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

        updateScrollProgress(newScrollY)

        val isPaused = activity.viewModel.isPaused.get()
        if (isPaused && newScrollY == scrollHeight) showRemovePausedDialog()

        // Handle swipe refresh state
        activity.article_swipe_refresh.isEnabled = newScrollY == 0
    }

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
    // TRANSITION
    // --------------

    /**
     * Postpone the shared elements enter transition, because the shared elements
     * is an image downloaded from network.
     */
    internal fun postponeSceneTransition() = activity.supportPostponeEnterTransition()

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy.
     * Start custom enter animations together with scene transition.
     *
     * @param sharedElement the shared element to animate for the transition.
     */
    internal fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.doOnPreDraw {
            // Used here to be sure that the web content is full loaded.
            waitCondition(activity.lifecycleScope, 2000L, { hasScroll }) {
                activity.supportStartPostponedEnterTransition()
            }
        }
    }

    /**
     * Set custom activity transition, if the article id equal zero,
     * the show article activity was call from source detail, so set the specific transition.
     *
     * Else, and only for API >= LOLLIPOP, add shared element activity transition.
     *
     * Else, update views background.
     *
     * @param article       the current article shown in the web view.
     * @param bgDrawable    the background drawable used for the transition.
     */
    internal fun setActivityTransition(article: Article?, bgDrawable: Drawable?) {
        when {
            article?.id == 0L -> {
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                activity.webView.updateBackground()
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && bgDrawable != null -> {
                activity.window.setBackgroundDrawable(bgDrawable)
                activity.window.sharedElementEnterTransition = getActivityTransition(true)
                activity.window.sharedElementReturnTransition = getActivityTransition(false)
                // To be sure that the coordinator and containers have a background color set.
                activity.window.enterTransition.doOnEnd { activity.webView.updateBackground() }
            }

            else -> activity.webView.updateBackground() // Add from bottom anim ??
        }
    }

    /**
     * Returns the activity transitions for enter and return transitions.
     * We use move transition for the shared element (the article image),
     * and add a custom transition animation with [ShowArticleTransition].
     *
     * @param isEnter true if is enter transition, false if is return.
     *
     * @return return the activity transition set.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getActivityTransition(isEnter: Boolean): Transition {
        val transitionSet = TransitionSet()
        val moveTransition = TransitionInflater.from(activity)
            .inflateTransition(android.R.transition.move)

        transitionSet.addTransition(moveTransition)
        transitionSet.addTransition(ShowArticleTransition(isEnter))

        return transitionSet
    }

    // --------------
    // ANIMATION
    // --------------

    /**
     * Show the scroll view, with alpha animation, delayed after set the scroll Y position.
     */
    private fun showScrollView() {
        // To prepare the view before the animation
        if (scrollable.alpha != 0f) scrollable.alpha = 0f

        // Seems to not delay after image download... // TODO remove ????
//        activity.article_image.doOnPreDraw {
            waitCondition(activity.lifecycleScope, 2000L, { hasScroll }) {
                loadingAnimBar.hide()
                getSVAlphaAnim().start()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    getCircularReveal().start()
            }
//        }
    }

    /**
     * Returns the alpha animator for the scroll view.
     *
     * @return the alpha animator for the scroll view.
     */
    private fun getSVAlphaAnim(): ValueAnimator =
        getValueAnimator(
            true,
            300L,
            DecelerateInterpolator(),
            { progress -> scrollable.alpha = progress }
        )

    /**
     * Returns the circular reveal animator for the page transition.
     *
     * @return the circular reveal animator.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getCircularReveal(): Animator {
        val width = scrollable.width
        val height = scrollable.height

        //Simply use the diagonal of the view
        val finalRadius = sqrt((width * width + height * height).toFloat())

        val anim = createCircularReveal(
            scrollable,
            width / 2,
            height / 2,
            0f,
            finalRadius
        )

        anim.interpolator = FastOutSlowInInterpolator()
        anim.duration = 300L

        return anim
    }

    /**
     * Resume paused article to the saved scroll position, with drawable transition,
     * play to pause and smooth scroll to the saved position.
     * Delay this animation after the activity shared element enter transition.
     *
     * @param scrollPercent the scroll position to restore.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun resumePausedArticle(scrollPercent: Float) {
        activity.window.enterTransition.doOnEnd {
            // Play drawable animation
            val anim = IconAnim().getIconAnim(R.id.pause_to_play, null)
            anim.doOnEnd {
                // Restore scroll position
                val yPercent = scrollPercent * getTextRatio()
                val y = (scrollHeight * yPercent).toInt()
                sv.smoothScrollTo(scrollable.scrollX, y, 1000)
            }
            anim.start()
        }
    }

    // --------------
    // UI
    // --------------

    /**
     * Returns the text ratio of the current web view.
     *
     * @return the current text ratio.
     */
    override fun getTextRatio(): Float =
        activity.webView.settings.textZoom.toFloat() / TEXT_SIZE_DEFAULT.toFloat()

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
        scrollable.doOnLayout {
            val scrollY = when {
                y != null -> y
                scrollPosition > -1 -> scrollPosition
                else -> 0
            }

            scrollable.scrollTo(scrollable.scrollX, scrollY)
            scrollPosition = -1
            hasScroll = true
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
     * Handle layout design, used between page navigation to hide or show ui elements.
     *
     * @param progress the loading progress of the page.
     */
    override fun handleDesign(progress: Int) {
        when {
            progress == 0 -> {
                isLayoutDesigned = false
                hasScroll = false
//                sv.visibility = View.INVISIBLE
                scrollable.alpha = 0f

                scrollBar.apply { visibility = View.INVISIBLE; this.progress = 0 }
                loadingAnimBar.show()
                loadingProgressBar.visibility = View.VISIBLE
            }
            progress in 80..100 -> {
                if (!isLayoutDesigned && !isFirstPage) {
                    isLayoutDesigned = true
                    activity.article_swipe_refresh.isRefreshing = false
                    if (!isHtmlData(actualUrl.mToString())) scrollTo(null)
                    else showScrollView()
                }
            }
            progress > 100 -> {
                isLayoutDesigned = true
                isRefresh = false
                scrollBar.visibility = View.VISIBLE
                loadingProgressBar.visibility = View.INVISIBLE
                isFirstPage = false
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
        if (newProgress <= 80 && !isLayoutDesigned || newProgress > 80 && isLayoutDesigned)
            updateProgressBar(loadingProgressBar, newProgress)
    }

    /**
     * Update scroll progress bar, below the app bar, with the new scroll Y value.
     * If the given scroll Y is null, direct get the current value in the nested scroll view.
     *
     * @param scrollY the new scroll Y value.
     */
    private fun updateScrollProgress(scrollY: Int) {
        val newProgress = getScrollYPercent(scrollY) * 100
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
    private fun showRemovePausedDialog() {
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