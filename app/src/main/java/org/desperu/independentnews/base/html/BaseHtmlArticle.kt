package org.desperu.independentnews.base.html

import okhttp3.ResponseBody
import org.desperu.independentnews.utils.BASTAMAG_BASE_URL
import org.desperu.independentnews.utils.HREF
import org.desperu.independentnews.utils.a
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
     * @param html the html code to correct.
     * @return the html code with corrected url links.
     */
    protected fun correctUrlLink(html: String?): String? =
        if (!html.isNullOrBlank()) {
            val document = Jsoup.parse(html)

            document.select(a).forEach {
                val urlLink = it.attr(HREF)
                if (!urlLink.contains("http"))
                    it.attr(HREF, BASTAMAG_BASE_URL + urlLink)
            }

            document.toString()
        } else
            null
}