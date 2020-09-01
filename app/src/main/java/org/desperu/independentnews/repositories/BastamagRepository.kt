package org.desperu.independentnews.repositories

import org.desperu.independentnews.models.api.bastamag.BastamagArticle
import org.desperu.independentnews.models.api.rss.Article
import org.desperu.independentnews.network.bastamag.BastamagRssService
import org.desperu.independentnews.network.bastamag.BastamagWebService

/**
 * Repository interface to get data from services.
 *
 * @author Desperu
 */
interface BastamagRepository {

    /**
     * Returns the list of viewed articles with their media metadata of the repository.
     *
     * @return the list of viewed articles with their media metadata of the repository.
     */
//    suspend fun getMostViewedArticles(): List<ViewedArticleWithMediaMetadata>

    /**
     * Returns the list of articles from the Rss flux of Bastamag.
     *
     * @return the list of articles from the Rss flux of Bastamag.
     */
    suspend fun getRssArticles(): List<Article>?

    /**
     * Marks an article as read in the repository.
     *
     * @param viewedArticle the article to mark as read.
     */
//    suspend fun markArticleAsRead(viewedArticle: ViewedArticle)
}

/**
 * Implementation of the BastamagRepository interface.
 *
 * @author Desperu
 *
 * @property rssService                        the service to request the Bastamag Rss Service.
 * @property webService                        the service to request the Bastamag Web Site.
 * @property mediaMetadataDao                  the database access object for media metadata.
 * @property viewedArticleWithMediaMetadataDao the database access object for viewed articles with
 *                                             media metadata.
 *
 * @constructor Instantiates a new BastamagRepositoryImpl.
 *
 * @param rssService                        the service to request the Bastamag Rss Service to set.
 * @param webService                        the service to request the Bastamag Web Site to set.
 * @param mediaMetadataDao                  the database access object for media metadata to set.
 * @param viewedArticleWithMediaMetadataDao the database access object for viewed articles with
 *                                          media metadata to set.
 */
class BastamagRepositoryImpl(
    private val rssService: BastamagRssService,
    private val webService: BastamagWebService
//    private val viewedArticleDao: ViewedArticleDao,
//    private val mediaMetadataDao: MediaMetadataDao,
//    private val viewedArticleWithMediaMetadataDao: ViewedArticleWithMediaMetadataDao
): BastamagRepository {

    /**
     * Returns the list of most viewed articles with their media metadata from the New York Times
     * API.
     *
     * @return the list of most viewed articles with their media metadata.
     */
//    override suspend fun getMostViewedArticles(): List<ViewedArticleWithMediaMetadata> {
//        val apiArticles = apiService.getMostViewedArticles().convert()
//        persist(apiArticles)
//        return withContext(Dispatchers.IO){
//            viewedArticleWithMediaMetadataDao.getAll()
//        }
//    }

    /**
     * Returns the list of articles from the Rss flux of Bastamag.
     *
     * @return the list of articles from the Rss flux of Bastamag.
     */
    override suspend fun getRssArticles(): List<Article>? {
        val articleList = rssService.getRssArticles().channel?.articleList
        articleList?.forEach { it.imageUrl = BastamagArticle(webService.getArticle(it.url.toString())).getImage()[0].toString() }
//        persist(articleList)
        return articleList
    }

    /**
     * Marks an article as read in database.
     *
     * @param viewedArticle the viewed article to mark as read in database.
     */
//    override suspend fun markArticleAsRead(viewedArticle: ViewedArticle) = withContext(Dispatchers.IO) {
//        viewedArticleDao.markAsRead(viewedArticle.id)
//    }

    /**
     * Persists (update the existing ones, and insert the non-existing ones) the viewed articles and
     * media data in database.
     *
     * @param viewedArticleWithMediaMetadata the viewed articles and media metadata to persist
     */
//    private suspend fun persist(viewedArticleWithMediaMetadata: List<ViewedArticleWithMediaMetadata>) = withContext(Dispatchers.IO) {
//        val idsToUpdate = getExistingIds(viewedArticleWithMediaMetadata)
//
//        val articlesToUpdate = viewedArticleWithMediaMetadata.filter { idsToUpdate.contains(it.viewedArticle.id) }.map { it.viewedArticle }
//        val articlesToInsert = viewedArticleWithMediaMetadata.filter { !idsToUpdate.contains(it.viewedArticle.id) }.map { it.viewedArticle }
//        val mediaMetadataToInsert = viewedArticleWithMediaMetadata.map { it.mediaMetadata }.flatten()
//
//        articlesToUpdate.forEach { article ->
//            viewedArticleDao.update(article.title, article.byLine, article.publishedDate, article.url, article.id)
//        }
//        viewedArticleDao.insertAll(*articlesToInsert.toTypedArray())
//
//        mediaMetadataDao.deleteWhereViewedArticleIdIn(idsToUpdate)
//        mediaMetadataDao.insertAll(*mediaMetadataToInsert.toTypedArray())
//    }

    /**
     * Returns the ids of the viewed articles from the given list that already exists in database.
     *
     * @param viewedArticleWithMediaMetadata the viewed articles and media metadata from which to
     *                                       get the list of existing ids in database.
     *
     * @return the ids of the viewed articles from the given list that already exists in database.
     */
//    private suspend fun getExistingIds(viewedArticleWithMediaMetadata: List<ViewedArticleWithMediaMetadata>) = withContext(Dispatchers.IO){
//        val ids = viewedArticleWithMediaMetadata.map { article -> article.viewedArticle.id }
//        val listToUpdate = viewedArticleWithMediaMetadataDao.getWhereIdIn(ids)
//        listToUpdate.map { article -> article.viewedArticle.id }
//    }
}