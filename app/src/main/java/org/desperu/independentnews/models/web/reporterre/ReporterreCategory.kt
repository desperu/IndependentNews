package org.desperu.independentnews.models.web.reporterre

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlCategory
import org.desperu.independentnews.extension.getChild
import org.desperu.independentnews.extension.getMatchAttr
import org.desperu.independentnews.utils.*

/**
 * Class which provides a model to parse reporterre category html page.
 *
 * @property htmlPage the reporterre article html page.
 * @property category the reporterre category.
 *
 * @constructor Instantiate a new ReporterreArticle.
 *
 * @param htmlPage the reporterre article html page to set.
 * @param category the reporterre category.
 */
data class ReporterreCategory(private val htmlPage: ResponseBody,
                              override val category: String // TODO useless?
): BaseHtmlCategory(htmlPage) {

    override fun getUrlArticleList(): List<String>? {
        val articleUrlList = mutableListOf<String>()

        findData(A, CLASS, BLOC_ARTICLE, null)?.attr(HREF)?.let { articleUrlList.add(it) }

        getTagList(A).getMatchAttr(CLASS, LIEN_ARTICLE).forEach {
            articleUrlList.add(it.attr(HREF))
            // for date
            //it.allElements.select(SPAN).getMatchAttr(CLASS, "petit_vert").ownText()
        }

        return articleUrlList
    }

    /**
     * Return next 10 articles url for this category.
     * @return next 10 articles url for this category.
     */
    override fun getNext10ArticlesUrl(): String? = findData(a, CLASS, LIEN_PAGINATION, null)?.attr(HREF)
}