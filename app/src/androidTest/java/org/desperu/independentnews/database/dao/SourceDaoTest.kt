package org.desperu.independentnews.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.Source
import org.junit.*
import org.junit.Assert.*

/**
 * Simple dao class test, for Source Dao Interface that check CRUD functions.
 */
@ExperimentalCoroutinesApi
class SourceDaoTest {

    // FOR DATA
    private lateinit var mDatabase: ArticleDatabase

    // Create an Source and source list for Db test
    private lateinit var source: Source
    private lateinit var sourceList: List<Source>

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    fun initDbAndData() {
        // init Db for test
        mDatabase = DaoTestHelper().initDb()

        // Set source and source list data for Db test
        source = DaoTestHelper().source
        sourceList = DaoTestHelper().sourceList
    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    fun insertAndGetSource() = runBlockingTest {
        // Given an Source that has been inserted into the DB
        val sourceId = mDatabase.sourceDao().insertSources(source)[0]

        // When getting the Source via the DAO
        val sourceDb = mDatabase.sourceDao().getSource(sourceId)

        // Then the retrieved Source match the original source object
        assertEquals(source, sourceDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    fun getAllSourcesList() = sourceListTest {
        // When getting All Sources via the DAO
        val allSources = mDatabase.sourceDao().getAll()

        // Then the retrieved All Source match the original source list object
        assertEquals(sourceList, allSources)
    }

    @Test
    fun getEnabledSourcesList() = sourceListTest {
        // When getting Enabled Sources via the DAO
        val enabledSources = mDatabase.sourceDao().getEnabled()

        // Then the retrieved Enabled Source match the original source list object
        assertEquals(sourceList, enabledSources)
    }

    @Test
    fun setDisabledSources() = oneSourceTest {
        // When set Enabled Sources via the DAO
        mDatabase.sourceDao().setIsEnabled(source.name, false)

        // When getting the Source via the DAO
        val sourceDb = mDatabase.sourceDao().getSource(source.id)

        // Then check the retrieved Source is enabled
        assertFalse(sourceDb.isEnabled)
    }

    @Test
    fun updateAndGetSource() = oneSourceTest {
        // Change some data to update them in database
        source.isEnabled = false

        // Given an Source that has been updated into the DB
        val rowAffected = mDatabase.sourceDao().updateSource(source)

        // Check that's there only one row affected when updating
        assertEquals(1, rowAffected)

        // When getting the Source via the DAO
        val sourceDb = mDatabase.sourceDao().getSource(source.id)

        // Then the retrieved Source match the updated source object
        assertEquals(source, sourceDb)
    }

    @Test
    fun deleteSourceAndCheckDb() = runBlockingTest {
        // Insert an source in database for the test
        mDatabase.sourceDao().insertSources(source)

        // Given an Source that has been inserted into the DB
        val rowAffected = mDatabase.sourceDao().deleteSource(source.name)

        // Check that's there only row affected when deleting
        assertEquals(1, rowAffected)

        // When getting the Source via the DAO
        val sourceDb = mDatabase.sourceDao().getSource(source.id)

        // Then the retrieved Source is empty
        assertNull(sourceDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * One source test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun oneSourceTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert an source in database for the test
        mDatabase.sourceDao().insertSources(source)

        // Execute the test
        block()

        // Delete inserted source for test
        mDatabase.sourceDao().deleteSource(source.name)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    /**
     * Source list test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun sourceListTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert a source list in database for the test
        mDatabase.sourceDao().insertSources(*sourceList.toTypedArray())

        // Execute the test
        block()

        // Delete inserted source list for test
        sourceList.forEach {
            mDatabase.sourceDao().deleteSource(it.name)
        }

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}