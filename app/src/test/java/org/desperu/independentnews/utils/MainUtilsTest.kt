package org.desperu.independentnews.utils

import androidx.fragment.app.Fragment
import org.desperu.independentnews.R
import org.desperu.independentnews.ui.main.fragment.categories.CategoriesFragment
import org.desperu.independentnews.utils.MainUtils.getDrawerItemIdFromFragKey
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
        val fragmentKey = 1000

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

    @Test
    fun given_topStoryFragKey_When_getDrawerItemIdFromFragKey_Then_checkResult() {
        val expected = R.id.activity_main_menu_drawer_top_story
        val output = getDrawerItemIdFromFragKey(FRAG_TOP_STORY)

        assertEquals(expected, output)
    }

    @Test
    fun given_catFragKey_When_getDrawerItemIdFromFragKey_Then_checkResult() {
        val expected = R.id.activity_main_menu_drawer_categories
        val output = getDrawerItemIdFromFragKey(FRAG_CATEGORY)

        assertEquals(expected, output)
    }

    @Test
    fun given_AllArticlesFragKey_When_getDrawerItemIdFromFragKey_Then_checkResult() {
        val expected = R.id.activity_main_menu_drawer_all_articles
        val output = getDrawerItemIdFromFragKey(FRAG_ALL_ARTICLES)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongFragKey_When_getDrawerItemIdFromFragKey_Then_checkError() {
        val fragmentKey = 1000

        val expected = "Fragment key not found : $fragmentKey"
        val output = try { getDrawerItemIdFromFragKey(fragmentKey) }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }
}