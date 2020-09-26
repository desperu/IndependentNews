package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.web.bastamag.BastamagArticle
import org.desperu.independentnews.network.bastamag.BastamagRssService
import org.desperu.independentnews.network.bastamag.BastamagWebService
import org.desperu.independentnews.utils.BASTAMAG
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

    /**
     * Marks an article as read in the repository.
     *
     * @param article the article to mark as read.
     */
    suspend fun markArticleAsRead(article: Article)
}

/**
 * Implementation of the BastamagRepository interface.
 *
 * @author Desperu
 *
 * @property rssService                        the service to request the Bastamag Rss Service.
 * @property webService                        the service to request the Bastamag Web Site.
 * @property articleDao                        the database access object for article.
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
                it.source = BASTAMAG

                val bastamagArticle = BastamagArticle(webService.getArticle(getPageNameFromUrl(it.url)))

                bastamagArticle.getSubtitle()?.let { subtitle -> it.subtitle = subtitle }
                bastamagArticle.getArticle()?.let { article -> it.article = article }
                bastamagArticle.getDescription()?.let { description -> it.description = description }
                bastamagArticle.getImage()[0]?.let { imageUrl -> it.imageUrl = imageUrl }
                bastamagArticle.getCss()?.let { css -> it.css = css }
            }

            persist(articleList)
            articleDao.getWhereUrlsInSorted(articleList.map { it.url })
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

    /**
     * Marks an article as read in database.
     *
     * @param article the viewed article to mark as read in database.
     */
    override suspend fun markArticleAsRead(article: Article) = withContext(Dispatchers.IO) {
        articleDao.markAsRead(article.id)
    }

    /**
     * Persists (update the existing ones, and insert the non-existing ones) the articles in database.
     *
     * @param articleList the articles list to persist.
     */
    private suspend fun persist(articleList: List<Article>?) = withContext(Dispatchers.IO) {
        articleList?.let {
            val urlsToUpdate = getExistingUrls(it)

            val articleListPair = it.partition { article ->  urlsToUpdate.contains(article.url) }
            val articlesToUpdate = articleListPair.first
            val articlesToInsert = articleListPair.second

            articlesToUpdate.forEach { article ->
                articleDao.update(
                    article.title,
                    article.subtitle,
                    article.publishedDate,
                    article.article,
                    article.categories,
                    article.description,
                    article.imageUrl,
                    article.css,
                    article.url
                )
            }
            articleDao.insertArticles(*articlesToInsert.toTypedArray())
        }

//        mediaMetadataDao.deleteWhereViewedArticleIdIn(idsToUpdate)
//        mediaMetadataDao.insertAll(*mediaMetadataToInsert.toTypedArray())
    }

    /**
     * Returns the url of the articles from the given list that already exists in database.
     *
     * @param articleList the articles from which to get the list of existing url in database.
     *
     * @return the url of the articles from the given list that already exists in database.
     */
    private suspend fun getExistingUrls(articleList: List<Article>) = withContext(Dispatchers.IO){
        val url = articleList.map { article -> article.url }
        val listToUpdate = articleDao.getWhereUrlsIn(url)
        listToUpdate.map { article -> article.url }
    }
}