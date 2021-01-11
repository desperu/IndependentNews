package org.desperu.independentnews.network.reporterre

import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.network.html.reporterre.ReporterreArticle
import org.desperu.independentnews.models.network.html.reporterre.ReporterreCategory
import org.desperu.independentnews.utils.REPORT_SEC_RESISTER
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.get

/**
 * Class test, for Reporterre Web Service Interface, that fetching data works.
 * And with use this fetched data to check parsing too.
 */
class ReporterreWebServiceTest : KoinTest {

    // FOR DATA
    private val reporterreWebService = get<ReporterreWebService>()
    private val articleUrl = "Declaration-pour-la-vie-les-zapatistes-annoncent-leur-venue-en-Europe"
    private val cssUrl = "https://reporterre.net/plugins-dist/mediabox/colorbox/black-striped/colorbox.css"

    @Test
    fun fetchArticle() = runBlocking {
        val responseBody = reporterreWebService.getArticle(articleUrl)
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
        ReporterreArticle(responseBody).toArticle(article)

        assertTrue(article.sourceName.isNotBlank())
        assertTrue(article.title.isNotBlank())
        assertTrue(article.section.isNotBlank())
        assertTrue(article.theme.isNotBlank())
        assertTrue(article.author.isNotBlank())
        assertTrue(article.publishedDate != 0L)
        assertTrue(article.article.isNotBlank())
        assertTrue(article.description.isNotBlank())
        assertTrue(article.imageUrl.isNotBlank())
        assertTrue(article.cssUrl.isNotBlank())
    }

    @Test
    fun fetchCategories() = runBlocking {
        val responseBody = reporterreWebService.getCategory(REPORT_SEC_RESISTER, "0")
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
        val reporterreCategory = ReporterreCategory(responseBody)
        val reportCatSize = reporterreCategory.getArticleList().size
        assertTrue((reportCatSize) >= 50)
    }

    @Test
    fun fetchCss() = runBlocking {
        val cssStyle = reporterreWebService.getCss(cssUrl).string()

        assertThat("Something was downloaded !", cssStyle.isNotBlank())
    }
}