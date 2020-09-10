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
     * Returns the list of articles with their image of the repository.
     *
     * @return the list of articles with their image of the repository.
     */
//    suspend fun getArticles(): List<Article>

    /**
     * Returns the list of articles from the Rss flux of Bastamag.
     *
     * @return the list of articles from the Rss flux of Bastamag.
     */
    suspend fun getRssArticles(): List<Article>?

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
 * @property viewedArticleWithMediaMetadataDao the database access object for viewed articles with
 *                                             media metadata.
 *
 * @constructor Instantiates a new BastamagRepositoryImpl.
 *
 * @param rssService                        the service to request the Bastamag Rss Service to set.
 * @param webService                        the service to request the Bastamag Web Site to set.
 * @param articleDao                        the database access object for article to set.
 * @param viewedArticleWithMediaMetadataDao the database access object for viewed articles with
 *                                          media metadata to set.
 */
class BastamagRepositoryImpl(
    private val rssService: BastamagRssService,
    private val webService: BastamagWebService,
    private val articleDao: ArticleDao
//    private val mediaMetadataDao: MediaMetadataDao,
//    private val viewedArticleWithMediaMetadataDao: ViewedArticleWithMediaMetadataDao
): BastamagRepository {

    /**
     * Returns the list of articles from all sources.
     *
     * @return the list of articles from all sources.
     */
//    override suspend fun getArticles(): List<Article> {
//        val apiArticles = apiService.getMostViewedArticles().convert()
//        persist(apiArticles)
//        return withContext(Dispatchers.IO){
//            viewedArticleWithMediaMetadataDao.getAll()
//        }
//        val newArticles = webService.getCategory("A la Une")
//
//    }

    /**
     * Returns the list of articles from the Rss flux of Bastamag.
     *
     * @return the list of articles from the Rss flux of Bastamag.
     */
    override suspend fun getRssArticles(): List<Article>? = withContext(Dispatchers.IO) {
        val rssArticleList = rssService.getRssArticles().channel?.rssArticleList
        return@withContext if (rssArticleList != null) {
            val articleList = rssArticleList.map { it.toArticle() }
            articleList.forEach {
                it.source = BASTAMAG

                val bastamagArticle = BastamagArticle(webService.getArticle(getPageNameFromUrl(it.url)))

                bastamagArticle.getArticle()?.let { article -> it.article = article }
                bastamagArticle.getImage()[0]?.let { imageUrl -> it.imageUrl = imageUrl }
                bastamagArticle.getDescription()?.let { description -> it.description = description }
                bastamagArticle.getCss()?.let { css -> it.css = css }
            }

            persist(articleList)
            articleDao.getWhereUrlsInSorted(articleList.map { it.url })
        } else
            null
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