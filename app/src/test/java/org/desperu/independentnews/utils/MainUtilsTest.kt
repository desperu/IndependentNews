package org.desperu.independentnews.utils

import androidx.fragment.app.Fragment
import org.desperu.independentnews.ui.main.fragment.categories.CategoriesFragment
import org.desperu.independentnews.utils.MainUtils.getFragFromKey
import org.desperu.independentnews.utils.MainUtils.retrievedKeyFromFrag
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Main Utils class test, to check that all utils functions work as needed.
 */
class MainUtilsTest {

    @Test
    fun given_fragKey_When_getFragFromKey_Then_checkResult() {
        val expected = CategoriesFragment::class.java
        val output: Class<out Fragment> = getFragFromKey(FRAG_CATEGORY)::class.java

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongKey_When_getFragClassFromKey_Then_checkError() {
        val fragmentKey = 100

        val expected = "Fragment key not found : $fragmentKey"
        val output = try { getFragFromKey(fragmentKey) }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_fragList_When_retrievedFragKeyFromClass_Then_checkResult() {
        val expected = FRAG_CATEGORY
        val fragment = CategoriesFragment()
        val output = retrievedKeyFromFrag(fragment)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongFragment_When_retrievedFragKeyFromClass_Then_checkError() {
        val fragment = Fragment()

        val expected = "Fragment class not found : ${fragment.javaClass.simpleName}"
        val output = try { retrievedKeyFromFrag(fragment) }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }
}