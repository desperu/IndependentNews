package org.desperu.independentnews.ui.main

import android.animation.ValueAnimator
import androidx.lifecycle.LifecycleCoroutineScope

/**
 * Interface to allow communications with Main Activity.
 */
interface MainInterface {

    /**
     * Get the fragment key value.
     */
    fun getFragmentKey(): Int

    /**
     * Set fragment key value.
     * @param fragmentKey the fragment key value to set.
     */
    fun setFragmentKey(fragmentKey: Int)

    /**
     * The main life cycle scope to execute block with coroutine support.
     */
    val mainLifecycleScope: LifecycleCoroutineScope

    /**
     * Refresh data for the application, fetch data from Rss and Web, and persist them
     * into the database.
     */
    fun refreshData()

    /**
     * Apply selected filters to the current article list.
     * @param selectedMap the map of selected filters to apply.
     * @param isFiltered true if apply filters to the list, false otherwise.
     */
    fun filterList(selectedMap: Map<Int, MutableList<String>>, isFiltered: Boolean)

    /**
     * Show new downloaded articles.
     */
    fun showNewArticles()

    /**
     * Update app bar on touch listener, used to finish app bar anim.
     */
    fun updateAppBarOnTouch()

    /**
     * Show or hide filter motion, depends of toShow value.
     * @param toShow true to show filter motion, false to hide.
     */
    fun showFilterMotion(toShow: Boolean)

    /**
     * Return the scale down animator for the recycler view of article list.
     * @return the scale down animator for the recycler view of article list.
     */
    fun getAdapterScaleDownAnimator(isScaledDown: Boolean): ValueAnimator?

    /**
     * Update filters motion state adapter state, when switch fragment.
     * @param isFiltered true if the adapter is filtered, false otherwise.
     */

    fun updateFiltersMotionState(isFiltered: Boolean)
}