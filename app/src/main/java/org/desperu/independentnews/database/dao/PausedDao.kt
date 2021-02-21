package org.desperu.independentnews.database.dao

import androidx.room.*
import org.desperu.independentnews.models.database.Paused

/**
 * The database access object for paused.
 */
@Dao
interface PausedDao {

    /**
     * Returns the paused from database ordered for the given paused id.
     * @param id the unique identifier of the paused to get from database.
     * @return the corresponding paused.
     */
    @Query("SELECT * FROM paused WHERE id=:id")
    suspend fun getPaused(id: Long): Paused

    /**
     * Returns the paused from database ordered for the given article id.
     * @param articleId the article id for which get the paused from database.
     * @return the corresponding paused.
     */
    @Query("SELECT * FROM paused WHERE articleId=:articleId")
    suspend fun getPausedForArticleId(articleId: Long): Paused?

    /**
     * Returns the paused list from database.
     * @return the paused list from database.
     */
    @Query("SELECT * FROM paused")
    suspend fun getAll(): List<Paused>

    /**
     * Inserts the given paused in database.
     * @param paused the paused to insert in database.
     * @return the id list of inserted paused.
     */
    @Insert
    suspend fun insertPaused(vararg paused: Paused): List<Long>

    /**
     * Update the given paused in database.
     * @param paused the paused to update in database.
     * @return the number of row affected.
     */
    @Update
    suspend fun updatePaused(paused: Paused): Int

    /**
     * Set the scroll position for the given article id in the database.
     *
     * @param articleId the unique identifier of the article to set the scroll position.
     * @param scrollPosition the value to update.
     */
    @Query("UPDATE paused SET scrollPosition=:scrollPosition WHERE articleId=:articleId")
    suspend fun setScrollPosition(articleId: Long, scrollPosition: Int)

    /**
     * Delete the given paused in the database.
     * @param paused the paused to delete in database.
     * @return the number of row affected.
     */
    @Delete
    suspend fun deletePaused(vararg paused: Paused): Int
}