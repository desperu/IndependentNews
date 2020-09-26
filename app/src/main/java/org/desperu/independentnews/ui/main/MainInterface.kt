package org.desperu.independentnews.ui.main

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
}