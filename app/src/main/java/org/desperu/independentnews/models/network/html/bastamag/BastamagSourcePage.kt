package org.desperu.independentnews.models.network.html.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlSourcePage
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.getChild
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.parseHtml.sources.correctBastaMediaUrl
import org.desperu.independentnews.extension.parseHtml.sources.setMainCssId
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.database.SourcePage
import org.desperu.independentnews.utils.*
import org.jsoup.nodes.Document
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
    private val buttonNameList = mutableListOf<String>()
    private val pageUrlList = mutableListOf<String>()

    // --- GETTERS ---

    override fun getTitle(): String? =
        findData(SMALL, CLASS, TITRE_ARTICLE, null)?.ownText()
            ?: findData(H1, CLASS, TITRE_PAGE_LIST, null)?.text()

    override fun getBody(): String? =
        findData(DIV, CLASS, MAIN, 0)?.outerHtml().updateBody()

    override fun getCssUrl(): String =
        findData(LINK, REL, STYLE_SHEET, null)?.attr(HREF).toFullUrl(BASTAMAG_BASE_URL)

    override fun getPageUrlList(): List<String> = pageUrlList

    override fun getButtonNameList(): List<String> = buttonNameList

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
        setButtonNameAndPageList()
        return SourcePage(
            url = url.toFullUrl(BASTAMAG_BASE_URL),
            title = getTitle().mToString(),
            body = getBody().mToString(),
            cssUrl = getCssUrl(),
            isPrimary = true
        )
    }

    /**
     * Convert BastamagSourcePage to SourcePage.
     *
     * @param url           the url of the source page.
     * @param buttonName    the button name of the source page.
     * @param position      the position of the source page in the list.
     *
     * @return source page with all data set.
     */
    internal fun toSourcePage(url: String?, buttonName: String, position: Int): SourcePage =
        SourcePage(
            url = url.mToString(),
            buttonName = buttonName,
            title = getTitle().mToString(),
            body = getBody().mToString(),
            cssUrl = getCssUrl(),
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
                .addNotes()
                .correctUrlLink(BASTAMAG_BASE_URL)
                .correctBastaMediaUrl()
                .setMainCssId()
                .mToString()
                .escapeHashtag()
                .forceHttps()
        }

    /**
     * Add notes at the end of the article body?
     *
     * @return the article body with notes at the end.
     */
    private fun Document?.addNotes(): Document? = // Duplicate in BastamagArticle
        this?.let {
            val notes = findData(DIV, CLASS, NOTES, null)?.outerHtml()

            notes?.let { select(BODY).append(it) }
            this
        }

    /**
     * Set all button name and page list, for all links.
     */
    private fun setButtonNameAndPageList() {
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

        buttonNameList.add(BASTA_EDITO)
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

        contacts?.let { addButtonNameAndUrlToList(it) }
    }

    /**
     * Set the "standards" buttons.
     */
    private fun setButtons() {
        getTagList(a).forEach {
            when (it.attr(CLASS)) {
                SPAN_SUPPORTS -> addButtonNameAndUrlToList(it)// For supports
                SPAN_ECONOMY -> addButtonNameAndUrlToList(it)// For economy
                SPAN_MOST_VIEWED -> addButtonNameAndUrlToList(it)// For most viewed articles
                SPAN_CGU -> addButtonNameAndUrlToList(it)// For CGU
            }
        }
    }

    /**
     * Add the button name and the url to each list for the given element, and remove from parent.
     *
     * @param element the element for which save title and url.
     */
    private fun addButtonNameAndUrlToList(element: Element) {
        buttonNameList.add(element.text())
        pageUrlList.add(element.attr(HREF).toFullUrl(BASTAMAG_BASE_URL))
        element.remove()
    }
}