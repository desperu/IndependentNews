package org.desperu.independentnews.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Paused
import org.desperu.independentnews.models.database.Source
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

/**
 * Simple dao class test, for Paused Dao Interface, that check CRUD functions.
 */
@ExperimentalCoroutinesApi
class PausedDaoTest {

    // FOR DATA
    private lateinit var mDatabase: ArticleDatabase
    // Create a Source for foreign key
    private lateinit var source: Source
    private var sourceId = 0L
    // Create a Article for foreign key
    private lateinit var article: Article
    private var articleId = 0L
    // Create a Paused and article page list for Db test
    private lateinit var paused: Paused
    private lateinit var pausedList: List<Paused>

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

        // Set Article for foreign keys matches
        article = DaoTestHelper().getArticle(sourceId)
        runBlockingTest { articleId = mDatabase.articleDao().insertArticles(article)[0] }

        // Set article page and article page list data for Db test
        paused = DaoTestHelper().getPaused(articleId)
        pausedList = DaoTestHelper().getPausedList(articleId)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    fun insertAndGetPausedWithId() = runBlockingTest {
        // Insert the given Paused into the DB
        val pausedId = mDatabase.pausedDao().insertPaused(paused)[0]

        // When getting the Paused via the DAO
        val pausedDb = mDatabase.pausedDao().getPaused(pausedId)

        // Then the retrieved Paused match the original Paused object
        assertEquals(paused, pausedDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    fun insertAndGetPausedForArticleId() = runBlockingTest {
        // Insert the given Paused into the DB
        mDatabase.pausedDao().insertPaused(paused)

        // When getting the Paused via the DAO
        val pausedDb = mDatabase.pausedDao().getPausedForArticleId(articleId)

        // Then the retrieved Paused match the original Paused object
        assertEquals(paused, pausedDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    fun getAllPauseds() = pausedListTest {
        // When getting All Pauseds via the DAO
        val allPauseds = mDatabase.pausedDao().getAll()

        // Then the retrieved All Pauseds match the original Paused list object
        assertEquals(pausedList, allPauseds)
    }

    @Test
    fun updateAndGetPaused() = onePausedTest {
        // Change some data to update them in database
        val newPaused = Paused(paused.id, articleId, 500)

        // Given a Article that has been updated into the DB
        val rowAffected = mDatabase.pausedDao().updatePaused(newPaused)

        // Check that's there only one row affected when updating
        assertEquals(1, rowAffected)

        // When getting the Paused via the DAO
        val pausedDb = mDatabase.pausedDao().getPaused(paused.id)

        // Then the retrieved Article match the updated article object
        assertEquals(newPaused, pausedDb)
    }

    @Test
    fun updateScrollPositionAndGetPaused() = onePausedTest {
        // Change some data to update them in database
        val newPaused = Paused(paused.id, articleId, 2000)

        // Given a Article that has been updated into the DB
        mDatabase.pausedDao().setScrollPosition(articleId, 2000)

        // When getting the Paused via the DAO
        val pausedDb = mDatabase.pausedDao().getPaused(paused.id)

        // Then the retrieved Article match the updated article object
        assertEquals(newPaused, pausedDb)
    }

    @Test
    fun deleteArticleAndCheckDb() = runBlockingTest {
        // Insert a article page in database for the test
        mDatabase.pausedDao().insertPaused(paused)

        // Given a Paused that has been inserted into the DB
        val rowAffected = mDatabase.pausedDao().deletePaused(paused)

        // Check that's there only row affected when deleting
        assertEquals(1, rowAffected)

        // When getting the Paused via the DAO
        val articleDb = mDatabase.pausedDao().getPaused(paused.id)

        // Then the retrieved Paused is empty
        assertNull(articleDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * One paused test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun onePausedTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert an article in database for the test
        mDatabase.pausedDao().insertPaused(paused)

        // Execute the test
        block()

        // Delete inserted article for test
        mDatabase.pausedDao().deletePaused(paused)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    /**
     * Paused list test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun pausedListTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert a article list in database for the test
        mDatabase.pausedDao().insertPaused(*pausedList.toTypedArray())

        // Execute the test
        block()

        // Delete inserted article list for test
        pausedList.forEach {
            mDatabase.pausedDao().deletePaused(it)
        }

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}