package org.desperu.independentnews.extension.parseHtml.sources

import org.desperu.independentnews.extension.parseHtml.attrToFullUrl
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.concatenateStringFromMutableList
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * Try to get author from Jsoup [Elements].
 * Needed to parse author from Reporterre html article.
 *
 * @return the author if found, else null.
 */
internal fun Elements?.getAuthor(): String? {
    this?.forEach {
        val stringList = it.text().split(":")
        if (stringList.size > 1 && stringList[0].contains(SOURCE))
            return stringList[1].replace(PHOTOS, "").replace(PHOTO, "")
    }
    return null
}

/**
 * Correct all media url's with their full url's in the given html code.
 *
 * @return the html code with corrected media url's.
 */
internal fun Document?.correctRepoMediaUrl(): Document? =
    this?.let {
        val toRemove = mutableListOf<Element>()

        select(IMG).forEach {
            val dataOriginal = it.attr(DATA_ORIGINAL)
            val urlLink = if (!dataOriginal.isNullOrBlank()) dataOriginal else it.attr(SRC)
            val fullUrl = urlLink.toFullUrl(REPORTERRE_BASE_URL)
            val parent = it.parent()

            it.attr(SRC, fullUrl)
            it.attr(DATA_ORIGINAL, fullUrl)

            if (!parent.`is`(a)) {
                parent.appendElement(a).attr(HREF, fullUrl).append(it.outerHtml())
                toRemove.add(it)
            }
        }
        toRemove.forEach { it.remove() }

        select(AUDIO).forEach { it.attrToFullUrl(SRC, REPORTERRE_BASE_URL) }
        this
    }

/**
 * Returns the concatenated css url list into the elements.
 *
 * @return the concatenated css url list into the elements.
 */
internal fun Elements.getCssUrl(): String =
    concatenateStringFromMutableList(
        this.getMatchAttr(REL, STYLE_SHEET)
            .map { it.attr(HREF).toFullUrl(REPORTERRE_BASE_URL) }
            .toMutableList()
    )