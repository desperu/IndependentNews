package org.desperu.independentnews.utils

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import io.mockk.every
import io.mockk.mockk
import org.desperu.independentnews.R
import org.desperu.independentnews.di.module.serviceModule
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.models.database.SourcePage
import org.desperu.independentnews.models.database.SourceWithData
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.utils.SourcesUtils.getAdditionalCss
import org.desperu.independentnews.utils.SourcesUtils.getBackgroundColorId
import org.desperu.independentnews.utils.SourcesUtils.getButtonLinkColor
import org.desperu.independentnews.utils.SourcesUtils.getLogoId
import org.desperu.independentnews.utils.SourcesUtils.getMiniLogoId
import org.desperu.independentnews.utils.SourcesUtils.getSourceNameFromUrl
import org.desperu.independentnews.utils.SourcesUtils.getSourceTextZoom
import org.desperu.independentnews.utils.SourcesUtils.getSourceTransitionName
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.test.KoinTest
import org.koin.test.get

/**
 * Source Utils class test, to check that all utils functions work as needed.
 */
class SourceUtilsTest : KoinTest {

    // FOR DATA
    private val sourceList = listOf(BASTAMAG, REPORTERRE, MULTINATIONALES)
    private var mockContext: Context = mockk()
    private lateinit var resources: ResourceService

    @Before
    fun before() {
        startKoin {
            androidContext(mockContext)
            modules(serviceModule)
        }

        resources = get()

        every { resources.getString(R.string.animation_source_list_to_detail_container) } returns "ImageAnimationSourceListToDetailContainer"
        every { resources.getString(R.string.animation_source_list_to_detail_image) } returns  "ImageAnimationSourceListToDetailImage"
    }

    @After
    fun after() {
        unloadKoinModules(listOf(serviceModule))
        stopKoin()
    }

    @Test
    fun given_sourceList_When_getSourceNameFromUrl_Then_checkResult() {
        val expectedList = listOf(BASTAMAG, REPORTERRE, MULTINATIONALES)

        val outputList = SOURCE_LIST.map { getSourceNameFromUrl(it.url) }

        outputList.forEachIndexed { index, output ->
            assertEquals(expectedList[index], output)
        }
    }

    @Test
    fun given_notSourceUrl_When_getSourceNameFromUrl_Then_checkError() {
        val url = "https://www.delachauxetniestle.com/livre/sauvons-la-biodiversite"
        val expected = "Source name not found from url : $url"

        val output = try { getSourceNameFromUrl(url) }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_sourceList_When_getAdditionalCss_Then_checkResult() {
        val expectedList = listOf(
            BASTA_ADD_CSS,
            REPORTERRE_ADD_CSS,
            MULTI_ADD_CSS,
            BASTA_ADD_CSS,
            REPORTERRE_SOURCE_ADD_CSS,
            MULTI_ADD_CSS
        )

        val testList = mutableListOf<String>()
        testList.addAll(SOURCE_LIST.map { it.name })
        testList.addAll(SOURCE_LIST.map { it.name + SOURCE })

        val outputList = testList.map { getAdditionalCss(it) }
        outputList.forEachIndexed { index, output ->
            assertEquals(expectedList[index], output)
        }
    }

    @Test
    fun given_notSourceUrl_When_getAdditionalCss_Then_checkError() {
        val sourceName = "Wrong Source"
        val expected = "Source name not found : $sourceName"

        val output = try { getAdditionalCss(sourceName) }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_sourceList_When_getSourceTextZoom_Then_checkResult() {
        val expectedList = listOf(
            ALTER_MEDIA_TEXT_ZOOM,  // Bastamag source name
            0,                      // Reporterre source name
            ALTER_MEDIA_TEXT_ZOOM,  // Multinationales source name
            ALTER_MEDIA_TEXT_ZOOM,  // Bastamag url
            0,                      // Reporterre url
            ALTER_MEDIA_TEXT_ZOOM,  // Multinationales url
            0,                      // Random url
        )

        val mapTest = mutableMapOf<String, String>()
        SOURCE_LIST.forEachIndexed { index, source -> mapTest["<html>$index"] = source.name }
        mapTest["https://www.bastamag.net/Jeux-olympiques-2024"] = ""
        mapTest["https://reporterre.net/Le-mur-anti-migrants-aux-Etats-Unis-est-un-fleau-pour-la-vie-sauvage"] = ""
        mapTest["https://multinationales.org/La-demesure-des-remunerations-patronales-et-ce-qu-il-y-a-derriere"] = ""
        mapTest["https://www.delachauxetniestle.com/livre/sauvons-la-biodiversite"] = ""

        val outputList = mapTest.map { getSourceTextZoom(it.key, it.value) }

        outputList.forEachIndexed { index, output ->
            assertEquals(expectedList[index], output)
        }
    }

    @Test
    fun given_notSourceUrl_When_getSourceTextZoom_Then_checkResult() {
        val emptyUrl = ""
        val sourceName = "Wrong Source"
        val expected = 0

        val output = getSourceTextZoom(emptyUrl, sourceName)

        assertEquals(expected, output)
    }

    @Test
    fun given_viewList_When_getSourceTransitionName_Then_checkResult() {
        val expectedList = listOf(
            R.string.animation_source_list_to_detail_container,
            R.string.animation_source_list_to_detail_image
        )

        val inputList = listOf(mockk<CardView>(), mockk<ImageView>())
        val outputList = inputList.mapIndexed { index, view -> getSourceTransitionName(view, index) }

        outputList.forEachIndexed { index, output ->
            assertEquals(resources.getString(expectedList[index]) + index, output)
        }
    }

    @Test
    fun given_wrongViewList_When_getSourceTransitionName_Then_checkError() {
        val wrongViewType = TextView(mockContext)
        val expected = "View type not found : $wrongViewType"

        val output = try { getSourceTransitionName(wrongViewType, 0) }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_sourceList_When_getMiniLogoId_Then_checkResult() {
        val expectedList = listOf(
            R.drawable.logo_mini_bastamag,
            R.drawable.logo_mini_reporterre,
            R.drawable.logo_mini_multinationales
        )

        val outputList = sourceList.map { getMiniLogoId(it) }

        outputList.forEachIndexed { index, output ->
            assertEquals(expectedList[index], output)
        }
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
        val expectedList = listOf(
            R.drawable.logo_bastamag,
            R.drawable.logo_reporterre,
            R.drawable.logo_multinationales
        )

        val outputList = sourceList.map { getLogoId(it) }

        outputList.forEachIndexed { index, output ->
            assertEquals(expectedList[index], output)
        }
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
        val expectedList = listOf(
            R.color.bastamag_background,
            R.color.reporterre_background,
            R.color.multinationales_background
        )

        val outputList = sourceList.map { getBackgroundColorId(it) }

        outputList.forEachIndexed { index, output ->
            assertEquals(expectedList[index], output)
        }
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