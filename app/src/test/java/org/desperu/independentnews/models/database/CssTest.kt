package org.desperu.independentnews.models.database

import android.content.ContentValues
import org.desperu.independentnews.utils.CSS_ARTICLE_ID
import org.desperu.independentnews.utils.CSS_CONTENT
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

    private val id: Long = 1L
    private val articleId: Long = 10L
    private val url: String = "https://www.reporterre.net/local/cache-css/8ef5d05e41385cd2bc955d69b8dc8fb7.css?1604084252"
    private val content: String = "a css style"

    @Test
    fun given_EmptyCss_When_createCss_Then_checkDefaultValues() {
        val css = Css()

        assertEquals(0L, css.id)
        assertEquals(0L, css.articleId)
        assertEquals("", css.url)
        assertEquals("", css.content)
    }

    @Test
    fun given_Css_When_createCss_Then_checkValues() {
        val css = Css(id, articleId, url, content)

        assertEquals(id, css.id)
        assertEquals(articleId, css.articleId)
        assertEquals(url, css.url)
        assertEquals(content, css.content)
    }

    @Test
    fun given_EmptyCss_When_setCssValues_Then_checkValues() {
        val expected = Css(id, articleId, url, content)

        val css = Css()
        css.id = id
        css.articleId = articleId
        css.url = url
        css.content = content

        assertEquals(expected, css)
    }

    @Test
    fun given_ContentValues_When_fromContentValues_Then_checkCssFields() {
        val expected = Css(id, articleId, url, content)

        val values = ContentValues()

        assertNotNull(values)

        values.put(CSS_ID, id)
        values.put(CSS_ARTICLE_ID, articleId)
        values.put(CSS_URL, url)
        values.put(CSS_CONTENT, content)

        val output = Css().fromContentValues(values)

        assertEquals(expected, output)
    }

    @Test
    fun given_Css_When_toContentValues_Then_checkResult() {
        val expected = ContentValues()

        assertNotNull(expected)

        expected.put(CSS_ID, id)
        expected.put(CSS_ARTICLE_ID, articleId)
        expected.put(CSS_URL, url)
        expected.put(CSS_CONTENT, content)

        val css = Css(id, articleId, url, content)

        val output = Css().toContentValues(css)

        assertEquals(expected, output)
    }
}