package org.desperu.independentnews.ui.sources

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.Source

/**
 * View Model witch provide data for source.
 *
 * @property source     the given source data for this view model.
 * @property router     the source router interface witch provide user redirection.
 *
 * @constructor Instantiates a new SourceViewModel.
 *
 * @param source        the given source data for this view model.
 * @param router        the source router interface witch provide user redirection to set.
 */
class SourceViewModel(val source: Source,
                      private val router: SourceRouter
) : ViewModel() {

    // ------------
    // LISTENERS
    // ------------

    /**
     * On click image listener.
     */
    val onClickImage = OnClickListener {
        router.openShowArticle(source, it)
    }

}