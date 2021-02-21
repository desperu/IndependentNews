package org.desperu.independentnews.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.models.database.Favorite
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Source
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

/**
 * Simple dao class test, for Favorite Dao Interface, that check CRUD functions.
 */
@ExperimentalCoroutinesApi
class FavoriteDaoTest {

    // FOR DATA
    private lateinit var mDatabase: ArticleDatabase
    // Create a Source for foreign key
    private lateinit var source: Source
    private var sourceId = 0L
    // Create a Article for foreign key
    private lateinit var article: Article
    private var articleId = 0L
    // Create a Favorite and article page list for Db test
    private lateinit var favorite: Favorite
    private lateinit var favoriteList: List<Favorite>

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
        favorite = DaoTestHelper().getFavorite(articleId)
        favoriteList = DaoTestHelper().getFavoriteList(articleId)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() { mDatabase.close() }

    @Test
    fun insertAndGetFavoriteWithId() = runBlockingTest {
        // Insert the given Favorite into the DB
        val favoriteId = mDatabase.favoriteDao().insertFavorite(favorite)[0]

        // When getting the Favorite via the DAO
        val favoriteDb = mDatabase.favoriteDao().getFavorite(favoriteId)

        // Then the retrieved Favorite match the original Favorite object
        assertEquals(favorite, favoriteDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    fun insertAndGetFavoriteForArticleId() = runBlockingTest {
        // Insert the given Favorite into the DB
        mDatabase.favoriteDao().insertFavorite(favorite)

        // When getting the Favorite via the DAO
        val favoriteDb = mDatabase.favoriteDao().getFavoriteForArticleId(articleId)

        // Then the retrieved Favorite match the original Favorite object
        assertEquals(favorite, favoriteDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    @Test
    fun getAllFavorites() = favoriteListTest {
        // When getting All Favorites via the DAO
        val allFavorites = mDatabase.favoriteDao().getAll()

        // Then the retrieved All Favorites match the original Favorite list object
        assertEquals(favoriteList, allFavorites)
    }

    @Test
    fun updateAndGetFavorite() = oneFavoriteTest {
        // Change some data to update them in database
        val newFavorite = Favorite(favorite.id, articleId)

        // Given a Article that has been updated into the DB
        val rowAffected = mDatabase.favoriteDao().updateFavorite(newFavorite)

        // Check that's there only one row affected when updating
        assertEquals(1, rowAffected)

        // When getting the Favorite via the DAO
        val favoriteDb = mDatabase.favoriteDao().getFavorite(favorite.id)

        // Then the retrieved Article match the updated article object
        assertEquals(newFavorite, favoriteDb)
    }

    @Test
    fun deleteArticleAndCheckDb() = runBlockingTest {
        // Insert a article page in database for the test
        mDatabase.favoriteDao().insertFavorite(favorite)

        // Given a Favorite that has been inserted into the DB
        val rowAffected = mDatabase.favoriteDao().deleteFavorite(favorite)

        // Check that's there only row affected when deleting
        assertEquals(1, rowAffected)

        // When getting the Favorite via the DAO
        val articleDb = mDatabase.favoriteDao().getFavorite(favorite.id)

        // Then the retrieved Favorite is empty
        assertNull(articleDb)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * One favorite test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun oneFavoriteTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert an article in database for the test
        mDatabase.favoriteDao().insertFavorite(favorite)

        // Execute the test
        block()

        // Delete inserted article for test
        mDatabase.favoriteDao().deleteFavorite(favorite)

        // Clean up coroutines
        cleanupTestCoroutines()
    }

    /**
     * Favorite list test helper.
     *
     * @param block the suspend block test to execute.
     */
    private inline fun favoriteListTest(crossinline block: suspend () -> Unit) = runBlockingTest {
        // Insert a article list in database for the test
        mDatabase.favoriteDao().insertFavorite(*favoriteList.toTypedArray())

        // Execute the test
        block()

        // Delete inserted article list for test
        favoriteList.forEach {
            mDatabase.favoriteDao().deleteFavorite(it)
        }

        // Clean up coroutines
        cleanupTestCoroutines()
    }
}