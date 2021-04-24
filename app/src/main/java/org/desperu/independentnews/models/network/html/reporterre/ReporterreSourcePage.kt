package org.desperu.independentnews.models.network.html.reporterre

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlSourcePage
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.sources.correctRepoMediaUrl
import org.desperu.independentnews.models.database.SourcePage
import org.desperu.independentnews.utils.*
import org.jsoup.nodes.Document

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
    private val pageUrlList = mutableListOf<String>()
    private val buttonNameList = mutableListOf<String>()

    // --- GETTERS ---

    override fun getTitle(): String? = findData(H1, null, null, null)?.text()

    override fun getBody(): String? =
        findData(DIV, CLASS, TEXTE, null)?.outerHtml().updateBody()

    override fun getCssUrl(): String = getTagList(LINK).getCssUrl(REPORTERRE_BASE_URL)

    override fun getPageUrlList(): List<String> = pageUrlList

    override fun getButtonNameList(): List<String> = buttonNameList

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
    internal fun toSourceEditorial(url: String): SourcePage {
        setButtonNameAndPageList()
        return SourcePage(
            url = url.toFullUrl(REPORTERRE_BASE_URL),
            title = getTitle().mToString(),
            body = getBody().mToString(),
            cssUrl = getCssUrl(),
            isPrimary = true
        )
    }

    /**
     * Convert ReporterreSourcePage to SourcePage.
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
            body = if (position != 2) getBody().mToString() else "", // For Men and Women of Reporterre
            cssUrl = getCssUrl(),
            position = position
        )

    // -----------------
    // UTILS
    // -----------------

    /**
     * Update source page body to correct and remove needed data.
     *
     * @return the source page body updated.
     */
    private fun String?.updateBody(): String? =
        this?.let {
            it.toDocument()
                .addElement(getTagList(ASIDE), BOX_TEXT_COMPLEMENT)
                .addScripts(NOTE_REDIRECT, PAGE_LISTENER)
                .addPageListener()
                .updateContactMails()
                .correctUrlLink(REPORTERRE_BASE_URL)
                .correctRepoMediaUrl()
                .setMainCssId(ID, CONTAINER)
//                .removeComment()
                .mToString()
                .forceHttps()
                .escapeHashtag()
        }

    /**
     * Set all button name and page list, for all links.
     */
    private fun setButtonNameAndPageList() {
        getTagList(A).getMatchAttr(CLASS, LIEN_RUBRIQUE).forEach {
            val url = it.attr(HREF).toFullUrl(REPORTERRE_BASE_URL)

            if (!pageUrlList.contains(url)) { // To avoid duplicate
                pageUrlList.add(url)
                it.text()?.let { buttonName -> buttonNameList.add(buttonName) }
            }
        }
    }

    /**
     * Update contact mails, needed because they are protected by cloudflare..
     *
     * @return the article with updated contact mails.
     */
    private fun Document?.updateContactMails(): Document? =
        this?.let {
            select(a).getMatchAttr(CLASS, SPIP_MAIL).forEachIndexed { index, element ->
                val mail = REPORTERRE_CONTACT_MAILS[index]

                element.attr(HREF, MAIL_TO + mail) // Update href redirect
                element.getChild(0)?.text("[$mail]") // Update displayed text
            }
            this
        }

    // TODO :
    //  - source page redirection bug
    //  - Source page duplicate, remove before store if already in db !!
    //  - Update source design
    //  - Add swipe to refresh
    //  - Add search
    //  - Reporterre new articles text size to correct
    //  - Reduce onPageShow time out ??? to 1000L ???
    //  - Add web fragment or web activity
    //  - Support contact in basta source page
}