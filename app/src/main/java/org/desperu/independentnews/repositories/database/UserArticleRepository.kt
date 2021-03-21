package org.desperu.independentnews.repositories.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.database.dao.FavoriteDao
import org.desperu.independentnews.database.dao.PausedDao
import org.desperu.independentnews.database.dao.SourceDao
import org.desperu.independentnews.extension.setSourceForEach
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Favorite
import org.desperu.independentnews.models.database.Paused
import org.desperu.independentnews.models.database.Source
import org.koin.core.KoinComponent
import java.util.*

/**
 * User Article Repository interface to get data from Favorite and Paused database.
 *
 * @author Desperu
 */
interface UserArticleRepository {

    /**
     * Return the favorite for the given article id.
     *
     * @param articleId the article id of the favorite.
     *
     * @return the favorite for the given article id, null if not find.
     */
    suspend fun getFavoriteArticle(articleId: Long): Favorite?

    /**
     * Return the paused for the given article id.
     *
     * @param articleId the article id of the paused.
     *
     * @return the paused for the given article id, null if not find.
     */
    suspend fun getPausedArticle(articleId: Long): Paused?

    /**
     * Returns the list of all favorites from the database.
     *
     * @return the list of all favorites from the database.
     */
    suspend fun getAllFavorites(): List<Favorite>

    /**
     * Returns the list of all paused from the database.
     *
     * @return the list of all paused from the database.
     */
    suspend fun getAllPaused(): List<Paused>

    /**
     * Returns the list of all favorites articles from the database.
     *
     * @return the list of all favorites articles from the database.
     */
    suspend fun getAllFavoriteArticles(): List<Article>

    /**
     * Returns the list of all paused articles from the database.
     *
     * @return the list of all paused articles from the database.
     */
    suspend fun getAllPausedArticles(): List<Article>

    /**
     * Handle favorite state for the current article, in the database,
     * switch it's state, enabled disabled.
     *
     * @param articleId the unique identifier of the article.
     *
     * @return true if is favorite after change, false if not.
     */
    suspend fun handleFavorite(articleId: Long): Boolean

    /**
     * Handle paused state for the current article, in the database,
     * switch it's state, enabled disabled.
     *
     * @param articleId         the unique identifier of the article.
     * @param scrollYPercent    the scroll y percent value, bind with text ratio.
     *
     * @return true if is paused after change, false if not.
     */
    suspend fun handlePaused(articleId: Long, scrollYPercent: Float): Boolean

    /**
     * Returns the list of user articles state.
     *
     * @param articleList the article list for which get state.
     *
     * @return the list of user articles state.
     */
    suspend fun getUserArticlesState(articleList: List<Article>): List<Pair<String, Any>>

    /**
     * Restore the user articles state.
     *
     * @param articlesState the state list of the user articles.
     *
     * @return the user articles state.
     */
    suspend fun restoreUserArticlesState(articlesState: List<Pair<String, Any>>)
}

/**
 * Implementation of the User Article Repository interface.
 *
 * @author Desperu
 *
 * @property favoriteDao    the database access for favorite.
 * @property pausedDao      the database access for paused.
 * @property articleDao     the database access for article.
 * @property sourceDao      the database access for source.
 *
 * @constructor Instantiates a new UserArticleRepositoryImpl.
 *
 * @param favoriteDao       the database access for favorite to set.
 * @param pausedDao         the database access for paused to set.
 * @param articleDao        the database access for article to set.
 * @param sourceDao         the database access for source to set.
 */
class UserArticleRepositoryImpl(
    private val favoriteDao: FavoriteDao,
    private val pausedDao: PausedDao,
    private val articleDao: ArticleDao,
    private val sourceDao: SourceDao
): UserArticleRepository, KoinComponent {

    /**
     * Return the favorite for the given article id.
     *
     * @param articleId the article id of the favorite.
     *
     * @return the favorite for the given article id, null if not find.
     */
    override suspend fun getFavoriteArticle(articleId: Long): Favorite? = withContext(Dispatchers.IO) {
        return@withContext favoriteDao.getFavoriteForArticleId(articleId)
    }

    /**
     * Return the paused for the given article id.
     *
     * @param articleId the article id of the paused.
     *
     * @return the paused for the given article id, null if not find.
     */
    override suspend fun getPausedArticle(articleId: Long): Paused? = withContext(Dispatchers.IO) {
        return@withContext pausedDao.getPausedForArticleId(articleId)
    }

    /**
     * Returns the list of all favorites from the database.
     *
     * @return the list of all favorites from the database.
     */
    override suspend fun getAllFavorites(): List<Favorite> = withContext(Dispatchers.IO) {
        return@withContext favoriteDao.getAll()
    }

    /**
     * Returns the list of all paused from the database.
     *
     * @return the list of all paused from the database.
     */
    override suspend fun getAllPaused(): List<Paused> = withContext(Dispatchers.IO) {
        return@withContext pausedDao.getAll()
    }

    /**
     * Returns the list of all favorites articles from the database.
     *
     * @return the list of all favorites articles from the database.
     */
    override suspend fun getAllFavoriteArticles(): List<Article> = withContext(Dispatchers.IO) {
        val articleIds = getAllFavorites().map { it.articleId }
        val articles = articleDao.getWhereIdsIn(articleIds).setSourceForEach(getSources())

        return@withContext reorderArticles(articles, articleIds)
    }

    /**
     * Returns the list of all paused articles from the database.
     *
     * @return the list of all paused articles from the database.
     */
    override suspend fun getAllPausedArticles(): List<Article> = withContext(Dispatchers.IO) {
        val articleIds = getAllPaused().map { it.articleId }
        val articles = articleDao.getWhereIdsIn(articleIds).setSourceForEach(getSources())

        return@withContext reorderArticles(articles, articleIds)
    }

    /**
     * Handle favorite state for the current article, in the database,
     * switch it's state, enabled disabled.
     *
     * @param articleId the unique identifier of the article.
     *
     * @return true if is favorite after change, false if not.
     */
    override suspend fun handleFavorite(articleId: Long): Boolean = withContext(Dispatchers.IO) {
        val favorite = getFavoriteArticle(articleId)
        val isFavorite = favorite != null
        val newFavorite = Favorite(
            articleId = articleId,
            creationDate = Calendar.getInstance().timeInMillis
        )

        if (!isFavorite) favoriteDao.insertFavorite(newFavorite)
        else favoriteDao.deleteFavorite(favorite!!)

        return@withContext !isFavorite
    }

    /**
     * Handle paused state for the current article, in the database,
     * switch it's state, enabled disabled.
     *
     * @param articleId         the unique identifier of the article.
     * @param scrollYPercent    the scroll y percent value, bind with text ratio.
     *
     * @return true if is paused after change, false if not.
     */
    override suspend fun handlePaused(
        articleId: Long,
        scrollYPercent: Float
    ): Boolean = withContext(Dispatchers.IO) {

        val paused = getPausedArticle(articleId)
        val isPaused = paused != null
        val newPaused = Paused(
            articleId = articleId,
            scrollPosition = scrollYPercent,
            creationDate = Calendar.getInstance().timeInMillis
        )

        if (!isPaused) pausedDao.insertPaused(newPaused)
        else pausedDao.deletePaused(paused!!)

        return@withContext !isPaused
    }

    /**
     * Returns the list of user articles state, favorite, paused and is read.
     *
     * @param articleList the article list for which get state.
     *
     * @return the list of user articles state.
     */
    override suspend fun getUserArticlesState(
        articleList: List<Article>
    ): List<Pair<String, Any>> = withContext(Dispatchers.IO) {

        val articlesState = mutableListOf<Pair<String, Any>>()

        articleList.forEach {
            val id = articleDao.getWhereTitlesIn(listOf(it.title)).getOrNull(0)?.id
            val title = it.title

            id?.let {
                val favorite = getFavoriteArticle(id)
                val paused = getPausedArticle(id)
                val isRead = articleDao.getArticle(id).read

                if (favorite != null) articlesState.add(Pair(title, favorite))
                if (paused != null) articlesState.add(Pair(title, paused))
                if (isRead) articlesState.add(Pair(title, isRead))
            }
        }

        return@withContext articlesState
    }

    /**
     * Restore the user articles state, favorite, paused an is read.
     *
     * @param articlesState the state list of the user articles.
     *
     * @return the user articles state.
     */
    override suspend fun restoreUserArticlesState(
        articlesState: List<Pair<String, Any>>
    ) = withContext(Dispatchers.IO) {

        articlesState.forEach {
            val articleId = articleDao.getWhereTitlesIn(listOf(it.first)).getOrNull(0)?.id
            val state = it.second

            articleId?.let {
                when (state) {
                    is Favorite -> {
                        state.articleId = articleId
                        favoriteDao.insertFavorite(state)
                    }
                    is Paused -> {
                        state.articleId = articleId
                        pausedDao.insertPaused(state)
                    }
                    is Boolean -> articleDao.markAsRead(articleId)
                }
            }
        }
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Get enabled sources from the database.
     * Get from database on each call to handle source state change, from a user action.
     */
    private suspend fun getSources(): List<Source> = withContext(Dispatchers.IO) {
        sourceDao.getEnabled()
    }

    /**
     * Reorder the given article list with the given ordered ids.
     *
     * @param articles      the article list to reorder.
     * @param orderedIds    the ordered ids to apply.
     *
     * @return the reordered article list.
     */
    private fun reorderArticles(articles: List<Article>?, orderedIds: List<Long>): List<Article> {
        val orderedArticles = mutableListOf<Article>()
        orderedIds.forEachIndexed { index, id ->
            val article = articles?.find { it.id == id }
            article?.let { orderedArticles.add(index, it) }
        }

        return orderedArticles
    }
}