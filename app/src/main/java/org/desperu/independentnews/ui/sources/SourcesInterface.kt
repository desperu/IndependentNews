package org.desperu.independentnews.ui.sources

import android.view.View
import org.desperu.independentnews.models.SourceWithData

/**
 * Interface that's allow communication with it's activity.
 */
interface SourcesInterface {

    /**
     * Redirects the user to the SourcesDetailFragment to show sources detail.
     *
     * @param sourceWithData the source with data to show in the fragment.
     * @param imageView the image view to animate.
     * @param itemPosition the position of the source item in the recycler view.
     */
    fun showSourceDetail(sourceWithData: SourceWithData, imageView: View, itemPosition: Int)
}