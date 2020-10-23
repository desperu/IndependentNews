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
 * @property articleRepository              the repository access for article database.
 *
 * @constructor Instantiates a new ReporterreRepositoryImpl.
 *
 * @param rssService                        the service to request the Reporterre Rss Service to set.
 * @param webService                        the service to request the Reporterre Web Site to set.
 * @param articleRepository                 the repository access for article database to set.
 */
class ReporterreRepositoryImpl(
    private val rssService: ReporterreRssService,
    private val webService: ReporterreWebService,
    private val articleRepository: ArticleRepository
): ReporterreRepository {

    /**
     * Returns the list of articles from the Rss flux of Reporterre.
     *
     * @return the list of articles from the Rss flux of Reporterre.
     */
    override suspend fun fetchRssArticles(): List<Article>? = withContext(Dispatchers.IO) {
        val rssArticleList = rssService.getRssArticles().channel?.rssArticleList

        if (!rssArticleList.isNullOrEmpty()) {
            val articleList = rssArticleList.map { it.toArticle() }
            articleRepository.updateTopStory(articleList)
            fetchArticleList(articleRepository.getNewArticles(articleList))
        }
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
        val articleList = mutableListOf<Article>()

        categories.forEach { category ->
            val categoryList = webService.getCategory(category, 0.toString())
            articleList.addAll(ReporterreCategory(categoryList).getArticleList())
        }

        fetchArticleList(articleRepository.getNewArticles(articleList))
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

        articleList.forEach {
            val reporterreArticle = ReporterreArticle(webService.getArticle(getPageNameFromUrl(it.url)))
            reporterreArticle.toArticle(it)
        }

        articleList
    }
}