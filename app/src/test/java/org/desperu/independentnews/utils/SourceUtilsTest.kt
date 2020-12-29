package org.desperu.independentnews.utils

import org.desperu.independentnews.R
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.models.database.SourcePage
import org.desperu.independentnews.models.database.SourceWithData
import org.desperu.independentnews.utils.SourcesUtils.getBackgroundColorId
import org.desperu.independentnews.utils.SourcesUtils.getButtonLinkColor
import org.desperu.independentnews.utils.SourcesUtils.getLogoId
import org.desperu.independentnews.utils.SourcesUtils.getMiniLogoId
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.IllegalArgumentException

/**
 * Source Utils class test, to check that all utils functions work as needed.
 */
class SourceUtilsTest {

    @Test
    fun given_bastamag_When_getMiniLogoId_Then_checkResult() {
        val expected = R.drawable.logo_mini_bastamag

        val output = getMiniLogoId(BASTAMAG)

        assertEquals(expected, output)
    }

    @Test
    fun given_reporterre_When_getMiniLogoId_Then_checkResult() {
        val expected = R.drawable.logo_mini_reporterre

        val output = getMiniLogoId(REPORTERRE)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongName_When_getMiniLogoId_Then_checkError() {
        val expected = "Source name not found : wrong name"

        val output = try { getMiniLogoId("wrong name") }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_bastamag_When_getLogoId_Then_checkResult() {
        val expected = R.drawable.logo_bastamag

        val output = getLogoId(BASTAMAG)

        assertEquals(expected, output)
    }

    @Test
    fun given_reporterre_When_getLogoId_Then_checkResult() {
        val expected = R.drawable.logo_reporterre

        val output = getLogoId(REPORTERRE)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongName_When_getLogoId_Then_checkError() {
        val expected = "Source name not found : wrong name"

        val output = try { getLogoId("wrong name") }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_bastamag_When_getBackgroundColorId_Then_checkResult() {
        val expected = R.color.bastamag_background

        val output = getBackgroundColorId(BASTAMAG)

        assertEquals(expected, output)
    }

    @Test
    fun given_reporterre_When_getBackgroundColorId_Then_checkResult() {
        val expected = R.color.reporterre_background

        val output = getBackgroundColorId(REPORTERRE)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongName_When_getBackgroundColorId_Then_checkError() {
        val expected = "Source name not found : wrong name"

        val output = try { getBackgroundColorId("wrong name") }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

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