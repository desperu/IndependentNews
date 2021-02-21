package org.desperu.independentnews.database.dao

import androidx.room.*
import org.desperu.independentnews.models.database.Favorite

/**
 * The database access object for favorite.
 */
@Dao
interface FavoriteDao {

    /**
     * Returns the favorite from database ordered for the given favorite id.
     * @param id the unique identifier of the favorite to get from database.
     * @return the corresponding favorite.
     */
    @Query("SELECT * FROM favorite WHERE id=:id")
    suspend fun getFavorite(id: Long): Favorite

    /**
     * Returns the favorite from database ordered for the given article id.
     * @param articleId the article id for which get the favorite from database.
     * @return the corresponding favorite.
     */
    @Query("SELECT * FROM favorite WHERE articleId=:articleId")
    suspend fun getFavoriteForArticleId(articleId: Long): Favorite?

    /**
     * Returns the favorite list from database.
     * @return the favorite list from database.
     */
    @Query("SELECT * FROM favorite")
    suspend fun getAll(): List<Favorite>

    /**
     * Inserts the given favorite in database.
     * @param favorite the favorite to insert in database.
     * @return the id list of inserted favorite.
     */
    @Insert
    suspend fun insertFavorite(vararg favorite: Favorite): List<Long>

    /**
     * Update the given favorite in database.
     * @param favorite the favorite to update in database.
     * @return the number of row affected.
     */
    @Update
    suspend fun updateFavorite(favorite: Favorite): Int

    /**
     * Delete the given favorite in the database.
     * @param favorite the favorite to delete in database.
     * @return the number of row affected.
     */
    @Delete
    suspend fun deleteFavorite(vararg favorite: Favorite): Int
}