package org.desperu.independentnews.models.web.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.BaseHtmlArticle
import org.desperu.independentnews.utils.*

/**
 *
 */
data class BastamagArticle(private val htmlPage: ResponseBody): BaseHtmlArticle(htmlPage) {
// TODO to clean, comment and model or utils ???

    // FOR DATA
    override val source = BASTAMAG

    // --- GETTERS ---

    override fun getTitle(): String? {
        // val elementTitle = document.select("title")
        // title = elementTitle[0].ownText()
        // if (!title.isNullOrBlank()) println("Title : $title")
        val h1 = findData(H1, ITEMPROP, HEADLINE)
        return if (h1 != null && !h1.allElements.isNullOrEmpty())
                   h1.child(0).ownText()
               else null
    }

    override fun getAuthor() : String? {
        val author = findData(SPAN, ITEMPROP, AUTHOR)
        return if (author != null && !author.allElements.isNullOrEmpty())
                   author.child(0).ownText()
               else null
    }

    override fun getDate(): String? = findData(TIME, PUBDATE, PUBDATE)?.attr(DATETIME)

//    internal fun getArticle() = parseData("div", "itemprop", "articleBody")?.outerHtml()
    override fun getArticle(): String? = findData(DIV, CLASS, MAIN)?.outerHtml()


    override fun getImage(): List<String?> {
        // if (it.attr("class") == "adapt-img spip_logo spip_logos intrinsic" && it.attr("itemprop") == "image") {
        val element = findData(IMG, ITEMPROP, IMAGE)
        return listOf(element?.attr(SRC), element?.attr(WIDTH), element?.attr(HEIGHT))
    }
}