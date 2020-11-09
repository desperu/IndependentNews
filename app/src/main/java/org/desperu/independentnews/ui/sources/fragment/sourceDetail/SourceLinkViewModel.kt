package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceRouter
import org.desperu.independentnews.utils.LOCAL_LINK
import org.desperu.independentnews.utils.WEB_LINK
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * View Model witch provide data for source link.
 *
 * @property sourcePage     the given source page for this view model.
 * @property type           the type of the button link to set.
 * @property router         the source router interface witch provide user redirection.
 *
 * @constructor Instantiates a new SourceLinkViewModel.
 *
 * @param sourcePage        the given source page for this view model to set.
 * @param type              the type of the button link to set.
 */
class SourceLinkViewModel(
    val sourcePage: SourcePage,
    private val type: Int
) : ViewModel(), KoinComponent {

    // FOR DATA
    private val router: SourceRouter = get()

    /**
     * On click button listener, to handle user redirection.
     */
    val onClickButton = OnClickListener {
        when (type) {
            LOCAL_LINK -> router.openShowArticle(sourcePage)
            WEB_LINK -> router.openShowArticle(switchToWebUrl(sourcePage))
        }
    }

    /**
     * Switch source page to web url use.
     *
     * @param sourcePage the source to switch.
     *
     * @return the source page switched to web url.
     */
    private fun switchToWebUrl(sourcePage: SourcePage): SourcePage {
        val url = sourcePage.url
        sourcePage.body = url
        return sourcePage
    }
}