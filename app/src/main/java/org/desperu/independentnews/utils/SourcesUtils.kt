package org.desperu.independentnews.utils

import org.desperu.independentnews.R
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.models.SourceWithData

/**
 * SourceUtils object witch provide utils functions for sources.
 */
object SourcesUtils {

    // -----------------
    // BUTTON LINK
    // -----------------

    /**
     * Returns the button link background color.
     *
     * @param sourceWithData the source with data that contains the source page link
     *                       to determine background color.
     *
     * @return the button link background color.
     */
    internal fun getButtonLinkColor(sourceWithData: SourceWithData) =
        when(sourceWithData.source.name) {
            BASTAMAG -> getBastaButtonLinkColor(sourceWithData.sourcePages.getOrNull(0))
            REPORTERRE -> R.color.reporterre_button_link
            else -> throw IllegalArgumentException("Source name not found : ${sourceWithData.source.name}")
        }

    /**
     * Returns the bastamag button link background color.
     *
     * @param sourcePage the source page link to determine background color.
     *
     * @return the bastamag button link background color.
     */
    private fun getBastaButtonLinkColor(sourcePage: SourcePage?) =
        when(sourcePage?.position) {
            1 -> R.color.bastamag_button_link_contact
            2 -> R.color.bastamag_button_link_support
            3 -> R.color.bastamag_button_link_economy
            4 -> R.color.bastamag_button_link_most_viewed
            5 -> R.color.bastamag_button_link_cgu
            else -> android.R.color.holo_green_light
        }
}