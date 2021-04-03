package org.desperu.independentnews.utils

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import org.desperu.independentnews.R
import org.desperu.independentnews.di.module.serviceModule
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.utils.FabsMenuUtils.getSubFabIcon
import org.desperu.independentnews.utils.FabsMenuUtils.getSubFabId
import org.desperu.independentnews.utils.FabsMenuUtils.getSubFabLabel
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
import java.lang.Exception

/**
 * Fabs Menu Utils class test, to check that all utils functions work as needed.
 */
class FabsMenuUtilsTest : KoinTest {

    // FOR DATA
    private val mockContext: Context = mockk()
    private lateinit var resources: ResourceService

    @Before
    fun before() {
        startKoin {
            androidContext(mockContext)
            modules(serviceModule)
        }

        resources = get()

        every { resources.getString(R.string.sub_fab_label_min_text) } returns "Minus text size"
        every { resources.getString(R.string.sub_fab_label_up_text) } returns "Up text size"
        every { resources.getString(R.string.sub_fab_label_star) } returns "Add favorite"
        every { resources.getString(R.string.sub_fab_label_remove_star) } returns "Remove Favorite"
        every { resources.getString(R.string.sub_fab_label_pause) } returns "Pause reading"
        every { resources.getString(R.string.sub_fab_label_remove_pause) } returns "Remove Pause"
        every { resources.getString(R.string.sub_fab_label_home) } returns "Back home"
    }

    @After
    fun after() {
        unloadKoinModules(serviceModule)
        stopKoin()
    }

    @Test
    fun given_subFabKey_When_getSubFabId_Then_checkResult() {
        val testMap = mapOf(
            Pair(SUB_FAB_MIN_TEXT, R.id.fab_minus_text),
            Pair(SUB_FAB_UP_TEXT, R.id.fab_up_text),
            Pair(SUB_FAB_STAR, R.id.fab_star),
            Pair(SUB_FAB_PAUSE, R.id.fab_pause),
            Pair(SUB_FAB_HOME, R.id.fab_home)
        )

        testMap.forEach { (subFabKey, expected) ->
            val output = getSubFabId(subFabKey)

            assertEquals(expected, output)
        }
    }

    @Test
    fun given_wrongSubFabKey_When_getSubFabId_Then_checkError() {
        val wrongSubFabKey = 1000

        val expected = "Sub Fab key not found : $wrongSubFabKey"
        val output = try { getSubFabId(wrongSubFabKey) }
        catch (e: Exception) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_subFabKey_When_getSubFabIcon_Then_checkResult() {
        val testMap = mapOf(
            Pair(SUB_FAB_MIN_TEXT, R.drawable.ic_baseline_minus_text_black_18),
            Pair(SUB_FAB_UP_TEXT, R.drawable.ic_baseline_up_text_black_18),
            Pair(SUB_FAB_STAR, R.drawable.ic_baseline_star_black_18),
            Pair(SUB_FAB_PAUSE, R.drawable.ic_baseline_pause_black_18),
            Pair(SUB_FAB_HOME, R.drawable.ic_baseline_home_black_18)
        )

        testMap.forEach { (subFabKey, expected) ->
            val output = getSubFabIcon(subFabKey)

            assertEquals(expected, output)
        }
    }

    @Test
    fun given_wrongSubFabKey_When_getSubFabIcon_Then_checkError() {
        val wrongSubFabKey = 1000

        val expected = "Sub Fab key not found : $wrongSubFabKey"
        val output = try { getSubFabIcon(wrongSubFabKey) }
        catch (e: Exception) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_subFabKey_When_getSubFabLabel_Then_checkResult() {
        val testMap = mapOf(
            Pair(SUB_FAB_MIN_TEXT, resources.getString(R.string.sub_fab_label_min_text)),
            Pair(SUB_FAB_UP_TEXT, resources.getString(R.string.sub_fab_label_up_text)),
            Pair(SUB_FAB_STAR, resources.getString(R.string.sub_fab_label_star)),
            Pair(SUB_FAB_REMOVE_STAR, resources.getString(R.string.sub_fab_label_remove_star)),
            Pair(SUB_FAB_PAUSE, resources.getString(R.string.sub_fab_label_pause)),
            Pair(SUB_FAB_REMOVE_PAUSE, resources.getString(R.string.sub_fab_label_remove_pause)),
            Pair(SUB_FAB_HOME, resources.getString(R.string.sub_fab_label_home))
        )

        testMap.forEach { (subFabKey, expected) ->
            val output = getSubFabLabel(subFabKey)

            assertEquals(expected, output)
        }
    }

    @Test
    fun given_wrongSubFabKey_When_getSubFabLabel_Then_checkError() {
        val wrongSubFabKey = 1000

        val expected = "Sub Fab key not found : $wrongSubFabKey"
        val output = try { getSubFabLabel(wrongSubFabKey) }
                     catch (e: Exception) { e.message }

        assertEquals(expected, output)
    }
}