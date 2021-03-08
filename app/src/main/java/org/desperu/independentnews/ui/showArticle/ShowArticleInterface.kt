package org.desperu.independentnews.ui.showArticle

import org.desperu.independentnews.models.database.Article

/**
 * Interface to allow communications with Show Article Activity.
 */
interface ShowArticleInterface {

    /**
     * The instance of the ShowArticleActivity.
     */
    val activity: ShowArticleActivity

    /**
     * The view model of the ShowArticleActivity.
     */
    val viewModel: ArticleViewModel

    /**
     * The article currently show in the web view.
     */
    val article: Article
}