package org.desperu.independentnews.utils

import org.desperu.independentnews.R
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.models.SourceWithData
import org.desperu.independentnews.utils.SourcesUtils.getButtonLinkColor
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.IllegalArgumentException

/**
 * Source Utils class test, to check that all utils functions work as needed.
 */
class SourceUtilsTest {

    @Test
    fun given_bastamagSWDP0_When_getButtonLinkColor_Then_checkResult() {
        val expected = android.R.color.holo_green_light

        val sourceWithData = SourceWithData(Source(name = BASTAMAG), listOf(SourcePage(position = 0)))
        val output = getButtonLinkColor(sourceWithData)

        assertEquals(expected, output)
    }

    @Test
    fun given_bastamagSWDP1_When_getButtonLinkColor_Then_checkResult() {
        val expected = R.color.bastamag_button_link_contact

        val sourceWithData = SourceWithData(Source(name = BASTAMAG), listOf(SourcePage(position = 1)))
        val output = getButtonLinkColor(sourceWithData)

        assertEquals(expected, output)
    }

    @Test
    fun given_bastamagSWDP2_When_getButtonLinkColor_Then_checkResult() {
        val expected = R.color.bastamag_button_link_support

        val sourceWithData = SourceWithData(Source(name = BASTAMAG), listOf(SourcePage(position = 2)))
        val output = getButtonLinkColor(sourceWithData)

        assertEquals(expected, output)
    }

    @Test
    fun given_bastamagSWDP3_When_getButtonLinkColor_Then_checkResult() {
        val expected = R.color.bastamag_button_link_economy

        val sourceWithData = SourceWithData(Source(name = BASTAMAG), listOf(SourcePage(position = 3)))
        val output = getButtonLinkColor(sourceWithData)

        assertEquals(expected, output)
    }

    @Test
    fun given_bastamagSWDP4_When_getButtonLinkColor_Then_checkResult() {
        val expected = R.color.bastamag_button_link_most_viewed

        val sourceWithData = SourceWithData(Source(name = BASTAMAG), listOf(SourcePage(position = 4)))
        val output = getButtonLinkColor(sourceWithData)

        assertEquals(expected, output)
    }

    @Test
    fun given_bastamagSWDP5_When_getButtonLinkColor_Then_checkResult() {
        val expected = R.color.bastamag_button_link_cgu

        val sourceWithData = SourceWithData(Source(name = BASTAMAG), listOf(SourcePage(position = 5)))
        val output = getButtonLinkColor(sourceWithData)

        assertEquals(expected, output)
    }

    @Test
    fun given_reporterreSWD_When_getButtonLinkColor_Then_checkResult() {
        val expected = R.color.reporterre_button_link

        val sourceWithData = SourceWithData(Source(name = REPORTERRE), listOf())
        val output = getButtonLinkColor(sourceWithData)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongSWD_When_getButtonLinkColor_Then_checkResult() {
        val expected = "Source name not found : wrong"

        val sourceWithData = SourceWithData(Source(name = "wrong"), listOf())
        val output = try { getButtonLinkColor(sourceWithData) }
                     catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }
}