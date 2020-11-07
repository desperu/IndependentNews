package org.desperu.independentnews.models

import org.desperu.independentnews.utils.BASTAMAG
import org.desperu.independentnews.utils.BASTAMAG_BASE_URL
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Source data class that's check setter, getter and default parameters.
 */
class SourceTest {

    private val id: Long = 1L
    private val name: String = BASTAMAG
    private val url: String = BASTAMAG_BASE_URL
    private val imageId: Int = 1
    private val logoId: Int = 2
    private val backgroundColorId: Int = 3
    private val isEnabled: Boolean = false

    @Test
    fun given_emptySource_When_createSource_Then_checkDefaultValues() {
        val source = Source()

        assertEquals(0L, source.id)
        assertEquals("", source.name)
        assertEquals("", source.url)
        assertEquals(0, source.imageId)
        assertEquals(0, source.logoId)
        assertEquals(0, source.backgroundColorId)
        assertEquals(true, source.isEnabled)
    }

    @Test
    fun given_source_When_createSource_Then_checkValues() {
        val source = Source(
            id, name, url, imageId, logoId, backgroundColorId, isEnabled
        )

        assertEquals(id, source.id)
        assertEquals(name, source.name)
        assertEquals(url, source.url)
        assertEquals(imageId, source.imageId)
        assertEquals(logoId, source.logoId)
        assertEquals(backgroundColorId, source.backgroundColorId)
        assertEquals(isEnabled, source.isEnabled)
    }

    @Test
    fun given_emptySource_When_setSourceValues_Then_checkValues() {
        val source = Source()

        source.isEnabled = isEnabled

        assertEquals(source.isEnabled, isEnabled)
    }
}