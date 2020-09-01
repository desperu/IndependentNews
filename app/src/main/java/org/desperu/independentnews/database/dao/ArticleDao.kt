package org.desperu.independentnews.database.dao

import android.database.Cursor
import androidx.room.*
import org.desperu.independentnews.models.Article

/**
 * The database access object for article.
 */
@Dao
interface ArticleDao {

    /**
     * Returns the article from database ordered for given article id.
     * @param articleId the article id to get the corresponding article from database.
     * @return the cursor access for the corresponding article.
     */
    @Query("SELECT * FROM Article WHERE id = :articleId")
    fun getArticleWithCursor(articleId: Long): Cursor?

    /**
     * Returns the article from database ordered for given article id.
     * @param articleId the article id to get the corresponding article from database.
     * @return the corresponding article.
     */
    @Query("SELECT * FROM Article WHERE id = :articleId")
    suspend fun getArticle(articleId: Long): Article

    /**
     * Returns the article list from database ordered from the most recent to the oldest.
     * @return the article list from database ordered from the most recent to the oldest.
     */
    @Transaction
    @Query("SELECT * FROM Article ORDER BY date DESC")
    suspend fun getAll(): List<Article>

    /**
     * Inserts the given article in database.
     * @param article the article to insert in database.
     * @return the row id for the inserted article.
     */
    @Insert
    suspend fun insertArticle(article: Article): Long

    /**
     * Update the given article in database.
     * @param article the article to update in database.
     * @return the number of row affected.
     */
    @Update
    suspend fun updateArticle(article: Article): Int

    /**
     * Delete the article in database for the given article id.
     * @param articleId the article id to delete in database.
     * @return the number of row affected.
     */
    @Query("DELETE FROM Article WHERE id = :articleId")
    suspend fun deleteArticle(articleId: Long): Int
}