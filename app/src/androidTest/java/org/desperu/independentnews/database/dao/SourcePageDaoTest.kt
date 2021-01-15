package org.desperu.independentnews.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.models.database.SourcePage
import org.junit.*
import org.junit.Assert.assertEquals

/**
 * Simple dao class test, for Source Page Dao Interface, that check CRUD functions.
 */
@ExperimentalCoroutinesApi
class SourcePageDaoTest {

    // FOR DATA
    private lateinit var mDatabase: ArticleDatabase
    // Create a Source for foreign key
    private lateinit var source: Source
    private var sourceId = 0L
    // Create a SourcePage and source page list for Db test
    private lateinit var sourcePage: SourcePage
    private lateinit var sourcePageList: List<SourcePage>

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

        // Set source page and source page list data for Db test
        sourcePage = DaoTestHelper().sourcePage(sourceId)
        sourcePageList = DaoTestHelper().getSourcePageList(sourceId)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    fun insertAndGetSourcePage() = runBlockingTest {
        // Insert the given SourcePage into the DB
        val sourcePageId = mDatabase.sourcePageDao().insertSourcePages(sourcePage)[0]

        // When getting the SourcePage via the DAO
        val sourcePageDb = mDatabase.sourcePageDao().getSourcePage(sourcePageId)

        // Then the retrieved SourcePage match the original SourcePage object
        assertEquals(sourcePage, sourcePageDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    fun getSourcePagesForSourceId() = sourcePageListTest {
        // When getting SourcePages for the given source id via the DAO
        val sourcePages = mDatabase.sourcePageDao().getPagesForSourceId(sourceId)

        // Then the retrieved SourcePages match the original SourcePages list object
        assertEquals(sourcePageList, sourcePages)
    }

    @Test
    fun getAllSourcePages() = sourcePageListTest {
        // When getting All SourcePages via the DAO
        val allSourcePages = mDatabase.sourcePageDao().getAll()

        // Then the retrieved All SourcePages match the original SourcePage list object
        assertEquals(sourcePageList, allSourcePages)
    }

    @Test
    fun updateAndGetSourcePage() = oneSourcePageTest {
        // Change some data to update them in database
        val newSourcePage = SourcePage(sourcePage.id, sourceId)

        // Given a Source that has been updated into the DB
        val rowAffected = mDatabase.sourcePageDao().updateSourcePage(newSourcePage)

        // Check that's there only one row affected when updating
        assertEquals(1, rowAffected)

        // When getting the SourcePage via the DAO
        val sourcePageDb = mDatabase.sourcePageDao().getSourcePage(sourcePage.id)

        // Then the retrieved Source match the updated source object
        assertEquals(newSourcePage, sourcePageDb)
    }

    @Test
    fun deleteSourceAndCheckDb() = runBlockingTest {
        // Insert a source page in database for the test
        mDatabase.sourcePageDao().insertSourcePages(sourcePage)

        // Given a SourcePage that has been inserted into the DB
        val rowAffected = mDatabase.sourcePageDao().deleteSourcePage(sourcePage.id)

        // Check that's there only row affected when deleting
        assertEquals(1, rowAffected)

        // When getting the SourcePage via the DAO
        val sourceDb = mDatabase.sourcePageDao().getSourcePage(sourcePage.id)

        // Then the retrieved SourcePage is empty
        Assert.assertNull(sourceDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * One source page test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun oneSourcePageTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert an source in database for the test
        mDatabase.sourcePageDao().insertSourcePages(sourcePage)

        // Execute the test
        block()

        // Delete inserted source for test
        mDatabase.sourcePageDao().deleteSourcePage(sourcePage.id)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    /**
     * Source list test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun sourcePageListTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert a source list in database for the test
        mDatabase.sourcePageDao().insertSourcePages(*sourcePageList.toTypedArray())

        // Execute the test
        block()

        // Delete inserted source list for test
        sourcePageList.forEach {
            mDatabase.sourcePageDao().deleteSourcePage(it.id)
        }

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}