package org.desperu.independentnews.extension

import org.jsoup.nodes.Element

/**
 * Safely return the child element at the specified position in this list.
 *
 * @param index the index of the child element to return.
 *
 * @return the child element at the specified position in this list.
 */
internal fun Element?.getChild(index: Int): Element? =
    if (this != null && !allElements.isNullOrEmpty())
        child(index)
    else
        null