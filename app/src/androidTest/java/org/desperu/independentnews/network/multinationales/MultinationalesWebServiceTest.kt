package org.desperu.independentnews.network.multinationales

import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.network.html.multinationales.MultinationalesArticle
import org.desperu.independentnews.models.network.html.multinationales.MultinationalesCategory
import org.desperu.independentnews.utils.MULTINATIONALES_SEC_ENQUETE
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.get

/**
 * Class test, for Multinationales Web Service Interface, that fetching data works.
 * And with use this fetched data to check parsing too.
 */
class MultinationalesWebServiceTest : KoinTest {

    // FOR DATA
    private val multinationalesWebService = get<MultinationalesWebService>()
    private val articleUrl = "Adieu-a-la-privatisation-Paris-Grenoble-et-le-combat-inacheve-de-la"
    private val cssUrl = "https://www.multinationales.org/local/cache-css/7a5edecca54db2c05acf17405d69170a.css?1609997496"

    @Test
    fun fetchArticle() = runBlocking {
        val responseBody = multinationalesWebService.getArticle(articleUrl)
        assertThat("Something was downloaded !", responseBody.contentLength() != 0L)

        // Then use response to test parsing article
        checkParsingArticle(responseBody)
    }

    /**
     * Check parsing article.
     *
     * @param responseBody the response body of the article request.
     */
    private fun checkParsingArticle(responseBody: ResponseBody) {
        val article = Article()
        MultinationalesArticle(responseBody).toArticle(article)

        assertTrue(article.title.isNotBlank())
        assertTrue(article.section.isBlank()) // Never set in the articles
        assertTrue(article.theme.isNotBlank())
        assertTrue(article.author.isNotBlank())
        assertTrue(article.publishedDate != 0L)
        assertTrue(article.article.isNotBlank())
        assertTrue(article.description.isNotBlank())
        assertTrue(article.imageUrl.isNotBlank())
        assertTrue(article.cssUrl.isNotBlank())
        assertTrue(article.source.name.isNotBlank())
    }

    @Test
    fun fetchCategories() = runBlocking {
        val responseBody = multinationalesWebService.getCategory(MULTINATIONALES_SEC_ENQUETE, "0")
        assertThat("Something was downloaded !", responseBody.contentLength() != 0L)

        // Then use response to test parsing category
        checkParsingCategory(responseBody)
    }

    /**
     * Check parsing category.
     *
     * @param responseBody the response body of the category request.
     */
    private fun checkParsingCategory(responseBody: ResponseBody) {
        val multinationalesCategory = MultinationalesCategory(responseBody)
        assertEquals(5, multinationalesCategory.getArticleList().size)
    }

    @Test
    fun fetchCss() = runBlocking {
        val cssStyle = multinationalesWebService.getCss(cssUrl).string()

        assertThat("Something was downloaded !", cssStyle.isNotBlank())
    }
}