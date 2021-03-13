package org.desperu.independentnews.repositories.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.storeDelayMillis
import java.util.*

/**
 * Article  Repository interface to get data from others repositories.
 *
 * @author Desperu
 */
interface ArticleRepository {

    /**
     * Persists (update the existing ones, and insert the non-existing ones) the articles in database.
     *
     * @param articleList the articles list to persist.
     *
     * @return the id list of persisted articles.
     */
    suspend fun persist(articleList: List<Article>): List<Long>

    /**
     * Update top story values if needed in database
     *
     * @param rssArticleList    the rss article list.
     */
    suspend fun updateTopStory(rssArticleList: List<Article>)

    /**
     * Delete the older articles than the millis limit, in the database.
     *
     * @return the number of row affected.
     */
    suspend fun removeOldArticles(): Int

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
    suspend fun getFilteredListFromDB(
        sources: List<Long>,
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
    suspend fun filterCategories(
        parsedMap: Map<Int, List<String>>,
        unMatchArticleList: List<Article>
    ): List<Article>

    /**
     * Returns the new articles list, for which we need to fetch the full article page.
     *
     * @param articleList the article list from rss or categories to compare.
     *
     * @return the new articles list, for which we need to fetch the full article page.
     */
    suspend fun getNewArticles(articleList: List<Article>): List<Article>
}

/**
 * Implementation of the Article Repository interface.
 *
 * @author Desperu
 *
 * @property articleDao             the database access object for article.
 * @property sourceRepository       the repository access for source from the database.
 * @property userArticleRepository  the repository access for user article from the database.
 * @property prefs                  the shared preferences service interface witch provide access
 *                                  to the app shared preferences.
 * @property nowMillis              the time in millis for now.
 * @property storeDelay             the delay for which store the articles in the database.
 *
 * @constructor Instantiates a new ArticleRepositoryImpl.
 *
 * @param articleDao                the database access object for article to set.
 * @param sourceRepository          the repository access for source from the database to set.
 * @param userArticleRepository     the repository access for user article from the database to set.
 * @param prefs                     the shared preferences service interface witch provide access
 *                                  to the app shared preferences to set.
 */
class ArticleRepositoryImpl(
    private val articleDao: ArticleDao,
    private val sourceRepository: SourceRepository,
    private val userArticleRepository: UserArticleRepository,
    private val prefs: SharedPrefService
): ArticleRepository {

    // FOR DATA
    private val nowMillis get() = Calendar.getInstance().timeInMillis
    private val storeDelay get() = prefs.getPrefs().getInt(STORE_DELAY, STORE_DELAY_DEFAULT)

    /**
     * Persists (update the existing ones, and insert the non-existing ones) the articles in database.
     *
     * @param articleList the articles list to persist.
     *
     * @return the id list of persisted articles.
     */
    override suspend fun persist(articleList: List<Article>): List<Long> = withContext(Dispatchers.IO) {
        val urlsToUpdate = getExistingUrls(articleList)

        val articleListPair = articleList.partition { article -> urlsToUpdate.contains(article.url) }
        val articlesToUpdate = articleListPair.first
        val articlesToInsert = articleListPair.second

        val ids = articlesToUpdate.map { it.id }.toMutableList()
        updateArticles(articlesToUpdate)

        ids.addAll(insertArticles(articlesToInsert))

        return@withContext ids
    }

    /**
     * Update top story values if needed in database.
     * Must be called with the full top story source article list,
     * in each fetchRssArticles source repository function, before [getNewArticles] call,
     * to be able to handle top story articles.
     *
     * @param rssArticleList    the rss article list.
     */
    override suspend fun updateTopStory(rssArticleList: List<Article>) = withContext(Dispatchers.IO) {

        if (rssArticleList.isNotEmpty()) {
            val rssUrls = rssArticleList.map { it.url }
            articleDao.markIsTopStory(*rssUrls.toTypedArray())

            val sourceId = getSources().find { it.name == rssArticleList[0].source.name }?.id
            val topStoryDB = sourceId?.let { articleDao.getTopStory(listOf(it)) }
            val notTopStory = topStoryDB?.filter { !rssUrls.contains(it.url) }

            if (!notTopStory.isNullOrEmpty())
                articleDao.markIsNotTopStory(*notTopStory.map { it.id }.toLongArray())
        }
    }

    /**
     * Delete the older articles than the millis limit, in the database.
     * Don't remove user articles, favorite or paused.
     *
     * @return the number of row affected.
     */
    override suspend fun removeOldArticles(): Int = withContext(Dispatchers.IO) {
        val idsToNotRemove = mutableListOf<Long>()
        userArticleRepository.getAllFavoriteArticles()?.map { it.id }?.let { idsToNotRemove.addAll(it) }
        userArticleRepository.getAllPausedArticles()?.map { it.id }?.let { idsToNotRemove.addAll(it) }
        // TODO add user article download, when nav between articles.

        articleDao.removeOldArticles(storeDelayMillis(nowMillis, storeDelay), idsToNotRemove)
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
    override suspend fun getFilteredListFromDB(
        sources: List<Long>,
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
    override suspend fun filterCategories(
        parsedMap: Map<Int, List<String>>,
        unMatchArticleList: List<Article>
    ): List<Article> = withContext(Dispatchers.Default) {

        val filterCategories = mutableListOf<Article>()

        val dates = parsedMap.getValue(DATES).map { it.toLong() }

        parsedMap.getValue(CATEGORIES).forEach { category ->
            filterCategories.addAll(
                articleDao.getFilteredListWithCategory(
                    parsedMap.getValue(SOURCES).map { it.toLong() },
                    "%$category%",
                    dates[0],
                    dates[1],
                    unMatchArticleList.map { it.id }
                )
            )
        }

        return@withContext filterCategories
    }

    /**
     * Returns the new articles list, for which we need to fetch the full article page.
     *
     * @param articleList the article list from rss or categories to compare.
     *
     * @return the new articles list, for which we need to fetch the full article page.
     */
    override suspend fun getNewArticles(articleList: List<Article>): List<Article> = withContext(Dispatchers.IO) {
        val newArticles = mutableListOf<Article>()
        val urls = getExistingUrls(articleList)

        val articleListPair = articleList.partition { article -> urls.contains(article.url) }
        val inDb = articleListPair.first
        val notInDb = articleListPair.second

        inDb.forEach { article ->
            val dBDate = articleDao.getArticle(article.url)?.publishedDate ?: 0L

            if (article.publishedDate > dBDate)
                newArticles.add(article)
        }

        newArticles.addAll(notInDb.filter { it.publishedDate > storeDelayMillis(nowMillis, storeDelay) })

        return@withContext newArticles
    }

    // -----------------
    // STORE DATA
    // -----------------

    /**
     * Update articles in the database.
     *
     * @param articlesToUpdate  the list of the articles to update.
     */
    private suspend fun updateArticles(articlesToUpdate: List<Article>) = withContext(Dispatchers.IO) {

        articlesToUpdate.forEach { article ->
            val categories = if (article.isTopStory) article.categories
                             else articleDao.getArticle(article.url)?.categories ?: ""

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
                article.url
            )
        }
    }

    /**
     * Insert articles in the database, set the source id for each before store.
     * Handle url update with title support, save and restore user articles state.
     *
     * @param articlesToInsert the list of articles to insert in the database.
     *
     * @return the list of ids for the inserted articles.
     */
    private suspend fun insertArticles(articlesToInsert: List<Article>): List<Long> = withContext(Dispatchers.IO) {
        // Handle user articles state
        val removedStates = userArticleRepository.getUserArticlesState(articlesToInsert)
        // To prevent duplicate if an article's url was updated
        removeExistingTitles(articlesToInsert)

        articlesToInsert.forEach { article ->
            val sourceId = getSources().find { it.name == article.source.name }?.id
            sourceId?.let { article.sourceId = it }
        }

        val ids = articleDao.insertArticles(*articlesToInsert.toTypedArray())
        // Restore user articles state
        userArticleRepository.restoreUserArticlesState(removedStates)

        return@withContext ids
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
    private suspend fun getExistingUrls(articleList: List<Article>): List<String> = withContext(Dispatchers.IO) {
        val urls = articleList.map { article -> article.url }
        val listToUpdate = articleDao.getWhereUrlsIn(urls)
        listToUpdate.map { article -> article.url }
    }

    /**
     * Remove the articles from the given list to insert that already exists in database.
     * Needed in case of the url have changed, to prevent duplicates.
     *
     * @param articlesToInsert the articles to insert from which check existing title in database.
     */
    private suspend fun removeExistingTitles(articlesToInsert: List<Article>) = withContext(Dispatchers.IO) {
        val titles = articlesToInsert.map { article -> article.title }
        val listToRemove = articleDao.getWhereTitlesIn(titles)
        listToRemove.forEach { articleDao.deleteArticle(it.id) }
    }

    /**
     * Get enabled sources from the database.
     * Get from database on each call to handle source state change, from a user action.
     */
    private suspend fun getSources(): List<Source> = withContext(Dispatchers.IO) {
        sourceRepository.getEnabledSources()
    }
}