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
     * @param index the specific index to get into the founded list.
     * @return the found value, null if not found.
     */
    protected fun findData(tag: String, attr: String?, value: String?, index: Int?): Element? {
        val elements = document.select(tag)
        elements.withIndex().forEach {
            if (attr == null && value == null && index == null) // Get first element
                return it.value
            else if (it.value.attr(attr) == value && index == null) // Get first match
                return it.value
            else if (it.index == index) // Get specified index
                return it.value
        }
        return null
    }

    // --- GETTERS ---

    internal fun getUrl(): String = document.location()

    internal fun getHtmlPage(): String = htmlPage.string()
}