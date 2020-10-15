package org.desperu.independentnews.database.dao

import android.database.Cursor
import androidx.room.*
import org.desperu.independentnews.models.Article

/**
 * The database access object for article.
 */
@Dao
interface ArticleDao {

    // -----------------
    // GETTERS
    // -----------------

    /**
     * Returns the article from database ordered for given id.
     *
     * @param id the id of the article to get from database.
     *
     * @return the cursor access for the corresponding article.
     */
    @Query("SELECT * FROM article WHERE id = :id")
    fun getArticleWithCursor(id: Long): Cursor?

    /**
     * Returns the article from database ordered for given id.
     *
     * @param id the id of the article to get from database.
     *
     * @return the corresponding article.
     */
    @Query("SELECT * FROM article WHERE id = :id")
    suspend fun getArticle(id: Long): Article

    /**
     * Returns the article from database ordered for given url.
     *
     * @param url the url of the article to get from database.
     *
     * @return the corresponding article.
     */
    @Query("SELECT * FROM article WHERE url = :url")
    suspend fun getArticle(url: String): Article

    /**
     * Returns the top story article list from database ordered from the most recent to the oldest.
     *
     * @return the top story article list from database ordered from the most recent to the oldest.
     */
    @Transaction
    @Query("SELECT * FROM article WHERE isTopStory = 1 ORDER BY publishedDate DESC")
    suspend fun getTopStory(): List<Article>

    /**
     * Returns the category article list from database ordered from the most recent to the oldest.
     *
     * @return the category article list from database ordered from the most recent to the oldest.
     */
    @Transaction
    @Query("SELECT * FROM article WHERE categories LIKE :categories OR section LIKE :categories OR theme LIKE :categories ORDER BY publishedDate DESC")
    suspend fun getCategory(categories: String): List<Article>

    /**
     * Returns the article list from database ordered from the most recent to the oldest.
     *
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

    // -----------------
    // FILTERS
    // -----------------

    /**
     * Returns the filtered article list from database ordered from the most recent to the oldest.
     *
     * @param sources       the sources list to search into database articles's data.
     * @param sections      the sections of article to get.
     * @param themes        the themes of article to get.
     * @param startDate     the published start date of article to get.
     * @param endDate       the published end date of article to get.
     * @param urls          the list of urls to search into.
     *
     * @return the filtered article list from database ordered from the most recent to the oldest.
     */
    @Transaction
    @Query("SELECT * FROM article WHERE sourceName IN (:sources) AND section IN (:sections) AND theme IN (:themes) AND (publishedDate BETWEEN :startDate AND :endDate) AND url IN (:urls) ORDER BY publishedDate DESC")
    suspend fun getFilteredList(sources: List<String>,
                                sections: List<String>,
                                themes: List<String>,
                                startDate: Long,
                                endDate: Long,
                                urls: List<String>
    ): List<Article>

    /**
     * Returns the filtered article list from database ordered from the most recent to the oldest.
     *
     * @param sources       the sources list to search into database articles's data.
     * @param themes        the themes of article to get.
     * @param startDate     the published start date of article to get.
     * @param endDate       the published end date of article to get.
     * @param urls          the list of urls to search into.
     *
     * @return the filtered article list from database ordered from the most recent to the oldest.
     */
    @Transaction
    @Query("SELECT * FROM article WHERE sourceName IN (:sources) AND theme IN (:themes) AND (publishedDate BETWEEN :startDate AND :endDate) AND url IN (:urls) ORDER BY publishedDate DESC")
    suspend fun getFilteredListWithThemes(sources: List<String>,
                                          themes: List<String>,
                                          startDate: Long,
                                          endDate: Long,
                                          urls: List<String>
    ): List<Article>

    /**
     * Returns the filtered article list from database ordered from the most recent to the oldest.
     *
     * @param sources       the sources list to search into database articles's data.
     * @param sections      the sections of article to get.
     * @param startDate     the published start date of article to get.
     * @param endDate       the published end date of article to get.
     * @param urls          the list of urls to search into.
     *
     * @return the filtered article list from database ordered from the most recent to the oldest.
     */
    @Transaction
    @Query("SELECT * FROM article WHERE sourceName IN (:sources) AND section IN (:sections) AND (publishedDate BETWEEN :startDate AND :endDate) AND url IN (:urls) ORDER BY publishedDate DESC")
    suspend fun getFilteredListWithSections(sources: List<String>,
                                            sections: List<String>,
                                            startDate: Long,
                                            endDate: Long,
                                            urls: List<String>
    ): List<Article>

    /**
     * Returns the filtered article list from database ordered from the most recent to the oldest.
     *
     * @param sources       the sources list to search into database articles's data.
     * @param startDate     the published start date of article to get.
     * @param endDate       the published end date of article to get.
     * @param urls          the list of urls to search into.
     *
     * @return the filtered article list from database ordered from the most recent to the oldest.
     */
    @Transaction
    @Query("SELECT * FROM article WHERE sourceName IN (:sources) AND (publishedDate BETWEEN :startDate AND :endDate) AND url IN (:urls) ORDER BY publishedDate DESC")
    suspend fun getFilteredList(sources: List<String>,
                                startDate: Long,
                                endDate: Long,
                                urls: List<String>
    ): List<Article>

    // -----------------
    // INSERT
    // -----------------

    /**
     * Inserts the given article in database.
     *
     * @param articles the articles to insert in database.
     *
     * @return the row id for the inserted article.
     */
    @Insert
    suspend fun insertArticles(vararg articles: Article)//: List<Long>

    // -----------------
    // UPDATE
    // -----------------

    /**
     * Mark the article with the given unique identifier as read in database.
     *
     * @param id the unique identifier of the article to mark as read.
     */
    @Query("UPDATE article SET read=1 WHERE id=:id")
    suspend fun markAsRead(id: Long)

    /**
     * Mark the article with the given unique identifier that IS top story in database.
     *
     * @param url the url of the article to mark that IS top story.
     */
    @Query("UPDATE article SET isTopStory = 1 WHERE url IN (:url)")
    suspend fun markIsTopStory(vararg url: String)

    /**
     * Mark the article with the given unique identifier that is NOT top story in database.
     *
     * @param id the unique identifier of the article to mark that is NOT top story.
     */
    @Query("UPDATE article SET isTopStory = 0 WHERE id IN (:id)")
    suspend fun markIsNotTopStory(vararg id: Long)

    /**
     * Update the title, the author line and the published date of the article with the given unique identifier.
     *
     * @param title          the title of the article.
     * @param section        the section of the article.
     * @param theme          the theme of the article.
     * @param author         the author of the article.
     * @param publishedDate  the date (timestamp) when the article has been published to set.
     * @param article        the body of the article.
     * @param categories     the categories of the article.
     * @param description    the description of the article.
     * @param imageUrl       the image url of the article.
     * @param cssUrl         the css url of the article.
     * @param url            the url of the article
     */
    @Query("UPDATE article SET title=:title, section=:section, theme=:theme, author=:author, publishedDate=:publishedDate, article=:article, categories=:categories, description=:description, imageUrl=:imageUrl, cssUrl=:cssUrl WHERE url=:url")
    suspend fun update(title: String,
                       section: String,
                       theme: String,
                       author: String,
                       publishedDate: Long,
                       article: String,
                       categories: String,
                       description: String,
                       imageUrl: String,
                       cssUrl: String,
                       url: String)

    /**
     * Update the given article in database.
     *
     * @param article the article to update in database.
     *
     * @return the number of row affected.
     */
    @Update
    suspend fun updateArticle(article: Article): Int

    // -----------------
    // DELETE
    // -----------------

    /**
     * Delete the article in database for the given id.
     *
     * @param id the id of the article to delete in database.
     *
     * @return the number of row affected.
     */
    @Query("DELETE FROM Article WHERE id = :id")
    suspend fun deleteArticle(id: Long): Int
}