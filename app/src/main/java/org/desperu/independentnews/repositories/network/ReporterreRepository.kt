package org.desperu.independentnews.repositories.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.helpers.FetchHelper.catchFetchArticle
import org.desperu.independentnews.helpers.FetchHelper.catchFetchSource
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
import org.desperu.independentnews.utils.Utils.deConcatenateStringToMutableList
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
}

/**
 * Implementation of the ReporterreRepository interface.
 *
 * @author Desperu
 *
 * @property rssService                     the service to request the Reporterre Rss Service.
 * @property webService                     the service to request the Reporterre Web Site.
 * @property articleRepository              the repository access for article database.
 *
 * @constructor Instantiates a new ReporterreRepositoryImpl.
 *
 * @param rssService                        the service to request the Reporterre Rss Service to set.
 * @param webService                        the service to request the Reporterre Web Site to set.
 * @param articleRepository                 the repository access for article database to set.
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
    override suspend fun fetchRssArticles(): List<Article>? = catchFetchArticle(REPORTERRE + RSS) {
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
    override suspend fun fetchCategories(): List<Article>? = catchFetchArticle(REPORTERRE + CATEGORY) {
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
    override suspend fun fetchSourcePages(): List<SourcePage>? = catchFetchSource(REPORTERRE) {
        val sourcePages = mutableListOf<SourcePage>()

        val responseBody = webService.getArticle(REPORTERRE_EDITO_URL)
        val reporterreSourcePage = ReporterreSourcePage(responseBody)
        sourcePages.add(reporterreSourcePage.toSourceEditorial(REPORTERRE_EDITO_URL)) // Add the editorial page, the primary

        reporterreSourcePage.getPageUrlList().forEachIndexed { index, pageUrl ->
            val buttonName = reporterreSourcePage.getButtonNameList()[index]

            val response = webService.getArticle(getPageNameFromUrl(pageUrl.mToString()))
            sourcePages.add(ReporterreSourcePage(response).toSourcePage(pageUrl, buttonName, index))
        }

        sourcePages
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Fetch article html page for each article in the given list.
     *
     * @param articleList the list of article to fetch html page.
     *
     * @return the article list with all fetched data.
     */
    private suspend fun fetchArticleList(
        articleList: List<Article>,
        type: String
    ): List<Article> = withContext(Dispatchers.IO) {

        articleList.forEachIndexed { index, article ->
            val reporterreArticle = ReporterreArticle(webService.getArticle(getPageNameFromUrl(article.url)))
            reporterreArticle.toArticle(article)

            // Fetch the css style too.
            article.cssStyle = fetchArticleCss(article)

            snackBarHelper?.showMessage(
                FETCH,
                listOf(REPORTERRE + type, (index + 1).toString(), articleList.size.toString())
            )
        }

        return@withContext articleList
    }

    /**
     * Fetch article css style, for each css url, and concatenate fetched css style.
     *
     * @param article the article for which fetch css.
     *
     * @return the concatenated css styles in string.
     */
    private suspend fun fetchArticleCss(article: Article): String {
        val cssUrls = deConcatenateStringToMutableList(article.cssUrl)
        var cssStyle = String()

        cssUrls.forEachIndexed { index, cssUrl ->
            if (index != 0) cssStyle += " "
            cssStyle += webService.getCss(cssUrl).string()
        }

        return cssStyle
    }
}