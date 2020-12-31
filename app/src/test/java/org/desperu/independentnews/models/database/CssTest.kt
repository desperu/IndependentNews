package org.desperu.independentnews.models.database

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Css data class that's check setter, getter and default parameters.
 */
class CssTest {

    private val id: Long = 1L
    private val articleId: Long = 10L
    private val url: String = "https://www.reporterre.net/local/cache-css/8ef5d05e41385cd2bc955d69b8dc8fb7.css?1604084252"
    private val content: String = "a css style"

    @Test
    fun given_emptyCss_When_createCss_Then_checkDefaultValues() {
        val css = Css()

        assertEquals(0L, css.id)
        assertEquals(0L, css.articleId)
        assertEquals("", css.url)
        assertEquals("", css.content)
    }

    @Test
    fun given_css_When_createCss_Then_checkValues() {
        val css = Css(id, articleId, url, content)

        assertEquals(id, css.id)
        assertEquals(articleId, css.articleId)
        assertEquals(url, css.url)
        assertEquals(content, css.content)
    }

    @Test
    fun given_emptyCss_When_setCssValues_Then_checkValues() {
        val css = Css()

        css.content = content

        assertEquals(content, css.content)
    }
}