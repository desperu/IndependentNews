package org.desperu.independentnews.models.web.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlCategory
import org.desperu.independentnews.utils.*
/**
 * Class which provides a model to parse bastamag category html page.
 *
 * @property htmlPage the bastamag article html page.
 * @property category the bastamag category.
 *
 * @constructor Instantiate a new BastamagArticle.
 *
 * @param htmlPage the bastamag article html page to set.
 * @param category the bastamag category.
 */
data class BastamagCategory(private val htmlPage: ResponseBody,
                            override val category: String // TODO useless?
): BaseHtmlCategory(htmlPage) {

    override fun getUrlArticleList(): List<String>? {
        //        val articleList = findData(ARTICLE, CLASS, ARTICLE_ENTRY)

        val articleUrlList = mutableListOf<String>()
//        val articleList = mutableListOf<Element>()
        val elements = document.select(ARTICLE)
        elements.forEach { article ->
            if (article.attr(CLASS) == ARTICLE_ENTRY) {
                article.select(H3).forEach {
                    if (it.attr(CLASS) == ENTRY_TITLE)
                        articleUrlList.add(it.select(a).attr(HREF))
                }
            }
        }

//        val tedt = articleUrlList.toString()
//        val test = tedt.toList()
//        val toertoer = concatenateStringFromMutableList(articleUrlList)
//        println(">>>>>>>>>> $tedt")
//        println(">>>>>>>>>> concatenate : $toertoer")
        return articleUrlList
    }

    /**
     * Return next 10 articles url for this category.
     * @return next 10 articles url for this category.
     */
    override fun getNext10ArticlesUrl(): String? = findData(a, CLASS, LIEN_PAGINATION, null)?.attr(HREF) // TODO get more than 20 articles ...
//    override fun getNextArticlesUrl(page: String): String? = "Approfondir?debut_articles=$page#pagination_articles"
}