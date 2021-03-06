package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.database.SourceWithData
import org.desperu.independentnews.ui.sources.SourcesInterface
import org.desperu.independentnews.ui.sources.fragment.SourceRouter
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * View Model witch provide data for source link.
 *
 * @property sourceWithData     the given source with data, (single source page) for this view model.
 * @property sourcesInterface   the interface that allow communication with the sources activity.
 * @property router             the source router interface witch provide user redirection.
 * @property sourcePage         the primary source pages of the source.
 *
 * @constructor Instantiates a new SourceLinkViewModel.
 *
 * @param sourceWithData        the given source with data, (single source page) for this view model to set.
 */
class SourceLinkViewModel(
    val sourceWithData: SourceWithData
) : ViewModel(), KoinComponent {

    // FOR DATA
    private val sourcesInterface: SourcesInterface get() = get()
    private val router: SourceRouter get() = get()
    val sourcePage = sourceWithData.sourcePages.getOrNull(0)

    /**
     * On click button listener, to handle user redirection.
     */
    val onClickButton = OnClickListener {
        router.openShowArticle(sourceWithData.toArticle(), sourcesInterface.isExpanded)
    }
}