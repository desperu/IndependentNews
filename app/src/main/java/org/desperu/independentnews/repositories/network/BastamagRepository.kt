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
import org.desperu.independentnews.models.network.html.bastamag.BastamagArticle
import org.desperu.independentnews.models.network.html.bastamag.BastamagCategory
import org.desperu.independentnews.models.network.html.bastamag.BastamagSourcePage
import org.desperu.independentnews.network.bastamag.BastamagRssService
import org.desperu.independentnews.network.bastamag.BastamagWebService
import org.desperu.independentnews.repositories.database.ArticleRepository
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl
import org.koin.java.KoinJavaComponent.getKoin

/**
 * Repository interface to get Bastamag data from services.
 *
 * @author Desperu
 */
interface BastamagRepository {

    /**
     * Returns the list of articles from the Rss flux of Bastamag.
     *
     * @return the list of articles from the Rss flux of Bastamag.
     */
    suspend fun fetchRssArticles(): List<Article>?

    /**
     * Returns the categories list of articles from the Web site of Bastamag.
     *
     * @return the categories list of articles from the Web site of Bastamag.
     */
    suspend fun fetchCategories(): List<Article>?

    /**
     * Returns the source page list of Basta ! from it's Web site.
     *
     * @return the source page list of Basta ! from it's Web site.
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
 * Implementation of the BastamagRepository interface.
 *
 * @author Desperu
 *
 * @property rssService             the service to request the Bastamag Rss Service.
 * @property webService             the service to request the Bastamag Web Site.
 * @property articleRepository      the repository access for article database.
 *
 * @constructor Instantiates a new BastamagRepositoryImpl.
 *
 * @param rssService                the service to request the Bastamag Rss Service to set.
 * @param webService                the service to request the Bastamag Web Site to set.
 * @param articleRepository         the repository access for article database to set.
 */
class BastamagRepositoryImpl(
    private val rssService: BastamagRssService,
    private val webService: BastamagWebService,
    private val articleRepository: ArticleRepository
): BastamagRepository {

    // FOR DATA
    private val snackBarHelper: SnackBarHelper? get() = getKoin().getOrNull()

    /**
     * Returns the list of articles from the Rss flux of Bastamag.
     *
     * @return the list of articles from the Rss flux of Bastamag.
     */
    override suspend fun fetchRssArticles(): List<Article>? = fetchWithMessage(BASTAMAG + RSS, FETCH) {
        val rssArticleList = rssService.getRssArticles().channel?.rssArticleList

        if (!rssArticleList.isNullOrEmpty()) {
            val articleList = rssArticleList.map { it.toArticle(BASTAMAG) }
            articleRepository.updateTopStory(articleList)

            val newArticles = articleRepository.getNewArticles(articleList)
            snackBarHelper?.showMessage(FIND, listOf(BASTAMAG + RSS, newArticles.size.toString()))

            fetchArticleList(newArticles, RSS)
        } else
            null
    }

    /**
     * Returns the categories list of articles from the Web site of Bastamag.
     *
     * @return the categories list of articles from the Web site of Bastamag.
     */
    override suspend fun fetchCategories(): List<Article>? = fetchWithMessage(BASTAMAG + CATEGORY, FETCH) {
        val categories = listOf(BASTA_SEC_DECRYPTER, BASTA_SEC_RESISTER, BASTA_SEC_INVENTER)
        val numbers = listOf(0, 10, 20, 30, 40)
        val articleList = mutableListOf<Article>()

        categories.forEach { category ->
            numbers.forEach { number ->
                val responseBody = webService.getCategory(category, number.toString())
                articleList.addAll(BastamagCategory(responseBody).getArticleList())
            }
        }

        val newArticles = articleRepository.getNewArticles(articleList)
        snackBarHelper?.showMessage(FIND, listOf(BASTAMAG + CATEGORY, newArticles.size.toString()))

        fetchArticleList(newArticles, CATEGORY)
    }

    /**
     * Returns the source pages list of Basta ! from it's Web site.
     *
     * @return the source page list of Basta ! from it's Web site.
     */
    override suspend fun fetchSourcePages(): List<SourcePage>? = fetchWithMessage(BASTAMAG, SOURCE_FETCH) {
        val sourcePages = mutableListOf<SourcePage>()

        val responseBody = webService.getArticle(BASTAMAG_EDITO_URL)
        val bastamagSourcePage = BastamagSourcePage(responseBody)
        sourcePages.add(bastamagSourcePage.toSourceEditorial(BASTAMAG_EDITO_URL)) // Add the editorial page, the primary

        bastamagSourcePage.getPageUrlList().forEachIndexed { index, pageUrl ->
            val buttonName = bastamagSourcePage.getButtonNameList()[index]

            val response = webService.getArticle(getPageNameFromUrl(pageUrl.mToString()))
            sourcePages.add(BastamagSourcePage(response).toSourcePage(pageUrl, buttonName, index))
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
            val bastamagArticle = BastamagArticle(webService.getArticle(getPageNameFromUrl(article.url)))
            bastamagArticle.toArticle(article)

            if (!type.isNullOrBlank())
                snackBarHelper?.showMessage(
                    FETCH,
                    listOf(BASTAMAG + type, (index + 1).toString(), articleList.size.toString())
                )
        }

        // Fetch the css style for the article list.
        fetchAndPersistCssList(articleList) { cssUrl -> webService.getCss(cssUrl).charStream().readText() }

        return@withContext articleList
    }
}