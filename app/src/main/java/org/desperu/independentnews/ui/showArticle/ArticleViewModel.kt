package org.desperu.independentnews.ui.showArticle

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.repositories.database.CssRepository
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * View Model witch provide data for Show Article Activity.
 *
 * @property article        the article object with contains data.
 * @property router         the image router which provide user redirection.
 * @property cssRepository  the repository that allow access for css from the database.
 *
 * @constructor Instantiates a new ArticleViewModel.
 *
 * @param article           the article object with contains data to set.
 * @param router            the image router which provide user redirection to set.
 */
class ArticleViewModel(
    val article: Article,
    private val router: ImageRouter
): ViewModel(), KoinComponent {

    // FOR DATA
    private val cssRepository: CssRepository = get()

    /**
     * On click image listener.
     */
    val onClickImage = OnClickListener { router.openShowImages(arrayListOf(article.imageUrl)) }

    /**
     * Returns the css of the current article.
     *
     * @return the css of the current article.
     */
    internal suspend fun getCss(): Css = withContext(Dispatchers.IO) {
        return@withContext cssRepository.getCssStyle(article.cssUrl)
    }
}