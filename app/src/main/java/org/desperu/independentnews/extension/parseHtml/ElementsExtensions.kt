package org.desperu.independentnews.extension.parseHtml

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

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