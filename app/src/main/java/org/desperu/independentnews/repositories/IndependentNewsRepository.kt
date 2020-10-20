package org.desperu.independentnews.repositories

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.extension.setSourceForEach
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
     * Fetch the list of articles from the Rss flux for all sources.
     */
    suspend fun fetchRssArticles()

    /**
     * Fetch the categories list of articles from the Web Site for all sources.
     */
    suspend fun fetchCategories()

    /**
     * Refresh data for the application, fetch data from Rss and Web, and persist them
     * into the database.
     */
    suspend fun refreshData()

    /**
     * Returns the top story list of articles from the database.
     *
     * @return the top story list of articles from the database.
     */
    suspend fun getTopStory(): List<Article>?

    /**
     * Returns the category list of articles from the database.
     *
     * @param category the category to search for.
     *
     * @return the category list of articles from the database.
     */
    suspend fun getCategory(category: String): List<Article>?

    /**
     * Returns the list of all articles from the database.
     *
     * @return the list of all articles from the database.
     */
    suspend fun getAllArticles(): List<Article>?

    /**
     * Returns the list of filtered articles from database.
     *
     * @param selectedMap the map of selected filters to apply.
     * @param actualList the actual list of article shown in the recycler view.
     *
     * @return the list of filtered articles from the database.
     */
    suspend fun getFilteredList(selectedMap: Map<Int, MutableList<String>>, actualList: List<Article>): List<Article>?

    /**
     * Marks an article as read in the database.
     *
     * @param article the article to mark as read.
     */
    suspend fun markArticleAsRead(article: Article)
}

/**
 * Implementation of the Independent News Repository interface.
 *
 * @author Desperu
 *
 * @property articleRepository              the repository access for article database.
 * @property sourceRepository               the repository access for source database.
 * @property reporterreRepository           the repository access for reporterre services.
 * @property bastamagRepository             the repository access for bastamag services.
 * @property articleDao                     the database access object for article.
 *
 * @constructor Instantiates a new IndependentNewsRepositoryImpl.
 * @param articleRepository                 the repository access for article database to set.
 * @param sourceRepository                  the repository access for source database to set.
 * @param reporterreRepository              the repository access for reporterre services to set.
 * @param bastamagRepository                the repository access for bastamag services to set.
 * @param articleDao                        the database access object for article to set.
 */
class IndependentNewsRepositoryImpl(
    private val articleRepository: ArticleRepository,
    private val sourceRepository: SourceRepository,
    private val reporterreRepository: ReporterreRepository,
    private val bastamagRepository: BastamagRepository,
    private val articleDao: ArticleDao
): IndependentNewsRepository {

    // FOR DATA
    private var sources: List<Source>? = null

    // -----------------
    // FETCH DATA (WEB)
    // -----------------

    /**
     * Fetch the list of articles from the Rss flux of all enabled sources,
     * and persist them in database.
     */
    override suspend fun fetchRssArticles() = withContext(Dispatchers.IO) {
        setSources()
        val rssArticleList = mutableListOf<Article>()
        try {
            sources?.forEach { source ->
                if (source.name == BASTAMAG)
                    bastamagRepository.fetchRssArticles()?.let { rssArticleList.addAll(it) }
                if (source.name == REPORTERRE)
                    reporterreRepository.fetchRssArticles()?.let { rssArticleList.addAll(it) }
            }
        } catch (e: Exception) {
          Log.e("IdeRepo-fetchRssArticle", "Error while fetching source web data.")
        }

        articleRepository.updateTopStory(rssArticleList)
        articleRepository.persist(rssArticleList)
    }

    /**
     * Fetch the categories list of articles from the Web Site for enabled all sources,
     * and persist them in database.
     */
    override suspend fun fetchCategories() = withContext(Dispatchers.IO) {
        setSources()
        val articleList = mutableListOf<Article>()
        try {
            sources?.forEach { source ->
                if (source.name == BASTAMAG)
                    bastamagRepository.fetchCategories()?.let { articleList.addAll(it) }
                if (source.name == REPORTERRE)
                    reporterreRepository.fetchCategories()?.let { articleList.addAll(it) }
            }
        } catch (e: Exception) {
            Log.e("IdeRepo-fetchCategories", "Error while fetching source web data.")
        }

        articleRepository.persist(articleList)
    }


    /**
     * Refresh data for the application, fetch data from Rss and Web, and persist them
     * into the database.
     */
    override suspend fun refreshData() = withContext(Dispatchers.IO) {
        fetchRssArticles()
        fetchCategories()
        articleRepository.removeOldArticles()
    }

    // -----------------
    // GET DATA (DATABASE)
    // -----------------

    /**
     * Returns the top story list of articles from the database.
     *
     * @return the top story list of articles from the database.
     */
    override suspend fun getTopStory(): List<Article>? = withContext(Dispatchers.IO) {
        setSources()
        sources?.let { articleDao.getTopStory().setSourceForEach(it) }
    }

    /**
     * Returns the category list of articles from the database.
     *
     * @return the category list of articles from the database.
     */ // TODO rename to rssCategories??
    override suspend fun getCategory(category: String): List<Article>? = withContext(Dispatchers.IO) {
        setSources()
        sources?.let { articleDao.getCategory("%$category%").setSourceForEach(it) }
    }

    /**
     * Returns list of all articles from the database.
     *
     * @return the list of all articles from the database.
     */
    override suspend fun getAllArticles(): List<Article>? = withContext(Dispatchers.IO) {
        setSources()
        sources?.let { articleDao.getAll().setSourceForEach(it) }
    }

    /**
     * Returns the list of filtered articles from database and category filter.
     *
     * @param selectedMap the map of selected filters to apply.
     * @param actualList the actual list of article shown in the recycler view.
     *
     * @return the list of filtered articles from the database.
     */
    override suspend fun getFilteredList(selectedMap: Map<Int, MutableList<String>>,
                                         actualList: List<Article>
    ): List<Article>? = withContext(Dispatchers.IO) {
        setSources()
        val parsedMap = FilterUtils.parseSelectedMap(selectedMap, sources!!)

        val filteredList = articleRepository.getFilteredListFromDB(
            parsedMap.getValue(SOURCES),
            parsedMap[THEMES],
            parsedMap[SECTIONS],
            parsedMap.getValue(DATES).map { it.toLong() },
            actualList.map { it.url }
        ).toMutableList()

        val unMatchArticleList = actualList.filter { article -> !filteredList.map { it.id }.contains(article.id)}
        filteredList.addAll(articleRepository.filterCategories(parsedMap, unMatchArticleList))

        return@withContext sources?.let { list ->
            articleDao.getWhereUrlsInSorted(filteredList.map { it.url })
                .setSourceForEach(list)
        }
    }

    /**
     * Marks an article as read in database.
     *
     * @param article the viewed article to mark as read in database.
     */
    override suspend fun markArticleAsRead(article: Article) = withContext(Dispatchers.IO) {
        articleDao.markAsRead(article.id)
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Set enabled sources from the database.
     */
    private suspend fun setSources() = withContext(Dispatchers.IO) {
        if (sources.isNullOrEmpty())
            sources = sourceRepository.getEnabledSources()
    }
}