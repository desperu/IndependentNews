package org.desperu.independentnews.repositories

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.extension.setSourceForEach
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.utils.BASTAMAG
import org.desperu.independentnews.utils.REPORTERRE
import org.desperu.independentnews.utils.Utils.deConcatenateStringToMutableList

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
 * @property sourceRepository               the repository access for source services.
 * @property reporterreRepository           the repository access for reporterre services.
 * @property bastamagRepository             the repository access for bastamag services.
 * @property articleDao                     the database access object for article.
 *
 * @constructor Instantiates a new IndependentNewsRepositoryImpl.
 *
 * @param sourceRepository                  the repository access for source services to set.
 * @param reporterreRepository              the repository access for reporterre services to set.
 * @param bastamagRepository                the repository access for bastamag services to set.
 * @param articleDao                        the database access object for article to set.
 */
class IndependentNewsRepositoryImpl(
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

        updateTopStory(rssArticleList)
        persist(rssArticleList)
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

        persist(articleList)
    }


    /**
     * Refresh data for the application, fetch data from Rss and Web, and persist them
     * into the database.
     */
    override suspend fun refreshData() = withContext(Dispatchers.IO) {
        fetchRssArticles()
        fetchCategories()
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

        val filteredList = getFilteredListFromDB(selectedMap, actualList).toMutableList()

        val unMatchArticleList = actualList.filter { article -> !filteredList.map { it.id }.contains(article.id)}

        filteredList.addAll(filterCategories(selectedMap, unMatchArticleList))

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

        articlesToUpdate.forEachIndexed { index, article ->
            val categories = if (article.isTopStory) article.categories
                             else articleDao.getArticle(urlsToUpdate[index]).categories
            articleDao.update(
                article.title,
                article.section,
                article.theme,
                article.author,
                article.publishedDate,
                article.article,
                categories,
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
     * Update top story values if needed in database
     */
    private suspend fun updateTopStory(articleList: List<Article>) = withContext(Dispatchers.IO) {
        val topStoryDB = articleDao.getTopStory()
        val notTopStory = topStoryDB.filterNot { articleList.contains(it) }

        if (articleList.isNotEmpty() && !notTopStory.isNullOrEmpty())
            articleDao.markIsNotTopStory(*notTopStory.map { it.id }.toLongArray())
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
     * Returns the list of filtered articles from database.
     *
     * @param selectedMap the map of selected filters to apply.
     * @param actualList the actual list of article shown in the recycler view.
     *
     * @return the list of filtered articles from the database.
     */
    private suspend fun getFilteredListFromDB(selectedMap: Map<Int, MutableList<String>>,
                                              actualList: List<Article>
    ): List<Article> = withContext(Dispatchers.IO) {

        val sourcesList = selectedMap[0]
        val sources = if (!sourcesList.isNullOrEmpty()) sourcesList else sources?.map { it.name }!!
        val themes = selectedMap[1]
        val sectionList = selectedMap[2]
        val sections = if (!sectionList.isNullOrEmpty()) deConcatenateStringToMutableList(sectionList[0])
                       else null
        val dates = listOf(0L, 999999999999999999L)
        val urls = actualList.map { it.url }

        // TODO for test
        println("$sources $sections $themes $dates")

        return@withContext when {
            !themes.isNullOrEmpty() && !sections.isNullOrEmpty() ->
                articleDao.getFilteredList(sources, sections, themes, dates[0], dates[1], urls)
            !themes.isNullOrEmpty() ->
                articleDao.getFilteredListWithThemes(sources, themes, dates[0], dates[1], urls)
            !sections.isNullOrEmpty() ->
                articleDao.getFilteredListWithSections(sources, sections, dates[0], dates[1], urls)
            else ->
                articleDao.getFilteredList(sources, dates[0], dates[1], urls)
        }
    }

    /**
     * Returns the list of filtered articles with categories filter.
     *
     * @param selectedMap the map of selected filters to apply.
     * @param unMatchArticleList the unmatched article list after db filter.
     *
     * @return the list of filtered articles with categories filter.
     */
    private suspend fun filterCategories(selectedMap: Map<Int, MutableList<String>>,
                                         unMatchArticleList: List<Article>
    ): List<Article> = withContext(Dispatchers.Default) {

        val filterCategories = mutableListOf<Article>()

        val themeList = selectedMap[1] ?: mutableListOf()
        val sectionList = selectedMap[2] ?: mutableListOf()

        if (themeList.isEmpty() && sectionList.isEmpty())
            return@withContext filterCategories

        unMatchArticleList.forEach article@{ article ->
            var themeMatch = false
            var sectionMatch = false
            val catList = deConcatenateStringToMutableList(article.categories)
            catList.forEach {
                if (it.isBlank()) return@article
                if (themeList.contains(it)) themeMatch = true
                if (sectionList.contains(it)) sectionMatch = true
            }
            if (themeList.isEmpty()) themeMatch = true
            if (sectionList.isEmpty()) sectionMatch = true

            if (themeMatch && sectionMatch)
                filterCategories.add(article)
        }

        return@withContext filterCategories
    }
}