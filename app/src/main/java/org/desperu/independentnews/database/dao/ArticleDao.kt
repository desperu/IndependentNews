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
     * Returns the article from database ordered for given id.
     * @param id the id of the article to get from database.
     * @return the cursor access for the corresponding article.
     */
    @Query("SELECT * FROM article WHERE id = :id")
    fun getArticleWithCursor(id: Long): Cursor?

    /**
     * Returns the article from database ordered for given id.
     * @param id the id of the article to get from database.
     * @return the corresponding article.
     */
    @Query("SELECT * FROM article WHERE id = :id")
    suspend fun getArticle(id: Long): Article

    /**
     * Returns the article list from database ordered from the most recent to the oldest.
     * @return the article list from database ordered from the most recent to the oldest.
     */
    @Transaction
    @Query("SELECT * FROM article ORDER BY publishedDate DESC")
    suspend fun getAll(): List<Article>

    /**
     * Returns the articles with media metadata for which the unique identifiers are in the given list.
     *
     * @param ids the list of unique identifiers of the articles to return.
     *
     * @return the articles for which the unique identifier are in the given list.
     */
    @Transaction
    @Query("SELECT * FROM article WHERE id IN (:ids)")
    suspend fun getWhereIdIn(ids: List<Long>): List<Article>

    /**
     * Inserts the given article in database.
     * @param articles the articles to insert in database.
     * @return the row id for the inserted article.
     */
    @Insert
    suspend fun insertArticles(vararg articles: Article)//: Long

    /**
     * Mark the article with the given unique identifier as read in database.
     *
     * @param id the unique identifier of the viewed article to mark as read.
     */
    @Query("UPDATE article SET read=1 WHERE id=:id")
    suspend fun markAsRead(id: Long)

    /**
     * Update the title, the author line and the published date of the article with the given unique identifier.
     *
     * @param title          the title of the viewed article.
     * @param publishedDate  the date (timestamp) when the viewed article has been published to set.
     * @param article        the body of the article.
     * @param categories     the categories of the article.
     * @param description    the description of the article.
     * @param imageUrl       the image url of the article.
     * @param url            the url of the viewed article
     * @param id             the unique identifier of the viewed article to update.
     */
    @Query("UPDATE article SET title=:title, publishedDate=:publishedDate, article=:article, categories=:categories, description=:description, imageUrl=:imageUrl, url=:url WHERE id=:id")
    suspend fun update(title: String, publishedDate: Long, article: String, categories: String,
                       description: String, imageUrl: String, url: String, id: Long)

    /**
     * Update the given article in database.
     * @param article the article to update in database.
     * @return the number of row affected.
     */
    @Update
    suspend fun updateArticle(article: Article): Int

    /**
     * Delete the article in database for the given id.
     * @param id the id of the article to delete in database.
     * @return the number of row affected.
     */
    @Query("DELETE FROM Article WHERE id = :id")
    suspend fun deleteArticle(id: Long): Int
}