package org.desperu.independentnews.models.network.html.multinationales

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlSourcePage
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.parseHtml.sources.addNotes
import org.desperu.independentnews.extension.parseHtml.sources.correctBastaMediaUrl
import org.desperu.independentnews.extension.parseHtml.toDocument
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.database.SourcePage
import org.desperu.independentnews.utils.*

/**
 * Class which provides a model to parse multinationales html source page.
 *
 * @property htmlPage the multinationales html source page.
 *
 * @constructor Instantiate a new MultinationalesSourcePage.
 *
 * @param htmlPage the multinationales html source page to set.
 */
data class MultinationalesSourcePage(private val htmlPage: ResponseBody): BaseHtmlSourcePage(htmlPage) {

    // FOR DATA
    override val sourceName = MULTINATIONALES

    // --- GETTERS ---

    override fun getTitle(): String? =
        getTagList(H1).getContainsAttr(CLASS, H1_MULTI)?.text()

    override fun getBody(): String? =
        findData(DIV, CLASS, MAIN, null)?.outerHtml().updateBody()

    override fun getCssUrl(): String = //getTagList(LINK).getCssUrl(MULTINATIONALES_BASE_URL)
        findData(LINK, REL, STYLE_SHEET, null)?.attr(HREF).toFullUrl(MULTINATIONALES_BASE_URL)

    override fun getPageUrlList(): List<String> = listOf()

    override fun getButtonNameList(): List<String> = listOf()

    // -----------------
    // CONVERT
    // -----------------

    /**
     * Convert MultinationalesSourcePage to SourcePage (Editorial).
     *
     * @param url the url of the source page.
     *
     * @return source page with all data set.
     */
    internal fun toSourceEditorial(url: String): SourcePage {
        return SourcePage(
            url = url.toFullUrl(MULTINATIONALES_BASE_URL),
            title = getTitle().mToString(),
            body = getBody().mToString(),
            cssUrl = getCssUrl(),
            isPrimary = true
        )
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Update source page body to add, correct and remove needed data.
     *
     * @return the source page body updated.
     */
    private fun String?.updateBody(): String? =
        this?.let {
            it.toDocument()
                .addNotes(getTagList(DIV))
                .correctUrlLink(MULTINATIONALES_BASE_URL)
                .correctBastaMediaUrl(MULTINATIONALES_BASE_URL)
                .setMainCssId(CLASS, CONTENT)
                .mToString()
                .escapeHashtag()
                .forceHttps()
        }
}