package org.desperu.independentnews.models

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Source With Data class that's check setter, getter and default parameters.
 */
class SourceWithDataTest {

    private val source = Source(2L)
    private val sourcePages = listOf(SourcePage(23L))

    @Test
    fun given_emptySource_When_createSource_Then_checkDefaultValues() {
        val sourceWithData = SourceWithData()

        assertEquals(Source(), sourceWithData.source)
        assertEquals(listOf<SourcePage>(), sourceWithData.sourcePages)
    }

    @Test
    fun given_source_When_createSource_Then_checkValues() {
        val sourceWithData = SourceWithData(source, sourcePages)

        assertEquals(source, sourceWithData.source)
        assertEquals(sourcePages, sourceWithData.sourcePages)
    }
}