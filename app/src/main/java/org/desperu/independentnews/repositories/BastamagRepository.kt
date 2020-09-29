package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.web.bastamag.BastamagArticle
import org.desperu.independentnews.network.bastamag.BastamagRssService
import org.desperu.independentnews.network.bastamag.BastamagWebService
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl

/**
 * Repository interface to get data from services.
 *
 * @author Desperu
 */
interface BastamagRepository {

    /**
     * Returns the list of articles from the Rss flux of Bastamag.
     *
     * @return the list of articles from the Rss flux of Bastamag.
     */
    suspend fun getRssArticles(): List<Article>?

    /**
     * Returns the top story list of articles from the Rss flux of Bastamag.
     *
     * @return the top story list of articles from the Rss flux of Bastamag.
     */
    suspend fun getTopStory(): List<Article>?

    /**
     * Returns the category list of articles from the Rss flux of Bastamag.
     *
     * @return the category list of articles from the Rss flux of Bastamag.
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
 * Implementation of the BastamagRepository interface.
 *
 * @author Desperu
 *
 * @property rssService                     the service to request the Bastamag Rss Service.
 * @property webService                     the service to request the Bastamag Web Site.
 * @property articleDao                     the database access object for article.
 *
 * @constructor Instantiates a new BastamagRepositoryImpl.
 *
 * @param rssService                        the service to request the Bastamag Rss Service to set.
 * @param webService                        the service to request the Bastamag Web Site to set.
 * @param articleDao                        the database access object for article to set.
 */
class BastamagRepositoryImpl(
    private val rssService: BastamagRssService,
    private val webService: BastamagWebService,
    private val articleDao: ArticleDao
): BastamagRepository {

    /**
     * Returns the list of articles from the Rss flux of Bastamag.
     *
     * @return the list of articles from the Rss flux of Bastamag.
     */
    override suspend fun getRssArticles(): List<Article>? = withContext(Dispatchers.IO) {
        val rssArticleList = rssService.getRssArticles().channel?.rssArticleList
        return@withContext if (!rssArticleList.isNullOrEmpty()) {
            val articleList = rssArticleList.map { it.toArticle() }
            articleList.forEach {
                val bastamagArticle = BastamagArticle(webService.getArticle(getPageNameFromUrl(it.url)))
                bastamagArticle.toArticle(it)
            }

            articleList
        } else
            null
    }

    /**
     * Returns the top story list of articles from the Rss flux of Bastamag.
     *
     * @return the top story list of articles from the Rss flux of Bastamag.
     */
    override suspend fun getTopStory(): List<Article>? = withContext(Dispatchers.IO) {
        val topStory = rssService.getRssArticles().channel?.rssArticleList
        return@withContext if (!topStory.isNullOrEmpty()) {
            val topStoryUrls = topStory.map { it.url.toString() }
            articleDao.getWhereUrlsInSorted(topStoryUrls)
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