package org.desperu.independentnews.models.web.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlSourcePage
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.getChild
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.utils.*
import org.jsoup.Jsoup
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
        setMainCssId(
            correctMediaUrl(
                escapeHashtag(
                    correctUrlLink(
//                        findData(DIV, CLASS, HERO_UNIT, 0)?.outerHtml(),
                        findData(DIV, CLASS, MAIN, 0)?.outerHtml(),
                        BASTAMAG_BASE_URL
                    )
                )
            )
        )

    override fun getImage(): List<String?> = listOf()

    override fun getCssUrl(): String? =
        findData(LINK, REL, STYLESHEET, null)?.attr(HREF).toFullUrl(BASTAMAG_BASE_URL)

    override fun getPageUrlList(): List<String?> = pageUrlList

    internal fun getTitleList() = titleList

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

    // TODO already in BastamagArticle ... serialize !!! put in ElementExtension???
    /**
     * Correct all media url's with their full url's in the given html code.
     * @param html the html code to correct.
     * @return the html code with corrected media url's.
     */
    private fun correctMediaUrl(html: String?): String? =
        if (!html.isNullOrBlank()) {
            val document = Jsoup.parse(html)

            document.select(IMG).forEach {
                it.attrToFullUrl(SRC, BASTAMAG_BASE_URL)
                correctSrcSetUrls(it)
            }

            document.select(SOURCE_TAG).forEach { correctSrcSetUrls(it) }
            document.select(AUDIO).forEach { it.attrToFullUrl(SRC, BASTAMAG_BASE_URL) }
            document.toString()
        } else
            null

    /**
     * Correct the srcset url value of the given element with their full url.
     * @param element the element for which correct url's.
     */
    private fun correctSrcSetUrls(element: Element) {
        val srcSetList = Utils.deConcatenateStringToMutableList(element.attr(SRCSET))
        val correctedList = srcSetList.map { it.toFullUrl(BASTAMAG_BASE_URL) }
        element.attr(SRCSET, Utils.concatenateStringFromMutableList(correctedList.toMutableList()))
    }

    /**
     * Set main css id to apply css style to the article body.
     * @param html the article body.
     * @return the article with main css id set.
     */
    private fun setMainCssId(html: String?): String? =
        if(!html.isNullOrBlank()) {
            val document = Jsoup.parse(html)
            document.select(BODY).getIndex(0)?.attr(CLASS, MAIN_CONTAINER)
            document.toString()
        } else
            null

    // TODO correct http to https, else web view not want "clear text not permitted"
}