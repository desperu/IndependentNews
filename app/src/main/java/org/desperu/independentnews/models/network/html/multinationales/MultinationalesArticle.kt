package org.desperu.independentnews.models.network.html.multinationales

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlArticle
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.parseHtml.sources.correctBastaMediaUrl
import org.desperu.independentnews.extension.parseHtml.toDocument
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.stringToDate
import org.jsoup.nodes.Document

/**
 * Class which provides a model to parse multinationales article html page.
 *
 * @property htmlPage the multinationales article html page.
 *
 * @constructor Instantiate a new MultinationalesArticle.
 *
 * @param htmlPage the multinationales article html page to set.
 */
data class MultinationalesArticle(private val htmlPage: ResponseBody): BaseHtmlArticle(htmlPage) {

    // FOR DATA
    override val sourceName = MULTINATIONALES

    // --- GETTERS ---

    override fun getTitle(): String? =
        getTagList(H1).getContainsAttr(CLASS, H1_MULTI)?.text()

    override fun getSection(): String? = null // No data in the web page ...

    override fun getTheme(): String? =
        getTagList(P).getContainsAttr(CLASS, SURTITRE)?.ownText()

    override fun getAuthor(): String? =
        findData(SPAN, CLASS, AUTHOR, null).getChild(0)?.text()

    override fun getPublishedDate(): String? =
        findData(TIME, PUBDATE, PUBDATE, null)?.attr(DATETIME)

    override fun getArticle(): String? =
        findData(DIV, CLASS, MAIN, null)?.outerHtml().updateArticleBody()

    override fun getDescription(): String? =
        getTagList(DIV).getContainsAttr(CLASS, CHAPO_MULTI)?.text()

    override fun getImage(): List<String?> {
        val element = findData(IMG, CLASS, IMAGE_SPIP, null)
        return listOf(
            element?.attr(SRC).toFullUrl(MULTINATIONALES_BASE_URL),
            element?.attr(WIDTH),
            element?.attr(HEIGHT)
        )
    }

    override fun getCssUrl(): String =// getTagList(LINK).getCssUrl(MULTINATIONALES_BASE_URL)
        findData(LINK, REL, STYLE_SHEET, null)?.attr(HREF).toFullUrl(MULTINATIONALES_BASE_URL)

    // -----------------
    // CONVERT
    // -----------------

    /**
     * Convert MultinationalesArticle to Article.
     * @param article the article to set data.
     * @return article with all data set.
     */
    internal fun toArticle(article: Article): Article {
        val author = getAuthor()
        val publishedDate = getPublishedDate()?.let { stringToDate(it)?.time }
        val description = getDescription()
        article.apply {
            title = getTitle().mToString()
            section = getSection().mToString()
            theme = getTheme().mToString()
            if (!author.isNullOrBlank()) this.author = author
            if (publishedDate != null) this.publishedDate = publishedDate
            this.article = getArticle().mToString()
            if (!description.isNullOrBlank()) this.description = description
            imageUrl = getImage()[0].mToString()
            cssUrl = getCssUrl()
            source = Source(name = this@MultinationalesArticle.sourceName)
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
                .setChapoItemprop()
                .addElement(getTagList(DIV), NOTES) // Add Notes at the end
                .addNoteRedirect()
                .correctUrlLink(MULTINATIONALES_BASE_URL) // TODO mail to error
                .correctBastaMediaUrl(MULTINATIONALES_BASE_URL)
                .setMainCssId(CLASS, CONTENT)
                .mToString()
                .forceHttps()
                .escapeHashtag()
        }

    /**
     * Set chapo div itemprop value to apply css style to the article description.
     *
     * @return the article with chapo itemprop set.
     */
    private fun Document?.setChapoItemprop(): Document? =
        this?.let {
            select(DIV).getContainsAttr(CLASS, CHAPO_MULTI)?.attr(ITEMPROP, DESCRIPTION)
            this
        }
}