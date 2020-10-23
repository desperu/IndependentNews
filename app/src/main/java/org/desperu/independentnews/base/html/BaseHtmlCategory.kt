package org.desperu.independentnews.base.html

import okhttp3.ResponseBody
import org.desperu.independentnews.models.Article

/**
 * Abstract base html category class witch provide standard functions to parse data from category's html page.
 *
 * @param htmlPage the html page retrieved from the retrofit request.
 */
abstract class BaseHtmlCategory(private val htmlPage: ResponseBody): BaseHtml(htmlPage) {

    // --- GETTERS ---

    internal abstract fun getArticleList(): List<Article>
}