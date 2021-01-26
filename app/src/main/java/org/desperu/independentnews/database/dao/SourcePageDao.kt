package org.desperu.independentnews.database.dao

import androidx.room.*
import org.desperu.independentnews.models.database.SourcePage

/**
 * The database access object for source page.
 */
@Dao
interface SourcePageDao {

    /**
     * Returns the source page from database ordered for the given source page id.
     * @param id the unique identifier of the source page to get from database.
     * @return the corresponding source page.
     */
    @Query("SELECT * FROM SourcePage WHERE id=:id")
    suspend fun getSourcePage(id: Long): SourcePage

    /**
     * Returns the source page from database ordered for the given url.
     * @param url the url of the source page to get from database.
     * @return the corresponding source page.
     */
    @Query("SELECT * FROM SourcePage WHERE url=:url")
    suspend fun getSourcePage(url: String): SourcePage?

    /**
     * Returns the source page from database ordered for the given source page id.
     * @param sourceId the unique identifier of the source to get it's source pages from database.
     * @return the corresponding source page.
     */
    @Query("SELECT * FROM SourcePage WHERE sourceId=:sourceId")
    suspend fun getPagesForSourceId(sourceId: Long): List<SourcePage>

    /**
     * Returns the source page list from database.
     * @return the source page list from database.
     */
    @Transaction // TODO remove useless transaction for all dao ?????
    @Query("SELECT * FROM SourcePage")
    suspend fun getAll(): List<SourcePage>

    /**
     * Inserts the given source page in database.
     * @param sourcePages the source pages to insert in database.
     * @return the id list of inserted source pages.
     */
    @Insert
    suspend fun insertSourcePages(vararg sourcePages: SourcePage): List<Long>

    /**
     * Update the given source page in database.
     * @param sourcePage the source page to update in database.
     * @return the number of row affected.
     */
    @Update
    suspend fun updateSourcePage(sourcePage: SourcePage): Int

    /**
     * Delete the source page in database for the given name.
     * @param id the unique identifier of the source page to delete in database.
     * @return the number of row affected.
     */
    @Query("DELETE FROM SourcePage WHERE id=:id")
    suspend fun deleteSourcePage(id: Long): Int
}