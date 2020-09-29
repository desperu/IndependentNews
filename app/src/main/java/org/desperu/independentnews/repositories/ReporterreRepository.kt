package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.web.reporterre.ReporterreArticle
import org.desperu.independentnews.network.reporterre.ReporterreRssService
import org.desperu.independentnews.network.reporterre.ReporterreWebService
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
    suspend fun getRssArticles(): List<Article>?

    /**
     * Returns the top story list of articles from the Rss flux of Reporterre.
     *
     * @return the top story list of articles from the Rss flux of Reporterre.
     */
    suspend fun getTopStory(): List<Article>?

    /**
     * Returns the category list of articles from the Rss flux of Reporterre.
     *
     * @return the category list of articles from the Rss flux of Reporterre.
     */
    suspend fun getCategory(category: String): List<Article>?

    /**
     * Returns list of all articles from the database.
     *
     * @return the list of all articles from the database.
     */
    suspend fun getAllArticles(): List<Article>?
}

/**
 * Implementation of the ReporterreRepository interface.
 *
 * @author Desperu
 *
 * @property rssService                     the service to request the Reporterre Rss Service.
 * @property webService                     the service to request the Reporterre Web Site.
 * @property articleDao                     the database access object for article.
 *
 * @constructor Instantiates a new ReporterreRepositoryImpl.
 *
 * @param rssService                        the service to request the Reporterre Rss Service to set.
 * @param webService                        the service to request the Reporterre Web Site to set.
 * @param articleDao                        the database access object for article to set.
 */
class ReporterreRepositoryImpl(
    private val rssService: ReporterreRssService,
    private val webService: ReporterreWebService,
    private val articleDao: ArticleDao
): ReporterreRepository {

    /**
     * Returns the list of articles from the Rss flux of Reporterre.
     *
     * @return the list of articles from the Rss flux of Reporterre.
     */
    override suspend fun getRssArticles(): List<Article>? = withContext(Dispatchers.IO) {
        val rssArticleList = rssService.getRssArticles().channel?.rssArticleList
        return@withContext if (!rssArticleList.isNullOrEmpty()) {
            val articleList = rssArticleList.map { it.toArticle() }
            articleList.forEach {
                val reporterreArticle = ReporterreArticle(webService.getArticle(getPageNameFromUrl(it.url)))
                reporterreArticle.toArticle(it)
            }

            articleList
        } else
            null
    }

    /**
     * Returns the top story list of articles from the Rss flux of Reporterre.
     *
     * @return the top story list of articles from the Rss flux of Reporterre.
     */
    override suspend fun getTopStory(): List<Article>? = withContext(Dispatchers.IO) {
        val topStory = rssService.getRssArticles().channel?.rssArticleList
        return@withContext if (!topStory.isNullOrEmpty()) {
            val topStoryUrls = topStory.map { it.url.toString() }
            articleDao.getWhereUrlsInSorted(topStoryUrls) // TODO use isTopStory in db article, and manage it ??
        } else
            null
    }

    /**
     * Returns the category list of articles from the database.
     *
     * @return the category list of articles from the database.
     */
    override suspend fun getCategory(category: String): List<Article>? = withContext(Dispatchers.IO) {
        return@withContext articleDao.getCategory("%$category%")
    }

    /**
     * Returns list of all articles from the database.
     *
     * @return the list of all articles from the database.
     */
    override suspend fun getAllArticles(): List<Article>? = withContext(Dispatchers.IO) {
        return@withContext articleDao.getAll()
    }
}