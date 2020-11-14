package org.desperu.independentnews.models.web.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlSourcePage
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.getChild
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.parseHtml.sources.correctBastaMediaUrl
import org.desperu.independentnews.extension.parseHtml.sources.setMainCssId
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.utils.*
import org.jsoup.nodes.Element

/**
 * Class which provides a model to parse bastamag html source page.
 *
 * @property htmlPage the bastamag html source page.
 *
 * @constructor Instantiate a new BastamagSourcePage.
 *
 * @param htmlPage the bastamag html source page to set.
 */
data class BastamagSourcePage(private val htmlPage: ResponseBody): BaseHtmlSourcePage(htmlPage) {

    // FOR DATA
    override val sourceName = BASTAMAG
    private val titleList = mutableListOf<String>()
    private val pageUrlList = mutableListOf<String>()

    // --- GETTERS ---

    override fun getTitle(): String? =
        findData(H1, CLASS, TITRE_PAGE_LIST, 0)?.text() // TODO null et owntext()

    override fun getBody(): String? =
        findData(DIV, CLASS, MAIN, 0)?.outerHtml().updateBody()

    override fun getCssUrl(): String? =
        findData(LINK, REL, STYLE_SHEET, null)?.attr(HREF).toFullUrl(BASTAMAG_BASE_URL)

    override fun getPageUrlList(): List<String> = pageUrlList

    internal fun getTitleList(): MutableList<String> = titleList
// TODO button name and source page title !!!!
    // -----------------
    // CONVERT
    // -----------------

    /**
     * Convert BastamagSourcePage to SourcePage (Editorial).
     *
     * @param url the url of the source page.
     *
     * @return source page with all data set.
     */
    internal fun toSourceEditorial(url: String): SourcePage {
        setTitleAndPageList()
        return SourcePage(
            url = url.toFullUrl(BASTAMAG_BASE_URL),
            title = getTitle().mToString(),
            body = getBody().mToString(),
            cssUrl = getCssUrl().mToString(),
            isPrimary = true
        )
    }

    /**
     * Convert BastamagSourcePage to SourcePage.
     *
     * @param url           the url of the source page.
     * @param position      the position of the source page in the list.
     * @param title         the title of the source page.
     *
     * @return source page with all data set.
     */
    internal fun toSourcePage(url: String?, position: Int, title: String): SourcePage =
        SourcePage(
            url = url.mToString(),
            title = title,
            body = getBody().mToString(),
            cssUrl = getCssUrl().mToString(),
            position = position
        )

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
                .correctUrlLink(BASTAMAG_BASE_URL)
                .correctBastaMediaUrl()
                .setMainCssId()
                .mToString()
                .escapeHashtag()
                .forceHttps()
        }

    /**
     * Set all title and page list, for all links.
     */
    private fun setTitleAndPageList() {
        setAfter()
        setContacts()
        setButtons()
    }

    /**
     * Set the after (read editorial and info about basta !), and remove from parent.
     */
    private fun setAfter() {
        val after = findData(SPAN, CLASS, LIRE, null)
            ?.getChild(0)

        titleList.add(BASTA_EDITO)
        val url = after?.attr(HREF)?.toFullUrl(BASTAMAG_BASE_URL)
        url?.let { pageUrlList.add(it) }

        after?.remove()
        findData(SPAN, CLASS, ENGLISH, null)?.remove()
    }

    /**
     * Set the contacts url and title, and remove from parent.
     */
    private fun setContacts() {
        val contacts = findData(DIV, CLASS, ADDRESS, null)
            ?.select(P)
            ?.getMatchAttr(CLASS, TOUS)
            ?.select(a)
            ?.getIndex(0)

        contacts?.let { addTitleAndUrlToList(it) }
    }

    /**
     * Set the "standards" buttons.
     */
    private fun setButtons() {
        getTagList(a).forEach {
            when (it.attr(CLASS)) {
                SPAN_SUPPORTS -> addTitleAndUrlToList(it)// For supports
                SPAN_ECONOMY -> addTitleAndUrlToList(it)// For economy
                SPAN_MOST_VIEWED -> addTitleAndUrlToList(it)// For most viewed articles
                SPAN_CGU -> addTitleAndUrlToList(it)// For CGU
            }
        }
    }

    /**
     * Add the title and the url to each list for the given element, and remove from parent.
     *
     * @param element the element for which save title and url.
     */
    private fun addTitleAndUrlToList(element: Element) {
        titleList.add(element.text())
        pageUrlList.add(element.attr(HREF).toFullUrl(BASTAMAG_BASE_URL))
        element.remove()
    }
}