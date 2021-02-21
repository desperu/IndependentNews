package org.desperu.independentnews.models.database

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Favorite data class that's check setter, getter and default parameters.
 */
class FavoriteTest {

    // FOR DATA
    private val id = 1L
    private val articleId = 2L

    @Test
    fun given_EmptyFavorite_When_createFavorite_Then_checkDefaultValues() {
        val favorite = Favorite()

        assertEquals(0L, favorite.id)
        assertEquals(0L, favorite.articleId)
    }

    @Test
    fun given_Favorite_When_createFavorite_Then_checkValues() {
        val favorite = Favorite(id, articleId)

        assertEquals(id, favorite.id)
        assertEquals(articleId, favorite.articleId)
    }

    @Test
    fun given_EmptyFavorite_When_setFavoriteValues_Then_checkValues() {
        val expected = Favorite(id, articleId)

        val favorite = Favorite()
        favorite.id = id
        favorite.articleId = articleId

        assertEquals(expected, favorite)
    }
}