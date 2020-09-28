package org.desperu.independentnews.base.html

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtml

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

    internal abstract fun getSection(): String?

    internal abstract fun getTheme(): String?

    internal abstract fun getAuthor(): String?

    internal abstract fun getPublishedDate(): String?

    internal abstract fun getArticle(): String?

    internal abstract fun getDescription(): String?

    internal abstract fun getImage(): List<String?>

    internal abstract fun getCss(): String?
}