package org.desperu.independentnews.utils

import android.content.Context
import org.desperu.independentnews.R

/**
 * SettingsUtils object witch provide utils functions for settings activity.
 */
object SettingsUtils {

    /**
     * Returns the corresponding title for given dialog key.
     *
     * @param context the context from this function is called.
     * @param dialogKey the key to show the corresponding title.
     *
     * @return the corresponding title for the given dialog key.
     *
     * @throws IllegalStateException if the dialog key was not found.
     */
    internal fun getDialogTitle(context: Context, dialogKey: Int): String = when(dialogKey) {
        NOTIF_TIME_DIALOG -> context.getString(R.string.activity_settings_text_notifications_time)
        TEXT_SIZE_DIALOG -> context.getString(R.string.activity_settings_text_article_text_size)
        REFRESH_TIME_DIALOG -> context.getString(R.string.activity_settings_text_refresh_article_list_time)
        STORE_DELAY_DIALOG -> context.getString(R.string.activity_settings_text_article_store_delay)
        RESET_DIALOG -> context.getString(R.string.activity_settings_text_category_reset_settings)
        else -> error("Error while retrieving the Dialog Key : $dialogKey")
    }

    /**
     * Returns the corresponding message for given dialog key.
     *
     * @param context the context from this function is called.
     * @param dialogKey the key to show the corresponding message.
     *
     * @return the corresponding message for the given dialog key.
     *
     * @throws IllegalStateException if the dialog key was not found.
     */
    internal fun getDialogMessage(context: Context, dialogKey: Int): String = when(dialogKey) {
        NOTIF_TIME_DIALOG -> context.getString(R.string.activity_settings_text_notifications_time_description)
        TEXT_SIZE_DIALOG -> context.getString(R.string.activity_settings_text_article_text_size_description)
        REFRESH_TIME_DIALOG -> context.getString(R.string.activity_settings_text_refresh_article_list_time_description)
        STORE_DELAY_DIALOG -> context.getString(R.string.activity_settings_text_article_store_delay_description)
        RESET_DIALOG -> context.getString(R.string.activity_settings_dialog_reset_settings_message)
        else -> error("Error while retrieving the Dialog Key : $dialogKey")
    }

    /**
     * Returns the corresponding dialog message for given dialog key.
     *
     * @param dialogKey the key to show the corresponding message.
     *
     * @return the corresponding dialog message for the given dialog key.
     *
     * @throws IllegalStateException if the dialog key was not found.
     */
    internal fun getMinAndMaxValues(dialogKey: Int): List<Int> = when(dialogKey) {
        NOTIF_TIME_DIALOG -> listOf(0, 24)
        TEXT_SIZE_DIALOG -> listOf(1, 200)
        REFRESH_TIME_DIALOG -> listOf(0, 24)
        STORE_DELAY_DIALOG -> listOf(1, 24)
        else -> error("Error while retrieving the Dialog Key : $dialogKey")
    }

    /**
     * Returns the corresponding toast message for given dialog key.
     *
     * @param context the context from this function is called.
     * @param dialogKey the key to show the corresponding message.
     *
     * @return the corresponding toast message for the given dialog key.
     *
     * @throws IllegalStateException if the dialog key was not found.
     */
    internal fun getToastMessage(context: Context, dialogKey: Int): String = when(dialogKey) {
        NOTIF_TIME_DIALOG,
        TEXT_SIZE_DIALOG,
        REFRESH_TIME_DIALOG,
        STORE_DELAY_DIALOG -> getToastWrongValue(context, dialogKey)
        RESET_DIALOG -> context.getString(R.string.activity_settings_toast_reset_settings_default)
        else -> error("Error while retrieving the Dialog Key : $dialogKey")
    }

    /**
     * Returns the corresponding wrong value toast message for the given dialog key.
     *
     * @param context the context from this function is called.
     * @param dialogKey the key to show the corresponding message.
     *
     * @return the corresponding wrong value toast message for the given dialog key.
     */
    private fun getToastWrongValue(context: Context, dialogKey: Int): String {
        val minMax = getMinAndMaxValues(dialogKey)

        return context.getString(
            R.string.activity_settings_toast_time_wrong_value,
            getDialogTitle(context, dialogKey),
            minMax[0],
            minMax[1]
        )
    }
}