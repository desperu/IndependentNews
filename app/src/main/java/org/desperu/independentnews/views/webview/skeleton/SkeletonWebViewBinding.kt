package org.desperu.independentnews.views.webview.skeleton

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.utils.SKELETON_CONTENT_LENGTH
import org.desperu.independentnews.utils.Utils.getRandomString

/**
 * Skeleton WebView Binding used to serialize View Model data for the skeleton web view.
 */
abstract class SkeletonWebViewBinding : ViewModel() {

    /**
     * Is Fetching state for the article to display, used to handle separately
     * the skeleton animation for the article data and the article web view content.
     */
    abstract val isFetching: ObservableBoolean

    /**
     * Is Loading state used to handle the skeleton animation for the article web view content.
     */
    abstract val isLoading: ObservableBoolean

    /**
     * The fake title for the skeleton web view.
     */
    abstract val title: String

    /**
     * The fake content for the skeleton web view.
     */
    val skeletonContent = getRandomString(SKELETON_CONTENT_LENGTH)

    /**
     * Skeleton animation duration.
     */
    val duration: Long = 250L
}