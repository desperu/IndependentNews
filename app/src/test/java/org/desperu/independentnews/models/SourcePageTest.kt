package org.desperu.independentnews.models

import org.desperu.independentnews.utils.BASTAMAG_BASE_URL
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Source Page class that's check setter, getter and default parameters.
 */
class SourcePageTest {

    private val id: Long = 1L
    private val sourceId: Long = 2L
    private val url: String = BASTAMAG_BASE_URL
    private val title: String = "a title"
    private val body: String = "a body"
    private val imageUrl: String = "an image url"
    private val cssUrl: String = "a css url"
    private val position: Int = 0
    private val isPrimary: Boolean = true

    @Test
    fun given_emptySourcePage_When_createEmptySourcePage_Then_checkDefaultValues() {
        val sourcePage = SourcePage()

        assertEquals(sourcePage.id, 0L)
        assertEquals(sourcePage.sourceId, 0L)
        assertEquals(sourcePage.url, "")
        assertEquals(sourcePage.title, "")
        assertEquals(sourcePage.body, "")
        assertEquals(sourcePage.imageUrl, "")
        assertEquals(sourcePage.cssUrl, "")
        assertEquals(sourcePage.position, -1)
        assertEquals(sourcePage.isPrimary, false)
    }

    @Test
    fun given_sourcePage_When_createSourcePage_Then_checkValues() {
        val sourcePage = SourcePage(
            id, sourceId, url, title, body, imageUrl, cssUrl, position, isPrimary
        )

        assertEquals(sourcePage.id, id)
        assertEquals(sourcePage.sourceId, sourceId)
        assertEquals(sourcePage.url, url)
        assertEquals(sourcePage.title, title)
        assertEquals(sourcePage.body, body)
        assertEquals(sourcePage.imageUrl, imageUrl)
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
}