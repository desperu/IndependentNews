package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.utils.*

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
     * @param sources       the parsed sources filter value.
     * @param themes        the parsed themes filter value.
     * @param sections      the parsed sections filter value.
     * @param dates         the parsed dates filter value.
     * @param urls          the urls list to search into.
     *
     * @return the list of filtered articles from the database.
     */
    suspend fun getFilteredListFromDB(sources: List<String>,
                                      themes: List<String>?,
                                      sections: List<String>?,
                                      dates: List<Long>,
                                      urls: List<String>
    ): List<Article>

    /**
     * Returns the list of filtered articles with categories filter.
     *
     * @param parsedMap the parsed map of selected filters to apply.
     * @param unMatchArticleList the unmatched article list after db filter.
     *
     * @return the list of filtered articles with categories filter.
     */
    suspend fun filterCategories(parsedMap: Map<Int, List<String>>,
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
     * @param sources       the parsed sources filter value.
     * @param themes        the parsed themes filter value.
     * @param sections      the parsed sections filter value.
     * @param dates         the parsed dates filter value.
     * @param urls          the urls list to search into.
     *
     * @return the list of filtered articles from the database.
     */
    override suspend fun getFilteredListFromDB(sources: List<String>,
                                                themes: List<String>?,
                                                sections: List<String>?,
                                                dates: List<Long>,
                                                urls: List<String>
    ): List<Article> = withContext(Dispatchers.IO) {
        when {
            !themes.isNullOrEmpty() && !sections.isNullOrEmpty() ->
                articleDao.getFilteredListWithAll(sources, sections, themes, dates[0], dates[1], urls)
            !themes.isNullOrEmpty() ->
                articleDao.getFilteredListWithThemes(sources, themes, dates[0], dates[1], urls)
            !sections.isNullOrEmpty() ->
                articleDao.getFilteredListWithSections(sources, sections, dates[0], dates[1], urls)
            else ->
                articleDao.getFilteredList(sources, dates[0], dates[1], urls)
        }
    }

    /**
     * Returns the list of filtered articles with categories filter from database.
     *
     * @param parsedMap the map of parsed filters to apply.
     * @param unMatchArticleList the unmatched article list after db filter.
     *
     * @return the list of filtered articles with categories filter from database.
     */
    override suspend fun filterCategories(parsedMap: Map<Int, List<String>>,
                                          unMatchArticleList: List<Article>
    ): List<Article> = withContext(Dispatchers.Default) {

        val filterCategories = mutableListOf<Article>()

        val dates = parsedMap.getValue(DATES).map { it.toLong() }

        parsedMap.getValue(CATEGORIES).forEach { category ->
            filterCategories.addAll(
                articleDao.getFilteredListWithCategory(
                    parsedMap.getValue(SOURCES),
                    "%$category%",
                    dates[0],
                    dates[1],
                    unMatchArticleList.map { it.id }
                )
            )
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