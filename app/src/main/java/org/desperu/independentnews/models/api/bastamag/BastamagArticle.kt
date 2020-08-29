package org.desperu.independentnews.models.api.bastamag

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

data class BastamagArticle(private val htmlPage: ResponseBody,
                           private val source: Int
) {

    // FOR DATA
    private lateinit var document: Document

    init {
        parseHtml()
    }

    private fun parseHtml() {
        document = Jsoup.parse(htmlPage.string())
    }

    // TODO getTag too
    // TODO get attribute ??
    private fun parseData(tag: String, attr: String?, value: String?): Element? {
        val elements = document.select(tag)
        elements.forEach {
            if (it.attr(attr) == value)
                return it
            else if (attr == null && value == null)
                return it
        }
        return null
    }

    // --- GETTERS ---

    internal fun getHtmlPage(): String? = htmlPage.string()

    internal fun getTitle(): String? {
        // val elementTitle = document.select("title")
        // title = elementTitle[0].ownText()
        // if (!title.isNullOrBlank()) println("Title : $title")
        val h1 = parseData("h1", "itemprop", "headline")
        return if (h1 != null && !h1.allElements.isNullOrEmpty())
                   h1.child(0).ownText()
               else null
    }

    internal fun getAuthor() : String? {
        val author = parseData("span", "itemprop", "author")
        return if (author != null && !author.allElements.isNullOrEmpty())
                   author.child(0).ownText()
               else null
    }

    internal fun getDate() = parseData("time", "pubdate", "pubdate")?.attr("datetime")

//    internal fun getArticle() = parseData("div", "itemprop", "articleBody")?.outerHtml()
    internal fun getArticle() = parseData("div", "class", "main")?.outerHtml()


    internal fun getImage(): List<String?> {
        // if (it.attr("class") == "adapt-img spip_logo spip_logos intrinsic" && it.attr("itemprop") == "image") {
        val element = parseData("img", "itemprop", "image")
        return listOf(element?.attr("src"), element?.attr("width"), element?.attr("height"))
    }
}