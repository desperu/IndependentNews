package org.desperu.independentnews.utils

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.desperu.independentnews.R
import org.desperu.independentnews.utils.FilterUtils.getFilterValue
import org.desperu.independentnews.utils.FilterUtils.parseSelectedMap
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Filter Utils class test, to check that all utils functions works as needed.
 */
class FilterUtilsTest {

    private val mockContext = mockk<Context>()
    private val filterBastamag = "Bastamag"
    private val filterEcology = arrayOf("ecologie", "écologie", "climat", "nature", "pollutions")
    private val ecologyString = "ecologie, écologie, climat, nature, pollutions"
    private val filterEcologyCorrected = arrayOf("ecologie", "écologie", "climat", "nature", "pollution")
    private val filterInternational = arrayOf("International")
    private val internationalString = "International"
    private val filterResist = arrayOf("reportage", "résister")
    private val resistString = "reportage, résister"
    private val filterDiscuss = arrayOf("etretient", "débattre")
    private val discussString = "etretient, débattre"

    @Before
    fun before() {
        every { mockContext.resources.getString(R.string.filter_layout_sources_bastamag) } returns filterBastamag
        every { mockContext.resources.getStringArray(R.array.filter_ecology) } returns filterEcology

    }

    @Test
    fun given_filterViewId_When_getFilterValue_Then_checkResult() {
        val outputString = getFilterValue(mockContext, R.id.filter_bastamag)
        assertEquals(filterBastamag, outputString)

        val outputStringFromArray = getFilterValue(mockContext, R.id.filter_ecology)
        assertEquals(ecologyString, outputStringFromArray)
    }

    @Test
    fun given_selectedMap_When_parseSelectedMap_Then_checkResult() {
        val expected = mapOf(
            Pair(SOURCES, mutableListOf(filterBastamag, REPORTERRE)),
            Pair(THEMES, mutableListOf(filterEcology.toList(), filterInternational.toList()).flatten()),
            Pair(SECTIONS, mutableListOf(filterResist.toList(), filterDiscuss.toList()).flatten()),
            Pair(DATES, mutableListOf("1602626400000", "1602799200000")),
            Pair(CATEGORIES, mutableListOf(
                filterEcologyCorrected.toList(),
                filterInternational.toList(),
                filterResist.toList(),
                filterDiscuss.toList()).flatten())
        )

        val selectedMap = mapOf(
            Pair(SOURCES, mutableListOf(filterBastamag, REPORTERRE)),
            Pair(THEMES, mutableListOf(ecologyString, internationalString)),
            Pair(SECTIONS, mutableListOf(resistString, discussString)),
            Pair(DATES, mutableListOf("14/10/2020", "16/10/2020")),
            Pair(CATEGORIES, mutableListOf(ecologyString, internationalString, resistString, discussString))
        )

        val output = runBlocking { parseSelectedMap(selectedMap, listOf(BASTAMAG_SOURCE, REPORTERRE_SOURCE)) }

        assertEquals(expected, output)
    }
}