package org.desperu.independentnews.utils

import org.desperu.independentnews.R
import org.desperu.independentnews.models.database.SourcePage
import org.desperu.independentnews.models.database.SourceWithData
import org.desperu.independentnews.utils.Utils.getDomainFromUrl

/**
 * SourceUtils object witch provide utils functions for sources.
 */
object SourcesUtils {

    /**
     * Returns the source name from the given source url.
     *
     * @param url the url from which retrieved the source name.
     *
     * @return the source name from the given source url.
     */
    internal fun getSourceNameFromUrl(url: String) = when (getDomainFromUrl(url)) {
        getDomainFromUrl(BASTAMAG_BASE_URL) -> BASTAMAG
        getDomainFromUrl(REPORTERRE_BASE_URL) -> REPORTERRE
        getDomainFromUrl(MULTINATIONALES_BASE_URL) -> MULTINATIONALES
        else -> throw IllegalArgumentException("Source name not found from url : $url")
    }

    // -----------------
    // LOGO
    // -----------------

    /**
     * Returns the unique identifier of the source mini logo drawable.
     *
     * @param sourceName the source name for which retrieved the mini logo id.
     *
     * @return the unique identifier of the source mini logo drawable.
     */
    internal fun getMiniLogoId(sourceName: String) = when(sourceName) {
        BASTAMAG -> R.drawable.logo_mini_bastamag
        REPORTERRE -> R.drawable.logo_mini_reporterre
        MULTINATIONALES -> R.drawable.logo_mini_multinationales
        else -> throw IllegalArgumentException("Source name not found : $sourceName")
    }

    /**
     * Returns the unique identifier of the source logo drawable.
     *
     * @param sourceName the source name for which retrieved the logo id.
     *
     * @return the unique identifier of the source logo drawable.
     */
    internal fun getLogoId(sourceName: String) = when(sourceName) {
        BASTAMAG -> R.drawable.logo_bastamag
        REPORTERRE -> R.drawable.logo_reporterre
        MULTINATIONALES -> R.drawable.logo_multinationales
        else -> throw IllegalArgumentException("Source name not found : $sourceName")
    }

    // -----------------
    // BACKGROUND COLOR
    // -----------------

    /**
     * Returns the unique identifier of the source background color.
     *
     * @param sourceName the source name for which retrieved the background color id.
     *
     * @return the unique identifier of the source background color.
     */
    internal fun getBackgroundColorId(sourceName: String) = when(sourceName) {
        BASTAMAG -> R.color.bastamag_background
        REPORTERRE -> R.color.reporterre_background
        MULTINATIONALES -> R.color.multinationales_background
        else -> throw IllegalArgumentException("Source name not found : $sourceName")
    }

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