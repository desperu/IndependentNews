package org.desperu.independentnews.database.dao

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.database.*
import org.desperu.independentnews.utils.BASTAMAG
import org.desperu.independentnews.utils.BASTAMAG_BASE_URL

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

    // -----------------
    // ARTICLE
    // -----------------

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

    // -----------------
    // CSS
    // -----------------

    /**
     * Css object for dao test.
     *
     * @return the created css object.
     */
    internal val css = Css(
        1000000000000000L,
        "a css url",
        "a ccs style"
    )

    /**
     * Get css list for DB test.
     *
     * @return the created css list.
     */
    internal val cssList: List<Css> =
        listOf(
            css,
            Css(1000000000000002L, "a css url 2", "a ccs style 2")
        )

    // -----------------
    // FAVORITE
    // -----------------

    /**
     * Favorite object for dao test.
     *
     * @param articleId the article id to set.
     *
     * @return the created favorite.
     */
    internal fun getFavorite(articleId: Long) = Favorite(
        200000000000000000L,
        articleId
    )

    /**
     * Favorite List for dao test.
     *
     * @param articleId the article id to set.
     *
     * @return the create favorite list.
     */
    internal fun getFavoriteList(articleId: Long): List<Favorite> =
        listOf(
            getFavorite(articleId),
            Favorite(
                200000000000000001L,
                articleId
            )
        )

    // -----------------
    // PAUSED
    // -----------------

    /**
     * Paused object for dao test.
     *
     * @param articleId the article id to set.
     *
     * @return the created paused.
     */
    internal fun getPaused(articleId: Long) = Paused(
        200000000000000000L,
        articleId,
        100
    )

    /**
     * Paused List for dao test.
     *
     * @param articleId the article id to set.
     *
     * @return the create paused list.
     */
    internal fun getPausedList(articleId: Long): List<Paused> =
        listOf(
            getPaused(articleId),
            Paused(
                200000000000000001L,
                articleId,
                100
            )
        )

    // -----------------
    // SOURCE
    // -----------------

    /**
     * Source object for dao test.
     */
    internal val source = Source(
        100000000000000000L,
        BASTAMAG + "test",
        BASTAMAG_BASE_URL,
        true
    )

    /**
     * Get source list for DB test.
     *
     * @return the created source list.
     */
    internal val sourceList = listOf(
        source,
        Source(
            100000000000000001L,
            source.name,
            source.url,
            source.isEnabled
        )
    )

    // -----------------
    // SOURCE PAGE
    // -----------------

    /**
     * Source Page object for dao test.
     *
     * @param sourceId the source id to set.
     *
     * @return the created source page.
     */
    internal fun sourcePage(sourceId: Long) = SourcePage(
        200000000000000000L,
        sourceId,
        "a source page url",
        "a button name",
        "a title",
        "a body",
        "a css url",
        3,
        false
    )

    /**
     * Source Page List for dao test.
     *
     * @param sourceId the source id to set.
     *
     * @return the create source page list.
     */
    internal fun getSourcePageList(sourceId: Long): List<SourcePage> =
        listOf(
            sourcePage(sourceId),
            SourcePage(
                200000000000000001L,
                sourceId,
                "a source page url",
                "a button name 2",
                "a title 2",
                "a body 2",
                "a css url 2",
                5,
                true
            )
        )
}