package org.desperu.independentnews.ui.sources.fragment.sourceList

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.database.SourceWithData
import org.desperu.independentnews.ui.sources.fragment.SourceRouter

/**
 * View Model witch provide data for source.
 *
 * @property sourceWithData     the given source with data for this view model.
 * @property itemPosition       the position of the source item in the recycler view.
 * @property router             the source router interface witch provide user redirection.
 *
 * @constructor Instantiates a new SourceViewModel.
 *
 * @param sourceWithData        the given source with data for this view model.
 * @param itemPosition          the position of the source item in the recycler view to set.
 * @param router                the source router interface witch provide user redirection to set.
 */
class SourceViewModel(
    val sourceWithData: SourceWithData,
    private val itemPosition: Int,
    private val router: SourceRouter
) : ViewModel() {

    // ------------
    // LISTENERS
    // ------------

    /**
     * On click image listener.
     */
    val onClickImage = OnClickListener {
        router.showSourceDetail(sourceWithData, it, itemPosition)
    }
}