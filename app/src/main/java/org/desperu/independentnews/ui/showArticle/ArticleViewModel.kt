package org.desperu.independentnews.ui.showArticle

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.Article

/**
 * View Model witch provide data for Show Article Activity.
 *
 * @property article    the article object with contains data.
 * @property router     the image router which provide user redirection.
 *
 * @constructor Instantiates a new ArticleViewModel.
 *
 * @param article       the article object with contains data to set.
 * @param router        the image router which provide user redirection to set.
 */
class ArticleViewModel(
    val article: Article,
    private val router: ImageRouter
): ViewModel() {

    /**
     * On click image listener.
     */
    val onClickImage = OnClickListener { showImage(article.imageUrl) }

    /**
     * Show image of the given image url, open Show Image Activity.
     *
     * @param imageUrl the url of the image to show.
     */
    internal fun showImage(imageUrl: String) {
        router.openShowImages(imageUrl)
    }
}