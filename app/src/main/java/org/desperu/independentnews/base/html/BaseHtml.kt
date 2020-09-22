package org.desperu.independentnews.base.html

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Abstract base html class witch provide standard functions to parse data from html page.
 *
 * @param htmlPage the html page retrieved from the retrofit request.
 */
abstract class BaseHtml(private val htmlPage: ResponseBody) {

    // FOR DATA
    protected lateinit var document: Document

    init {
        parseHtml()
    }

    /**
     * Parse the html page to Jsoup document.
     */
    private fun parseHtml() {
        document = Jsoup.parse(htmlPage.string())
    }

    // TODO getTag too
    // TODO get attribute ??
    /**
     * Find data from the Jsoup document.
     * @param tag the html tag to find.
     * @param attr the attribute to find.
     * @param value the value of the attribute to check.
     * @return the found value, null if not found.
     */
    protected fun findData(tag: String, attr: String?, value: String?): Element? {
        val elements = document.select(tag)
        elements.forEach {
            if (it.attr(attr) == value) // Get first match
                return it
            else if (attr == null && value == null) // Get first element
                return it
        }
        return null
    }

    // --- GETTERS ---

    internal fun getUrl(): String = document.location()

    internal fun getHtmlPage(): String = htmlPage.string()
}