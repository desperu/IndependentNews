package org.desperu.independentnews.anim

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View

/**
 * SystemUiHelp witch provide functions for system ui.
 */
object SystemUiHelper {// TODO in the good folder/place ??
                       //  must be called from activity only !!!!
                       //  create a service with an interface and koin !!!!

    /**
     * Show full system ui, status and navigation bar.
     *
     * @param activity the activity on which perform change.
     */
    internal fun showFullSystemUi(activity: Activity) =
        setDecorUiVisibility(activity, showFullSystemUi)

    /**
     * Hide system ui, status bar and navigation bar, sticky.
     *
     * @param activity the activity on which perform change.
     */
    internal fun hideSystemUi(activity: Activity) =
        setDecorUiVisibility(activity, hideSystemUi)

    /**
     * Hide full system ui, status bar and navigation bar, sticky.
     *
     * @param activity the activity on which perform change.
     */
    internal fun hideFullSystemUi(activity: Activity) =
        setDecorUiVisibility(activity, hideFullSystemUi)

    /**
     * Up navigation bar, set alpha to 1.0f, and status bar is shown.
     *
     * @param activity the activity on which perform change.
     */
    internal fun upNavAndStatusBar(activity: Activity) =
        removeDecorUiFlag(activity, lowNavAndStatusBar)

    /**
     * Set screen orientation to let user choose.
     *
     * @param activity the activity on which perform change.
     */
    internal fun setOrientationUser(activity: Activity) =
        setOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_USER)

    /**
     * Set screen orientation to force to landscape.
     *
     * @param activity the activity on which perform change.
     */
    internal fun setOrientationLandscape(activity: Activity) =
        setOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    // --------------
    // FLAGS
    // --------------

    /**
     * Show full system ui flag.
     */
    private const val showFullSystemUi = View.SYSTEM_UI_FLAG_VISIBLE

    /**
     * Hide system ui flags.
     */
    private const val hideSystemUi = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Should stabilize the system ui (not really test)
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Layout draw under status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Re hide status and/or navigation bar
                    or View.SYSTEM_UI_FLAG_LOW_PROFILE // Alpha 0.5 the navigation bar
            )

    /**
     * Hide full system ui flags.
     */
    private const val hideFullSystemUi = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Should stabilize the system ui (not really test)
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Layout draw under status bar
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE // Hide status and/or navigation once
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Re hide status and/or navigation bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hide navigation bar
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Layout draw under navigation bar
//                    or View.SYSTEM_UI_FLAG_LOW_PROFILE // Alpha 0.5 the navigation bar
            )

    /**
     * Low nav bar flag.
     */
    private const val lowNavAndStatusBar = (
            View.SYSTEM_UI_FLAG_LOW_PROFILE // Alpha 0.5 the navigation bar
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            )

    // --------------
    // UTILS
    // --------------

    /**
     * Set decor system ui visibility for the given activity.
     *
     * @param activity the activity on which perform change.
     * @param flags the decor system ui visibility flags to set.
     */
    private fun setDecorUiVisibility(activity: Activity, flags: Int) {
        activity.window.decorView.systemUiVisibility = flags
    }

    /**
     * Remove decor system ui visibility for the given activity.
     *
     * @param activity the activity on which perform change.
     * @param flags the decor system ui visibility flags to remove.
     */
    private fun removeDecorUiFlag(activity: Activity, flags: Int) {
        activity.window.decorView.systemUiVisibility -= flags
    }

    /**
     * Set requested screen orientation for the given activity.
     *
     * @param activity the activity on which perform change.
     * @param flags the screen orientation flags to set.
     */
    private fun setOrientation(activity: Activity, flags: Int) {
        activity.requestedOrientation = flags
    }
}