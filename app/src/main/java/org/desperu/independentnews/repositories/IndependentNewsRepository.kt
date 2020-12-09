package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.extension.setSourceForEach
import org.desperu.independentnews.helpers.SnackBarHelper
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.repositories.database.ArticleRepository
import org.desperu.independentnews.repositories.database.SourceRepository
import org.desperu.independentnews.repositories.network.BastamagRepository
import org.desperu.independentnews.repositories.network.ReporterreRepository
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.FilterUtils.parseSelectedMap
import org.desperu.independentnews.utils.Utils.millisToStartOfDay
import org.koin.java.KoinJavaComponent.getKoin
import java.util.*

/**
 * Independent News Repository interface to get data from others repositories.
 *
 * @author Desperu
 */
interface IndependentNewsRepository {

    /**
     * Fetch the list of articles from the Rss flux for all sources.
     *
     * @return the id list of persisted articles.
     */
    suspend fun fetchRssArticles(): List<Long>

    /**
     * Fetch the categories list of articles from the Web Site for all sources.
     *
     * @return the id list of persisted articles.
     */
    suspend fun fetchCategories(): List<Long>

    /**
     * Refresh data for the application, fetch data from Rss and Web, persist them
     * into the database, and remove old articles.
     *
     * @return the number of row affected for removed articles.
     */
    suspend fun refreshData(): Int

    /**
     * Returns the top story list of articles from the database.
     *
     * @return the top story list of articles from the database.
     */
    suspend fun getTopStory(): List<Article>?

    /**
     * Returns the category list of articles from the database.
     *
     * @param categories the category list to search for in database.
     *
     * @return the category list of articles from the database.
     */
    suspend fun getCategory(categories: List<String>): List<Article>?

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

    /**
     * Returns the today article list, articles published today.
     *
     * @return the today article list, articles published today.
     */
    suspend fun getTodayArticles(): List<Article>?

    /**
     * Create all sources in database for first apk start.
     *
     * @return the id list of inserted sources.
     */
    suspend fun createSourcesForFirstStart(): List<Long>
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
 * @property snackBarHelper                 the helper to display fetch messages to the user
 *                                          with the snack bar.
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
    private val snackBarHelper: SnackBarHelper? get() = getKoin().getOrNull()

    // -----------------
    // FETCH DATA (WEB)
    // -----------------

    /**
     * Fetch the list of articles from the Rss flux of all enabled sources,
     * and persist them in database.
     *
     * @return the id list of persisted articles.
     */
    override suspend fun fetchRssArticles(): List<Long> = withContext(Dispatchers.IO) {
        val rssArticleList = mutableListOf<Article>()

        // TODO serialize with base (BaseNetworkRepo) and use when to get good repo
        getSources().forEach { source ->
            when (source.name) {
                BASTAMAG -> bastamagRepository.fetchRssArticles()?.let { rssArticleList.addAll(it) }
                REPORTERRE -> reporterreRepository.fetchRssArticles()?.let { rssArticleList.addAll(it) }
            }
        }

        return@withContext articleRepository.persist(rssArticleList)
    }

    /**
     * Fetch the categories list of articles from the Web Site for all enabled sources,
     * and persist them in database.
     *
     * @return the id list of persisted articles.
     */
    override suspend fun fetchCategories(): List<Long> = withContext(Dispatchers.IO) {
        val articleList = mutableListOf<Article>()

        getSources().forEach { source ->
            when (source.name) {
                BASTAMAG -> bastamagRepository.fetchCategories()?.let { articleList.addAll(it) }
                REPORTERRE -> reporterreRepository.fetchCategories()?.let { articleList.addAll(it) }
            }
        }

        return@withContext articleRepository.persist(articleList)
    }

    /**
     * Refresh data for the application, fetch data from Rss and Web, and persist them
     * into the database.
     *
     * @return the number of row affected for removed articles.
     */
    override suspend fun refreshData(): Int = withContext(Dispatchers.IO) {
        var newArticles = 0
        newArticles += fetchRssArticles().size
        newArticles += fetchCategories().size

        snackBarHelper?.showMessage(
            if (newArticles > 0) END_FIND else END_NOT_FIND,
            listOf(newArticles.toString())
        )

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
        val sources = getSources()
        articleDao.getTopStory(sources.map { source -> source.id }).setSourceForEach(sources)
    }

    /**
     * Returns the category list of articles from the database.
     *
     * @param categories the category list to search for in database.
     *
     * @return the category list of articles from the database.
     */
    override suspend fun getCategory(categories: List<String>): List<Article>? = withContext(Dispatchers.IO) {
        val sources = getSources()
        val articleList = mutableListOf<Article>()

        categories.forEach { category ->
            articleList.addAll(
                articleDao.getCategory("%$category%", sources.map { it.id })
            )
        }

        articleDao.getWhereUrlsInSorted(articleList.map { it.url }).setSourceForEach(sources)
    }

    /**
     * Returns list of all articles from the database.
     *
     * @return the list of all articles from the database.
     */
    override suspend fun getAllArticles(): List<Article>? = withContext(Dispatchers.IO) {
        val sources = getSources()
        articleDao.getAll(sources.map { it.id }).setSourceForEach(sources)
    }

    /**
     * Returns the list of filtered articles from database and category filter.
     *
     * @param selectedMap the map of selected filters to apply.
     * @param actualList the actual list of article shown in the recycler view.
     *
     * @return the list of filtered articles from the database.
     */
    override suspend fun getFilteredList(
        selectedMap: Map<Int, MutableList<String>>,
        actualList: List<Article>
    ): List<Article>? = withContext(Dispatchers.IO) {
        val sources = getSources()

        val parsedMap = parseSelectedMap(selectedMap, sources)

        val filteredList = articleRepository.getFilteredListFromDB(
            parsedMap.getValue(SOURCES),
            parsedMap[THEMES],
            parsedMap[SECTIONS],
            parsedMap.getValue(DATES).map { it.toLong() },
            actualList.map { it.url }
        ).toMutableList()

        val unMatchArticleList = actualList.filter { article ->
            !filteredList.map { it.id }.contains(article.id)
        }
        filteredList.addAll(articleRepository.filterCategories(parsedMap, unMatchArticleList))

        articleDao
            .getWhereUrlsInSorted(filteredList.map { it.url })
            .setSourceForEach(sources)
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
     * Returns the today article list, articles published today.
     *
     * @return the today article list, articles published today.
     */
    override suspend fun getTodayArticles(): List<Article>? = withContext(Dispatchers.IO) {
        val todayStartMillis = millisToStartOfDay(Calendar.getInstance().timeInMillis)
        val sources = getSources()

        articleDao.getTodayArticle(todayStartMillis, sources.map { it.id } )
            .setSourceForEach(sources)
    }

    // -----------------
    // SOURCES
    // -----------------

    /**
     * Get enabled sources from the database.
     * Get from database on each call to handle source state change, from a user action.
     */
    private suspend fun getSources(): List<Source> = withContext(Dispatchers.IO) {
        sourceRepository.getEnabledSources()
    }

    /**
     * Create all sources in database for first apk start.
     *
     * @return the id list of inserted sources pages.
     */
    override suspend fun createSourcesForFirstStart(): List<Long> = withContext(Dispatchers.IO) {
        val sourceList = sourceRepository.getAll()
        val sourceIds: List<Long>

        // If has already sources in database, delete their source page to re-fetch.
        if (sourceList.isNotEmpty()) {
            sourceIds = sourceList.map { it.source.id }
            sourceRepository.deleteAllSourcePages()
        } else
            sourceIds = sourceRepository.insertSources(*SOURCE_LIST.toTypedArray())

        return@withContext fetchSourcePages(sourceIds)
    }

    /**
     * Fetch and store in database the source pages for each source.
     *
     * @param sourceIds the list of source unique identifier.
     *
     * @return the id list of inserted source pages.
     */
    private suspend fun fetchSourcePages(sourceIds: List<Long>): List<Long> = withContext(Dispatchers.IO) {
        val sourcePages = mutableListOf<SourcePage>()

            SOURCE_LIST.forEachIndexed { index, source ->

                val fetchedSourcePages = when (source.name) {
                    BASTAMAG -> bastamagRepository.fetchSourcePages()
                    REPORTERRE -> reporterreRepository.fetchSourcePages()
                    else -> listOf()
                }

                fetchedSourcePages?.forEach { it.sourceId = sourceIds[index] }

                fetchedSourcePages?.let { sourcePages.addAll(it) }
            }

        return@withContext sourceRepository.insertSourcePages(*sourcePages.toTypedArray())
    }
}