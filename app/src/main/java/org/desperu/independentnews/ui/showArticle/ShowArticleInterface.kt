package org.desperu.independentnews.ui.showArticle

import android.view.View
import androidx.fragment.app.Fragment

/**
 * Interface to allow communications with Show Article Activity.
 */
interface ShowArticleInterface {

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy.
     *
     * @param sharedElement the shared element to animate for the transition.
     */
    fun scheduleStartPostponedTransition(sharedElement: View)

    /**
     * Return the given fragment position into the view pager.
     * @param fragment the fragment to find position into the view pager view.
     */
    fun getFragmentPosition(fragment: Fragment): Int?

    /**
     * Return the position of the clicked item into the recycler view.
     * @return the position of the clicked item into the recycler view.
     */
    fun getClickedItemPosition(): Int?
}