package org.desperu.independentnews.models.web.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlArticle
import org.desperu.independentnews.extension.parseHtml.getChild
import org.desperu.independentnews.extension.parseHtml.getIndex
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.stringToDate
import org.jsoup.Jsoup

/**
 * Class which provides a model to parse bastamag article html page.
 *
 * @property htmlPage the bastamag article html page.
 *
 * @constructor Instantiate a new BastamagArticle.
 *
 * @param htmlPage the bastamag article html page to set.
 */
data class BastamagArticle(private val htmlPage: ResponseBody): BaseHtmlArticle(htmlPage) {

    // FOR DATA
    override val sourceName = BASTAMAG

    // --- GETTERS ---

    override fun getTitle(): String? =
        findData(H1, ITEMPROP, HEADLINE, null)?.getChild(0)?.text()

    override fun getSection(): String? =
        findData(SPAN, CLASS, DIVIDER, 1)?.parent()?.getChild(0)?.ownText()

    override fun getTheme(): String? =
        findData(HEADER, CLASS, CARTOUCHE, null)
            ?.ownerDocument()?.select(P).getIndex(0)?.ownText()

    override fun getAuthor() : String? =
        findData(SPAN, ITEMPROP, AUTHOR, null)?.getChild(0)?.text()

    override fun getPublishedDate(): String? =
        findData(TIME, PUBDATE, PUBDATE, null)?.attr(DATETIME)

    override fun getArticle(): String? =
        setMainCssId(
            correctImagesUrl(
                correctUrlLink(
                    findData(DIV, CLASS, MAIN, null)?.outerHtml()
                )
            )
        )

    override fun getDescription(): String? =
        findData(DIV, ITEMPROP, DESCRIPTION, null)?.getChild(0)?.text()

    override fun getImage(): List<String?> {
        val element = findData(IMG, ITEMPROP, IMAGE, null)
        return listOf(
            BASTAMAG_BASE_URL + element?.attr(SRC),
            element?.attr(WIDTH),
            element?.attr(HEIGHT)
        )
    }

    override fun getCssUrl(): String? =
        BASTAMAG_BASE_URL + findData(LINK, REL, STYLESHEET, null)?.attr(HREF)

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
            sourceName = this@BastamagArticle.sourceName
            if (getUrl().isNotBlank()) url = getUrl()
            title = getTitle().mToString()
            section = getSection().mToString()
            theme = getTheme().mToString()
            if (!author.isNullOrBlank()) this.author = author
            if (publishedDate != null) this.publishedDate = publishedDate
            this.article = getArticle().mToString()
            if (!description.isNullOrBlank()) this.description = description
            imageUrl = getImage()[0].mToString()
            cssUrl = getCssUrl().mToString()
        }

        return article
    }

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