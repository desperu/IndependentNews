package org.desperu.independentnews.extension

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleItemViewModel

/**
 * Set the source for each article of the list.
 *
 * @param sourceList the list of source from database.
 *
 * @return the article list with the source set.
 */
internal suspend fun List<Article>?.setSourceForEach(
    sourceList: List<Source>
): List<Article>? = withContext(Dispatchers.IO) {

    this@setSourceForEach?.forEach { article ->
        val source = sourceList.find { article.sourceId == it.id }
        source?.let { article.source = it }
    }
    return@withContext this@setSourceForEach
}

/**
 * Transform the article list to article item view model list.
 *
 * @return the mapped list in article item view model, an empty list if is null.
 */
internal fun List<Article>?.toArticleItemVMList(): List<ArticleItemViewModel> =
    this?.let {
        map { article -> ArticleItemViewModel(article) }

    } ?: emptyList()