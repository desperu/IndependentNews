package org.desperu.independentnews.models.network.html.reporterre

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlArticle
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.sources.correctRepoMediaUrl
import org.desperu.independentnews.extension.parseHtml.correctUrlLink
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.parseHtml.sources.getAuthor
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.literalDateToMillis
import org.jsoup.nodes.Document

/**
 * Class which provides a model to parse reporterre article html page.
 *
 * @property htmlPage the reporterre article html page.
 *
 * @constructor Instantiate a new ReporterreArticle.
 *
 * @param htmlPage the reporterre article html page to set.
 */
data class ReporterreArticle(private val htmlPage: ResponseBody): BaseHtmlArticle(htmlPage) {

    // FOR DATA
    override val sourceName = REPORTERRE

    // --- GETTERS ---

    override fun getTitle(): String? = findData(H1, null, null, null)?.text()

    override fun getSection(): String? =
        findData(P, CLASS, GLIBELLE, null)?.text()
            ?.split(" — ")?.getOrNull(0)

    override fun getTheme(): String? =
        findData(P, CLASS, GLIBELLE, null)?.text()
            ?.split(" — ")?.run { getOrNull(lastIndex) }

    override fun getAuthor() : String? {
        val article = findData(DIV, STYLE, TEXT_ALIGN, null)

        var author = findData(A, CLASS, LIEN_AUTEUR, null)?.text()

        if (author.isNullOrBlank())
            author = findData(HR, SIZE, ONE, null)?.ownText()

        if (author.isNullOrBlank())
            author = article?.select(UL).getAuthor()

        if (author.isNullOrBlank())
            author = article?.select(P).getAuthor()

        return author?.removePrefix(" ")
    }

    override fun getPublishedDate(): String? =
        findData(SPAN, CLASS, DATE, null)?.text()

    override fun getArticle(): String? =
        findData(DIV, CLASS, TEXTE, null)?.outerHtml().updateArticleBody()

    override fun getDescription(): String? = findData(DIV, CLASS, CHAPO, null)?.text()

    override fun getImage(): List<String?> {
        val element = findData(IMG, CLASS, IMAGE_LOGO, null)
        return listOf(
            element?.attr(SRC).toFullUrl(REPORTERRE_BASE_URL),
            element?.attr(WIDTH),
            element?.attr(HEIGHT)
        )
    }

    override fun getCssUrl(): String = getTagList(LINK).getCssUrl(REPORTERRE_BASE_URL)

    // -----------------
    // CONVERT
    // -----------------

    /**
     * Convert ReporterreArticle to Article.
     * @param article the article to set data.
     * @return article with all data set.
     */
    internal fun toArticle(article: Article): Article {
        val author = getAuthor()
        val publishedDate = getPublishedDate()?.let { literalDateToMillis(it) }
        val description = getDescription()
        article.apply {
            title = getTitle().mToString()
            section = getSection().mToString()
            theme = getTheme().mToString()
            if (!author.isNullOrBlank()) this.author = author
            if (this.publishedDate == 0L && publishedDate != null) this.publishedDate = publishedDate
            this.article = getArticle().mToString()
            if (!description.isNullOrBlank()) this.description = description
            imageUrl = getImage()[0].mToString()
            cssUrl = getCssUrl()
            source = Source(name = this@ReporterreArticle.sourceName)
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
                .addElement(getTagList(ASIDE), BOX_TEXT_COMPLEMENT)
                .addElement(getTagList(ASIDE), BOX_AFTER_ARTICLE)
                .addScripts(NOTE_REDIRECT, PAGE_LISTENER)
                .addDescription()
                .addDonateCall()
                .addPageListener()
                .correctUrlLink(REPORTERRE_BASE_URL)
                .correctRepoMediaUrl()
                .setMainCssId(ID, CONTAINER)
                .removeComment()
                .mToString()
                .escapeHashtag()
                .forceHttps()
        }

    /**
     * Add description with the article body.
     *
     * @return the full article, description and article body.
     */
    private fun Document?.addDescription(): Document? =
        this?.let {
            val description =
                if (!getDescription().isNullOrBlank())
                    findData(DIV, CLASS, CHAPO, null)?.outerHtml()
                else
                    ""

            select(DIV)?.getMatchAttr(CLASS, TEXTE)?.before(description)
            this
        }

    /**
     * Add donation call to the article body.
     *
     * @return the article with donation call.
     */
    private fun Document?.addDonateCall(): Document? =
        this?.let {
            if (!NO_DONATE_CALL.contains(getSection()))
                select(DIV).getMatchAttr(ID, APPEL_DON).getIndex(0)?.append(DONATE_CALL)
            this
        }

    /**
     * Remove comment section.
     *
     * @return the article without comment section.
     */
    private fun Document?.removeComment(): Document? =
        this?.let {
            select(DIV).getMatchAttr(CLASS, NOTE_NO_PRINT).remove()
            this
        }
}