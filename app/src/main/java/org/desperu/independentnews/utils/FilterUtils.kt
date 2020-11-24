package org.desperu.independentnews.utils

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.IdRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.R
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.utils.Utils.concatenateStringFromMutableList
import org.desperu.independentnews.utils.Utils.deConcatenateStringToMutableList
import org.desperu.independentnews.utils.Utils.intStringToDate

/**
 * FilterUtils object witch provide utils functions for filters. Needed to get filters value used to
 * filter the article list, from the selected filters by the user.
 */
object FilterUtils {

    /**
     * Array of unique identifiers of filters view. Used to enable filters.
     */
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
    )

    /**
     * Get the filter value  from the view id.
     *
     * @param context the context from this function is called.
     * @param id the unique identifier of the resource view.
     *
     * @return the filter value in string format.
     */
    internal fun getFilterValue(context: Context,@IdRes id: Int): String = when (id) {
        // Filter Sources
        R.id.filter_bastamag -> context.resources.getString(R.string.filter_layout_sources_bastamag)
        R.id.filter_reporterre -> context.resources.getString(R.string.filter_layout_sources_reporterre)
        R.id.filter_multinational -> context.resources.getString(R.string.filter_layout_sources_multinational)

        // Filter Themes
        R.id.filter_ecology -> getStringFromArrayRes(context, R.array.filter_ecology)
        R.id.filter_social -> getStringFromArrayRes(context, R.array.filter_social)
        R.id.filter_economy -> getStringFromArrayRes(context, R.array.filter_economy)
        R.id.filter_politic -> getStringFromArrayRes(context, R.array.filter_politic)
        R.id.filter_international -> getStringFromArrayRes(context, R.array.filter_international)
        R.id.filter_energy -> getStringFromArrayRes(context, R.array.filter_energy)
        R.id.filter_agriculture -> getStringFromArrayRes(context, R.array.filter_agriculture)
        R.id.filter_health -> getStringFromArrayRes(context, R.array.filter_health)

        // Filter Sections
        R.id.filter_alternatives -> getStringFromArrayRes(context, R.array.filter_alternatives)
        R.id.filter_decrypt -> getStringFromArrayRes(context, R.array.filter_decrypt)
        R.id.filter_resist -> getStringFromArrayRes(context, R.array.filter_resist)
        R.id.filter_discuss -> getStringFromArrayRes(context, R.array.filter_discuss)
        R.id.filter_chronicle -> getStringFromArrayRes(context, R.array.filter_chronicle)

        else -> ""
    }

    /**
     * Get string from array string resource, with unique identifier of the array resource.
     *
     * @param context the context from this method is called.
     * @param arrayId the unique identifier of the array resource.
     *
     * @return the array value in string.
     */
    private fun getStringFromArrayRes(context: Context, @ArrayRes arrayId: Int): String =
        concatenateStringFromMutableList(context.resources.getStringArray(arrayId).toMutableList())

    /**
     * Return the parsed select map, for each filters.
     *
     * @param selectedMap the selected map to parse.
     *
     * @return the parsed select map filters.
     */
    internal suspend fun parseSelectedMap(
        selectedMap: Map<Int, MutableList<String>>,
        sources: List<Source>
    ): Map<Int, List<String>> = withContext(Dispatchers.Default) {

        val parsedMap = mutableMapOf<Int, List<String>>().withDefault { listOf() }

        val sourcesList = selectedMap[SOURCES]
        parsedMap[SOURCES] = if (!sourcesList.isNullOrEmpty()) sourcesList else sources.map { it.name }

        parsedMap[THEMES] =
            deConcatenateStringToMutableList(
                concatenateStringFromMutableList(selectedMap.getValue(THEMES))).filterNot { it.isBlank() }

        parsedMap[SECTIONS] =
            deConcatenateStringToMutableList(
                concatenateStringFromMutableList(selectedMap.getValue(SECTIONS))).filterNot { it.isBlank() }

        val defaultDates = listOf(Long.MIN_VALUE.toString(), Long.MAX_VALUE.toString())
        parsedMap[DATES] =
            if (selectedMap.getValue(DATES).isNotEmpty())
                selectedMap.getValue(DATES).mapIndexed { index, date -> // TODO error with end date when search day - 1
                    (intStringToDate(date)?.time ?: defaultDates[index]).toString()
                }
            else
                defaultDates

        parsedMap[CATEGORIES] = listOf(parsedMap.getValue(THEMES), parsedMap.getValue(SECTIONS))
            .flatten()
            .map { it.removeSuffix("s") }
            .filterNot { it.isBlank() }

        parsedMap
    }
}