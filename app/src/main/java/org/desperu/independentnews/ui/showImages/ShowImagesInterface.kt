package org.desperu.independentnews.ui.showImages

import android.view.MotionEvent
import android.view.View

interface ShowImagesInterface {

    /**
     * Dispatch the motion event to the view pager to consume it.
     *
     * @param ev the motion event action to dispatch.
     *
     * @return true if the event was consumed, false if not.
     */
    fun viewPagerOnTouchEvent(ev: MotionEvent?): Boolean

    /**
     * Show app bar.
     *
     * @param toShow true to show, false to hide.
     */
    fun showAppBar(toShow: Boolean)

    /**
     * On click back arrow.
     */
    fun onClickBackArrow(v: View)

    /**
     * Returns the current value of the page position.
     */
    fun getPosition(): Float
}