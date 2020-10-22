package org.desperu.independentnews.service

import androidx.core.graphics.drawable.toBitmap
import androidx.test.platform.app.InstrumentationRegistry
import org.desperu.independentnews.test.R
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.*
import org.junit.Test
import org.koin.test.KoinTest

/**
 * Simple resource class test, for Resource Service that check all functions rocks.
 */
class ResourcesServiceInstrumentedTest : KoinTest {

    // Context of the app under test.
    private val appContext = InstrumentationRegistry.getInstrumentation().context
    private val resource = ResourceServiceImpl(appContext) as ResourceService

    @Test
    fun getString() {
        val expected = "a string for test"

        val output = resource.getString(R.string.string_test)

        assertEquals(expected, output)
    }

    @Test
    fun getStringArray() {
        val expected = arrayOf("item1", "item2")

        val output = resource.getStringArray(R.array.test_list)

        assertArrayEquals(expected, output)
    }

    @Test
    fun getDrawable() {
        val expected = appContext.getDrawable(R.drawable.for_test_ic_baseline_add_circle_black_24)

        val output = resource.getDrawable(R.drawable.for_test_ic_baseline_add_circle_black_24)

        assertThat(
            "The expected Drawable is null, can't perform test !!" +
                    " Check the repertory src/androidTest/res/drawable",
            expected != null
        )
        assertTrue(expected!!.toBitmap().sameAs(output.toBitmap()))
    }
}