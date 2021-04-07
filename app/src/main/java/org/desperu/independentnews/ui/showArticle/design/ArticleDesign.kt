package org.desperu.independentnews.ui.showArticle.design

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.View
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
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.layout_fabs_menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.design.getValueAnimator
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.helpers.DialogHelper
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.ShowArticleTransition
import org.desperu.independentnews.ui.showArticle.fabsMenu.IconAnim
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClientInterface
import org.desperu.independentnews.utils.AUTO_REMOVE_PAUSE
import org.desperu.independentnews.utils.AUTO_REMOVE_PAUSE_DEFAULT
import org.desperu.independentnews.utils.REMOVE_PAUSED
import org.desperu.independentnews.utils.TEXT_SIZE_DEFAULT
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.parameter.parametersOf

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
    private val activity = get<ShowArticleInterface>().activity
    private val actualUrl get() = getKoin().getOrNull<MyWebViewClientInterface>()?.actualUrl
    private val dialogHelper: DialogHelper = get()
    private val prefs: SharedPrefService = get()

    // FOR DESIGN
    private val sv: NestedScrollView by bindView(activity, R.id.article_scroll_view)
    private val scrollBar: ProgressBar by bindView(activity, R.id.article_scroll_progress_bar)
    private val loadingAnimBar: ContentLoadingProgressBar by bindView(activity, R.id.article_loading_progress_bar)
    private val loadingProgressBar: ProgressBar by bindView(activity, R.id.appbar_loading_progress_bar)

    // FOR DATA
    override var isFirstPage = true
    private var isLayoutDesigned = false
    private val scrollHeight get() = sv.getChildAt(0).bottom - sv.measuredHeight
    override var scrollPosition = -1
    private var hasScroll = false
    private var dialogCount = 0

    init {
        configureKoinDependency()
        setupScrollListener()
        configureAppBar()
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
            sv.setOnScrollChangeListener { _, _, scrollY, _, _ -> onScrollChanged(scrollY) }
        else
            sv.viewTreeObserver.addOnScrollChangedListener { onScrollChanged(null) }
    }

    /**
     * Scroll Changed Listener, update scroll progress bar
     * and handle paused article reach bottom.
     *
     * @param scrollY the given scroll Y position.
     */
    private fun onScrollChanged(scrollY: Int?) {
        val newScrollY = scrollY ?: sv.scrollY
        updateScrollProgress(newScrollY)

        val isPaused = activity.viewModel.isPaused.get()
        if (isPaused && newScrollY == scrollHeight) showRemovePausedDialog()
    }

    /**
     * Configure the app bar for special use, with full collapse and hide status bar.
     */
    private fun configureAppBar() {
        activity.appbar_container.tag = activity::class.java.simpleName
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
            waitHasScroll { activity.supportStartPostponedEnterTransition() }
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
        val body = article?.article ?: ""
        val sourceName = article?.source?.name ?: ""

        when {
            article?.id == 0L -> {
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                activity.web_view.updateBackground(body, sourceName)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && bgDrawable != null -> {
                activity.window.setBackgroundDrawable(bgDrawable)
                activity.window.sharedElementEnterTransition = getActivityTransition(true)
                activity.window.sharedElementReturnTransition = getActivityTransition(false)
            }

            else -> activity.web_view.updateBackground(body, sourceName) // Add from bottom anim ??
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
        if (sv.alpha != 0f) sv.alpha = 0f
        sv.visibility = View.VISIBLE

        val anim = getValueAnimator(
            true,
            300L,
            DecelerateInterpolator(),
            { progress -> sv.alpha = progress }
        )

        // Seems to not delay after image download...
        activity.article_image.doOnPreDraw { waitHasScroll { anim.start() } }
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
                sv.smoothScrollTo(sv.scrollX, y, 1000)
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
        activity.web_view.settings.textZoom.toFloat() / TEXT_SIZE_DEFAULT.toFloat()

    /**
     * Returns the current scroll y value in percent.
     *
     * @param svScrollY the scroll y value.
     *
     * @return the current scroll y value in percent.
     */
    override fun getScrollYPercent(svScrollY: Int): Float {
        val scrollY = if (svScrollY != 0) svScrollY else sv.scrollY
        return (scrollY.toFloat() / scrollHeight.toFloat())
    }

    /**
     * Save the scroll position of the scroll view.
     */
    override fun saveScrollPosition() { scrollPosition = sv.scrollY }

    /**
     * Scroll vertically to the y position value, if null restore scroll position.
     *
     * @param y the y value, vertical axe, to scroll to.
     */
    override fun scrollTo(y: Int?) {
        sv.doOnLayout {
            val scrollY = when {
                y != null -> y
                scrollPosition > -1 -> scrollPosition
                else -> 0
            }

            sv.scrollTo(sv.scrollX, scrollY)
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
        sv.scrollTo(sv.scrollX, (scrollHeight * yPercent).toInt())
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
                sv.visibility = View.INVISIBLE
                sv.alpha = 0f

                scrollBar.apply { visibility = View.INVISIBLE; this.progress = 0 }
                loadingAnimBar.apply { visibility = View.VISIBLE; show() }
                loadingProgressBar.visibility = View.VISIBLE
            }
            progress in 80..100 -> {
                if (!isLayoutDesigned && !isFirstPage) {
                    isLayoutDesigned = true
                    if (!isHtmlData(actualUrl.mToString())) scrollTo(null)
                    loadingAnimBar.hide()
                    showScrollView()
                }
            }
            progress > 100 -> {
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
    override fun hideArticleDataContainer(toHide: Boolean) {
        activity.article_data_container.visibility = if (toHide) View.GONE else View.VISIBLE
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
    internal fun showFabsMenu(toShow: Boolean, toDelay: Boolean = true) {
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

    // --------------
    // UTILS
    // --------------

    /**
     * Wait has scroll before invoke the given block.
     *
     * @param block the given block to invoke after has scroll.
     */
    private fun waitHasScroll(block: () -> Unit) {
        val waitHasScroll = activity.lifecycleScope.async(Dispatchers.Main) {
            do { delay(50) } while (!hasScroll)
            block()
        }

        waitHasScroll[waitHasScroll.key]
    }
}