package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.web.bastamag.BastamagArticle
import org.desperu.independentnews.models.web.bastamag.BastamagCategory
import org.desperu.independentnews.network.bastamag.BastamagRssService
import org.desperu.independentnews.network.bastamag.BastamagWebService
import org.desperu.independentnews.utils.BASTA_SEC_DECRYPTER
import org.desperu.independentnews.utils.BASTA_SEC_INVENTER
import org.desperu.independentnews.utils.BASTA_SEC_RESISTER
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl

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
}

/**
 * Implementation of the BastamagRepository interface.
 *
 * @author Desperu
 *
 * @property rssService                     the service to request the Bastamag Rss Service.
 * @property webService                     the service to request the Bastamag Web Site.
 * @property articleRepository              the repository access for article database.
 *
 * @constructor Instantiates a new BastamagRepositoryImpl.
 *
 * @param rssService                        the service to request the Bastamag Rss Service to set.
 * @param webService                        the service to request the Bastamag Web Site to set.
 * @param articleRepository                 the repository access for article database to set.
 */
class BastamagRepositoryImpl(
    private val rssService: BastamagRssService,
    private val webService: BastamagWebService,
    private val articleRepository: ArticleRepository
): BastamagRepository {

    /**
     * Returns the list of articles from the Rss flux of Bastamag.
     *
     * @return the list of articles from the Rss flux of Bastamag.
     */
    override suspend fun fetchRssArticles(): List<Article>? = withContext(Dispatchers.IO) {
        val rssArticleList = rssService.getRssArticles().channel?.rssArticleList

        if (!rssArticleList.isNullOrEmpty()) {
            val articleList = rssArticleList.map { it.toArticle() }
            articleRepository.updateTopStory(articleList)
            fetchArticleList(articleRepository.getNewArticles(articleList))
        }
        else
            null
    }

    /**
     * Returns the categories list of articles from the Web site of Bastamag.
     *
     * @return the categories list of articles from the Web site of Bastamag.
     */
    override suspend fun fetchCategories(): List<Article>? = withContext(Dispatchers.IO) {
        val categories = listOf(BASTA_SEC_DECRYPTER, BASTA_SEC_RESISTER, BASTA_SEC_INVENTER)
        val number = listOf(0, 10, 20, 30, 40)
        val articleList = mutableListOf<Article>()

        categories.forEach {category ->
            number.forEach {number ->
                val categoryList = webService.getCategory(category, number.toString())
                articleList.addAll(BastamagCategory(categoryList).getArticleList())
            }
        }

        fetchArticleList(articleRepository.getNewArticles(articleList))
    }

    /**
     * Fetch article html page for each article in the given list.
     *
     * @param articleList the list of article to fetch html page.
     *
     * @return the article list with all fetched data.
     */
    private suspend fun fetchArticleList(
        articleList: List<Article>
    ): List<Article> = withContext(Dispatchers.IO) {

        articleList.forEach {
            val bastamagArticle = BastamagArticle(webService.getArticle(getPageNameFromUrl(it.url)))
            bastamagArticle.toArticle(it)
        }

        articleList
    }
}