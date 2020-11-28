package org.desperu.independentnews.extension.parseHtml.sources

import org.desperu.independentnews.extension.parseHtml.attrToFullUrl
import org.desperu.independentnews.extension.parseHtml.getIndex
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.concatenateStringFromMutableList
import org.desperu.independentnews.utils.Utils.deConcatenateStringToMutableList
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Correct all media url's with their full url's in the html code.
 *
 * @return the html code with corrected media url's.
 */
internal fun Document?.correctBastaMediaUrl(): Document? =
    this?.let {
        val toRemove = mutableListOf<Element>()

        select(IMG).forEach {
            if (it.attr(CLASS) == PUCE) { it.remove(); return@forEach }

            correctSrcSetUrls(it)
            it.attrToFullUrl(SRC, BASTAMAG_BASE_URL)

            val parent = it.parent()
            if (parent.`is`(PICTURE)) {
                parent.parent().appendElement(a).attr(HREF, it.attr(SRC)).append(it.outerHtml())
                toRemove.add(parent)
            } else {
                parent.appendElement(a).attr(HREF, it.attr(SRC)).append(it.outerHtml())
                toRemove.add(it)
            }
        }
        toRemove.forEach { it.remove() }

        select(SOURCE_TAG).forEach { correctSrcSetUrls(it) }
        select(AUDIO).forEach { it.attrToFullUrl(SRC, BASTAMAG_BASE_URL) }
        this
    }

/**
 * Correct the srcset url value of the given element with their full url.
 *
 * @param element the element for which correct url's.
 */
private fun correctSrcSetUrls(element: Element) {
    val srcSetList = deConcatenateStringToMutableList(element.attr(SRCSET))
    val correctedList = srcSetList.map { it.toFullUrl(BASTAMAG_BASE_URL) }
    element.attr(SRCSET, concatenateStringFromMutableList(correctedList.toMutableList()))
}

/**
 * Set main css id to apply css style to the article body.
 *
 * @return the article with main css id set.
 */
internal fun Document?.setMainCssId(): Document? =
    this?.let {
        select(BODY).getIndex(0)?.attr(CLASS, MAIN_CONTAINER)
        this
    }