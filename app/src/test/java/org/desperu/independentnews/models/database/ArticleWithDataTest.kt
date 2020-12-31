package org.desperu.independentnews.models.database

import org.desperu.independentnews.utils.BASTAMAG
import org.desperu.independentnews.utils.BASTAMAG_BASE_URL
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Article With Data class that's check setter, getter and default parameters.
 */
class ArticleWithDataTest {

    // FOR DATA
    private val article = Article(
        1L, 2L, BASTAMAG, "an article url", "a title", "a section",
        "a theme", "an author", 150000000L, "a body",
        "a category", "a description", "an image url",
        "a css url", isTopStory = false, read = false
    )

    private val css = Css(10L, 1L, "a css url", "a css style")
    private val source =  Source(2L, BASTAMAG, BASTAMAG_BASE_URL, false)

    @Test
    fun given_emptyArticleWithData_When_createArticleWithData_Then_checkDefaultValues() {
        val articleWithData = ArticleWithData()

        assertEquals(Article(), articleWithData.article)
        assertEquals(Css(), articleWithData.css)
        assertEquals(Source(), articleWithData.source)
    }

    @Test
    fun given_sourceWithData_When_createSource_Then_checkValues() {
        val articleWithData = ArticleWithData(article, css, source)

        assertEquals(article, articleWithData.article)
        assertEquals(css, articleWithData.css)
        assertEquals(source, articleWithData.source)
    }
}