package org.desperu.independentnews.provider

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.database.dao.DaoTestHelper
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.utils.CSS_ARTICLE_ID
import org.desperu.independentnews.utils.CSS_CONTENT
import org.desperu.independentnews.utils.CSS_ID
import org.desperu.independentnews.utils.CSS_URL
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNull.notNullValue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Simple content provider class test, for Independent News Content Provider, that check CRUD functions.
 */
@ExperimentalCoroutinesApi
class IdeNewsProviderTest {

    // FOR DATA
    private lateinit var mContentResolver: ContentResolver
    private lateinit var mDatabase: ArticleDatabase
    // Create a Source for foreign key
    private lateinit var source: Source
    private var sourceId = 0L
    // Create an Article for foreign key
    private lateinit var article: Article
    private var articleId = 0L

    // DATA SET FOR TEST
    private var id = 0L
    private val url: String = "https://www.reporterre.net/local/cache-css/8ef5d05e41385cd2bc955d69b8dc8fb7.css?1604084252"
    private val content: String = "a css style"

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        // init Db for test
        mDatabase = DaoTestHelper().initDb()

        // Set Source for foreign keys matches
        source = DaoTestHelper().source
        runBlockingTest { sourceId = mDatabase.sourceDao().insertSources(source)[0] }

        // Set Article for foreign keys matches
        article = DaoTestHelper().getArticle(sourceId)
        runBlockingTest { articleId = mDatabase.articleDao().insertArticles(article)[0] }

        // Set content resolver
        mContentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @After
    fun close() {
        // Remove inserted article and source for the test
        runBlocking {
            mDatabase.sourceDao().deleteSource(source.name)
        }

        mDatabase.close()
    }

    @Test
    fun getCssWhenNoCssInserted() {
        val cursor: Cursor? = mContentResolver.query(
            ContentUris.withAppendedId(IdeNewsProvider.URI_CSS, id),
            null,
            null,
            null,
            null
        )
        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(0))
        cursor?.close()
    }

    @Test
    fun insertAndGetCss() {
        // BEFORE : Adding demo item
        val cssUri: Uri? = mContentResolver.insert(IdeNewsProvider.URI_CSS, generateCss())
        val cssId = cssUri?.let { ContentUris.parseId(it) }

        // TEST
        val cursor: Cursor? = cssUri?.let {
            mContentResolver.query(it, null, null, null, null)
        }

        cursor?.let {
            assertThat(cursor, notNullValue())
            assertThat(cursor.count, `is`(1))
            assertThat(cursor.moveToFirst(), `is`(true))

            assertEquals(cursor.getLong(cursor.getColumnIndexOrThrow(CSS_ID)), cssId)
            assertThat(cursor.getLong(cursor.getColumnIndexOrThrow(CSS_ARTICLE_ID)), `is`(articleId))
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow(CSS_URL)), `is` (url))
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow(CSS_CONTENT)), `is` (content))
        }

        // Delete created css after test
        cssUri?.let { mContentResolver.delete(it, null, null) }
        cursor?.close()
    }

    @Test
    fun getCssType() {
        val cssUri: Uri? = mContentResolver.insert(IdeNewsProvider.URI_CSS, generateCss())

        val output = cssUri?.let { mContentResolver.getType(it) }

        assertThat(output, `is`("vnd.android.cursor.item/org.desperu.independentnews.provider.Css"))

        // Delete created css after test
        cssUri?.let { mContentResolver.delete(it, null, null) }
    }

    @Test
    fun insertAndDeleteCss() {
        // BEFORE : Adding demo item
        val cssUri: Uri? = mContentResolver.insert(IdeNewsProvider.URI_CSS, generateCss())

        // TEST
        val deleted: Int? = cssUri?.let { mContentResolver.delete(it, null, null) }

        assertThat(deleted, `is`(1))
    }

    @Test
    fun insertAndUpdateCss() {
        // BEFORE : Adding demo item
        val contentValues = generateCss()
        val cssUri: Uri? = mContentResolver.insert(IdeNewsProvider.URI_CSS, contentValues)
        val cssId = cssUri?.let { ContentUris.parseId(it) }

        // UPDATE DATA
        contentValues.put(CSS_ID, cssId)
        contentValues.put(CSS_ARTICLE_ID, articleId + 1)
        contentValues.put(CSS_URL, url + 1)
        contentValues.put(CSS_CONTENT, content + 1)

        val updated: Int? = cssUri?.let { mContentResolver.update(it, contentValues, null, null) }
        assertThat(updated, `is`(1))

        // TEST
        val cursor: Cursor? = cssUri?.let { mContentResolver.query(it, null, null, null, null) }

        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(1))
        assertThat(cursor?.moveToFirst(), `is`(true))

        assertEquals(cursor?.getLong(cursor.getColumnIndexOrThrow(CSS_ID)), cssId)
        assertThat(cursor?.getLong(cursor.getColumnIndexOrThrow(CSS_ARTICLE_ID)), `is`(articleId + 1))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow(CSS_URL)), `is` (url + 1))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow(CSS_CONTENT)), `is` (content + 1))

        // Delete created css after test
        cssUri?.let { mContentResolver.delete(it, null, null) }
        cursor?.close()
    }

    private fun generateCss(): ContentValues {
        val values = ContentValues()

        values.put(CSS_ID, 1000000000000000000L)
        values.put(CSS_ARTICLE_ID, articleId)
        values.put(CSS_URL, url)
        values.put(CSS_CONTENT, content)

        return values
    }
}