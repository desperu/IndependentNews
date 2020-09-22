package org.desperu.independentnews.base.html

import okhttp3.ResponseBody

/**
 * Abstract base html category class witch provide standard functions to parse data from category's html page.
 *
 * @param htmlPage the html page retrieved from the retrofit request.
 */
abstract class BaseHtmlCategory(private val htmlPage: ResponseBody): BaseHtml(htmlPage) {

    // FOR DATA
    protected abstract val category: String

    // --- GETTERS ---

    internal abstract fun getUrlArticleList(): List<String>?

    internal abstract fun getNext10ArticlesUrl(): String?
}