package org.desperu.independentnews.models

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

    @Test
    fun given_emptyArticle_When_createArticle_Then_checkDefaultValues() {
        val article = Article()

        assertEquals(article.id, 0L)
        assertEquals(article.sourceId, 0L)
        assertEquals(article.url, "")
        assertEquals(article.title, "")
        assertEquals(article.section, "")
        assertEquals(article.theme, "")
        assertEquals(article.author, "")
        assertEquals(article.publishedDate, 0L)
        assertEquals(article.article, "")
        assertEquals(article.categories, "")
        assertEquals(article.description, "")
        assertEquals(article.imageUrl, "")
        assertEquals(article.cssUrl, "")
        assertEquals(article.isTopStory, false)
        assertEquals(article.read, false)
        assertEquals(article.source, Source())
    }

    @Test
    fun given_article_When_createArticle_Then_checkValues() {
        val articleSet = Article(
            id, sourceId, sourceName, url, title, section, theme, author, publishedDate,
            article, categories, description, imageUrl, cssUrl, isTopStory, read, source
        )

        assertEquals(articleSet.id, id)
        assertEquals(articleSet.sourceId, sourceId)
        assertEquals(articleSet.url, url)
        assertEquals(articleSet.title, title)
        assertEquals(articleSet.section, section)
        assertEquals(articleSet.theme, theme)
        assertEquals(articleSet.author, author)
        assertEquals(articleSet.publishedDate, publishedDate)
        assertEquals(articleSet.article, article)
        assertEquals(articleSet.categories, categories)
        assertEquals(articleSet.description, description)
        assertEquals(articleSet.imageUrl, imageUrl)
        assertEquals(articleSet.cssUrl, cssUrl)
        assertEquals(articleSet.isTopStory, isTopStory)
        assertEquals(articleSet.read, read)
        assertEquals(articleSet.source, source)
    }

    @Test
    fun given_emptyArticle_When_setArticleValues_Then_checkValues() {
        val articleEmpty = Article()

        articleEmpty.id = id
        articleEmpty.sourceId = sourceId
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

        assertEquals(articleEmpty.id, id)
        assertEquals(articleEmpty.sourceId, sourceId)
        assertEquals(articleEmpty.url, url)
        assertEquals(articleEmpty.title, title)
        assertEquals(articleEmpty.section, section)
        assertEquals(articleEmpty.theme, theme)
        assertEquals(articleEmpty.author, author)
        assertEquals(articleEmpty.publishedDate, publishedDate)
        assertEquals(articleEmpty.article, article)
        assertEquals(articleEmpty.categories, categories)
        assertEquals(articleEmpty.description, description)
        assertEquals(articleEmpty.imageUrl, imageUrl)
        assertEquals(articleEmpty.cssUrl, cssUrl)
        assertEquals(articleEmpty.isTopStory, isTopStory)
        assertEquals(articleEmpty.read, read)
        assertEquals(articleEmpty.source, source)
    }
}