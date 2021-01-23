package org.desperu.independentnews.models.network.html.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlArticle
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.sources.addNotes
import org.desperu.independentnews.extension.parseHtml.sources.correctBastaMediaUrl
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.stringToDate

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
        getTagList(P).getContainsAttr(CLASS, SURTITRE)?.ownText()

    override fun getAuthor() : String? =
        findData(SPAN, CLASS, AUTHOR, null).getChild(0)?.text()

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

    override fun getCssUrl(): String = getTagList(LINK).getCssUrl(BASTAMAG_BASE_URL)

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
            title = getTitle().mToString()
            section = getSection().mToString()
            theme = getTheme().mToString()
            if (!author.isNullOrBlank()) this.author = author
            if (publishedDate != null) this.publishedDate = publishedDate
            this.article = getArticle().mToString()
            if (!description.isNullOrBlank()) this.description = description
            imageUrl = getImage()[0].mToString()
            cssUrl = getCssUrl()
            source = Source(name = this@BastamagArticle.sourceName)
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
                .addNotes(getTagList(DIV))
                .correctUrlLink(BASTAMAG_BASE_URL)
                .correctBastaMediaUrl(BASTAMAG_BASE_URL)
                .setMainCssId(CLASS, MAIN_CONTAINER)
                .mToString()
                .forceHttps()
                .escapeHashtag()
        }
}