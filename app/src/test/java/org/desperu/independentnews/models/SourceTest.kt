package org.desperu.independentnews.models

import org.desperu.independentnews.utils.BASTAMAG
import org.desperu.independentnews.utils.BASTAMAG_BASE_URL
import org.desperu.independentnews.utils.BASTAMAG_EDITO_URL
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Source data class that's check setter, getter and default parameters.
 */
class SourceTest {

    private val id: Long = 1L
    private val name: String = BASTAMAG
    private val url: String = BASTAMAG_BASE_URL
    private val editorialUrl: String = BASTAMAG_EDITO_URL
    private val editorial: String = "an editorial"
    private val imageId: Int = 1
    private val logoId: Int = 2
    private val isEnabled: Boolean = false

    @Test
    fun given_emptySource_When_createSource_Then_checkDefaultValues() {
        val source = Source()

        assertEquals(source.id, 0L)
        assertEquals(source.name, "")
        assertEquals(source.url, "")
        assertEquals(source.editorialUrl, "")
        assertEquals(source.editorial, "")
        assertEquals(source.imageId, 0)
        assertEquals(source.logoId, 0)
        assertEquals(source.isEnabled, true)
    }

    @Test
    fun given_source_When_createSource_Then_checkValues() {
        val source = Source(
            id, name, url, editorialUrl, editorial, imageId, logoId, isEnabled
        )

        assertEquals(source.id, id)
        assertEquals(source.name, name)
        assertEquals(source.url, url)
        assertEquals(source.editorialUrl, editorialUrl)
        assertEquals(source.editorial, editorial)
        assertEquals(source.imageId, imageId)
        assertEquals(source.logoId, logoId)
        assertEquals(source.isEnabled, isEnabled)
    }

    @Test
    fun given_emptySource_When_setSourceValues_Then_checkValues() {
        val source = Source()

        source.name = name
        source.url = url
        source.editorialUrl = editorialUrl
        source.editorial = editorial
        source.imageId = imageId
        source.logoId = logoId
        source.isEnabled = isEnabled

        assertEquals(source.name, name)
        assertEquals(source.url, url)
        assertEquals(source.editorialUrl, editorialUrl)
        assertEquals(source.editorial, editorial)
        assertEquals(source.imageId, imageId)
        assertEquals(source.logoId, logoId)
        assertEquals(source.isEnabled, isEnabled)
    }
}