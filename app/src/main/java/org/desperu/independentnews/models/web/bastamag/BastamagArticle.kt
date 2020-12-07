package org.desperu.independentnews.models.web.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlArticle
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.sources.correctBastaMediaUrl
import org.desperu.independentnews.extension.parseHtml.sources.setMainCssId
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.stringToDate
import org.jsoup.nodes.Document

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
        findData(H1, ITEMPROP, HEADLINE, null).getChild(0)?.text()

    override fun getSection(): String? =
        findData(SPAN, CLASS, DIVIDER, 1)?.parent().getChild(0)?.ownText()

    override fun getTheme(): String? =
        findData(P, CLASS, SURTITRE, null)?.ownText()

    override fun getAuthor() : String? =
        findData(SPAN, ITEMPROP, AUTHOR, null).getChild(0)?.text()

    override fun getPublishedDate(): String? =
        findData(TIME, PUBDATE, PUBDATE, null)?.attr(DATETIME)

    override fun getArticle(): String? =
        findData(DIV, CLASS, MAIN, null)?.outerHtml().updateArticleBody()

    override fun getDescription(): String? =
        findData(DIV, ITEMPROP, DESCRIPTION, null).getChild(0)?.text()

    override fun getImage(): List<String?> {
        val element = findData(IMG, ITEMPROP, IMAGE, null)
        return listOf(
            element?.attr(SRC).toFullUrl(BASTAMAG_BASE_URL),
            element?.attr(WIDTH),
            element?.attr(HEIGHT)
        )
    }

    override fun getCssUrl(): String =
        findData(LINK, REL, STYLE_SHEET, null)?.attr(HREF).toFullUrl(BASTAMAG_BASE_URL)

    // -----------------
    // CONVERT
    // -----------------

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
            title = getTitle().mToString()
            section = getSection().mToString()
            theme = getTheme().mToString()
            if (!author.isNullOrBlank()) this.author = author
            if (publishedDate != null) this.publishedDate = publishedDate
            this.article = getArticle().mToString()
            if (!description.isNullOrBlank()) this.description = description
            imageUrl = getImage()[0].mToString()
            cssUrl = getCssUrl()
        }

        return article
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Update article body to add, correct and remove needed data.
     *
     * @return the article body updated.
     */
    private fun String?.updateArticleBody(): String? =
        this?.let {
            it.toDocument()
                .addNotes()
                .correctUrlLink(BASTAMAG_BASE_URL)
                .correctBastaMediaUrl()
                .setMainCssId()
                .mToString()
                .forceHttps()
                .escapeHashtag()
        }

    /**
     * Add notes at the end of the article body?
     *
     * @return the article body with notes at the end.
     */
    private fun Document?.addNotes(): Document? =
        this?.let {
            val notes = findData(DIV, CLASS, NOTES, null)?.outerHtml()

            notes?.let { select(BODY).append(it) }
            this
        }
}