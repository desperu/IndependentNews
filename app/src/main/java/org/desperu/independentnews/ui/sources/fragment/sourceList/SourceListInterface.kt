package org.desperu.independentnews.ui.sources.fragment.sourceList

import android.view.View

/**
 * Interface that's allow communication with it's fragment.
 */
interface SourceListInterface {

    /**
     * Return the source list adapter instance.
     * @return the source list adapter instance.
     */
    fun getRecyclerAdapter(): SourceListAdapter?

    /**
     * Update shared element transition name.
     *
     * @param itemPosition      the position of the item that contains the shared elements.
     * @param sharedElements    the shared elements to animate during transition.
     */
    fun updateTransitionName(itemPosition: Int, vararg sharedElements: View)
}