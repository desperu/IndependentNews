package org.desperu.independentnews.extension.parseHtml

import org.desperu.independentnews.utils.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Returns the string convert to Jsoup document.
 *
 * @return the string convert to Jsoup document.
 */
internal fun String?.toDocument(): Document? = this?.let { Jsoup.parse(this) }

/**
 * Correct all url links with their full url in the given html code.
 *
 * @param baseUrl the base url of the link.
 *
 * @return the html code with corrected url links.
 */
internal fun Document?.correctUrlLink(baseUrl: String): Document? =
    this?.let {

        // TODO properly handle note redirect and pdf, preview and redirect
        select(a).forEach {
            when {
                it.attr(ONCLICK).isNotBlank() -> it.removeAttr(ONCLICK)
                it.attr(HREF).matches("#nh([0-9]){2}-([0-9]){2}".toRegex()) -> return@forEach
                it.attr(HREF).matches("#nb([0-9]){2}-([0-9]){2}".toRegex()) -> return@forEach
                it.attr(HREF).matches("#nh([0-9]){2}".toRegex()) -> return@forEach
                it.attr(HREF).matches("#nb([0-9]){2}".toRegex()) -> return@forEach
                else -> it.attrToFullUrl(HREF, baseUrl)
            }
        }

        select(EMBED).forEach {
            if (it.attr(SRC).contains(".pdf"))
                it.remove()
        }

        this
    }

/**
 * Escape hashtag (#) character to prevent new API bug...
 *
 * @return the html code with escaped hashtag.
 */
internal fun String?.escapeHashtag(): String? =
    if (!this.isNullOrBlank()) {
        this.replace("#", "%23")
    } else
        null

/**
 * Replace all http occurrences with https into the html code, to force https redirection.
 *
 * @return html code with https redirection.
 */
internal fun String?.forceHttps(): String? =
    if (!this.isNullOrBlank()) {
        this.replace("http://", "https://")
    } else
        null