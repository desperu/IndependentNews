package org.desperu.independentnews.helpers

import android.app.Activity

/**
 * SystemUiHelper witch provide functions for system ui.
 */
interface SystemUiHelper {

    /**
     * Set decor system ui visibility for the given activity.
     *
     * @param flags the decor system ui visibility flags to set.
     */
    fun setDecorUiVisibility(flags: Int)

    /**
     * Remove decor system ui visibility for the given activity.
     *
     * @param flags the decor system ui visibility flags to remove.
     */
    fun removeDecorUiFlag(flags: Int)

    /**
     * Set requested screen orientation for the given activity.
     *
     * @param flags the screen orientation flags to set.
     */
    fun setOrientation(flags: Int)
}

/**
 * Implementation of the SystemUiHelper which use an Activity instance
 * to provide helper functions for system ui navigation bars, orientation ...
 *
 * @constructor Instantiate a new SystemUiHelperImpl.
 *
 * @param activity the Activity instance used to provide system ui helper functions.
 */
class SystemUiHelperImpl(private val activity: Activity) : SystemUiHelper {// TODO in the good folder/place ??

    /**
     * Set decor system ui visibility for the activity.
     *
     * @param flags the decor system ui visibility flags to set.
     */
    override fun setDecorUiVisibility(flags: Int) {
        activity.window.decorView.systemUiVisibility = flags
    }

    /**
     * Remove decor system ui visibility for the activity.
     *
     * @param flags the decor system ui visibility flags to remove.
     */
    override fun removeDecorUiFlag(flags: Int) {
        activity.window.decorView.systemUiVisibility -= flags
    }

    /**
     * Set requested screen orientation for the activity.
     *
     * @param flags the screen orientation flags to set.
     */
    override fun setOrientation(flags: Int) {
        activity.requestedOrientation = flags
    }
}