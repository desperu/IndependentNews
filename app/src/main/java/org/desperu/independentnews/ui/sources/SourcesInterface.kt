package org.desperu.independentnews.ui.sources

import android.view.View
import org.desperu.independentnews.models.Source

/**
 * Interface that's allow communication with it's activity.
 */
interface SourcesInterface {

    /**
     * Redirects the user to the SourcesDetailFragment to show sources detail.
     *
     * @param source the source to show in the fragment.
     * @param imageView the image view to animate.
     * @param itemPosition the position of the source item in the recycler view.
     */
    fun showSourceDetail(source: Source, imageView: View, itemPosition: Int)
}