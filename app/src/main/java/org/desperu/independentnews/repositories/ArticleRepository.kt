package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.utils.Utils

/**
 * Article  Repository interface to get data from others repositories.
 *
 * @author Desperu
 */
interface ArticleRepository {

    /**
     * Update top story values if needed in database
     */
    suspend fun updateTopStory(rssArticleList: List<Article>)

    /**
     * Persists (update the existing ones, and insert the non-existing ones) the articles in database.
     *
     * @param articleList the articles list to persist.
     */
    suspend fun persist(articleList: List<Article>)

    /**
     * Returns the list of filtered articles from database.
     *
     * @param selectedMap the map of selected filters to apply.
     * @param actualList the actual list of article shown in the recycler view.
     *
     * @return the list of filtered articles from the database.
     */
    suspend fun getFilteredListFromDB(selectedMap: Map<Int, MutableList<String>>,
                                              actualList: List<Article>
    ): List<Article>

    /**
     * Returns the list of filtered articles with categories filter.
     *
     * @param selectedMap the map of selected filters to apply.
     * @param unMatchArticleList the unmatched article list after db filter.
     *
     * @return the list of filtered articles with categories filter.
     */
    suspend fun filterCategories(selectedMap: Map<Int, MutableList<String>>,
                                         unMatchArticleList: List<Article>
    ): List<Article>
}

/**
 * Implementation of the Article Repository interface.
 *
 * @author Desperu
 *
 * @property sourceRepository               the repository access for source services.
 * @property articleDao                     the database access object for article.
 *
 * @constructor Instantiates a new ArticleRepositoryImpl.
 *
 * @param sourceRepository                  the repository access for source services to set.
 * @param articleDao                        the database access object for article to set.
 */
class ArticleRepositoryImpl(
    private val sourceRepository: SourceRepository,
    private val articleDao: ArticleDao
): ArticleRepository {

    // FOR DATA
    private var sources: List<Source>? = null

    /**
     * Persists (update the existing ones, and insert the non-existing ones) the articles in database.
     *
     * @param articleList the articles list to persist.
     */
    override suspend fun persist(articleList: List<Article>) = withContext(Dispatchers.IO) {
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

        setSources()
        articlesToInsert.forEach { article ->
            val sourceId = sources?.find { it.name == article.sourceName }?.id
            sourceId?.let { article.sourceId = it }
        }
        articleDao.insertArticles(*articlesToInsert.toTypedArray())
    }

    /**
     * Update top story values if needed in database.
     *
     * @param rssArticleList the rss article list.
     */
    override suspend fun updateTopStory(rssArticleList: List<Article>) = withContext(Dispatchers.IO) {
        if (rssArticleList.isNotEmpty()) {
            articleDao.markIsTopStory(*rssArticleList.map { it.url }.toTypedArray())

            val topStoryDB = articleDao.getTopStory()
            val notTopStory = topStoryDB.filterNot { !rssArticleList.contains(it) }

            if (!notTopStory.isNullOrEmpty())
                articleDao.markIsNotTopStory(*notTopStory.map { it.id }.toLongArray())
        }
    }

    /**
     * Returns the list of filtered articles from database.
     *
     * @param selectedMap the map of selected filters to apply.
     * @param actualList the actual list of article shown in the recycler view.
     *
     * @return the list of filtered articles from the database.
     */
    override suspend fun getFilteredListFromDB(selectedMap: Map<Int, MutableList<String>>,
                                              actualList: List<Article>
    ): List<Article> = withContext(Dispatchers.IO) {

        val sourcesList = selectedMap[0]
        val sources = if (!sourcesList.isNullOrEmpty()) sourcesList else sources?.map { it.name }!!
        val themes = selectedMap[1]
        val sectionList = selectedMap[2]
        val sections = if (!sectionList.isNullOrEmpty()) Utils.deConcatenateStringToMutableList(sectionList[0])
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
    override suspend fun filterCategories(selectedMap: Map<Int, MutableList<String>>,
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
            val catList = Utils.deConcatenateStringToMutableList(article.categories)
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

    // -----------------
    // UTILS
    // -----------------

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
    private suspend fun setSources() = withContext(Dispatchers.IO) {
        if (sources.isNullOrEmpty())
            sources = sourceRepository.getEnabledSources()
    }
}