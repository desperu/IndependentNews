package org.desperu.independentnews.base.html

import okhttp3.ResponseBody
import org.desperu.independentnews.extension.parseHtml.attrToFullUrl
import org.desperu.independentnews.utils.*
import org.jsoup.Jsoup

/**
 * Abstract base html article class witch provide standard functions to parse data from article's html page.
 *
 * @param htmlPage the html page retrieved from the retrofit request.
 */
abstract class BaseHtmlArticle(private val htmlPage: ResponseBody): BaseHtml(htmlPage) {

    // FOR DATA
    protected abstract val sourceName: String

    // --- GETTERS ---

    internal abstract fun getTitle(): String?

    internal abstract fun getSection(): String?

    internal abstract fun getTheme(): String?

    internal abstract fun getAuthor(): String?

    internal abstract fun getPublishedDate(): String?

    internal abstract fun getArticle(): String?

    internal abstract fun getDescription(): String?

    internal abstract fun getImage(): List<String?>

    internal abstract fun getCssUrl(): String?

    /**
     * Correct all url links with their full url in the given html code.
     *
     * @param html the html code to correct.
     * @param baseUrl the base url of the link.
     *
     * @return the html code with corrected url links.
     */
    protected fun correctUrlLink(html: String?, baseUrl: String): String? =
        if (!html.isNullOrBlank()) {
            val document = Jsoup.parse(html)
            val element = document.select(a)
            element.forEach {
                when {
                    it.attr(HREF).contains("#") -> it.removeAttr(HREF)
                    it.attr(ONCLICK).isNotBlank() -> it.removeAttr(ONCLICK)
                    else -> it.attrToFullUrl(HREF, baseUrl)
                }
            }

            document.select(EMBED).forEach {
                if (it.attr(SRC).contains(".pdf"))
                    it.remove()
            }

            document.toString()
        } else
            null

    /**
     * Escape hashtag (#) character to prevent new API bug...
     *
     * @param html the html code to correct.
     *
     * @return the html code with escaped hashtag.
     */
    protected fun escapeHashtag(html: String?): String? =
        if (!html.isNullOrBlank()) {
            html.replace("#", "%23")
        } else
            null
}