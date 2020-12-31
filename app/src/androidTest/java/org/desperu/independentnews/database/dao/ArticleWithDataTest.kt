package org.desperu.independentnews.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.models.database.Source
import org.junit.*
import org.junit.Assert.assertEquals

/**
 * Simple dao class test, for Article With Data Dao Interface, that check CRUD functions.
 */
@ExperimentalCoroutinesApi
class ArticleWithDataTest {

    // FOR DATA
    private lateinit var mDatabase: ArticleDatabase
    // Create a Source for Db test and foreign key
    private lateinit var source: Source
    private var sourceId = 0L
    // Create a list of Articles for Db test and foreign key
    private lateinit var articleList: List<Article>
    private var articleId = 0L
    // And another list of Css for Db test
    private lateinit var cssList: List<Css>

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    fun initDbAndData() {
        // init Db for test
        mDatabase = DaoTestHelper().initDb()

        runBlockingTest {
            // Set Source for Db test and foreign key match
            source = DaoTestHelper().source
            sourceId = mDatabase.sourceDao().insertSources(source)[0]

            // Set article list for Db test and foreign key match
            articleList = DaoTestHelper().getArticleList(sourceId)
            articleId = mDatabase.articleDao().insertArticles(*articleList.toTypedArray())[0]

            // Set css list for Db test and foreign key match
            cssList = DaoTestHelper().getCssList(articleId)
            cssList[1].articleId = articleId + 1
        }

    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    fun insertAndGetArticleWithData() = runBlockingTest {
        // Insert the given css list data into the DB
        mDatabase.cssDao().insertCss(*cssList.toTypedArray())

        // When getting the ArticleWithData via the DAO
        val articleWithDataDb = mDatabase.articleWithDataDao().getArticleWithData(articleId)

        // Then the retrieved source match the original objects
        assertEquals(source, articleWithDataDb.source)

        // And the retrieved articleList match the original SourcePageList object
        assertEquals(articleList[0], articleWithDataDb.article)

        // And the retrieved cssList match the original SourcePageList object
        assertEquals(cssList[0], articleWithDataDb.css)

        // Clean up coroutines
        cleanupTestCoroutines()
    }
    
    @Test
    fun insertAndGetAll() = runBlockingTest {
        // Insert the given css list data into the DB
        mDatabase.cssDao().insertCss(*cssList.toTypedArray())

        // When getting the ArticleWithData via the DAO
        val articleWithDataListDb = mDatabase.articleWithDataDao().getAll()

        // Then the retrieved source match the original objects
        assertEquals(source, articleWithDataListDb[0].source)

        // And the retrieved articleList match the original SourcePageList object
        assertEquals(articleList, listOf(articleWithDataListDb[0].article, articleWithDataListDb[1].article))

        // And the retrieved cssList match the original SourcePageList object
        assertEquals(cssList, listOf(articleWithDataListDb[0].css, articleWithDataListDb[1].css))

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}