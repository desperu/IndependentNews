package org.desperu.independentnews.extension.parseHtml.sources

import org.desperu.independentnews.extension.parseHtml.attrToFullUrl
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.utils.*
import org.jsoup.nodes.Document
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

        select(IMG).forEach {
            val dataOriginal = it.attr(DATA_ORIGINAL)
            val urlLink = if (!dataOriginal.isNullOrBlank()) dataOriginal else it.attr(SRC)
            it.attr(SRC, urlLink.toFullUrl(REPORTERRE_BASE_URL))
        }

        select(AUDIO).forEach { it.attrToFullUrl(SRC, REPORTERRE_BASE_URL) }
        this
    }