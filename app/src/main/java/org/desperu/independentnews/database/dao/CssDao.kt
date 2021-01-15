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
     * Returns the css from database ordered for the given css url.
     * @param url the url of the css to get from database.
     * @return the corresponding css.
     */
    @Query("SELECT * FROM css WHERE url=:url")
    suspend fun getCssForUrl(url: String): Css?

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
     * Delete the given css in the database.
     * @param css the css to delete in database.
     * @return the number of row affected.
     */
    @Delete
    suspend fun deleteCss(vararg css: Css): Int
}