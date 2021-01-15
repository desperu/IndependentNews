package org.desperu.independentnews.utils

import android.content.Context
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.mockk.every
import io.mockk.mockk
import org.desperu.independentnews.R
import org.desperu.independentnews.di.module.repositoryModule
import org.desperu.independentnews.di.module.serviceModule
import org.desperu.independentnews.di.module.viewModelModule
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.ui.main.fragment.categories.CategoriesFragment
import org.desperu.independentnews.utils.MainUtils.getDrawerItemIdFromFragKey
import org.desperu.independentnews.utils.MainUtils.getFragFromKey
import org.desperu.independentnews.utils.MainUtils.retrievedKeyFromFrag
import org.desperu.independentnews.utils.MainUtils.setTitle
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
 * Main Utils class test, to check that all utils functions work as needed.
 */
class MainUtilsTest : KoinTest {

    // FOR DATA
    private val mockContext: Context = mockk()
    private val mockTextView: TextView = mockk()
    private lateinit var resources: ResourceService

    @Before
    fun before() {
        every { mockTextView.text = any() } returns Unit

        startKoin {
            androidContext(mockContext)
            modules(serviceModule, viewModelModule, repositoryModule)
        }

        resources = get()

        every { resources.getString(R.string.navigation_drawer_top_story) } returns "Top Story"
        every { resources.getString(R.string.navigation_drawer_all_articles) } returns "All Articles"
        every { resources.getString(R.string.fragment_today_articles) } returns "Today Articles"
        every { resources.getString(R.string.app_name) } returns "Independent News"
    }

    @After
    fun after() {
        unloadKoinModules(serviceModule)
        stopKoin()
    }

    @Test
    fun given_fragKey_When_getFragFromKey_Then_checkResult() {
        val expected = CategoriesFragment::class.java
        val output: Class<out Fragment> = getFragFromKey(FRAG_CATEGORY)::class.java

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongKey_When_getFragFromKey_Then_checkError() {
        val fragmentKey = 1000

        val expected = "Fragment key not found : $fragmentKey"
        val output = try { getFragFromKey(fragmentKey) }
                     catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_fragList_When_retrievedKeyFromFrag_Then_checkResult() {
        val expected = FRAG_CATEGORY
        val fragment = CategoriesFragment()
        val output = retrievedKeyFromFrag(fragment)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongFragment_When_retrievedKeyFromFrag_Then_checkError() {
        val fragment = Fragment()

        val expected = "Fragment class not found : ${fragment.javaClass.simpleName}"
        val output = try { retrievedKeyFromFrag(fragment) }
                     catch (e: IllegalArgumentException) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_fragKey_When_setTitle_Then_checkResult() {
        val testMap = mapOf(
            Pair(FRAG_TOP_STORY, R.string.navigation_drawer_top_story),
            Pair(FRAG_ALL_ARTICLES, R.string.navigation_drawer_all_articles),
            Pair(FRAG_TODAY_ARTICLES, R.string.fragment_today_articles),
            Pair(NO_FRAG, R.string.app_name) // For else
        )

        testMap.forEach { (fragKey, stringKey) ->
            val expected = resources.getString(stringKey)
            every { mockTextView.getText() } returns expected

            setTitle(mockTextView, fragKey)
            val output = mockTextView.text

            assertEquals(expected, output)
        }
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
        val testMap = mapOf(
            Pair(NO_FRAG, 0),
            Pair(FRAG_TOP_STORY, R.id.activity_main_menu_drawer_top_story),
            Pair(FRAG_CATEGORY, R.id.activity_main_menu_drawer_categories),
            Pair(FRAG_ALL_ARTICLES, R.id.activity_main_menu_drawer_all_articles),
            Pair(FRAG_TODAY_ARTICLES, 0)
        )

        testMap.forEach { (fragKey, id) ->
            val output = getDrawerItemIdFromFragKey(fragKey)

            assertEquals(id, output)
        }
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