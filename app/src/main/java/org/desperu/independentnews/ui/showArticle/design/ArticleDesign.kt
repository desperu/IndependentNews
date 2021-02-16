package org.desperu.independentnews.ui.showArticle.design

import android.os.Build
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.postOnAnimationDelayed
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.activity_show_article.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.desperu.independentnews.R
import org.desperu.independentnews.anim.AnimHelper.animatedValue
import org.desperu.independentnews.anim.AnimHelper.fromSideAnimator
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.design.getValueAnimator
import org.desperu.independentnews.extension.design.setScale
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClientInterface
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.parameter.parametersOf

class ArticleDesign : ArticleDesignInterface, KoinComponent {

    // FOR COMMUNICATION
    private val activity = get<ShowArticleInterface>().activity
    private val actualUrl get() = getKoin().getOrNull<MyWebViewClientInterface>()?.actualUrl

    // FOR DESIGN
    private val sv: NestedScrollView by bindView(activity, R.id.article_scroll_view)
    private val scrollBar: ProgressBar by bindView(activity, R.id.article_scroll_progress_bar)
    private val loadingAnimBar: ContentLoadingProgressBar by bindView(activity, R.id.article_loading_progress_bar)
    private val loadingProgressBar: ProgressBar by bindView(activity, R.id.appbar_loading_progress_bar)

    // FOR DATA
    override var scrollPosition = -1
    private var isLayoutDesigned = false
    private var hasScroll = false
    private var isFirstPage = true

    init {
        configureKoinDependency()
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

    // --------------
    // ANIMATION
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
        sharedElement.doOnPreDraw { activity.supportStartPostponedEnterTransition() }
    }

    /**
     * Configure views animation when activity appear (enter animation).
     *
     * @param articleId the unique identifier of the article.
     */
    internal fun configureViewAnimations(articleId: Long?) {
        if (articleId != 0L) {
            activity.apply {
                val animator =
                    getValueAnimator(
                        true,
                        resources.getInteger(R.integer.enter_anim_duration).toLong(),
                        DecelerateInterpolator(),
                        { progress ->

                            article_source_name.apply { // From left
                                translationX = animatedValue(-right, progress)
                                rootView.article_source_image.translationX = translationX // to sync with name anim
                            }
                            fromSideAnimator(
                                listOf(article_subtitle, article_date),
                                progress,
                                false
                            )
                            fromSideAnimator(listOf(article_author), progress, true)
                            article_title.setScale(progress)
                            web_view.apply {
                                alpha = (progress - 0.65f) / 0.35f // (progress - 0.8f) / 0.2f // not shown because css update
                                translationY = animatedValue(
                                    sv.bottom - web_view.top,
                                    progress
                                )//100.dp - 100.dp * progress
                            }
//                        article_root_view.alpha = progress // create anim mistake
                        }
                    )

                article_image.postOnAnimation { animator.start() }
                article_image.postOnAnimationDelayed(animator.duration * 2) { clearAnimations() }
            }

        }
    }

    /**
     * Clear all animated value for each views. Needed to prevent ui mistake,
     * when not play anim until it's end.
     */
    private fun clearAnimations() {
        activity.apply {
            val views = listOf(article_source_image, article_source_name, article_subtitle, article_date, article_author)
            views.forEach { it.translationX = 0f }
            article_title.setScale(1f)
            web_view.apply { alpha = 1f; translationY = 0f }
        }
    }

    /**
     * Set custom activity transition, only for source detail to source page transition.
     *
     * @param articleId the unique identifier of the article.
     */
    internal fun setActivityTransition(articleId: Long?) {
        if (articleId == 0L)
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

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
            { progressVal -> sv.alpha = progressVal }
        )

        activity.article_image.doOnPreDraw {
            val startAnim = activity.lifecycleScope.async(Dispatchers.Main) {
                do { delay(50) } while (!hasScroll)
                anim.start()
            }

            startAnim[startAnim.key]
        }

    }

    // --------------
    // UI
    // --------------

    /**
     * Setup progress bar with scroll view scroll position.
     */
    internal fun setupProgressBarWithScrollView() {
        var svScrollY = 0
        val setup = {
            svScrollY = if (svScrollY != 0) svScrollY else sv.scrollY
            val scrollHeight = sv.getChildAt(0).bottom - sv.measuredHeight
            val progress = (svScrollY.toFloat() / scrollHeight.toFloat()) * 100f

            // Use animation for API >= Nougat (24)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                scrollBar.setProgress(progress.toInt(), true)
            else
                scrollBar.progress = progress.toInt()
        }
        // TODO on scroll video should pause ...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            sv.setOnScrollChangeListener { _, _, scrollY, _, _ -> svScrollY = scrollY; setup() }
        else
            sv.viewTreeObserver.addOnScrollChangedListener { svScrollY = 0; setup() }
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
     * Update loading progress bar, in the app bar, with the new progress value.
     *
     * @param newProgress the new progress value.
     */
    override fun updateProgress(newProgress: Int) {
        if (newProgress <= 80 && !isLayoutDesigned || newProgress > 80 && isLayoutDesigned)
            loadingProgressBar.progress = newProgress
    }
}