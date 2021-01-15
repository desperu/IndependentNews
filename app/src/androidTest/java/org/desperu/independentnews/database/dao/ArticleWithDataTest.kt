package org.desperu.independentnews.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.database.Article
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
        }
    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    fun insertAndGetArticleWithData() = runBlockingTest {
        // When getting the ArticleWithData via the DAO
        val articleWithDataDb = mDatabase.articleWithDataDao().getArticleWithData(articleId)

        // Then the retrieved source match the original objects
        assertEquals(source, articleWithDataDb.source)

        // And the retrieved article match the original article object
        assertEquals(articleList[0], articleWithDataDb.article)

        // Clean up coroutines
        cleanupTestCoroutines()
    }
    
    @Test
    fun insertAndGetAll() = runBlockingTest {
        // When getting the ArticleWithData via the DAO
        val articleWithDataListDb = mDatabase.articleWithDataDao().getAll()

        // Then the retrieved source match the original objects
        assertEquals(source, articleWithDataListDb[0].source)

        // And the retrieved articleList match the original ArticleList object
        assertEquals(articleList, listOf(articleWithDataListDb[0].article, articleWithDataListDb[1].article))

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}