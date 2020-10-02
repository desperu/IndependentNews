package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.utils.*

/**
 * Independent News Repository interface to get data from others repositories.
 *
 * @author Desperu
 */
interface IndependentNewsRepository {

    /**
     * Returns the list of articles from the Rss flux of Reporterre.
     *
     * @return the list of articles from the Rss flux of Reporterre.
     */
    suspend fun getRssArticles(): List<Article>?

    /**
     * Returns the top story list of articles from the Rss flux of Bastamag.
     *
     * @return the top story list of articles from the Rss flux of Bastamag.
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

    /**
     * Create all sources in database for first apk start.
     */
    suspend fun createSourcesForFirstStart()

    /**
     * Set enabled sources from the database.
     */
    suspend fun setSources()
}

/**
 * Implementation of the Independent News Repository interface.
 *
 * @author Desperu
 *
 * @property rssService                     the service to request the Reporterre Rss Service.
 * @property webService                     the service to request the Reporterre Web Site.
 *
 * @constructor Instantiates a new IndependentNewsRepositoryImpl.
 *
 * @param rssService                        the service to request the Reporterre Rss Service to set.
 * @param webService                        the service to request the Reporterre Web Site to set.
 */
class IndependentNewsRepositoryImpl(
    private val sourceRepository: SourceRepository,
    private val reporterreRepository: ReporterreRepository,
    private val bastamagRepository: BastamagRepository,
    private val articleDao: ArticleDao
): IndependentNewsRepository {

    // FOR DATA
    private var sources: List<Source>? = null

//    init {
//        runBlocking(Dispatchers.IO) { setSources() } // TODO lock ui ??
//    }

    /**
     * Returns the list of articles from the Rss flux of all sources.
     *
     * @return the list of articles from the Rss flux of all sources.
     */
    override suspend fun getRssArticles(): List<Article>? = withContext(Dispatchers.IO) {
        setSources()
        val rssArticleList = mutableListOf<Article>()
        sources?.forEach { source ->
            if (source.name == BASTAMAG)
                bastamagRepository.getRssArticles()?.let { rssArticleList.addAll(it) }
            if (source.name == REPORTERRE)
                reporterreRepository.getRssArticles()?.let { rssArticleList.addAll(it) }
        }

        persist(rssArticleList)

        return@withContext rssArticleList
    }

    /**
     * Returns the top story list of articles from the Rss flux of Bastamag.
     *
     * @return the top story list of articles from the Rss flux of Bastamag.
     */
    override suspend fun getTopStory(): List<Article>? = withContext(Dispatchers.IO) {
        setSources()
        val topStoryList = mutableListOf<Article>()
        sources?.forEach { source ->
            if (source.name == BASTAMAG)
                bastamagRepository.getTopStory()?.let { topStoryList.addAll(it) }
            if (source.name == REPORTERRE)
                reporterreRepository.getTopStory()?.let { topStoryList.addAll(it) }
        }

        topStoryList
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
        setSources()
        val allArticles = mutableListOf<Article>()
        sources?.forEach { source ->
            if (source.name == BASTAMAG)
                bastamagRepository.getAllArticles()?.let { allArticles.addAll(it) }
            if (source.name == REPORTERRE)
                reporterreRepository.getAllArticles()?.let { allArticles.addAll(it) }
        }

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
     * Create all sources in database for first apk start.
     */
    override suspend fun createSourcesForFirstStart() = withContext(Dispatchers.IO) {
        val sources = mutableListOf<Source>()
        sources.add(Source(0, BASTAMAG, BASTAMAG_BASE_URL, BASTAMAG_EDITO_URL))
        sources.add(Source(0, REPORTERRE, REPORTERRE_BASE_URL, REPORTERRE_EDITO_URL))
        sourceRepository.createSources(*sources.toTypedArray())
        setSources() // TODO mistake with vm call getTopStory before here runBlocking??
    }

    /**
     * Persists (update the existing ones, and insert the non-existing ones) the articles in database.
     *
     * @param articleList the articles list to persist.
     */
    private suspend fun persist(articleList: List<Article>) = withContext(Dispatchers.IO) {
        val urlsToUpdate = getExistingUrls(articleList)

        val articleListPair = articleList.partition { article -> urlsToUpdate.contains(article.url) }
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
                article.cssUrl,
                article.url
            )
        }

        articlesToInsert.forEach { article ->
            val sourceId = sources?.find { it.name == article.sourceName }?.id
            sourceId?.let { article.sourceId = it }
        }
        articleDao.insertArticles(*articlesToInsert.toTypedArray())
    }

    /**
     * Returns the url of the articles from the given list that already exists in database.
     *
     * @param articleList the articles from which to get the list of existing url in database.
     *
     * @return the url of the articles from the given list that already exists in database.
     */
    private suspend fun getExistingUrls(articleList: List<Article>) = withContext(Dispatchers.IO) {
        val url = articleList.map { article -> article.url }
        val listToUpdate = articleDao.getWhereUrlsIn(url)
        listToUpdate.map { article -> article.url }
    }

    /**
     * Set enabled sources from the database.
     */
    override suspend fun setSources() = withContext(Dispatchers.IO) {
        if (sources.isNullOrEmpty())
            sources = sourceRepository.getEnabledSources()
    }
}