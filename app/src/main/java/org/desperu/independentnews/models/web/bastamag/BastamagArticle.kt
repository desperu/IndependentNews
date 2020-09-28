package org.desperu.independentnews.models.web.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlArticle
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
        val h1 = findData(H1, ITEMPROP, HEADLINE, null)
        return if (h1 != null && !h1.allElements.isNullOrEmpty())
                   h1.child(0).text()
               else null
    }

    // TODO mistake with rss categories and article subtitle or category??
//    override fun getSubtitle(): String? =
//        findData(HEADER, CLASS, CARTOUCHE)?.ownerDocument()?.select(P)?.get(0)?.ownText()

    override fun getSection(): String? =findData(P, CLASS, BASTA_THEME_CLASS, null)?.text()

    override fun getTheme(): String? = findData(HEADER, CLASS, CARTOUCHE, null)?.ownerDocument()?.select(P)?.get(0)?.ownText()

    override fun getAuthor() : String? {
        val author = findData(SPAN, ITEMPROP, AUTHOR, null)
        return if (author != null && !author.allElements.isNullOrEmpty())
                   author.child(0).text()
               else null
    }

    override fun getPublishedDate(): String? = findData(TIME, PUBDATE, PUBDATE, null)?.attr(DATETIME)

//    internal fun getArticle() = parseData("div", "itemprop", "articleBody")?.outerHtml()
    override fun getArticle(): String? = setMainCssId(correctImagesUrl(findData(DIV, CLASS, MAIN, null)?.outerHtml()))

    override fun getDescription(): String? {
        val description = findData(DIV, ITEMPROP, DESCRIPTION, null)
        return if (description != null && !description.allElements.isNullOrEmpty())
                   description.child(0)?.text()
               else
                   null
    }

    override fun getImage(): List<String?> {
        // if (it.attr("class") == "adapt-img spip_logo spip_logos intrinsic" && it.attr("itemprop") == "image") {
        val element = findData(IMG, ITEMPROP, IMAGE, null)
        return listOf(BASTAMAG_BASE_URL + element?.attr(SRC), element?.attr(WIDTH), element?.attr(HEIGHT))
    }

    override fun getCss(): String? = BASTAMAG_BASE_URL + findData(LINK, REL, STYLESHEET, null)?.attr(HREF)//findData("style", "type", "'text/css'")?.ownText()

    /**
     * Convert BastamagArticle to Article.
     * @param article the article to set data.
     * @return article with all data set.
     */
    internal fun toArticle(article: Article): Article {
        val author = getAuthor()
        val publishedDate = getPublishedDate()?.let { stringToDate(it)?.time }
        val description = getDescription()
        article.apply {
            source = this@BastamagArticle.source
            if (getUrl().isNotBlank()) url = getUrl()
            title = getTitle().toString()
            section = getSection().toString()
            theme = getTheme().toString()
            if (!author.isNullOrBlank()) this.author = author
            if (publishedDate != null) this.publishedDate = publishedDate
            this.article = getArticle().toString()
            if (!description.isNullOrBlank()) this.description = description
            imageUrl = getImage()[0].toString()
            css = getCss().toString()
        }

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

    /**
     * Set main css id to apply css style to the article body.
     * @param html the article body.
     * @return the article with main css id set.
     */
    private fun setMainCssId(html: String?): String? =
        if(!html.isNullOrBlank()) {
            val document = Jsoup.parse(html)
            document.select(BODY)[0].attr(CLASS, MAIN_CONTAINER)
            document.toString()
        } else
            null
}