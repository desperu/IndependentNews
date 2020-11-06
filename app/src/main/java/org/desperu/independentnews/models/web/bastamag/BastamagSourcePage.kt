package org.desperu.independentnews.models.web.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlSourcePage
import org.desperu.independentnews.extension.parseHtml.getChild
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.mToString
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

//    override fun getPublishedDate(): String? =
//        findData(TIME, PUBDATE, PUBDATE, null)?.attr(DATETIME)

    override fun getBody(): String? =
//        setMainCssId(
//            correctMediaUrl(
//                escapeHashtag(
                    correctUrlLink(
//                        findData(DIV, CLASS, HERO_UNIT, 0)?.outerHtml(),
                        findData(DIV, CLASS, MAIN, 0)?.outerHtml(),
                        BASTAMAG_BASE_URL
                    )
//                )
//            )
//        )

    override fun getImage(): List<String?> = listOf()

    override fun getCssUrl(): String? =
        findData(LINK, REL, STYLESHEET, null)?.attr(HREF).toFullUrl(BASTAMAG_BASE_URL)

//    override fun getPosition(): Int? {
//
//
//        return 0
//    }

    override fun getPageUrlList(): List<String?> = pageUrlList

//    override fun isPrimary(): Boolean? {
//        TODO("Not yet implemented")
//    }

    internal fun getTitleList() = titleList

    // -----------------
    // UTILS
    // -----------------

    /**
     * Set all title and page list, for all links.
     */
    private fun setTitleAndPageList() {
        setAfter()
        setContacts()
        setButtons()
    }

    /**
     * Set the after (read editorial and info about basta !)
     */
    private fun setAfter() {
        val after = findData(SPAN, CLASS, LIRE, null)
            ?.getChild(0)

        after?.text()?.let { titleList.add(it) }
        val url = after?.attr(HREF)?.toFullUrl(BASTAMAG_BASE_URL)
        url?.let { pageUrlList.add(it) }
    }

    /**
     * Set the contacts url and title.
     */
    private fun setContacts() {
        val contacts = findData(DIV, CLASS, ADDRESS, null)
            ?.select(P)?.getMatchAttr(CLASS, TOUS)
            ?.select(a)

        contacts?.text()?.let { titleList.add(it) }
        val url = contacts?.attr(HREF)?.toFullUrl(BASTAMAG_BASE_URL)
        url?.let { pageUrlList.add(it) }
    }

    /**
     * Set the "standards" button.
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
     * Add the title and the url to each list for the given element.
     *
     * @param element the element for which save title and url.
     */
    private fun addTitleAndUrlToList(element: Element) {
        titleList.add(element.text())
        pageUrlList.add(element.attr(HREF).toFullUrl(BASTAMAG_BASE_URL))
    }

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
//            imageUrl = getImage()[0].mToString(),
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
//            imageUrl = getImage()[0].mToString(),
            cssUrl = getCssUrl().mToString(),
            position = position
        )
}