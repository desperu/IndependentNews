package org.desperu.independentnews.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.desperu.independentnews.models.database.SourceWithData

/**
 * The database access object for source with all data.
 */
@Dao
interface SourceWithDataDao {

    /**
     * Returns the source with all data from database.
     *
     * @return the source with all data from database.
     */
    @Transaction
    @Query("SELECT * FROM source")
    suspend fun getAll(): List<SourceWithData>
}