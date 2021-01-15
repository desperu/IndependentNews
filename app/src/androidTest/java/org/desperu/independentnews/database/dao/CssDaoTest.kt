package org.desperu.independentnews.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.database.Css
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Simple dao class test, for Css Dao Interface that check CRUD functions.
 */
@ExperimentalCoroutinesApi
class CssDaoTest {

    // FOR DATA
    private lateinit var mDatabase: ArticleDatabase
    // Create a Css and css list for Db test
    private lateinit var css: Css
    private var cssId = 0L
    private lateinit var cssList: List<Css>

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    fun initDbAndData() {
        // init Db for test
        mDatabase = DaoTestHelper().initDb()

        // Set css and css list data for Db test
        css = DaoTestHelper().css
        cssList = DaoTestHelper().cssList
    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    fun insertAndGetCss() = runBlockingTest {
        // Given a Css that has been inserted into the DB
        val cssId = mDatabase.cssDao().insertCss(css)[0]

        // When getting the Css via the DAO
        val cssDb = mDatabase.cssDao().getCss(cssId)

        // Then the retrieved Css match the original css object
        assertEquals(css, cssDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    fun getCssForUrl() = oneCssTest {
        // When getting Css via the DAO
        val cssDb = mDatabase.cssDao().getCssForUrl(css.url)

        // Then the retrieved Css match the original css object
        assertEquals(css, cssDb)
    }

    @Test
    fun getAllCssList() = cssListTest {
        // When getting All Css via the DAO
        val allCss = mDatabase.cssDao().getAll()

        // Then the retrieved All Css match the original css list object
        assertEquals(cssList, allCss)
    }

    @Test
    fun updateAndGetCss() = oneCssTest {
        // Change some data to update them in database
        css.style = "a new css style"

        // Given a Css that has been updated into the DB
        val rowAffected = mDatabase.cssDao().updateCss(css)

        // Check that's there only one row affected when updating
        assertEquals(1, rowAffected)

        // When getting the Css via the DAO
        val cssDb = mDatabase.cssDao().getCss(cssId)

        // Then the retrieved Css match the updated css object
        assertEquals(css, cssDb)
    }

    @Test
    fun deleteArticleCssAndCheckDb() = runBlockingTest {
        // Insert a Css in database for the test
        mDatabase.cssDao().insertCss(css)[0]

        // Given a Css that has been inserted into the DB
        val rowAffected = mDatabase.cssDao().deleteCss(css)

        // Check that's there only row affected when deleting
        assertEquals(1, rowAffected)

        // When getting the Css via the DAO
        val sourceDb = mDatabase.cssDao().getCss(css.id)

        // Then the retrieved Css is empty
        assertNull(sourceDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * One css test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun oneCssTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert a css in database for the test
        cssId = mDatabase.cssDao().insertCss(css)[0]

        // Execute the test
        block()

        // Delete inserted css for test
        mDatabase.cssDao().deleteCss(css)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    /**
     * Css list test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun cssListTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert a css list in database for the test
        mDatabase.cssDao().insertCss(*cssList.toTypedArray())

        // Execute the test
        block()

        // Delete inserted css list for test
        mDatabase.cssDao().deleteCss(*cssList.toTypedArray())

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}