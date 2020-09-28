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

    /**
     * Marks an article as read in the repository.
     *
     * @param article the article to mark as read.
     */
    suspend fun markArticleAsRead(article: Article)
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

//                reporterreArticle.getSection()?.let { section -> it.section = section }
//                reporterreArticle.getTheme()?.let { theme -> it.theme = theme }
//                reporterreArticle.getAuthor()?.let { author -> if (!author.isBlank()) it.author = author }
//                reporterreArticle.getArticle()?.let { article -> it.article = article }
//                reporterreArticle.getDescription()?.let { description -> if (description.isNotBlank()) it.description = description }
//                reporterreArticle.getImage()[0]?.let { imageUrl -> it.imageUrl = imageUrl }
//                reporterreArticle.getCss()?.let { css -> it.css = css }

                reporterreArticle.toArticle(it)
            }

            persist(articleList)
            articleDao.getWhereUrlsInSorted(articleList.map { it.url })
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
                    article.section,
                    article.theme,
                    article.author,
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