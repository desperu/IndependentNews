package org.desperu.independentnews.models.database

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Paused data class that's check setter, getter and default parameters.
 */
class PausedTest {

    // FOR DATA
    private val id = 1L
    private val articleId = 2L
    private val scrollPosition = 1245f
    private val creationDate = 1561563416L

    @Test
    fun given_EmptyPaused_When_createPaused_Then_checkDefaultValues() {
        val paused = Paused()

        assertEquals(0L, paused.id)
        assertEquals(0L, paused.articleId)
        assertEquals(0f, paused.scrollPosition)
        assertEquals(0L, paused.creationDate)
    }

    @Test
    fun given_Paused_When_createPaused_Then_checkValues() {
        val paused = Paused(id, articleId, scrollPosition, creationDate)

        assertEquals(id, paused.id)
        assertEquals(articleId, paused.articleId)
        assertEquals(scrollPosition, paused.scrollPosition)
        assertEquals(creationDate, paused.creationDate)
    }

    @Test
    fun given_EmptyPaused_When_setPausedValues_Then_checkValues() {
        val expected = Paused(id, articleId, scrollPosition, creationDate)

        val paused = Paused()
        paused.id = id
        paused.articleId = articleId
        paused.scrollPosition = scrollPosition
        paused.creationDate = creationDate

        assertEquals(expected, paused)
    }
}