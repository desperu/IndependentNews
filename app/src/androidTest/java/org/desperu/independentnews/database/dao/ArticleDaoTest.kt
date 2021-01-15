package org.desperu.independentnews.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.utils.Utils.millisToStartOfDay
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Simple dao class test, for Article Dao Interface, that CRUD functions.
 */
@ExperimentalCoroutinesApi
class ArticleDaoTest {

    // FOR DATA
    private lateinit var mDatabase: ArticleDatabase
    // Create a Source for foreign key
    private lateinit var source: Source
    private var sourceId = 0L
    // Create an Article  and an Article List for Db test
    private lateinit var article: Article
    private lateinit var articleList: List<Article>

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    fun initDbAndData() {
        // init Db for test
        mDatabase = DaoTestHelper().initDb()

        // Set Source for foreign keys matches
        source = DaoTestHelper().source
        runBlockingTest { sourceId = mDatabase.sourceDao().insertSources(source)[0] }

        // Set source data for Db test
        article = DaoTestHelper().getArticle(sourceId)
        articleList = DaoTestHelper().getArticleList(sourceId)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    fun insertAndGetArticle() = runBlockingTest {
        // Given an Article that has been inserted into the DB
        val articleId = mDatabase.articleDao().insertArticles(article)[0]

        // When getting the Article via the DAO
        val articleDb = mDatabase.articleDao().getArticle(articleId)

        // Then the retrieved Article match the original article object
        assertEquals(article, articleDb)

        // Do the same for get with url
        val articleDbUrl = mDatabase.articleDao().getArticle(article.url)

        // Then check it match the original
        assertEquals(article, articleDbUrl)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    fun getTopStory() = articleListTest {
        // Try to get Top Story Articles in the database
        val topStories = mDatabase.articleDao().getTopStory(listOf(sourceId))

        // Then check that the list size equal two
        assertTrue(topStories.size == 2)
    }

    @Test
    fun getCategory() = articleListTest {
        // Try to get All Articles in the database
        val categories = mDatabase.articleDao().getCategory(
            "%${article.theme}%",
            listOf(sourceId)
        )

        // Then check that the list size equal two
        assertTrue(categories.size == 2)
    }

    @Test
    fun getAll() = articleListTest {
        // Try to get All Articles in the database
        val allArticles = mDatabase.articleDao().getAll(listOf(sourceId))

        // Then check that the list size equal two
        assertTrue(allArticles.size == 2)
    }

    @Test
    fun getWhereUrlsIn() = articleListTest {
        // Try to get All Articles in the database in the given url list
        val urlsIn = mDatabase.articleDao().getWhereUrlsIn(articleList.map { it.url })

        // Then check that the list isn't empty
        assertTrue(urlsIn.isNotEmpty())
    }

    @Test
    fun getWhereTitlesIn() = articleListTest {
        // Try to get All Articles in the database in the given title list
        val titlesIn = mDatabase.articleDao().getWhereTitlesIn(articleList.map { it.title })

        // Then check that the list isn't empty
        assertTrue(titlesIn.isNotEmpty())
    }

    @Test
    fun getWhereUrlsInSorted() = articleListTest {
        // Try to get All Articles in the database in the given url list
        val urlsInSorted = mDatabase.articleDao().getWhereUrlsInSorted(articleList.map { it.url })

        // Then check that the list isn't empty
        assertTrue(urlsInSorted.isNotEmpty())
    }

    @Test
    fun getTodayArticles() = articleListTest {
        // Get the today start in millis
        val todayStartMillis = millisToStartOfDay(Calendar.getInstance().timeInMillis)

        // Update the published dates of the articles for the test
        articleList[0].publishedDate = todayStartMillis
        articleList[1].publishedDate = todayStartMillis + 1000
        mDatabase.articleDao().updateArticle(*articleList.toTypedArray())

        // Try to get Today Articles from the database
        val todayArticles = mDatabase.articleDao().getTodayArticle(todayStartMillis, listOf(sourceId))

        // Then check that the list size match the today articles in database
        assertEquals(articleList.size, todayArticles.size)
    }

    @Test
    fun getFilteredListWithAll() = articleListTest {
        // Try to get filtered Articles list from the database in the given url list
        val withAllFilters = mDatabase.articleDao().getFilteredListWithAll(
            listOf(sourceId),
            listOf(article.section),
            listOf(article.theme),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            listOf(article.url)
        )
        // Then check that the list isn't empty
        assertTrue(withAllFilters.isNotEmpty())
    }

    @Test
    fun getFilteredListWithThemes() = articleListTest {
        // Try to get filtered Articles list from the database in the given url list
        val withThemeFilters = mDatabase.articleDao().getFilteredListWithThemes(
            listOf(sourceId),
            listOf(article.theme),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            listOf(article.url)
        )
        // Then check that the list isn't empty
        assertTrue(withThemeFilters.isNotEmpty())
    }

    @Test
    fun getFilteredListWithSections() = articleListTest {
        // Try to get filtered Articles list from the database in the given url list
        val withSectionFilters = mDatabase.articleDao().getFilteredListWithSections(
            listOf(sourceId),
            listOf(article.section),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            listOf(article.url)
        )
        // Then check that the list isn't empty
        assertTrue(withSectionFilters.isNotEmpty())
    }

    @Test
    fun getFilteredListWithCategory() = articleListTest {
        // Try to get filtered Articles list from the database in the given url list
        val withCategoryFilters = mDatabase.articleDao().getFilteredListWithCategory(
            listOf(sourceId),
            article.categories,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            listOf(article.id)
        )
        // Then check that the list isn't empty
        assertTrue(withCategoryFilters.isNotEmpty())
    }

    @Test
    fun getFilteredList() = articleListTest {
        // Try to get filtered Articles list from the database in the given url list
        val withFilters = mDatabase.articleDao().getFilteredList(
            listOf(sourceId),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            listOf(article.url)
        )
        // Then check that the list isn't empty
        assertTrue(withFilters.isNotEmpty())
    }

    @Test
    fun markArticleAsRead() = oneArticleTest {
        // When mark article as read
        mDatabase.articleDao().markAsRead(article.id)

        // When getting the Article via the DAO
        val articleDb = mDatabase.articleDao().getArticle(article.id)

        // Then the retrieved Article is read
        assertTrue(articleDb.read)
    }

    @Test
    fun markIsTopStory() = oneArticleTest {
        // When mark article is NOT Top Story
        mDatabase.articleDao().markIsTopStory(article.url)

        // When getting the Article via the DAO
        val articleDb = mDatabase.articleDao().getArticle(article.id)

        // Then the retrieved Article is top story
        assertTrue(articleDb.isTopStory)
    }

    @Test
    fun markIsNotTopStory() = oneArticleTest {
        // When mark article is NOT Top Story
        mDatabase.articleDao().markIsNotTopStory(article.id)

        // When getting the Article via the DAO
        val articleDb = mDatabase.articleDao().getArticle(article.id)

        // Then the retrieved Article is not top story
        assertFalse(articleDb.isTopStory)
    }

    @Test
    fun updateArticle() = oneArticleTest {
        // Change some data to update them in database
        article.title = "title"
        article.section = "section"
        article.theme = "theme"
        article.author = "author"
        article.publishedDate = 50L
        article.article = "article"
        article.categories = "categories"
        article.description = "description"
        article.imageUrl = "imageUrl"

        // Given an Article that has been updated into the DB
        mDatabase.articleDao().update(
            article.title,
            article.section,
            article.theme,
            article.author,
            article.publishedDate,
            article.article,
            article.categories,
            article.description,
            article.imageUrl,
            article.url
        )

        // When getting the Article via the DAO
        val articleDb = mDatabase.articleDao().getArticle(article.id)

        // Then the retrieved Article match the updated article object
        assertEquals(article, articleDb)
    }

    @Test
    fun updateAndGetArticle() = oneArticleTest {
        // Change some data to update them in database
        article.publishedDate = 222L
        article.article = "Viva la liberta !"

        // Given an Article that has been updated into the DB
        val rowAffected = mDatabase.articleDao().updateArticle(article)

        // Check that's there only one row affected when updating
        assertEquals(1, rowAffected)

        // When getting the Article via the DAO
        val articleDb = mDatabase.articleDao().getArticle(article.id)

        // Then the retrieved Article match the updated article object
        assertEquals(article, articleDb)
    }

    @Test
    fun deleteArticleAndCheckDb() = runBlockingTest {
        // Insert an article in database for the test
        mDatabase.articleDao().insertArticles(article)

        // Given an Article that has been deleted into the DB
        val rowAffected = mDatabase.articleDao().deleteArticle(article.id)

        // Check that's there only one row affected when deleting
        assertEquals(1, rowAffected)

        // When getting the Article via the DAO
        val articleDb = mDatabase.articleDao().getArticle(article.id)

        // Then the retrieved a null object
        assertNull(articleDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    fun deleteOldArticleAndCheckDb() = runBlockingTest {
        // Insert an article in database for the test
        article.publishedDate = 0L
        mDatabase.articleDao().insertArticles(article)

        // Given an Article that has been deleted into the DB
        val rowAffected = mDatabase.articleDao().removeOldArticles(1L)

        // Check that's there only one row affected when deleting
        assertEquals(1, rowAffected)

        // When getting the Article via the DAO
        val articleDb = mDatabase.articleDao().getArticle(article.id)

        // Then the retrieved a null object
        assertNull(articleDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * One article test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun oneArticleTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert an article in database for the test
        val id = mDatabase.articleDao().insertArticles(article)

        // Execute the test
        block()

        // Delete inserted article for test
        mDatabase.articleDao().deleteArticle(id[0])

        // Remove Source inserted for foreign keys matches, after test
        mDatabase.sourceDao().deleteSource(source.name)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    /**
     * Article test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun articleListTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert an article list in database for the test
        val ids = mDatabase.articleDao().insertArticles(*articleList.toTypedArray())

        // Execute the test
        block()

        // Delete inserted article list for test
        ids.forEach { mDatabase.articleDao().deleteArticle(it) }

        // Remove Source inserted for foreign keys matches, after test
        mDatabase.sourceDao().deleteSource(source.name)

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}