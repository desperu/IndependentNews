package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceRouter
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * View Model witch provide data for source link.
 *
 * @property sourcePage     the given source page for this view model.
 * @property router         the source router interface witch provide user redirection.
 *
 * @constructor Instantiates a new SourceLinkViewModel.
 *
 * @param sourcePage        the given source page for this view model to set.
 */
class SourceLinkViewModel(
    val sourcePage: SourcePage
) : ViewModel(), KoinComponent {

    // FOR DATA
    private val router: SourceRouter = get()

    /**
     * On click button listener, to handle user redirection.
     */
    val onClickButton = OnClickListener {
        router.openShowArticle(sourcePage)
    }
}