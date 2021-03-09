package org.desperu.independentnews.repositories.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.FavoriteDao
import org.desperu.independentnews.database.dao.PausedDao
import org.desperu.independentnews.models.database.Favorite
import org.desperu.independentnews.models.database.Paused
import org.koin.core.KoinComponent

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
     * Returns the list of all favorites with data from the database.
     *
     * @return the list of all favorites with data from the database.
     */
    suspend fun getAllFavorites(): List<Favorite>

    /**
     * Returns the list of all paused with data from the database.
     *
     * @return the list of all paused with data from the database.
     */
    suspend fun getAllPaused(): List<Paused>

    /**
     * Insert the given favorite in the database.
     *
     * @param favorite the favorite to insert.
     *
     * @return the row id of inserted favorite.
     */
    suspend fun insertFavorite(vararg favorite: Favorite): List<Long>

    /**
     * Insert the given paused in the database.
     *
     * @param paused the paused to insert.
     *
     * @return the row id of inserted paused.
     */
    suspend fun insertPaused(vararg paused: Paused): List<Long>

    /**
     * Delete favorite from the database.
     *
     * @param favorite the favorite to delete.
     *
     * @return the number of row affected.
     */
    suspend fun deleteFavorite(vararg favorite: Favorite): Int

    /**
     * Delete paused from the database.
     *
     * @param paused the paused to delete.
     *
     * @return the number of row affected.
     */
    suspend fun deletePaused(vararg paused: Paused): Int
}

/**
 * Implementation of the User Article Repository interface.
 *
 * @author Desperu
 *
 * @property favoriteDao    the database access for favorite.
 * @property pausedDao      the database access for paused to set.
 *
 * @constructor Instantiates a new UserArticleRepositoryImpl.
 *
 * @param favoriteDao       the database access for favorite to set.
 * @param pausedDao         the database access for paused to set.
 */
class UserArticleRepositoryImpl(
    private val favoriteDao: FavoriteDao,
    private val pausedDao: PausedDao
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
     * Returns the list of all favorites with data from the database.
     *
     * @return the list of all favorites with data from the database.
     */
    override suspend fun getAllFavorites(): List<Favorite> = withContext(Dispatchers.IO) {
        return@withContext favoriteDao.getAll()
    }

    /**
     * Returns the list of all paused with data from the database.
     *
     * @return the list of all paused with data from the database.
     */
    override suspend fun getAllPaused(): List<Paused> = withContext(Dispatchers.IO) {
        return@withContext pausedDao.getAll()
    }

    /**
     * Insert the given favorite in the database.
     *
     * @param favorite the favorite to insert.
     *
     * @return the row id of inserted favorite.
     */
    override suspend fun insertFavorite(vararg favorite: Favorite): List<Long> = withContext(Dispatchers.IO) {
        return@withContext favoriteDao.insertFavorite(*favorite)
    }

    /**
     * Insert the given paused in the database.
     *
     * @param paused the paused to insert.
     *
     * @return the row id of inserted paused.
     */
    override suspend fun insertPaused(vararg paused: Paused): List<Long> = withContext(Dispatchers.IO) {
        return@withContext pausedDao.insertPaused(*paused)
    }

    /**
     * Delete favorite from the database.
     *
     * @param favorite the favorite to delete.
     *
     * @return the number of row affected.
     */
    override suspend fun deleteFavorite(vararg favorite: Favorite): Int = withContext(Dispatchers.IO) {
        return@withContext favoriteDao.deleteFavorite()
    }

    /**
     * Delete paused from the database.
     *
     * @param paused the paused to delete.
     *
     * @return the number of row affected.
     */
    override suspend fun deletePaused(vararg paused: Paused): Int = withContext(Dispatchers.IO) {
        return@withContext pausedDao.deletePaused(*paused)
    }
}