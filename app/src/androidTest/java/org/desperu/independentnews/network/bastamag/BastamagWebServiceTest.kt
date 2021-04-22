package org.desperu.independentnews.network.bastamag

import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.network.html.bastamag.BastamagArticle
import org.desperu.independentnews.models.network.html.bastamag.BastamagCategory
import org.desperu.independentnews.utils.BASTA_SEC_DECRYPTER
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.get

/**
 * Class test, for Bastamag Web Service Interface, that fetching data works.
 * And with use this fetched data to check parsing too.
 */
class BastamagWebServiceTest : KoinTest {

    // FOR DATA
    private val bastamagWebService = get<BastamagWebService>()
    private val articleUrl = "Sympathies-fascistes-oppression-coloniale-brutalites-anti-ouvrieres-Michelin-caoutchouc"
    private val cssUrl = "https://www.bastamag.net/local/cache-css/012e01c534abe935e2dde8d496f3e681.css?1619045642"

    @Test
    fun fetchArticle() = runBlocking {
        val responseBody = bastamagWebService.getArticle(articleUrl)
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
        BastamagArticle(responseBody).toArticle(article)

        assertTrue(article.title.isNotBlank())
        assertTrue(article.section.isNotBlank())
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
        val responseBody = bastamagWebService.getCategory(BASTA_SEC_DECRYPTER, "0")
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
        val bastaCategory = BastamagCategory(responseBody)
        assertEquals(10, bastaCategory.getArticleList().size)
    }

    @Test
    fun fetchCss() = runBlocking {
        val cssStyle = bastamagWebService.getCss(cssUrl).string()

        assertThat("Something was downloaded !", cssStyle.isNotBlank())
    }
}