package org.desperu.independentnews.helpers

import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.showInBrowser
import org.desperu.independentnews.ui.firstStart.FirstStartInterface
import org.desperu.independentnews.utils.ABOUT
import org.desperu.independentnews.utils.CONNEXION
import org.desperu.independentnews.utils.CONNEXION_START
import org.desperu.independentnews.utils.FIRST_START_ERROR
import org.koin.java.KoinJavaComponent.getKoin

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
    private val firstStartInterface: FirstStartInterface? = getKoin().getOrNull()
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
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
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

            CONNEXION_START -> configureFirstStartButton(builder)

            FIRST_START_ERROR -> configureFirstStartButton(builder)
        }
    }

    /**
     * Configure buttons for the first start alert dialog builder.
     *
     * @param builder the dialog builder to add buttons.
     */
    private fun configureFirstStartButton(builder: AlertDialog.Builder) {
        builder
            .setPositiveButton(R.string.dialog_cant_start_retry) { dialogInterface, _ ->
                dialogInterface.dismiss()
                firstStartInterface?.retryFetchData()
            }
            .setNegativeButton(R.string.dialog_cant_start_quit) { dialogInterface, _ ->
                dialogInterface.dismiss()
                firstStartInterface?.closeApplication()
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
            ABOUT -> setMovementMethod(dialog)
            CONNEXION_START -> { setMovementMethod(dialog); setUnCancellable(dialog) }
            FIRST_START_ERROR -> setUnCancellable(dialog)
        }
    }

    /**
     * Set movement method for the message text of the alert dialog.
     * Use custom library to handle link redirect, because there's
     * an error for .pdf web url redirect in api 29.
     *
     * @param dialog the dialog to add features.
     */
    private fun setMovementMethod(dialog: AlertDialog) {
        dialog.findViewById<TextView>(android.R.id.message)?.movementMethod =
            BetterLinkMovementMethod.newInstance().apply {

                setOnLinkClickListener { _, url ->

                    if (url.endsWith(".pdf")) {
                        activity.showInBrowser(url)
                        true
                    } else
                        false
                }

                setOnLinkLongClickListener { _, _ ->
                    true // to lock the action
                }
            }
    }

    /**
     * Set the alert dialog not cancellable.
     *
     * @param dialog the dialog to add features.
     */
    private fun setUnCancellable(dialog: AlertDialog) {
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
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
        CONNEXION_START -> resources.getString(R.string.dialog_no_connexion_title)
        FIRST_START_ERROR -> resources.getString(R.string.dialog_fetch_source_error_title)
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
        CONNEXION_START -> R.string.dialog_cant_start_message
        FIRST_START_ERROR -> R.string.dialog_fetch_source_error_message
        else -> throw IllegalArgumentException("Dialog key not found : $dialogKey")
    }
}