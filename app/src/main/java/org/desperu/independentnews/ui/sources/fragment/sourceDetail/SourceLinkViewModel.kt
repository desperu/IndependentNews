package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceRouter

/**
 * View Model witch provide data for source link.
 *
 * @property sourcePage         the given source page for this view model.
 * @property router             the source router interface witch provide user redirection.
 *
 * @constructor Instantiates a new SourceLinkViewModel.
 *
 * @param sourcePage            the given source page for this view model.
 * @param router                the source router interface witch provide user redirection to set.
 */
class SourceLinkViewModel(
    val sourcePage: SourcePage,
    private val router: SourceRouter
) : ViewModel() {

    /**
     * On click link listener, to handle user redirection.
     */
    val onClickLink = OnClickListener {
        router.openShowArticle(sourcePage)
    }
}