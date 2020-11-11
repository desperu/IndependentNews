package org.desperu.independentnews.models

import org.desperu.independentnews.utils.BASTAMAG
import org.desperu.independentnews.utils.BASTAMAG_BASE_URL
import org.desperu.independentnews.utils.REPORTERRE_BASE_URL
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Source With Data class that's check setter, getter and default parameters.
 */
class SourceWithDataTest {

    // FOR DATA
    private val source = Source(1L, BASTAMAG, BASTAMAG_BASE_URL,
        1, 2, 3, false)

    private val sourcePage1 = SourcePage(1L, 2L, BASTAMAG_BASE_URL, "a title",
        "a body", "an image url", "a css url",0,  true)
    private val sourcePage2 = SourcePage(2L, 2L, REPORTERRE_BASE_URL, "a title2",
        "a body2", "an image url2", "a css url2",1,  false)

    private val sourcePages = listOf(sourcePage1, sourcePage2)


    @Test
    fun given_emptySourceWithData_When_createSource_Then_checkDefaultValues() {
        val sourceWithData = SourceWithData()

        assertEquals(Source(), sourceWithData.source)
        assertEquals(listOf<SourcePage>(), sourceWithData.sourcePages)
    }

    @Test
    fun given_sourceWithData_When_createSource_Then_checkValues() {
        val sourceWithData = SourceWithData(source, sourcePages)

        assertEquals(source, sourceWithData.source)
        assertEquals(sourcePages, sourceWithData.sourcePages)
    }

    @Test
    fun given_sourceWithData_When_toSimplePage_Then_checkSimplePageValue() {
        val expected = SourceWithData(source, listOf(sourcePage2))

        val sourceWithData = SourceWithData(source, sourcePages)
        val output = sourceWithData.toSimplePage(1)

        assertEquals(expected, output)
    }

    @Test
    fun given_emptySourceWithData_When_toSimplePage_Then_checkEmptyValue() {
        val expected = SourceWithData()

        val sourceWithData = SourceWithData()
        val output = sourceWithData.toSimplePage(1)

        assertEquals(expected, output)
    }

    @Test
    fun given_sourceWithData_When_toArticle_Then_checkArticleValues() {
        val expected = Article(
            sourceName = BASTAMAG,
            url = BASTAMAG_BASE_URL,
            title = "a title",
            article = "a body",
            cssUrl = "a css url",
            source = source
        )

        val sourceWithData = SourceWithData(source, sourcePages)
        val output = sourceWithData.toArticle()

        assertEquals(expected, output)
    }

    @Test
    fun given_sourceWithData_When_toArticle_Then_checkEmptyValues() {
        val expected = Article()

        val sourceWithData = SourceWithData()
        val output = sourceWithData.toArticle()

        assertEquals(expected, output)
    }
}