package org.desperu.independentnews.extension.parseHtml

import org.jsoup.nodes.Element

/**
 * Safely return the child element at the specified position in this list.
 *
 * @param index the index of the child element to return.
 *
 * @return the child element at the specified position in this list.
 */
internal fun Element?.getChild(index: Int): Element? =
    if (this != null && allElements.size > index)
        child(index)
    else
        null

/**
 * Convert the attribute value (local url) of the element to a full url, if it's not already one.
 *
 * @param attr the attribute to convert the url.
 * @param baseUrl the base url of the web site from came the local url.
 */
internal fun Element.attrToFullUrl(attr: String, baseUrl: String) {
    val value = this.attr(attr)
    if (!value.isNullOrBlank())
        this.attr(attr, value.toFullUrl(baseUrl))
}