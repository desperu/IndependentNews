package org.desperu.independentnews.base

import okhttp3.ResponseBody

/**
 * Abstract base html article class witch provide standard functions to parse data from article's html page.
 *
 * @param htmlPage the html page retrieved from the retrofit request.
 */
abstract class BaseHtmlArticle(private val htmlPage: ResponseBody): BaseHtml(htmlPage) {

    // FOR DATA
    protected abstract val source: String

    // --- GETTERS ---

    internal abstract fun getTitle(): String?

    internal abstract fun getAuthor(): String?

    internal abstract fun getDate(): String?

    internal abstract fun getArticle(): String?

    internal abstract fun getImage(): List<String?>
}