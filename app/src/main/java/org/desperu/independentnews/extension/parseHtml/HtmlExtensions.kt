package org.desperu.independentnews.extension.parseHtml

import org.desperu.independentnews.utils.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.StringBuilder

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
                it.attr(HREF).isNullOrBlank() -> it.removeAttr(HREF) // Check there's no error
                it.attr(ONCLICK).isNotBlank() -> it.removeAttr(ONCLICK)
                else -> it.attrToFullUrl(HREF, baseUrl)
            }
        }

        this
    }

// TODO on test check it in https://reporterre.net/Les-mareyeuses-d-Abidjan-en-lutte-contre-la-peche-industrielle
//  and need to set url to full url for each photo !!!
internal fun Document?.correctScriptUrl(baseUrl: String): Document? =
    this?.let {

        select("script").forEach {
            val url = it.attr(SRC).removePrefix("/").toFullUrl(baseUrl)
            it.attr(SRC, url)

            val script = it.ownText().setJsImageListToFullUrl(baseUrl)
            it.text(script)
        }

        this
    }

internal fun String?.setJsImageListToFullUrl(baseUrl: String): String? =
    this?.let {
        val result = StringBuilder()

        val list = split("\'")
        list.forEachIndexed { index, fourthItem ->

            var fullUrl: String? = null

            if (fourthItem.contains("/") && !fourthItem.contains("/js/galleria/"))
                fullUrl = fourthItem.removePrefix("/").toFullUrl(baseUrl)

            if (index != 0) result.append("\'")
            result.append(fullUrl ?: fourthItem)
        }

        result.toString()
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