package org.desperu.independentnews.helpers

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.desperu.independentnews.R
import org.desperu.independentnews.utils.*

/**
 * DialogHelper witch provide functions to display messages into alert dialog.
 */
interface DialogHelper {

    /**
     * Show message in alert dialog.
     *
     * @param dialogKey the dialog key to display the corresponding message.
     */
    fun showDialog(dialogKey: Int)
}

/**
 * Implementation of the DialogHelper which use an Activity instance
 * to display message into alert dialog.
 *
 * @property activity the Activity instance used to display the message.
 *
 * @constructor Instantiate a new DialogHelperImpl.
 *
 * @param activity the Activity instance used to display the message, to set.
 */
class DialogHelperImpl(private val activity: AppCompatActivity) : DialogHelper {

    // FOR DATA
    private val resources = activity.resources

    // --------------
    // CALL FUNCTION
    // --------------

    /**
     * Show message in alert dialog.
     *
     * @param dialogKey the dialog key to display the corresponding message.
     */
    override fun showDialog(dialogKey: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.AlertDialogTheme)
            .setTitle(getTitle(dialogKey))
            .setMessage(getMessage(dialogKey))

        configureButtons(dialogKey, builder)

        val dialog = builder.show()
        configureAdditionalFeatures(dialogKey, dialog)
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure buttons for the alert dialog builder.
     *
     * @param dialogKey the dialog key to display the corresponding message.
     * @param builder   the dialog builder to add buttons.
     */
    private fun configureButtons(dialogKey: Int, builder: AlertDialog.Builder) {
        when (dialogKey) {

            ABOUT -> builder.setPositiveButton(
                R.string.activity_main_dialog_about_positive_button,
                null
            )

            CONNEXION -> builder.setPositiveButton(
                R.string.activity_main_dialog_about_positive_button,
                null
            )
        }
    }

    /**
     * Configure additional features for the alert dialog.
     *
     * @param dialogKey the dialog key to display the corresponding message.
     * @param dialog    the dialog to add features.
     */
    private fun configureAdditionalFeatures(dialogKey: Int, dialog: AlertDialog) {
        when (dialogKey) {
            ABOUT ->
                dialog.findViewById<TextView>(android.R.id.message)?.movementMethod =
                    LinkMovementMethod.getInstance()
        }
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Returns the corresponding title for the given dialog key.
     *
     * @param dialogKey the dialog bar key to display the corresponding message.
     *
     * @return the corresponding title for the given dialog key.
     */
    private fun getTitle(dialogKey: Int) = when(dialogKey) {
        ABOUT -> "${resources.getString(R.string.activity_main_dialog_about_title)} ${resources.getString(R.string.app_name)}"
        CONNEXION -> resources.getString(R.string.dialog_no_connexion_title)
        else -> throw IllegalArgumentException("Dialog key not found : $dialogKey")
    }

    /**
     * Returns the corresponding message for the given dialog key.
     *
     * @param dialogKey the dialog bar key to display the corresponding message.
     *
     * @return the corresponding message for the given dialog key.
     */
    private fun getMessage(dialogKey: Int) = when(dialogKey) {
        ABOUT -> R.string.activity_main_dialog_about_message
        CONNEXION -> R.string.dialog_no_connexion_message
        else -> throw IllegalArgumentException("Dialog key not found : $dialogKey")
    }
}