package org.desperu.independentnews.repositories.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.helpers.FetchHelper
import org.desperu.independentnews.helpers.FetchHelper.fetchAndPersistCssList
import org.desperu.independentnews.helpers.SnackBarHelper
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.SourcePage
import org.desperu.independentnews.models.network.html.multinationales.MultinationalesArticle
import org.desperu.independentnews.models.network.html.multinationales.MultinationalesCategory
import org.desperu.independentnews.models.network.html.multinationales.MultinationalesSourcePage
import org.desperu.independentnews.network.multinationales.MultinationalesRssService
import org.desperu.independentnews.network.multinationales.MultinationalesWebService
import org.desperu.independentnews.repositories.database.ArticleRepository
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl
import org.koin.java.KoinJavaComponent

/**
 * Repository interface to get Multinationales data from services.
 *
 * @author Desperu
 */
interface MultinationalesRepository {

    /**
     * Returns the list of articles from the Rss flux of Multinationales.
     *
     * @return the list of articles from the Rss flux of Multinationales.
     */
    suspend fun fetchRssArticles(): List<Article>?

    /**
     * Returns the categories list of articles from the Web site of Multinationales.
     *
     * @return the categories list of articles from the Web site of Multinationales.
     */
    suspend fun fetchCategories(): List<Article>?

    /**
     * Returns the source page list of Multinationales from it's Web site.
     *
     * @return the source page list of Multinationales from it's Web site.
     */
    suspend fun fetchSourcePages(): List<SourcePage>?
}

/**
 * Implementation of the MultinationalesRepository interface.
 *
 * @author Desperu
 *
 * @property rssService             the service to request the Multinationales Rss Service.
 * @property webService             the service to request the Multinationales Web Site.
 * @property articleRepository      the repository access for article database.
 *
 * @constructor Instantiates a new MultinationalesRepositoryImpl.
 *
 * @param rssService                the service to request the Multinationales Rss Service to set.
 * @param webService                the service to request the Multinationales Web Site to set.
 * @param articleRepository         the repository access for article database to set.
 */
class MultinationalesRepositoryImpl(
    private val rssService: MultinationalesRssService,
    private val webService: MultinationalesWebService,
    private val articleRepository: ArticleRepository
): MultinationalesRepository {

    // FOR DATA
    private val snackBarHelper: SnackBarHelper? get() = KoinJavaComponent.getKoin().getOrNull()

    /**
     * Returns the list of articles from the Rss flux of Multinationales.
     *
     * @return the list of articles from the Rss flux of Multinationales.
     */
    override suspend fun fetchRssArticles(): List<Article>? =
        FetchHelper.catchFetchArticle(MULTINATIONALES + RSS) {
            val rssArticleList = rssService.getRssArticles().channel?.rssArticleList

            if (!rssArticleList.isNullOrEmpty()) {
                val articleList = rssArticleList.map { it.toArticle(MULTINATIONALES) }
                articleRepository.updateTopStory(articleList)

                val newArticles = articleRepository.getNewArticles(articleList)
                snackBarHelper?.showMessage(
                    FIND,
                    listOf(MULTINATIONALES + RSS, newArticles.size.toString())
                )

                fetchArticleList(newArticles, RSS)
            } else
                null
        }

    /**
     * Returns the categories list of articles from the Web site of Multinationales.
     *
     * @return the categories list of articles from the Web site of Multinationales.
     */
    override suspend fun fetchCategories(): List<Article>? =
        FetchHelper.catchFetchArticle(MULTINATIONALES + CATEGORY) {
            val categories = listOf(MULTINATIONALES_SEC_ENQUETE)
            val numbers = mutableListOf<Int>()
            for (i in 0..29) { numbers.add(i * 5) }
            val articleList = mutableListOf<Article>()

            categories.forEach { category ->
                numbers.forEach { number ->
                    val responseBody = webService.getCategory(category, number.toString())
                    articleList.addAll(MultinationalesCategory(responseBody).getArticleList())
                }
            }

            val newArticles = articleRepository.getNewArticles(articleList)
            snackBarHelper?.showMessage(
                FIND,
                listOf(MULTINATIONALES + CATEGORY, newArticles.size.toString())
            )

            fetchArticleList(newArticles, CATEGORY)
        }

    /**
     * Returns the source pages list of Multinationales from it's Web site.
     *
     * @return the source page list of Multinationales from it's Web site.
     */
    override suspend fun fetchSourcePages(): List<SourcePage>? =
        FetchHelper.catchFetchSource(MULTINATIONALES) {
            val sourcePages = mutableListOf<SourcePage>()

            val responseBody = webService.getArticle(MULTINATIONALES_EDITO_URL)
            val multinationalesSourcePage = MultinationalesSourcePage(responseBody)
            sourcePages.add(multinationalesSourcePage.toSourceEditorial(MULTINATIONALES_EDITO_URL)) // Add the editorial page, the primary

            // Fetch the css style for the source page list.
            fetchAndPersistCssList(sourcePages) { cssUrl ->
                val style = webService.getCss(cssUrl).charStream().readText()
                style.replace(MULTI_ORIG_CSS_BODY, MULTI_NEW_CSS_BODY) // Needed to force left text align
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
            val multinationalesArticle =
                MultinationalesArticle(webService.getArticle(getPageNameFromUrl(article.url)))
            multinationalesArticle.toArticle(article)

            snackBarHelper?.showMessage(
                FETCH,
                listOf(MULTINATIONALES + type, (index + 1).toString(), articleList.size.toString())
            )
        }

        // Fetch the css style for the article list.
        fetchAndPersistCssList(articleList) { cssUrl ->
            webService.getCss(cssUrl).charStream().readText() }

        return@withContext articleList
    }
}