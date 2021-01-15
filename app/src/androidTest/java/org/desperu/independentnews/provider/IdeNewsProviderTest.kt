package org.desperu.independentnews.provider

import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.desperu.independentnews.utils.CSS_STYLE
import org.desperu.independentnews.utils.CSS_ID
import org.desperu.independentnews.utils.CSS_URL
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNull.notNullValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test

/**
 * Simple content provider class test, for Independent News Content Provider, that check CRUD functions.
 */
@ExperimentalCoroutinesApi
class IdeNewsProviderTest {

    // FOR DATA
    private val mContentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver

    // DATA SET FOR TEST
    private var id = 0L
    private val url: String = "https://www.reporterre.net/local/cache-css/8ef5d05e41385cd2bc955d69b8dc8fb7.css?1604084252"
    private val style: String = "a css style"

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
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow(CSS_URL)), `is` (url))
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow(CSS_STYLE)), `is` (style))
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
        contentValues.put(CSS_URL, url + 1)
        contentValues.put(CSS_STYLE, style + 1)

        val updated: Int? = cssUri?.let { mContentResolver.update(it, contentValues, null, null) }
        assertThat(updated, `is`(1))

        // TEST
        val cursor: Cursor? = cssUri?.let { mContentResolver.query(it, null, null, null, null) }

        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(1))
        assertThat(cursor?.moveToFirst(), `is`(true))

        assertEquals(cursor?.getLong(cursor.getColumnIndexOrThrow(CSS_ID)), cssId)
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow(CSS_URL)), `is` (url + 1))
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow(CSS_STYLE)), `is` (style + 1))

        // Delete created css after test
        cssUri?.let { mContentResolver.delete(it, null, null) }
        cursor?.close()
    }

    private fun generateCss(): ContentValues {
        val values = ContentValues()

        values.put(CSS_ID, 1000000000000000000L)
        values.put(CSS_URL, url)
        values.put(CSS_STYLE, style)

        return values
    }
}