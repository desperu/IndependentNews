package org.desperu.independentnews.repositories.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.helpers.FetchHelper.catchFetch
import org.desperu.independentnews.helpers.FetchHelper.fetchAndPersistCssList
import org.desperu.independentnews.helpers.FetchHelper.fetchWithMessage
import org.desperu.independentnews.helpers.SnackBarHelper
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.SourcePage
import org.desperu.independentnews.models.network.html.reporterre.ReporterreArticle
import org.desperu.independentnews.models.network.html.reporterre.ReporterreCategory
import org.desperu.independentnews.models.network.html.reporterre.ReporterreSourcePage
import org.desperu.independentnews.network.reporterre.ReporterreRssService
import org.desperu.independentnews.network.reporterre.ReporterreWebService
import org.desperu.independentnews.repositories.database.ArticleRepository
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl
import org.koin.java.KoinJavaComponent.getKoin

/**
 * Repository interface to get data from Reporterre services.
 *
 * @author Desperu
 */
interface ReporterreRepository {

    /**
     * Returns the list of articles from the Rss flux of Reporterre.
     *
     * @return the list of articles from the Rss flux of Reporterre.
     */
    suspend fun fetchRssArticles(): List<Article>?

    /**
     * Returns the categories list of articles from the Web site of Reporterre.
     *
     * @return the categories list of articles from the Web site flux of Reporterre.
     */
    suspend fun fetchCategories(): List<Article>?

    /**
     * Returns the source page list of Reporterre from it's Web site.
     *
     * @return the source page list of Reporterre from it's Web site.
     */
    suspend fun fetchSourcePages(): List<SourcePage>?

    /**
     * Convenience function to fetch only one article.
     *
     * @param article the article to fetch all data.
     *
     * @return the fetched article with all data.
     */
    suspend fun fetchArticle(article: Article): List<Article>?
}

/**
 * Implementation of the ReporterreRepository interface.
 *
 * @author Desperu
 *
 * @property rssService             the service to request the Reporterre Rss Service.
 * @property webService             the service to request the Reporterre Web Site.
 * @property articleRepository      the repository access for article database.
 *
 * @constructor Instantiates a new ReporterreRepositoryImpl.
 *
 * @param rssService                the service to request the Reporterre Rss Service to set.
 * @param webService                the service to request the Reporterre Web Site to set.
 * @param articleRepository         the repository access for article database to set.
 */
class ReporterreRepositoryImpl(
    private val rssService: ReporterreRssService,
    private val webService: ReporterreWebService,
    private val articleRepository: ArticleRepository
): ReporterreRepository {

    // FOR DATA
    private val snackBarHelper: SnackBarHelper? get() = getKoin().getOrNull()

    /**
     * Returns the list of articles from the Rss flux of Reporterre.
     *
     * @return the list of articles from the Rss flux of Reporterre.
     */
    override suspend fun fetchRssArticles(): List<Article>? = fetchWithMessage(REPORTERRE + RSS, FETCH) {
        val rssArticleList = rssService.getRssArticles().channel?.rssArticleList

        if (!rssArticleList.isNullOrEmpty()) {
            val articleList = rssArticleList.map { it.toArticle(REPORTERRE) }
            articleRepository.updateTopStory(articleList)

            val newArticles = articleRepository.getNewArticles(articleList)
            snackBarHelper?.showMessage(FIND, listOf(REPORTERRE + RSS, newArticles.size.toString()))

            // Limit size for first start, but need to automatic inject in ui when all list is fetched

            fetchArticleList(newArticles, RSS)
        } else
            null
    }

    /**
     * Returns the categories list of articles from the Web site of Reporterre.
     *
     * @return the categories list of articles from the Web site of Reporterre.
     */
    override suspend fun fetchCategories(): List<Article>? = fetchWithMessage(REPORTERRE + CATEGORY, FETCH) {
        val categories = listOf(REPORT_SEC_DECRYPTER, REPORT_SEC_RESISTER, REPORT_SEC_INVENTER)
        val articleList = mutableListOf<Article>()

        categories.forEach { category ->
            val responseBody = webService.getCategory(category, 0.toString())
            articleList.addAll(ReporterreCategory(responseBody).getArticleList())
        }

        val newArticles = articleRepository.getNewArticles(articleList)
        snackBarHelper?.showMessage(FIND, listOf(REPORTERRE + CATEGORY, newArticles.size.toString()))

        fetchArticleList(newArticles, CATEGORY)
    }

    /**
     * Returns the source page list of Reporterre from it's Web site.
     *
     * @return the source page list of Reporterre from it's Web site.
     */
    override suspend fun fetchSourcePages(): List<SourcePage>? = fetchWithMessage(REPORTERRE, SOURCE_FETCH) {
        val sourcePages = mutableListOf<SourcePage>()

        val responseBody = webService.getArticle(REPORTERRE_EDITO_URL)
        val reporterreSourcePage = ReporterreSourcePage(responseBody)
        sourcePages.add(reporterreSourcePage.toSourceEditorial(REPORTERRE_EDITO_URL)) // Add the editorial page, the primary

        reporterreSourcePage.getPageUrlList().forEachIndexed { index, pageUrl ->
            val buttonName = reporterreSourcePage.getButtonNameList()[index]

            val response = webService.getArticle(getPageNameFromUrl(pageUrl.mToString()))
            sourcePages.add(ReporterreSourcePage(response).toSourcePage(pageUrl, buttonName, index))
        }

        // Fetch the css style for the source page list.
        fetchAndPersistCssList(sourcePages) { cssUrl -> webService.getCss(cssUrl).charStream().readText() }

        sourcePages
    }

    /**
     * Convenience function to fetch only one article.
     *
     * @param article the article to fetch all data.
     *
     * @return the fetched article with all data.
     */
    override suspend fun fetchArticle(article: Article): List<Article>? = catchFetch {
        fetchArticleList(listOf(article), null)
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Fetch article html page for each article in the given list.
     *
     * @param articleList   the list of article to fetch html page.
     * @param type          the article type to fetch.
     *
     * @return the article list with all fetched data.
     */
    private suspend fun fetchArticleList(
        articleList: List<Article>,
        type: String?
    ): List<Article> = withContext(Dispatchers.IO) {

        articleList.forEachIndexed { index, article ->
            val reporterreArticle = ReporterreArticle(webService.getArticle(getPageNameFromUrl(article.url)))
            reporterreArticle.toArticle(article)

            if (!type.isNullOrBlank())
                snackBarHelper?.showMessage(
                    FETCH,
                    listOf(REPORTERRE + type, (index + 1).toString(), articleList.size.toString())
                )
        }

        // Fetch and persist the css style for the article list.
        fetchAndPersistCssList(articleList) { cssUrl -> webService.getCss(cssUrl).charStream().readText() }

        return@withContext articleList
    }
}