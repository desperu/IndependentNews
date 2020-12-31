package org.desperu.independentnews.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.models.database.Source
import org.junit.*
import org.junit.Assert.*

/**
 * Simple dao class test, for Css Dao Interface that check CRUD functions.
 */
@ExperimentalCoroutinesApi
class CssDaoTest {

    // FOR DATA
    private lateinit var mDatabase: ArticleDatabase
    // Create a Source for foreign key
    private lateinit var source: Source
    private var sourceId = 0L
    // Create an Article for foreign key
    private lateinit var article: Article
    private var articleId = 0L
    // Create a Css and css list for Db test
    private lateinit var css: Css
    private lateinit var cssList: List<Css>

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

        // Set css and css list data for Db test
        css = DaoTestHelper().getCss(articleId)
        cssList = DaoTestHelper().getCssList(articleId)
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
    fun getArticleCssList() = oneCssTest {
        // When getting Article Css via the DAO
        val articleCss = mDatabase.cssDao().getArticleCss(articleId)

        // Then the retrieved Article Css match the original css object
        assertEquals(css, articleCss)
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
        css.content = "a new content"

        // Given a Css that has been updated into the DB
        val rowAffected = mDatabase.cssDao().updateCss(css)

        // Check that's there only one row affected when updating
        assertEquals(1, rowAffected)

        // When getting the Css via the DAO
        val cssDb = mDatabase.cssDao().getCss(css.id)

        // Then the retrieved Css match the updated css object
        assertEquals(css, cssDb)
    }

    @Test
    fun deleteCssAndCheckDb() = runBlockingTest {
        // Insert a Css in database for the test
        mDatabase.cssDao().insertCss(css)

        // Given a Css that has been inserted into the DB
        val rowAffected = mDatabase.cssDao().deleteArticleCss(css.articleId)

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
        mDatabase.cssDao().insertCss(css)

        // Execute the test
        block()

        // Delete inserted css for test
        mDatabase.cssDao().deleteArticleCss(css.articleId)

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
        cssList.forEach {
            mDatabase.cssDao().deleteArticleCss(it.articleId)
        }

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}