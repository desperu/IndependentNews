package org.desperu.independentnews.extension

import android.app.Activity
import android.content.Intent
import android.net.Uri

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