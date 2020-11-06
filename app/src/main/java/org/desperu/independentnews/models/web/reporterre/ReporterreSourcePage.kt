package org.desperu.independentnews.models.web.reporterre

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlSourcePage
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.utils.*

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

//    override fun getPublishedDate(): String? =
//        findData(SPAN, CLASS, DATEPUBLICATION, null)?.text()

    override fun getBody(): String? =
//        correctMediaUrl(
//            escapeHashtag(
//                addDescription(
                    correctUrlLink(
                        findData(DIV, CLASS, TEXTE, null)?.outerHtml(),
                        REPORTERRE_BASE_URL
                    )
//                )
//            )
//        )

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
            imageUrl = getImage()[0].mToString(),
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
            imageUrl = getImage()[0].mToString(),
            cssUrl = getCssUrl().mToString(),
            position = position
        )
}