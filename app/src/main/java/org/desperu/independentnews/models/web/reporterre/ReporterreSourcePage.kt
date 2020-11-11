package org.desperu.independentnews.models.web.reporterre

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlSourcePage
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.utils.*
import org.jsoup.Jsoup

/**
 * Class which provides a model to parse reporterre source html page.
 *
 * @property htmlPage the reporterre article html page.
 *
 * @constructor Instantiate a new ReporterreSourcePage.
 *
 * @param htmlPage the reporterre source html page to set.
 */
data class ReporterreSourcePage(private val htmlPage: ResponseBody): BaseHtmlSourcePage(htmlPage) {

    // FOR DATA
    override val sourceName = REPORTERRE

    // --- GETTERS ---

    override fun getTitle(): String? = findData(H1, null, null, null)?.text()

    override fun getBody(): String? =
        correctMediaUrl(
            escapeHashtag(
//                addDescription(
                    correctUrlLink(
                        findData(DIV, CLASS, TEXTE, null)?.outerHtml(),
                        REPORTERRE_BASE_URL
                    )
//                )
            )
        )

    override fun getImage(): List<String?> { // TODO useless into the body...
        val element = findData(IMG, CLASS, LAZY, null)
        return listOf(
            element?.attr(DATA_ORIGINAL).toFullUrl(REPORTERRE_BASE_URL),
            element?.attr(WIDTH),
            element?.attr(HEIGHT)
        )
    }

    override fun getCssUrl(): String? =
        findData(LINK, REL, STYLESHEET, null)?.attr(HREF).toFullUrl(REPORTERRE_BASE_URL)

    override fun getPageUrlList(): List<String?> {
        val pageUrlList = mutableListOf<String>()

        getTagList(A).getMatchAttr(CLASS, LIEN_RUBRIQUE).forEach {
            pageUrlList.add(it.attr(HREF).toFullUrl(REPORTERRE_BASE_URL))
        }

        return pageUrlList
    }

    // -----------------
    // CONVERT
    // -----------------

    /**
     * Convert ReporterreSourcePage to SourcePage (Editorial).
     *
     * @param url the url of the source page.
     *
     * @return source page with all data set.
     */
    internal fun toSourceEditorial(url: String): SourcePage =
        SourcePage(
            url = url.toFullUrl(REPORTERRE_BASE_URL),
            title = getTitle().mToString(),
            body = getBody().mToString(),
            cssUrl = getCssUrl().mToString(),
            isPrimary = true
        )

    /**
     * Convert ReporterreSourcePage to SourcePage.
     *
     * @param url           the url of the source page.
     * @param position      the position of the source page in the list.
     *
     * @return source page with all data set.
     */
    internal fun toSourcePage(url: String?, position: Int): SourcePage =
        SourcePage(
            url = url.mToString(),
            title = getTitle().mToString(),
            body = getBody().mToString(),
            cssUrl = getCssUrl().mToString(),
            position = position
        )

    // -----------------
    // UTILS
    // -----------------

    // TODO already in ReporterreArticle, put into ElementExtension ???
    /**
     * Correct all media url's with their full url's in the given html code.
     * @param html the html code to correct.
     * @return the html code with corrected media url's.
     */
    private fun correctMediaUrl(html: String?): String? =
        if (!html.isNullOrBlank()) {
            val document = Jsoup.parse(html)

            document.select(IMG).forEach {
                val dataOriginal = it.attr(DATA_ORIGINAL)
                val urlLink = if (!dataOriginal.isNullOrBlank()) dataOriginal else it.attr(SRC)
                it.attr(SRC, urlLink.toFullUrl(REPORTERRE_BASE_URL)) }

            document.select(AUDIO).forEach { it.attrToFullUrl(SRC, REPORTERRE_BASE_URL) }
            document.toString()
        } else
            null
}