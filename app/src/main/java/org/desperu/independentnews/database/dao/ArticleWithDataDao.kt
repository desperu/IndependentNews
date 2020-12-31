package org.desperu.independentnews.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.desperu.independentnews.models.database.ArticleWithData

/**
 * The database access object for article with all data.
 */
@Dao
interface ArticleWithDataDao {

    /**
     * Returns the article with data from database, ordered for the given article id.
     *
     * @param id the unique identifier of the article to get from database.
     *
     * @return the corresponding article with data.
     */
    @Transaction
    @Query("SELECT * FROM article WHERE id=:id")
    suspend fun getArticleWithData(id: Long): ArticleWithData

    /**
     * Returns the article with all data from database.
     *
     * @return the article with all data from database.
     */
    @Transaction
    @Query("SELECT * FROM article")
    suspend fun getAll(): List<ArticleWithData>
}