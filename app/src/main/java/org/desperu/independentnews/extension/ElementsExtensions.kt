package org.desperu.independentnews.extension

import org.desperu.independentnews.utils.PHOTO
import org.desperu.independentnews.utils.PHOTOS
import org.desperu.independentnews.utils.SOURCE
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
 * Safely return the element at the specified position in this list.
 *
 * @param index the index of the element to return.
 *
 * @return the element at the specified position in this list.
 */
internal fun Elements?.getIndex(index: Int): Element? =
    if (!this.isNullOrEmpty() && size > index)
        get(index)
    else
        null

/**
 * Return the list of element that match the given attr/value couple.
 *
 * @param attr the attribute to find.
 * @param value the value of the attribute to check.
 *
 * @return the list of element that match the given attr/value couple.
 */
internal fun Elements?.getMatchAttr(attr: String, value: String): Elements {
    val elements = Elements()
    this?.forEach { element ->
        if (element.attr(attr) == value)
            elements.add(element)
    }
    return elements
}