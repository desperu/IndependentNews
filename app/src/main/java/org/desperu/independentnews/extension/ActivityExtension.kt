package org.desperu.independentnews.extension

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import org.desperu.independentnews.R

/**
 * Start activity, for the given class name.
 *
 * @param kClass the java class name.
 */
internal fun <T: Activity> Activity.showActivity(kClass: Class<T>) =
    startActivity(Intent(this, kClass))

/**
 * Start activity for result, for the given class name.
 *
 * @param kClass the java class name.
 * @param requestCode the request code for the result.
 */
internal fun <T: Activity> Activity.showActivityForResult(kClass: Class<T>, requestCode: Int) =
    startActivityForResult(Intent(this, kClass), requestCode)

/**
 * Show the given url in the web browser.
 *
 * @param url the url to open in the browser.
 */
internal fun Activity.showInBrowser(url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW)
    browserIntent.setDataAndType(Uri.parse(url), "text/html")
    this.startActivity(browserIntent)
}

/**
 * Open default mail activity to send a mail to the address in the given uri.
 * Create a chooser to allow the user to decide the receiving apk.
 *
 * @param uriString the uri string for which open a mail to screen.
 */
internal fun Activity.sendMailTo(uriString: String) {
    val mailIntent = Intent(Intent.ACTION_SENDTO)
    val uri = Uri.parse(uriString)

    mailIntent.data = uri // only email apps should handle this
    mailIntent.putExtra(Intent.EXTRA_EMAIL, uri.encodedPath)
//    intent.putExtra(Intent.EXTRA_SUBJECT, subject)

//    if (mailIntent.resolveActivity(packageManager) != null) startActivity(mailIntent)
    startActivity(Intent.createChooser(mailIntent, getString(R.string.intent_chooser_send_mail_title)))
}

/**
 * Share an article with it's title and url, to other applications.
 * Create a chooser to allow the user to decide the receiving apk.
 *
 * @param title the title of the article.
 * @param url   the url of the article.
 */
@Suppress("Deprecation")
internal fun Activity.shareArticle(title: String?, url: String?) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/plain"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    } else
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

    // Add data to the intent, the receiving app will decide
    // what to do with it.
    share.putExtra(Intent.EXTRA_SUBJECT, title)
    share.putExtra(Intent.EXTRA_TEXT, url)

    startActivity(Intent.createChooser(share, getString(R.string.intent_chooser_share_article_title)))
}