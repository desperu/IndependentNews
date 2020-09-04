package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.web.bastamag.BastamagArticle
import org.desperu.independentnews.models.web.rss.RssArticle
import org.desperu.independentnews.network.bastamag.BastamagRssService
import org.desperu.independentnews.network.bastamag.BastamagWebService

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
    suspend fun getRssArticles(): List<RssArticle>?

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
    override suspend fun getRssArticles(): List<RssArticle>? {
        val rssArticleList = rssService.getRssArticles().channel?.rssArticleList
        return if (rssArticleList != null) {
            rssArticleList.forEach {
                it.imageUrl =
                    BastamagArticle(webService.getArticle(it.url.toString())).getImage()[0].toString()
            }
            val articleList = rssArticleList.map { it.toArticle() }
            persist(articleList)
            return rssArticleList
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
            val idsToUpdate = getExistingIds(it)

            val articleListPair = it.partition { article ->  idsToUpdate.contains(article.id) }
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
                    article.url,
                    article.id
                )
            }
            articleDao.insertArticles(*articlesToInsert.toTypedArray())
        }

        return@withContext null

//        mediaMetadataDao.deleteWhereViewedArticleIdIn(idsToUpdate)
//        mediaMetadataDao.insertAll(*mediaMetadataToInsert.toTypedArray())
    }

    /**
     * Returns the ids of the articles from the given list that already exists in database.
     *
     * @param articleList the articles from which to get the list of existing ids in database.
     *
     * @return the ids of the articles from the given list that already exists in database.
     */
    private suspend fun getExistingIds(articleList: List<Article>) = withContext(Dispatchers.IO){
        val ids = articleList.map { article -> article.id }
        val listToUpdate = articleDao.getWhereIdIn(ids)
        listToUpdate.map { article -> article.id }
    }
}