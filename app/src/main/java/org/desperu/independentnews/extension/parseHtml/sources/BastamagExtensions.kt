package org.desperu.independentnews.extension.parseHtml.sources

import org.desperu.independentnews.extension.parseHtml.attrToFullUrl
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.getTagList
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.concatenateStringFromMutableList
import org.desperu.independentnews.utils.Utils.deConcatenateStringToMutableList
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Add notes at the end of the article body.
 *
 * @return the article body with notes at the end.
 */
internal fun Document?.addNotes(): Document? = // Used in multinationales too
    this?.let {
        val notes = getTagList(DIV).getMatchAttr(CLASS, NOTES).getOrNull(0)?.outerHtml()

        notes?.let { select(BODY).append(it) }
        this
    }

/**
 * Correct all media url's with their full url's in the html code.
 *
 * @param baseUrl the base url of the current jsoup document.
 *
 * @return the html code with corrected media url's.
 */
internal fun Document?.correctBastaMediaUrl(baseUrl: String): Document? = // Used in multinationales too
    this?.let {
        val toRemove = mutableListOf<Element>()

        select(IMG).forEach {
            if (it.attr(CLASS) == PUCE) { it.remove(); return@forEach }

            correctSrcSetUrls(it)
            it.attrToFullUrl(SRC, baseUrl)

            val parent = it.parent()
            when {
                // General image structure, wrap into Picture tag
                parent.`is`(PICTURE) -> {
                    parent.parent().appendElement(a).attr(HREF, it.attr(SRC)).append(it.outerHtml())
                    toRemove.add(parent)
                }
                // Source page "Who are us ?", home source page
                parent.`is`(SPAN) && parent.attr(CLASS) == PHOTO_ATTR_VAL -> {
                    parent.parent().attrToFullUrl(HREF, baseUrl)
                }
                // Source page "Basta a tool for those", button "They support us"
                parent.`is`(SPAN) && parent.attr(CLASS) == SPIP_DOCUMENT -> {
                    parent.appendElement(a).attr(HREF, it.attr(SRC)).append(it.outerHtml())
                    toRemove.add(it)
                }
            }
        }
        toRemove.forEach { it.remove() }

        select(SOURCE_TAG).forEach { correctSrcSetUrls(it) }
        select(AUDIO).forEach { it.attrToFullUrl(SRC, baseUrl) }
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