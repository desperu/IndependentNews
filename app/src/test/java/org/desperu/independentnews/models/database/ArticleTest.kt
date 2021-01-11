package org.desperu.independentnews.models.database

import org.desperu.independentnews.utils.BASTAMAG
import org.desperu.independentnews.utils.BASTAMAG_BASE_URL
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Simple model class test, for Article data class that's check setter, getter and default parameters.
 */
class ArticleTest {

    private val id: Long = 1L
    private val sourceId: Long = 2L
    private val sourceName: String = BASTAMAG
    private val url: String = BASTAMAG_BASE_URL
    private val title: String = "A title"
    private val section: String = "a section"
    private val theme: String = "a theme"
    private val author: String = "an author"
    private val publishedDate: Long = 0L
    private val article: String = "an article"
    private val categories: String = "a category"
    private val description: String = "a description"
    private val imageUrl: String = "an image url"
    private val cssUrl: String = "an css url"
    private val isTopStory: Boolean = false
    private val read: Boolean = false
    private val source: Source = Source()
    private val cssStyle = "a css style"

    @Test
    fun given_emptyArticle_When_createArticle_Then_checkDefaultValues() {
        val article = Article()

        assertEquals(0L, article.id)
        assertEquals(0L, article.sourceId)
        assertEquals("", article.sourceName)
        assertEquals("", article.url)
        assertEquals("", article.title)
        assertEquals("", article.section)
        assertEquals("", article.theme)
        assertEquals("", article.author)
        assertEquals(0L, article.publishedDate)
        assertEquals("", article.article)
        assertEquals("", article.categories)
        assertEquals("", article.description)
        assertEquals("", article.imageUrl)
        assertEquals("", article.cssUrl)
        assertEquals(false, article.isTopStory)
        assertEquals(false, article.read)
        assertEquals(Source(), article.source)
        assertEquals("", article.cssStyle)
    }

    @Test
    fun given_article_When_createArticle_Then_checkValues() {
        val articleSet = Article(
            id, sourceId, sourceName, url, title, section, theme, author, publishedDate, article,
            categories, description, imageUrl, cssUrl, isTopStory, read, source, cssStyle
        )

        assertEquals(id, articleSet.id)
        assertEquals(sourceId, articleSet.sourceId)
        assertEquals(sourceName, articleSet.sourceName)
        assertEquals(url, articleSet.url)
        assertEquals(title, articleSet.title)
        assertEquals(section, articleSet.section)
        assertEquals(theme, articleSet.theme)
        assertEquals(author, articleSet.author)
        assertEquals(publishedDate, articleSet.publishedDate)
        assertEquals(article, articleSet.article)
        assertEquals(categories, articleSet.categories)
        assertEquals(description, articleSet.description)
        assertEquals(imageUrl, articleSet.imageUrl)
        assertEquals(cssUrl, articleSet.cssUrl)
        assertEquals(isTopStory, articleSet.isTopStory)
        assertEquals(read, articleSet.read)
        assertEquals(source, articleSet.source)
        assertEquals(cssStyle, articleSet.cssStyle)
    }

    @Test
    fun given_emptyArticle_When_setArticleValues_Then_checkValues() {
        val articleEmpty = Article()

        articleEmpty.id = id
        articleEmpty.sourceId = sourceId
        articleEmpty.sourceName = sourceName
        articleEmpty.url = url
        articleEmpty.title = title
        articleEmpty.section = section
        articleEmpty.theme = theme
        articleEmpty.author = author
        articleEmpty.publishedDate = publishedDate
        articleEmpty.article = article
        articleEmpty.categories = categories
        articleEmpty.description = description
        articleEmpty.imageUrl = imageUrl
        articleEmpty.cssUrl = cssUrl
        articleEmpty.isTopStory = isTopStory
        articleEmpty.read = read
        articleEmpty.source = source
        articleEmpty.cssStyle = cssStyle

        assertEquals(id, articleEmpty.id)
        assertEquals(sourceId, articleEmpty.sourceId)
        assertEquals(sourceName, articleEmpty.sourceName)
        assertEquals(url, articleEmpty.url)
        assertEquals(title, articleEmpty.title)
        assertEquals(section, articleEmpty.section)
        assertEquals(theme, articleEmpty.theme)
        assertEquals(author, articleEmpty.author)
        assertEquals(publishedDate, articleEmpty.publishedDate)
        assertEquals(article, articleEmpty.article)
        assertEquals(categories, articleEmpty.categories)
        assertEquals(description, articleEmpty.description)
        assertEquals(imageUrl, articleEmpty.imageUrl)
        assertEquals(cssUrl, articleEmpty.cssUrl)
        assertEquals(isTopStory, articleEmpty.isTopStory)
        assertEquals(read, articleEmpty.read)
        assertEquals(sourceId, articleEmpty.sourceId)
        assertEquals(cssStyle, articleEmpty.cssStyle)
    }
}