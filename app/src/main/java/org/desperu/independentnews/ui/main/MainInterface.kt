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
     * Return the scale down animator for the recycler view of article list.
     * @return the scale down animator for the recycler view of article list.
     */
    fun getAdapterScaleDownAnimator(isScaledDown: Boolean): ValueAnimator?

    /**
     * Apply selected filters to the current article list.
     * @param selectedMap the map of selected filters to apply.
     */
    fun filterList(selectedMap: Map<Int, MutableList<String>>)
}