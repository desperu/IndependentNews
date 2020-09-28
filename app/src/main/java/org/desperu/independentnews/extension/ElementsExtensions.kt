package org.desperu.independentnews.extension

import org.desperu.independentnews.utils.PHOTO
import org.desperu.independentnews.utils.PHOTOS
import org.desperu.independentnews.utils.SOURCE
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