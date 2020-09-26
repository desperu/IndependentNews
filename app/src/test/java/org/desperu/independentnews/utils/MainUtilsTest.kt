package org.desperu.independentnews.utils

import androidx.fragment.app.Fragment
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment
import org.desperu.independentnews.utils.MainUtils.getFragClassFromKey
import org.desperu.independentnews.utils.MainUtils.retrievedFragKeyFromClass
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Main Utils class test, to check that all utils functions work as needed.
 */
class MainUtilsTest {

    @Test
    fun given_fragKey_When_getFragClassFromKey_Then_checkResult() {
        val expected = ArticleListFragment::class.java
        val output: Class<Fragment> = getFragClassFromKey(FRAG_TOP_STORY)

        assertEquals(expected, output)
    }

//    @Test
//    fun given_fragMapKey_When_getFragClassFromKey_Then_checkResult() {
//        val expected = MapsFragment::class.java
//        val output: Class<Fragment> = getFragClassFromKey(FRAG_ESTATE_MAP)
//
//        assertEquals(expected, output)
//    }
//
//    @Test
//    fun given_fragDetailKey_When_getFragClassFromKey_Then_checkResult() {
//        val expected = EstateDetailFragment::class.java
//        val output: Class<Fragment> = getFragClassFromKey(FRAG_ESTATE_DETAIL)
//
//        assertEquals(expected, output)
//    }

    @Test
    fun given_wrongKey_When_getFragClassFromKey_Then_checkError() {
        val fragmentKey = 100

        val expected = "Fragment key not found : $fragmentKey"
        val output = try { getFragClassFromKey<Fragment>(fragmentKey) }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_fragList_When_retrievedFragKeyFromClass_Then_checkResult() {
        val expected = FRAG_TOP_STORY
        val output = retrievedFragKeyFromClass(ArticleListFragment::class.java)

        assertEquals(expected, output)
    }

//    @Test
//    fun given_fragMap_When_retrievedFragKeyFromClass_Then_checkResult() {
//        val expected = FRAG_ESTATE_MAP
//        val output = retrievedFragKeyFromClass(MapsFragment::class.java)
//
//        assertEquals(expected, output)
//    }
//
//    @Test
//    fun given_fragDetail_When_retrievedFragKeyFromClass_Then_checkResult() {
//        val expected = FRAG_ESTATE_DETAIL
//        val output = retrievedFragKeyFromClass(EstateDetailFragment::class.java)
//
//        assertEquals(expected, output)
//    }

    @Test
    fun given_wrongFragment_When_retrievedFragKeyFromClass_Then_checkError() {
        val fragment = Fragment::class.java

        val expected = "Fragment class not found : ${fragment.simpleName}"
        val output = try { retrievedFragKeyFromClass(fragment) }
        catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }
}