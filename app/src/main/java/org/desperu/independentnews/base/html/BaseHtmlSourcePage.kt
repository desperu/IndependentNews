package org.desperu.independentnews.base.html

import okhttp3.ResponseBody

/**
 * Abstract base html source page class witch provide standard functions to parse data
 * from the source html page.
 *
 * @param htmlPage the html page retrieved from the retrofit request.
 */
abstract class BaseHtmlSourcePage(private val htmlPage: ResponseBody): BaseHtml(htmlPage) {

    // FOR DATA
    protected abstract val sourceName: String

    // --- GETTERS ---

    internal abstract fun getTitle(): String?

    internal abstract fun getBody(): String?

    internal abstract fun getCssUrl(): String?

    internal abstract fun getPageUrlList(): List<String?>

    internal abstract fun getButtonNameList(): List<String?>
}