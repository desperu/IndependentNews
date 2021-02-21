package org.desperu.independentnews.utils

import android.content.Context
import com.google.common.base.CharMatcher.any
import io.mockk.every
import io.mockk.mockk
import org.desperu.independentnews.R
import org.desperu.independentnews.utils.SettingsUtils.getDialogMessage
import org.desperu.independentnews.utils.SettingsUtils.getDialogTitle
import org.desperu.independentnews.utils.SettingsUtils.getMinAndMaxValues
import org.desperu.independentnews.utils.SettingsUtils.getToastMessage
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Settings Utils class test, to check that all utils functions work as needed.
 */
class SettingsUtilsTest {

    // FOR DATA
    private val mockContext: Context = mockk()
    private val notifTime = "Notifications Time"
    private val articleTextSize = "Article text size"
    private val refreshListTime = "Refresh article list time"
    private val articleStoreDelay = "Article store delay"

    @Before
    fun before() {
        // For Dialog Title
        every { mockContext.getString(R.string.activity_settings_text_notifications_time) } returns notifTime
        every { mockContext.getString(R.string.activity_settings_text_article_text_size) } returns articleTextSize
        every { mockContext.getString(R.string.activity_settings_text_refresh_article_list_time) } returns refreshListTime
        every { mockContext.getString(R.string.activity_settings_text_article_store_delay) } returns articleStoreDelay
        every { mockContext.getString(R.string.activity_settings_text_category_reset_settings) } returns "Reset Settings"

        // For Dialog Message
        every { mockContext.getString(R.string.activity_settings_text_notifications_time_description) } returns "Set notification time (default is 12am)."
        every { mockContext.getString(R.string.activity_settings_text_article_text_size_description) } returns "Set article text size, in percent, default 100, min 1, max 200."
        every { mockContext.getString(R.string.activity_settings_text_refresh_article_list_time_description) } returns "Set refresh article list time (default is 11am)."
        every { mockContext.getString(R.string.activity_settings_text_article_store_delay_description) } returns "Set the delay to store article in the application, in month, default is 6, min 1, max 24."
        every { mockContext.getString(R.string.activity_settings_dialog_reset_settings_message) } returns "Are you sure you want reset settings to default?"

        // For toast message
        every { mockContext.getString(R.string.activity_settings_toast_time_wrong_value, any(), any(), any()) } returns "${any()} must be under ${any()} and ${any()} !"
        every { mockContext.getString(R.string.activity_settings_toast_reset_settings_default) } returns "Settings Reset to default !"
    }

    @Test
    fun given_dialogKey_When_getDialogTitle_Then_checkResult() {
        val testMap = mapOf(
            Pair(NOTIF_TIME_DIALOG, notifTime),
            Pair(TEXT_SIZE_DIALOG, articleTextSize),
            Pair(REFRESH_TIME_DIALOG, refreshListTime),
            Pair(STORE_DELAY_DIALOG, articleStoreDelay),
            Pair(RESET_DIALOG, mockContext.getString(R.string.activity_settings_text_category_reset_settings))
        )

        testMap.forEach { (dialogKey, expected) ->
            val output = getDialogTitle(mockContext, dialogKey)

            assertEquals(expected, output)
        }
    }

    @Test
    fun given_wrongDialogKey_When_getDialogTitle_Then_checkError() {
        val dialogKey = 1000

        val expected = "Error while retrieving the Dialog Key : $dialogKey"
        val output = try { getDialogTitle(mockContext, dialogKey)
        }
        catch (e: Exception) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_dialogKey_When_getDialogMessage_Then_checkResult() {
        val testMap = mapOf(
            Pair(NOTIF_TIME_DIALOG, mockContext.getString(R.string.activity_settings_text_notifications_time_description)),
            Pair(TEXT_SIZE_DIALOG, mockContext.getString(R.string.activity_settings_text_article_text_size_description)),
            Pair(REFRESH_TIME_DIALOG, mockContext.getString(R.string.activity_settings_text_refresh_article_list_time_description)),
            Pair(STORE_DELAY_DIALOG, mockContext.getString(R.string.activity_settings_text_article_store_delay_description)),
            Pair(RESET_DIALOG, mockContext.getString(R.string.activity_settings_dialog_reset_settings_message))
        )

        testMap.forEach { (dialogKey, expected) ->
            val output = getDialogMessage(mockContext, dialogKey)

            assertEquals(expected, output)
        }
    }

    @Test
    fun given_wrongDialogKey_When_getDialogMessage_Then_checkError() {
        val dialogKey = 1000

        val expected = "Error while retrieving the Dialog Key : $dialogKey"
        val output = try { getDialogMessage(mockContext, dialogKey)
        }
        catch (e: Exception) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_dialogKey_When_getMinAndMaxValues_Then_checkResult() {
        val testMap = mapOf(
            Pair(NOTIF_TIME_DIALOG, listOf(0, 24)),
            Pair(TEXT_SIZE_DIALOG, listOf(1, 200)),
            Pair(REFRESH_TIME_DIALOG, listOf(0, 24)),
            Pair(STORE_DELAY_DIALOG, listOf(1, 24))
        )

        testMap.forEach { (dialogKey, expected) ->
            val output = getMinAndMaxValues(dialogKey)

            assertEquals(expected, output)
        }
    }

    @Test
    fun given_wrongDialogKey_When_getMinAndMaxValues_Then_checkError() {
        val dialogKey = 1000

        val expected = "Error while retrieving the Dialog Key : $dialogKey"
        val output = try { getMinAndMaxValues(dialogKey)
        }
        catch (e: Exception) { e.message }

        assertEquals(expected, output)
    }

    @Test
    fun given_dialogKey_When_getToastMessage_Then_checkResult() {
        val wrongValueToast = mockContext.getString(R.string.activity_settings_toast_time_wrong_value, any(), any(), any())
        val testMap = mapOf(
            Pair(NOTIF_TIME_DIALOG, wrongValueToast),
            Pair(TEXT_SIZE_DIALOG, wrongValueToast),
            Pair(REFRESH_TIME_DIALOG, wrongValueToast),
            Pair(STORE_DELAY_DIALOG, wrongValueToast),
            Pair(RESET_DIALOG, mockContext.getString(R.string.activity_settings_toast_reset_settings_default))
        )

        testMap.forEach { (dialogKey, expected) ->
            val output = getToastMessage(mockContext, dialogKey)

            assertEquals(expected, output)
        }
    }

    @Test
    fun given_wrongDialogKey_When_getToastMessage_Then_checkError() {
        val dialogKey = 1000

        val expected = "Error while retrieving the Dialog Key : $dialogKey"
        val output = try { getToastMessage(mockContext, dialogKey)
        }
        catch (e: Exception) { e.message }

        assertEquals(expected, output)
    }
}