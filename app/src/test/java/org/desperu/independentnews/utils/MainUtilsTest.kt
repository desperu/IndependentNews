package org.desperu.independentnews.utils

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.mockk.every
import io.mockk.mockk
import org.desperu.independentnews.R
import org.desperu.independentnews.di.module.serviceModule
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListViewModel
import org.desperu.independentnews.ui.main.fragment.articleList.FRAG_KEY
import org.desperu.independentnews.ui.main.fragment.viewPager.ViewPagerFragment
import org.desperu.independentnews.ui.main.fragment.viewPager.VP_FRAG_KEY
import org.desperu.independentnews.utils.MainUtils.getDrawerItemIdFromFragKey
import org.desperu.independentnews.utils.MainUtils.getFragFromKey
import org.desperu.independentnews.utils.MainUtils.retrievedKeyFromFrag
import org.desperu.independentnews.utils.MainUtils.setTitle
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get


/**
 * Main Utils class test, to check that all utils functions work as needed.
 */
class MainUtilsTest : KoinTest {

    // FOR DATA
    private val mockContext: Context = mockk()
    private val mockTextView: TextView = mockk()
    private val mockArticleListVM: ArticleListViewModel = mockk()
    private val mockBundle = mockk<Bundle>()
    private lateinit var resources: ResourceService

    private val testModule = module {
        viewModel { mockArticleListVM }
    }

    @Before
    fun before() {
        startKoin {
            androidContext(mockContext)
            modules(testModule, serviceModule)
        }

        resources = get()

        every { mockTextView.text = any() } returns Unit
        every { resources.getString(R.string.navigation_drawer_top_story) } returns "Top Story"
        every { resources.getString(R.string.navigation_drawer_all_articles) } returns "All Articles"
        every { resources.getString(R.string.navigation_drawer_user_articles) } returns "My Articles"
        every { resources.getString(R.string.fragment_today_articles) } returns "Today Articles"
        every { resources.getString(R.string.app_name) } returns "Independent News"

        every { mockBundle.containsKey(any()) } returns true
        every { mockBundle.putInt(any(), any()) } returns Unit
    }

    @After
    fun after() {
        unloadKoinModules(listOf(testModule, serviceModule))
        stopKoin()
    }

    @Test
    fun given_fragKey_When_getFragFromKey_Then_checkResult() {
        val expectedList = listOf(ArticleListFragment::class.java, ViewPagerFragment::class.java)

        val fragKeyList = listOf(
            FRAG_TOP_STORY,
            FRAG_CATEGORY,
            FRAG_ALL_ARTICLES,
            FRAG_USER_ARTICLE,
            FRAG_TODAY_ARTICLES
        )

        fragKeyList.forEach {
            val output: Class<out Fragment> = getFragFromKey(it)::class.java

            val index = if (it != FRAG_CATEGORY && it != FRAG_USER_ARTICLE) 0 else 1
            assertEquals(expectedList[index], output)
        }
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
    fun given_topStoryFrag_When_retrievedKeyFromFrag_Then_checkResult() {
        val expected = FRAG_ALL_ARTICLES

        val fragment = ArticleListFragment()
        every { mockBundle.getInt(FRAG_KEY) } returns FRAG_ALL_ARTICLES
        mockBundle.putInt(FRAG_KEY, FRAG_ALL_ARTICLES)
        fragment.arguments = mockBundle

        val output = retrievedKeyFromFrag(fragment)

        assertEquals(expected, output)
    }

    @Test
    fun given_vpFrag_When_retrievedKeyFromFrag_Then_checkResult() {
        val expected = FRAG_ECOLOGY

        val fragment = ViewPagerFragment()
        every { mockBundle.getInt(any()) } returns FRAG_ECOLOGY
//        every { mockBundle.getInt(VP_FRAG_KEY) } returns FRAG_ECOLOGY
        mockBundle.putInt(VP_FRAG_KEY, FRAG_ECOLOGY)
        fragment.arguments = mockBundle

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
            Pair(FRAG_USER_ARTICLE, R.string.navigation_drawer_user_articles),
            Pair(FRAG_TODAY_ARTICLES, R.string.fragment_today_articles),
            Pair(NO_FRAG, R.string.app_name) // For else
        )

        testMap.forEach { (fragKey, stringId) ->
            val expected = resources.getString(stringId)
            every { mockTextView.text } returns expected

            setTitle(mockTextView, fragKey)
            val output = mockTextView.text

            assertEquals(expected, output)
        }
    }

    @Test
    fun given_AllArticlesFragKey_When_getDrawerItemIdFromFragKey_Then_checkResult() {
        val testMap = mapOf(
            Pair(NO_FRAG, 0),
            Pair(FRAG_TOP_STORY, R.id.activity_main_menu_drawer_top_story),
            Pair(FRAG_CATEGORY, R.id.activity_main_menu_drawer_categories),
            Pair(FRAG_ALL_ARTICLES, R.id.activity_main_menu_drawer_all_articles),
            Pair(FRAG_USER_ARTICLE, R.id.activity_main_menu_drawer_user_article),
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