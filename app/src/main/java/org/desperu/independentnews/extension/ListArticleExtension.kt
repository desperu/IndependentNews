package org.desperu.independentnews.extension

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source

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