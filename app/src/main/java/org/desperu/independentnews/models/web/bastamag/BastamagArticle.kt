package org.desperu.independentnews.models.web.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.BaseHtmlArticle
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.stringToDate
import org.jsoup.Jsoup

/**
 *
 */
data class BastamagArticle(private val htmlPage: ResponseBody): BaseHtmlArticle(htmlPage) {
// TODO to clean, comment and model or utils ??? set property when init class as in rss/category???

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

    override fun getSubtitle(): String? = findData(P, CLASS, SUBTITLE)?.ownText()

    override fun getAuthor() : String? {
        val author = findData(SPAN, ITEMPROP, AUTHOR)
        return if (author != null && !author.allElements.isNullOrEmpty())
                   author.child(0).ownText()
               else null
    }

    override fun getPublishedDate(): String? = findData(TIME, PUBDATE, PUBDATE)?.attr(DATETIME)

//    internal fun getArticle() = parseData("div", "itemprop", "articleBody")?.outerHtml()
    override fun getArticle(): String? = setMainCssId(correctImagesUrl(findData(DIV, CLASS, MAIN)?.outerHtml()))

    override fun getDescription(): String? = findData(DIV, ITEMPROP, DESCRIPTION)?.child(0)?.ownText()//text()

    override fun getImage(): List<String?> {
        // if (it.attr("class") == "adapt-img spip_logo spip_logos intrinsic" && it.attr("itemprop") == "image") {
        val element = findData(IMG, ITEMPROP, IMAGE)
        return listOf(BASTAMAG_BASE_URL + element?.attr(SRC), element?.attr(WIDTH), element?.attr(HEIGHT))
    }

    override fun getCss(): String? = BASTAMAG_BASE_URL + findData(LINK, REL, STYLESHEET)?.attr(HREF)//findData("style", "type", "'text/css'")?.ownText()

    /**
     * Convert BastamagArticle to Article.
     */
    internal fun toArticle(): Article {
        val article = Article(
            source = BASTAMAG,
            url = getUrl(),
            title = getTitle().toString(),
            subtitle = getSubtitle().toString(),
            author = getAuthor().toString(),
            article = getArticle().toString(),
            description = getDescription().toString(),
            imageUrl = getImage()[0].toString()
        )

//        if (categoryList != null)
//            Utils.concatenateStringFromMutableList(categoryList.mapNotNull { it.category }
//                .toMutableList())

        getPublishedDate()?.let { publishedDate -> stringToDate(publishedDate)?.time?.let { article.publishedDate = it } }

        return article
    }

    // TODO to put in utils?? or html utils with parse and find data.
    /**
     * Correct all images url's with their full url's in the given html code.
     * @param html the html code to correct.
     * @return the html code with corrected images url's.
     */
    private fun correctImagesUrl(html: String?): String? =
        if (!html.isNullOrBlank()) {
            val document = Jsoup.parse(html)
            document.select(IMG).forEach { it.attr(SRC, BASTAMAG_BASE_URL + it.attr(SRC)) }
            document.toString()
        } else
            null

    private fun setMainCssId(html: String?): String? =
        if(!html.isNullOrBlank()) {
            val document = Jsoup.parse(html)
            document.select(BODY)[0].attr(CLASS, MAIN_CONTAINER)
            document.toString()
        } else
            null
}