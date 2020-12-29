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
 * Simple dao class test, for Source With Data Dao Interface, that check CRUD functions.
 */
@ExperimentalCoroutinesApi
class SourceWithDataDaoTest {

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
        // Insert the given SourcePageList into the DB
        mDatabase.sourcePageDao().insertSourcePages(*sourcePageList.toTypedArray())

        // When getting the SourceWithData via the DAO
        val sourceWithDataDb = mDatabase.sourceWithDataDao().getAll()

        // Then the retrieved Sources match the original Sources object
        assertEquals(source, sourceWithDataDb[0].source)

        // And the retrieved SourcePagesList match the original SourcePageList object
        assertEquals(sourcePageList, sourceWithDataDb[0].sourcePages)

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}