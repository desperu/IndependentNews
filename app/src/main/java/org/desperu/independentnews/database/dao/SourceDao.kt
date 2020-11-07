package org.desperu.independentnews.database.dao

import androidx.room.*
import org.desperu.independentnews.models.Source

/**
 * The database access object for source.
 */
@Dao
interface SourceDao {

    /**
     * Returns the source from database ordered for the given source id.
     * @param id the unique identifier of the source to get from database.
     * @return the corresponding source.
     */
    @Query("SELECT * FROM source WHERE id=:id")
    suspend fun getSource(id: Long): Source

    /**
     * Returns the source list from database.
     * @return the source list from database.
     */
    @Transaction
    @Query("SELECT * FROM source")
    suspend fun getAll(): List<Source>

    /**
     * Returns the sources that are enabled.
     * @return the sources that are enabled.
     */
    @Transaction
    @Query("SELECT * FROM source WHERE isEnabled=1")
    suspend fun getEnabled(): List<Source>

    /**
     * Inserts the given source in database.
     * @param sources the sources to insert in database.
     * @return the id list of inserted sources.
     */
    @Insert
    suspend fun insertSources(vararg sources: Source): List<Long>

    /**
     * Set the source with the given name as enabled in database.
     *
     * @param name the name of the source to set as enabled.
     * @param isEnabled the value to update.
     */
    @Query("UPDATE source SET isEnabled=:isEnabled WHERE name=:name")
//    @Query("UPDATE source SET isEnabled=1 WHERE name=:name")
    suspend fun setIsEnabled(name: String, isEnabled: Boolean)

    /**
     * Update the given source in database.
     * @param source the source to update in database.
     * @return the number of row affected.
     */
    @Update
    suspend fun updateSource(source: Source): Int

    /**
     * Delete the source in database for the given name.
     * @param name the name of the source to delete in database.
     * @return the number of row affected.
     */
    @Query("DELETE FROM source WHERE name=:name")
    suspend fun deleteSource(name: String): Int
}