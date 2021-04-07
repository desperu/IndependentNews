package org.desperu.independentnews.extension.parseHtml

import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.isNoteRedirect
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.lang.StringBuilder

/**
 * Returns the string convert to Jsoup document.
 *
 * @return the string convert to Jsoup document.
 */
internal fun String?.toDocument(): Document? = this?.let { Jsoup.parse(this) }

/**
 * Add view port meta data in the head tag of the given html code.
 *
 * @return the html code with the view port.
 */
internal fun Document?.addViewPort(): Document? = // TODO seems to change nothing
    this?.let {
        select(HEAD).getIndex(0)
            ?.appendElement(META)
            ?.attr(NAME, "viewport")
            ?.attr(CONTENT, "width=device-width, initial-scale=1, target-densitydpi=high-dpi")
        this
    }

/**
 * Add notes at the end of the article body.
 *
 * @param elements  the element list in which search notes.
 * @param value     the value of the attr to match.
 *
 * @return the article body with notes at the end.
 */
internal fun Document?.addNotes(elements: Elements, value: String): Document? =
    this?.let {
        val notes = elements.getMatchAttr(CLASS, value).getOrNull(0)?.outerHtml()

        notes?.let { select(BODY).append(it) }
        this
    }

/**
 * Add note redirect javascript to enable note redirection.
 */
internal fun Document?.addNoteRedirect(): Document? =
    this?.let {
        select(HEAD).getIndex(0)
            ?.appendElement(SCRIPT)
            ?.attr(TYPE, TEXT_JS)
            ?.append(NOTE_REDIRECT)
        this
    }

/**
 * Correct all url links as needed, remove blank, note javascript redirect,
 * remove on click or with their full url in the given html code.
 *
 * @param baseUrl the base url of the link.
 *
 * @return the html code with corrected url links.
 */
internal fun Document?.correctUrlLink(baseUrl: String): Document? =
    this?.let {

        select(a).forEach {
            when {
                it.attr(HREF).isNullOrBlank() -> it.removeAttr(HREF) // Check there's no error
                isNoteRedirect(it.attr(HREF)) ->
                    it.attr(HREF, "javascript:scrollToElement('${it.attr(HREF).removePrefix("#")}')")
                it.attr(ONCLICK).isNotBlank() -> it.removeAttr(ONCLICK)
                else -> it.attrToFullUrl(HREF, baseUrl)
            }
        }

        this
    }

/**
 * Set main css id to apply css style to the article body.
 *
 * @param attr      the attribute to set.
 * @param value     the value of the attribute to set.
 *
 * @return the article with main css id set.
 */
internal fun Document?.setMainCssId(attr:String, value: String): Document? =
    this?.let {
        select(BODY).getIndex(0)?.attr(attr, value)
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