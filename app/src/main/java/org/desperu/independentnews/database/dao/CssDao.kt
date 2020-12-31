package org.desperu.independentnews.database.dao

import androidx.room.*
import org.desperu.independentnews.models.database.Css

/**
 * The database access object for css.
 */
@Dao
interface CssDao {

    /**
     * Returns the css from database ordered for the given css id.
     * @param id the unique identifier of the css to get from database.
     * @return the corresponding css.
     */
    @Query("SELECT * FROM css WHERE id=:id")
    suspend fun getCss(id: Long): Css

    /**
     * Returns the css from database ordered for the given article id.
     * @param articleId the unique identifier of the article for which get the css from database.
     * @return the corresponding css.
     */
    @Query("SELECT * FROM css WHERE articleId=:articleId")
    suspend fun getArticleCss(articleId: Long): Css

    /**
     * Returns the css list from database.
     * @return the css list from database.
     */
    @Query("SELECT * FROM css")
    suspend fun getAll(): List<Css>

    /**
     * Inserts the given css in database.
     * @param css the css to insert in database.
     * @return the id list of inserted css.
     */
    @Insert
    suspend fun insertCss(vararg css: Css): List<Long>

    /**
     * Update the given css in database.
     * @param css the css to update in database.
     * @return the number of row affected.
     */
    @Update
    suspend fun updateCss(css: Css): Int

    /**
     * Delete the css in database for the given article id.
     * @param articleId the unique identifier of the article for which delete the css in database.
     * @return the number of row affected.
     */
    @Query("DELETE FROM css WHERE articleId=:articleId")
    suspend fun deleteArticleCss(articleId: Long): Int
}