package org.desperu.independentnews.network.multinationales

import kotlinx.coroutines.runBlocking
import org.desperu.independentnews.models.network.rss.RssResponse
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.get

/**
 * Class test, for Multinationales Rss Service Interface, check that fetching data properly works.
 */
class MultinationalesRssServiceTest : KoinTest {

    // FOR DATA
    private val multinationalesRssService = get<MultinationalesRssService>()

    @Test
    fun fetchRssArticles() = runBlocking {
        val rssResponse = multinationalesRssService.getRssArticles()
        val channel = rssResponse .channel
        assertThat("Something was downloaded !", channel != null)

        // Then use response to test parsing rss flux
        checkParsingRss(rssResponse)
    }

    /**
     * Check parsing rss response and child.
     *
     * @param rssResponse the rss response of the rss flux.
     */
    private fun checkParsingRss(rssResponse: RssResponse) {
        val rssArticleList = rssResponse.channel?.rssArticleList
        assertThat("The article list is not empty !", !rssArticleList.isNullOrEmpty())

        val rssArticle = rssArticleList?.get(0)
        assertTrue(!rssArticle?.title.isNullOrBlank())
        assertTrue(!rssArticle?.url.isNullOrBlank())
        assertTrue(!rssArticle?.permUrl.isNullOrBlank())
        assertTrue(!rssArticle?.publishedDate.isNullOrBlank())
//        assertTrue(!rssArticle?.author.isNullOrBlank()) // Not always set
        assertTrue(!rssArticle?.categoryList.isNullOrEmpty())
        assertTrue(!rssArticle?.description.isNullOrBlank())
    }
}