package org.desperu.independentnews.models.web.reporterre

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlArticle
import org.desperu.independentnews.extension.getAuthor
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.literalDateToMillis
import org.jsoup.Jsoup

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
        findData(A, CLASS, ARIANNE, 1)?.text()?.removeSuffix(" >")

    override fun getTheme(): String? = findData(A, CLASS, LIENTHEME, null)?.text()

    override fun getAuthor() : String? {
        val article = findData(DIV, STYLE, TEXT_ALIGN, null)

        var author = findData(A, CLASS, LIENAUTEUR, null)?.text()

        if (author.isNullOrBlank())
            author = findData(HR, SIZE, ONE, null)?.ownText()

        if (author.isNullOrBlank())
            author = article?.select(UL).getAuthor()

        if (author.isNullOrBlank())
            author = article?.select(P).getAuthor()

        return author?.removePrefix(" ")
    }

    override fun getPublishedDate(): String? =
        findData(SPAN, CLASS, DATEPUBLICATION, null)?.text()

    override fun getArticle(): String? =
        correctImagesUrl(addDescription(findData(DIV, CLASS, TEXTE, null)?.outerHtml()))

    override fun getDescription(): String? = findData(DIV, CLASS, CHAPO, null)?.text()

    override fun getImage(): List<String?> {
        val element = findData(IMG, CLASS, REPO_IMAGE_CLASS, null)
        return listOf(
            REPORTERRE_BASE_URL + element?.attr(SRC),
            element?.attr(WIDTH),
            element?.attr(HEIGHT)
        )
    }

    override fun getCssUrl(): String? =
        REPORTERRE_BASE_URL + findData(LINK, REL, STYLESHEET, null)?.attr(HREF)

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
            sourceName = this@ReporterreArticle.sourceName
            if (getUrl().isNotBlank()) url = getUrl()
            title = getTitle().toString()
            section = getSection().toString()
            theme = getTheme().toString()
            if (!author.isNullOrBlank()) this.author = author
            if (publishedDate != null) this.publishedDate = publishedDate
            this.article = getArticle().toString()
            if (!description.isNullOrBlank()) this.description = description
            imageUrl = getImage()[0].toString()
            cssUrl = getCssUrl().toString()
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
            document.select(IMG).forEach { it.attr(SRC, REPORTERRE_BASE_URL + it.attr(DATA_ORIGINAL)) }
            document.toString()
        } else
            null

    /**
     * Add description with the article body.
     * @param article the article body.
     * @return the full article, description and article body in html format.
     */
    private fun addDescription(article: String?): String? =
        if (!article.isNullOrBlank()) {
            val description = if (!getDescription().isNullOrBlank())
                                  findData(DIV, CLASS, CHAPO, null)?.outerHtml()
                              else
                                  ""
            val document = Jsoup.parse(description + article)
            document.toString()
        } else
            null
}