package org.desperu.independentnews.database.dao

import androidx.room.*
import org.desperu.independentnews.models.SourcePage

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

//    /**
//     * Returns the source pages that are enabled.
//     * @return the source pages that are enabled.
//     */
//    @Transaction
//    @Query("SELECT * FROM SourcePage WHERE isPrimary=1")
//    suspend fun getEnabled(): List<SourcePage>

    /**
     * Inserts the given source page in database.
     * @param sourcePages the source pages to insert in database.
     * @return the id list of inserted source pages.
     */
    @Insert
    suspend fun insertSourcePages(vararg sourcePages: SourcePage): List<Long>

//    /**
//     * Set the source page with the given name as enabled in database.
//     *
//     * @param name the name of the source page to set as enabled.
//     * @param isEnabled the value to update.
//     */
//    @Query("UPDATE SourcePage SET isEnabled=:isEnabled WHERE name=:name")
////    @Query("UPDATE source page SET isEnabled=1 WHERE name=:name")
//    suspend fun setIsEnabled(name: String, isEnabled: Boolean)

//    /**
//     * Update the editorial url, the editorial and the image url of the source page with the given unique identifier.
//     *
//     * @param editorialUrl    the editorial url of the source page.
//     * @param editorial       the editorial of the source page.
//     * @param image           the unique identifier of the source page image.
//     * @param name            the name of the source page.
//     */
//    @Query("UPDATE SourcePage SET editorialUrl=:editorialUrl, editorial=:editorial, imageId=:image WHERE name=:name")
//    suspend fun update(editorialUrl: String, editorial: String, image: Int, name: String): Int

    /**
     * Update the given source page in database.
     * @param sourcePage the source page to update in database.
     * @return the number of row affected.
     */
    @Update
    suspend fun updateSource(sourcePage: SourcePage): Int

    /**
     * Delete the source page in database for the given name.
     * @param id the unique identifier of the source page to delete in database.
     * @return the number of row affected.
     */
    @Query("DELETE FROM SourcePage WHERE id=:id")
    suspend fun deleteSource(id: Long): Int
}