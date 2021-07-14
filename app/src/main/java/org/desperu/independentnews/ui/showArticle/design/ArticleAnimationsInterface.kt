package org.desperu.independentnews.ui.showArticle.design

/**
 * Interface to allow communications with Article Animations.
 */
interface ArticleAnimationsInterface {

    /**
     * Show the scroll view, with alpha animation, delayed after set the scroll Y position.
     */
    fun showScrollView()

    /**
     * Resume paused article to the saved scroll position, with drawable transition,
     * play to pause and smooth scroll to the saved position.
     * Delay this animation after the activity shared element enter transition.
     *
     * @param scrollPercent the scroll position to restore.
     */
    fun resumePausedArticle(scrollPercent: Float)
}