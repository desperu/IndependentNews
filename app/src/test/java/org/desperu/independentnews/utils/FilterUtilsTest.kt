package org.desperu.independentnews.utils

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.desperu.independentnews.R
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.utils.FilterUtils.getFilterValue
import org.desperu.independentnews.utils.FilterUtils.parseSelectedMap
import org.desperu.independentnews.utils.Utils.intStringToDate
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Filter Utils class test, to check that all utils functions works as needed.
 */
class FilterUtilsTest {

    // FOR DATA
    private val mockContext = mockk<Context>()
    private val filterEcology = arrayOf("ecologie", "écologie", "climat", "nature", "pollutions")
    private val ecologyString = "ecologie, écologie, climat, nature, pollutions"
    private val filterEcologyCorrected = arrayOf("ecologie", "écologie", "climat", "nature", "pollution")
    private val filterInternational = arrayOf("International")
    private val internationalString = "International"
    private val filterResist = arrayOf("reportage", "résister")
    private val resistString = "reportage, résister"
    private val filterDiscuss = arrayOf("etretient", "débattre")
    private val discussString = "etretient, débattre"
    private val startDate = "14/10/2020"
    private val endDate = "16/10/2020"

    @Before
    fun before() {
        every { mockContext.resources.getString(R.string.filter_layout_sources_bastamag) } returns BASTAMAG
        every { mockContext.resources.getStringArray(R.array.filter_ecology) } returns filterEcology

    }

    @Test
    fun given_filterViewId_When_getFilterValue_Then_checkResult() {
        val outputString = getFilterValue(mockContext, R.id.filter_bastamag)
        assertEquals(BASTAMAG, outputString)

        val outputStringFromArray = getFilterValue(mockContext, R.id.filter_ecology)
        assertEquals(ecologyString, outputStringFromArray)
    }

    @Test
    fun given_selectedMap_When_parseSelectedMap_Then_checkResult() {
        val expected = mapOf(
            Pair(SOURCES, mutableListOf("1", "2")),
            Pair(THEMES, mutableListOf(filterEcology.toList(), filterInternational.toList()).flatten()),
            Pair(SECTIONS, mutableListOf(filterResist.toList(), filterDiscuss.toList()).flatten()),
            Pair(DATES, mutableListOf(
                intStringToDate(startDate)?.time.toString(),
                intStringToDate(endDate)?.time?.plus(86400000L).toString())
            ),
            Pair(CATEGORIES, mutableListOf(
                filterEcologyCorrected.toList(),
                filterInternational.toList(),
                filterResist.toList(),
                filterDiscuss.toList()).flatten())
        )

        val selectedMap = mapOf(
            Pair(SOURCES, mutableListOf(BASTAMAG, REPORTERRE)),
            Pair(THEMES, mutableListOf(ecologyString, internationalString)),
            Pair(SECTIONS, mutableListOf(resistString, discussString)),
            Pair(DATES, mutableListOf(startDate, endDate)),
            Pair(CATEGORIES, mutableListOf(ecologyString, internationalString, resistString, discussString))
        )

        val basta = Source(1L, BASTAMAG_SOURCE.name, BASTAMAG_SOURCE.url, BASTAMAG_SOURCE.isEnabled)
        val reporterre = Source(2L, REPORTERRE_SOURCE.name, REPORTERRE_SOURCE.url, REPORTERRE_SOURCE.isEnabled)

        val output = runBlocking { parseSelectedMap(selectedMap, listOf(basta, reporterre)) }

        assertEquals(expected, output)
    }
}