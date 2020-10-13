package org.desperu.independentnews.base.html

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

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


    /**
     * Find data from the Jsoup document.
     *
     * @param tag the html tag to find.
     * @param attr the attribute to find.
     * @param value the value of the attribute to check.
     * @param index the specific index to get into the founded list.
     *
     * @return the found value, null if not found.
     */
    protected fun findData(tag: String, attr: String?, value: String?, index: Int?): Element? {
        var matches = 0
        val elements = document.select(tag)
        elements.withIndex().forEach {

            if (attr == null && value == null) {
                if (index == null) return it.value // Get first element
                else if (it.index == index) return it.value // Get specified index
            }

            else if (it.value.attr(attr) == value) {
                when (index) {
                    null -> return it.value // Get first match
                    matches -> return it.value // Get indexed match
                    else -> matches += 1
                }
            }
        }

        return null
    }

    // TODO getTag too
    // TODO get attribute ??
    protected fun getTagList(tag: String): Elements? = document.select(tag)

    protected fun getAttribute(elements: Elements, attr: String, value: String): Element? {
        elements.forEach { element ->
            if (element.attr(attr) == value)
                return element
        }
        return null
    }

    protected fun getMatchIndex(elements: Elements, attr: String, value: String, index: Int): Element? {
        var matches = 0
        elements.forEach {
            if (it.attr(attr) == value) {
                if (index == matches) return it // Get indexed match
                else matches += 1
            }
        }
        return null
    }

    // --- GETTERS ---

    internal fun getUrl(): String = document.location()

    internal fun getHtmlPage(): String = htmlPage.string()
}