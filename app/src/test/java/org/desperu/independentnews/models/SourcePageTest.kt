package org.desperu.independentnews.models

import org.desperu.independentnews.utils.BASTAMAG_BASE_URL
import org.desperu.independentnews.utils.EQUALS
import org.desperu.independentnews.utils.NOT_EQUALS
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Source Page class that's check setter, getter and default parameters.
 */
class SourcePageTest {

    private val id: Long = 1L
    private val sourceId: Long = 2L
    private val url: String = BASTAMAG_BASE_URL
    private val buttonName: String = "a button name"
    private val title: String = "a title"
    private val body: String = "a body"
    private val cssUrl: String = "a css url"
    private val position: Int = 0
    private val isPrimary: Boolean = true

    private val sourcePage = SourcePage(
        id, sourceId, url, buttonName, title, body, cssUrl, position, isPrimary
    )

    @Test
    fun given_emptySourcePage_When_createEmptySourcePage_Then_checkDefaultValues() {
        val sourcePage = SourcePage()

        assertEquals(sourcePage.id, 0L)
        assertEquals(sourcePage.sourceId, 0L)
        assertEquals(sourcePage.url, "")
        assertEquals(sourcePage.buttonName, "")
        assertEquals(sourcePage.title, "")
        assertEquals(sourcePage.body, "")
        assertEquals(sourcePage.cssUrl, "")
        assertEquals(sourcePage.position, -1)
        assertEquals(sourcePage.isPrimary, false)
    }

    @Test
    fun given_sourcePage_When_createSourcePage_Then_checkValues() {
        val sourcePage = SourcePage(
            id, sourceId, url, buttonName, title, body, cssUrl, position, isPrimary
        )

        assertEquals(sourcePage.id, id)
        assertEquals(sourcePage.sourceId, sourceId)
        assertEquals(sourcePage.url, url)
        assertEquals(sourcePage.buttonName, buttonName)
        assertEquals(sourcePage.title, title)
        assertEquals(sourcePage.body, body)
        assertEquals(sourcePage.cssUrl, cssUrl)
        assertEquals(sourcePage.position, position)
        assertEquals(sourcePage.isPrimary, isPrimary)
    }

    @Test
    fun given_emptySource_When_setSourceValues_Then_checkValues() {
        val sourcePage = SourcePage()

        sourcePage.sourceId = sourceId

        assertEquals(sourcePage.sourceId, sourceId)
    }

    @Test
    fun given_sameSourcePage_When_compareTo_Then_checkEquals() {
        val expected = EQUALS

        val output = sourcePage.compareTo(sourcePage)

        assertEquals(expected, output)
    }

    @Test
    fun given_otherSourcePage_When_compareTo_Then_checkNotEquals() {
        val expected = NOT_EQUALS

        val otherSourceWithData = SourcePage()
        val output = sourcePage.compareTo(otherSourceWithData)

        assertEquals(expected, output)
    }
}