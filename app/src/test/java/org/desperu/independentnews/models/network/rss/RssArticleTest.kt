package org.desperu.independentnews.models.network.rss

import org.desperu.independentnews.utils.REPORTERRE
import org.desperu.independentnews.utils.Utils.stringToDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Simple model class test, for Rss Article data class that's check converter.
 */
class RssArticleTest {

    // FOR DATA
    private val title = "A title"
    private val url = "an url"
    private val permUrl = "a perm url"
    private val publishedDate = "2020-10-13T09:40:19Z"
    private val publishedDateLong = stringToDate(publishedDate)?.time
    private val author = "an author"
    private val categoryList = listOf(Category("a category"))
    private val categories = "a category"
    private val htmlDescription = "<p>a description</p>"
    private val description = "a description"
    private val sourceName = REPORTERRE


    @Test
    fun given_rssArticle_When_toArticle_Then_checkValues() {
        val rssArticle = RssArticle(title, url, permUrl, publishedDate, author, categoryList, htmlDescription)
        val article = rssArticle.toArticle(sourceName)

        assertEquals(sourceName, article.sourceName)
        assertEquals(url, article.url)
        assertEquals(title, article.title)
        assertEquals(author, article.author)
        assertEquals(publishedDateLong, article.publishedDate)
        assertEquals(categories, article.categories)
        assertEquals(description, article.description)
        assertTrue(article.isTopStory)

    }
}