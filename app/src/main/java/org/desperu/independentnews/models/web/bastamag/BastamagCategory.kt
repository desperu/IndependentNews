package org.desperu.independentnews.models.web.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlCategory
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.concatenateStringFromMutableList
import org.jsoup.nodes.Element

// TODO to comment
data class BastamagCategory(private val htmlPage: ResponseBody,
                            override val category: String
): BaseHtmlCategory(htmlPage) {

    override fun getUrlArticleList(): List<String>? {
        //        val articleList = findData(ARTICLE, CLASS, ARTICLE_ENTRY)

        val articleUrlList = mutableListOf<String>()
        val articleList = mutableListOf<Element>()
        val elements = document.select(ARTICLE)
        elements.forEach { article ->
            if (article.attr(CLASS) == ARTICLE_ENTRY) {
                article.select(H3).forEach {
                    if (it.attr(CLASS) == ENTRY_TITLE)
                        articleUrlList.add(it.select(A).attr(HREF))
                }
            }
        }

        val tedt = articleUrlList.toString()
        val test = tedt.toList()
        val toertoer = concatenateStringFromMutableList(articleUrlList)
        println(">>>>>>>>>> $tedt")
        println(">>>>>>>>>> concatenate : $toertoer")
        return articleUrlList
    }

    /**
     * Return next 10 articles url for this category.
     * @return next 10 articles url for this category.
     */
    override fun getNext10ArticlesUrl(): String? = findData(A, CLASS, LIEN_PAGINATION)?.attr(HREF) // TODO get more than 20 articles ...
//    override fun getNextArticlesUrl(page: String): String? = "Approfondir?debut_articles=$page#pagination_articles"
}