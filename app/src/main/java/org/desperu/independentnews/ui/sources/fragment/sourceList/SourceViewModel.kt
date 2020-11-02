package org.desperu.independentnews.ui.sources.fragment.sourceList

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.Source

/**
 * View Model witch provide data for source.
 *
 * @property source         the given source data for this view model.
 * @property itemPosition   the position of the source item in the recycler view.
 * @property router         the source router interface witch provide user redirection.
 *
 * @constructor Instantiates a new SourceViewModel.
 *
 * @param source            the given source data for this view model.
 * @param itemPosition      the position of the source item in the recycler view to set.
 * @param router            the source router interface witch provide user redirection to set.
 */
class SourceViewModel(
    val source: Source,
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
        router.showSourceDetail(source, it, itemPosition)
    }
}