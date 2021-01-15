package org.desperu.independentnews.models.database

import android.content.ContentValues
import org.desperu.independentnews.utils.CSS_STYLE
import org.desperu.independentnews.utils.CSS_ID
import org.desperu.independentnews.utils.CSS_URL
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Simple model class test, for Css data class that's check setter, getter and default parameters.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest= Config.NONE)
class CssTest {

    // FOR DATA
    private val id: Long = 1L
    private val url: String = "https://www.reporterre.net/local/cache-css/8ef5d05e41385cd2bc955d69b8dc8fb7.css?1604084252"
    private val cssStyle: String = "a css style"

    @Test
    fun given_EmptyCss_When_createCss_Then_checkDefaultValues() {
        val css = Css()

        assertEquals(0L, css.id)
        assertEquals("", css.url)
        assertEquals("", css.style)
    }

    @Test
    fun given_Css_When_createCss_Then_checkValues() {
        val css = Css(id, url, cssStyle)

        assertEquals(id, css.id)
        assertEquals(url, css.url)
        assertEquals(cssStyle, css.style)
    }

    @Test
    fun given_EmptyCss_When_setCssValues_Then_checkValues() {
        val expected = Css(id, url, cssStyle)

        val css = Css()
        css.id = id
        css.url = url
        css.style = cssStyle

        assertEquals(expected, css)
    }

    @Test
    fun given_ContentValues_When_fromContentValues_Then_checkCssFields() {
        val expected = Css(id, url, cssStyle)

        val values = ContentValues()

        assertNotNull(values)

        values.put(CSS_ID, id)
        values.put(CSS_URL, url)
        values.put(CSS_STYLE, cssStyle)

        val output = Css().fromContentValues(values)

        assertEquals(expected, output)
    }

    @Test
    fun given_Css_When_toContentValues_Then_checkResult() {
        val expected = ContentValues()

        assertNotNull(expected)

        expected.put(CSS_ID, id)
        expected.put(CSS_URL, url)
        expected.put(CSS_STYLE, cssStyle)

        val css = Css(id, url, cssStyle)

        val output = Css().toContentValues(css)

        assertEquals(expected, output)
    }
}