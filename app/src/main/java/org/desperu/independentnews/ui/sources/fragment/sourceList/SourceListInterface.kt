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
     * @param itemPosition the position of the item that contains the shared element.
     * @param image the image for which update it's transition name.
     */
    fun updateTransitionName(itemPosition: Int, image: View)
}