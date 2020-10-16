package org.desperu.independentnews.utils

import android.content.Context
import androidx.annotation.ArrayRes
import org.desperu.independentnews.R
import org.desperu.independentnews.utils.Utils.concatenateStringFromMutableList

/**
 * FilterUtils object witch provide utils functions for filters.
 */
// TODO to comment and write test
object FilterUtils {

    internal val filterViewsId = arrayOf(
        // Filter Sources
        R.id.filter_bastamag, R.id.filter_reporterre, R.id.filter_multinational,

        // Filter Themes
        R.id.filter_ecology, R.id.filter_social,
        R.id.filter_economy, R.id.filter_politic,
        R.id.filter_international, R.id.filter_energy,
        R.id.filter_agriculture, R.id.filter_health,

        // Filter Sections
        R.id.filter_alternatives, R.id.filter_decrypt,
        R.id.filter_resist, R.id.filter_discuss,
        R.id.filter_chronicle

        // Filter Dates
    )

    internal fun getFilterValue(context: Context, id: Int): String = when (id) {
        // Filter Sources
        R.id.filter_bastamag -> context.resources.getString(R.string.filter_layout_sources_bastamag)
        R.id.filter_reporterre -> context.resources.getString(R.string.filter_layout_sources_reporterre)
        R.id.filter_multinational -> context.resources.getString(R.string.filter_layout_sources_multinational)

        // Filter Themes
        R.id.filter_ecology -> getStringFromArray(context, R.array.filter_ecology)
        R.id.filter_social -> getStringFromArray(context, R.array.filter_social)
        R.id.filter_economy -> getStringFromArray(context, R.array.filter_economy)
        R.id.filter_politic -> getStringFromArray(context, R.array.filter_politic)
        R.id.filter_international -> getStringFromArray(context, R.array.filter_international)
        R.id.filter_energy -> getStringFromArray(context, R.array.filter_energy)
        R.id.filter_agriculture -> getStringFromArray(context, R.array.filter_agriculture)
        R.id.filter_health -> getStringFromArray(context, R.array.filter_health)

        // Filter Sections
        R.id.filter_alternatives -> getStringFromArray(context, R.array.filter_alternatives)
        R.id.filter_decrypt -> getStringFromArray(context, R.array.filter_decrypt)
        R.id.filter_resist -> getStringFromArray(context, R.array.filter_resist)
        R.id.filter_discuss -> getStringFromArray(context, R.array.filter_discuss)
        R.id.filter_chronicle -> getStringFromArray(context, R.array.filter_chronicle)

        // Filter Dates

        else -> ""
    }

    private fun getStringFromArray(context: Context, @ArrayRes arrayId: Int) =
        concatenateStringFromMutableList(context.resources.getStringArray(arrayId).toMutableList())
}