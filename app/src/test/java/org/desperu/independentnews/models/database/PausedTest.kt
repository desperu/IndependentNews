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
    private val scrollPosition = 100

    @Test
    fun given_EmptyPaused_When_createPaused_Then_checkDefaultValues() {
        val favorite = Paused()

        assertEquals(0L, favorite.id)
        assertEquals(0L, favorite.articleId)
        assertEquals(0, favorite.scrollPosition)
    }

    @Test
    fun given_Paused_When_createPaused_Then_checkValues() {
        val favorite = Paused(id, articleId, scrollPosition)

        assertEquals(id, favorite.id)
        assertEquals(articleId, favorite.articleId)
        assertEquals(scrollPosition, favorite.scrollPosition)
    }

    @Test
    fun given_EmptyPaused_When_setPausedValues_Then_checkValues() {
        val expected = Paused(id, articleId, scrollPosition)

        val favorite = Paused()
        favorite.id = id
        favorite.articleId = articleId
        favorite.scrollPosition = scrollPosition

        assertEquals(expected, favorite)
    }
}