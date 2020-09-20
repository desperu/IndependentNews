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
     * Returns the articles for which the url are in the given list.
     *
     * @param urls the list of urls of the articles to return.
     *
     * @return the articles for which the url are in the given list.
     */
    @Transaction
    @Query("SELECT * FROM article WHERE url IN (:urls)")
    suspend fun getWhereUrlsIn(urls: List<String>): List<Article>

    /**
     * Returns the articles for which the url are in the given list ordered from the most recent to the oldest.
     *
     * @param urls the list of urls of the articles to return.
     *
     * @return the articles for which the url are in the given list ordered from the most recent to the oldest.
     */
    @Transaction
    @Query("SELECT * FROM article WHERE url IN (:urls) ORDER BY publishedDate DESC ")
    suspend fun getWhereUrlsInSorted(urls: List<String>): List<Article>

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
     * @param title          the title of the article.
     * @param subtitle       the subtitle of the article.
     * @param publishedDate  the date (timestamp) when the article has been published to set.
     * @param article        the body of the article.
     * @param categories     the categories of the article.
     * @param description    the description of the article.
     * @param imageUrl       the image url of the article.
     * @param css            the css style of the article.
     * @param url            the url of the article
     */
    @Query("UPDATE article SET title=:title, subtitle=:subtitle, publishedDate=:publishedDate, article=:article, categories=:categories, description=:description, imageUrl=:imageUrl, css=:css WHERE url=:url")
    suspend fun update(title: String, subtitle: String, publishedDate: Long, article: String,
                       categories: String, description: String, imageUrl: String, css: String, url: String)

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