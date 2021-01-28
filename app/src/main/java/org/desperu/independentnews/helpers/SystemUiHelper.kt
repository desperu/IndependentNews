package org.desperu.independentnews.helpers

import androidx.appcompat.app.AppCompatActivity

/**
 * SystemUiHelper witch provide functions for system ui.
 */
interface SystemUiHelper {

    /**
     * Set windows flag for the activity.
     *
     * @param flags the window flags to set.
     */
    fun setWindowFlag(flags: Int)

    /**
     * Set decor system ui visibility for the given activity.
     *
     * @param flags the decor system ui visibility flags to set.
     */
    fun setDecorUiVisibility(flags: Int)

    /**
     * Remove windows flags for the activity.
     *
     * @param flags the window flags to remove.
     */
    fun removeWindowFlag(flags: Int)

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
 * @property activity the Activity instance used to provide system ui helper functions.
 *
 * @constructor Instantiate a new SystemUiHelperImpl.
 *
 * @param activity the Activity instance used to provide system ui helper functions to set.
 */
@Suppress("Deprecation")
class SystemUiHelperImpl(private val activity: AppCompatActivity) : SystemUiHelper {

    /**
     * Set windows flag for the activity.
     *
     * @param flags the window flags to set.
     */
    override fun setWindowFlag(flags: Int) { activity.window.addFlags(flags) }

    /**
     * Set decor system ui visibility for the activity.
     *
     * @param flags the decor system ui visibility flags to set.
     */
    override fun setDecorUiVisibility(flags: Int) {
        activity.window.decorView.systemUiVisibility = flags
    }

    /**
     * Remove windows flags for the activity.
     *
     * @param flags the window flags to remove.
     */
    override fun removeWindowFlag(flags: Int) { activity.window.clearFlags(flags) }

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