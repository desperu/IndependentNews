package org.desperu.independentnews.database.dao

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.utils.BASTAMAG
import org.desperu.independentnews.utils.BASTAMAG_BASE_URL
import org.desperu.independentnews.utils.BASTAMAG_EDITO_URL

/**
 * Dao Test Helper which provide initialized database, and models with data,
 * to support dao test.
 */
class DaoTestHelper {

    /**
     * Init Database for Dao test.
     *
     * @return the created ArticleDatabase instance.
     */
    internal fun initDb(): ArticleDatabase =
        // init Db for test
        Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            ArticleDatabase::class.java
        )
            // allowing main thread queries, just for testing
            .allowMainThreadQueries()
            .build()

    /**
     * Article object for dao test.
     *
     * @param sourceId the source id to set.
     *
     * @return the created article object.
     */
    internal fun getArticle(sourceId: Long) = Article(
        100000000000000000L,
        sourceId,
        BASTAMAG,
        BASTAMAG_BASE_URL,
        "A title",
        "a section",
        "a theme",
        "an author",
        0L,
        "an article",
        "a category",
        "a description",
        "an image url",
        "an css url",
        isTopStory = true,
        read = false,
        source = Source()
    )

    /**
     * Get article list for DB test.
     *
     * @param sourceId the source id to set.
     *
     * @return the created article list.
     */
    internal fun getArticleList(sourceId: Long): List<Article> {
        val article2 = getArticle(sourceId).copy()
        article2.id = 100000000000000001L
        article2.description = "a description 2"
        return listOf(getArticle(sourceId), article2)
    }

    /**
     * Source object for dao test.
     */
    val source = Source(
        100000000000000000L,
        BASTAMAG + "test",
        BASTAMAG_BASE_URL,
        BASTAMAG_EDITO_URL,
        "an editorial",
        1,
        2,
        true
    )

    /**
     * Get source list for DB test.
     *
     * @return the created source list.
     */
    internal fun getSourceList(): List<Source> {
        val source2 = source.copy()
        source2.id = 100000000000000001L
        source2.editorial = "an editorial 2"
        return listOf(source, source2)
    }
}