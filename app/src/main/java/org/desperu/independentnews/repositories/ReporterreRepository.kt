package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.web.reporterre.ReporterreArticle
import org.desperu.independentnews.models.web.reporterre.ReporterreCategory
import org.desperu.independentnews.network.reporterre.ReporterreRssService
import org.desperu.independentnews.network.reporterre.ReporterreWebService
import org.desperu.independentnews.utils.REPORT_SEC_RESISTER
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl

/**
 * Repository interface to get data from Reporterre services.
 *
 * @author Desperu
 */
interface ReporterreRepository {

    /**
     * Returns the list of articles from the Rss flux of Reporterre.
     *
     * @return the list of articles from the Rss flux of Reporterre.
     */
    suspend fun fetchRssArticles(): List<Article>?

    /**
     * Returns the categories list of articles from the Web site of Reporterre.
     *
     * @return the categories list of articles from the Web site flux of Reporterre.
     */
    suspend fun fetchCategories(): List<Article>?
}

/**
 * Implementation of the ReporterreRepository interface.
 *
 * @author Desperu
 *
 * @property rssService                     the service to request the Reporterre Rss Service.
 * @property webService                     the service to request the Reporterre Web Site.
 *
 * @constructor Instantiates a new ReporterreRepositoryImpl.
 *
 * @param rssService                        the service to request the Reporterre Rss Service to set.
 * @param webService                        the service to request the Reporterre Web Site to set.
 */
class ReporterreRepositoryImpl(
    private val rssService: ReporterreRssService,
    private val webService: ReporterreWebService
): ReporterreRepository {

    /**
     * Returns the list of articles from the Rss flux of Reporterre.
     *
     * @return the list of articles from the Rss flux of Reporterre.
     */
    override suspend fun fetchRssArticles(): List<Article>? = withContext(Dispatchers.IO) {
        val rssArticleList = rssService.getRssArticles().channel?.rssArticleList

        if (!rssArticleList.isNullOrEmpty())
            fetchArticleList(rssArticleList.map { it.toArticle() })
        else
            null
    }

    /**
     * Returns the categories list of articles from the Web site of Reporterre.
     *
     * @return the categories list of articles from the Web site of Reporterre.
     */
    override suspend fun fetchCategories(): List<Article>? = withContext(Dispatchers.IO) {
        val categories = listOf(REPORT_SEC_RESISTER)
        val urls = mutableListOf<String>()

        categories.forEach { category ->
            val reporterreCategory =
                ReporterreCategory(webService.getCategory(category, 0.toString()), category)
            reporterreCategory.getUrlArticleList()?.let { urls.addAll(it) }
        }

        fetchArticleList(urls.map { Article(url = it) })
    }

    /**
     * Fetch article html page for each article in the given list.
     *
     * @param articleList the list of article to fetch html page.
     *
     * @return the article list with all fetched data.
     */
    private suspend fun fetchArticleList(
        articleList: List<Article>
    ): List<Article> = withContext(Dispatchers.IO) {

        // TODO : no fetch articles already in database, use url to know,
        //  and use date to know if they are update, and so fetch it
        articleList.forEach {
            val reporterreArticle = ReporterreArticle(webService.getArticle(getPageNameFromUrl(it.url)))
            reporterreArticle.toArticle(it)
        }

        articleList
    }
}