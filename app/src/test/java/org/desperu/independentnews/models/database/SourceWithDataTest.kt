package org.desperu.independentnews.models.database

import org.desperu.independentnews.utils.*
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Source With Data class that's check setter, getter and default parameters.
 */
class SourceWithDataTest {

    // FOR DATA
    private val source = Source(1L, BASTAMAG, BASTAMAG_BASE_URL, false)

    private val url = BASTAMAG_EDITO_URL
    private val title = "a title"
    private val body = "a source page body"
    private val cssUrl = "a css url"

    private val sourcePage1 = SourcePage(1L, 2L, url, "a button name",
        title, body, cssUrl, 0,  true)
    private val sourcePage2 = SourcePage(2L, 4L, REPORTERRE_EDITO_URL, "a button name 2",
        "a title2", "a body2","a css url 2", 1,  false)

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
            url = url,
            title = title,
            article = body,
            cssUrl = cssUrl,
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

    @Test
    fun given_sameSourceWithData_When_compareTo_Then_checkEquals() {
        val expected = EQUALS

        val sourceWithData = SourceWithData(source, sourcePages)
        val output = sourceWithData.compareTo(sourceWithData)

        assertEquals(expected, output)
    }

    @Test
    fun given_otherSourceWithData_When_compareTo_Then_checkNotEquals() {
        val expected = NOT_EQUALS

        val sourceWithData = SourceWithData(source, sourcePages)
        val otherSourceWithData = SourceWithData(source, listOf(sourcePage2))
        val output = sourceWithData.compareTo(otherSourceWithData)

        assertEquals(expected, output)
    }
}