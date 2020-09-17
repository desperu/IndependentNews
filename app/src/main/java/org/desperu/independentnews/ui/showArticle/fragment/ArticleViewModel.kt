package org.desperu.independentnews.ui.showArticle.fragment

import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.Article

/**
 * View Model witch provide data for Article Fragment.
 *
 * @param article the article object with contains data.
 *
 * @constructor Instantiates a new ArticleViewModel.
 *
 * @property  article the article object with contains data to set.
 */
class ArticleViewModel(val article: Article): ViewModel()