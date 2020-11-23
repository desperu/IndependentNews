package org.desperu.independentnews.ui.showArticle

import android.content.pm.ActivityInfo
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import org.desperu.independentnews.R
import org.desperu.independentnews.views.NoScrollWebView

/**
 * My custom Web Chrome Client, used to show full screen video in a custom view.
 *
 * @property webView the web view witch is used this custom Web Chrome Client.
 *
 * @constructor Instantiates a new MyWebChromeClient.
 *
 * @param webView the web view witch used this custom Web Chrome Client to set.
 */
class MyWebChromeClient(private val webView: NoScrollWebView) : WebChromeClient() {

    // FOR DATA
    private var mCustomView: View? = null
    private val customViewContainer = webView.rootView.findViewById<FrameLayout>(R.id.video_container)
    private var customViewCallback: CustomViewCallback? = null
    private val parentCallback = webView.context as ShowArticleInterface

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onShowCustomView(
        view: View,
        requestedOrientation: Int,
        callback: CustomViewCallback
    ) {
        onShowCustomView(
            view,
            callback
        )
    }

    override fun onShowCustomView(view: View, callback: CustomViewCallback) {

        // if a view already exists then immediately terminate the new one
        if (mCustomView != null) {
            callback.onCustomViewHidden()
            return
        }

        // Set custom view
        mCustomView = view
        customViewContainer.addView(view)
        customViewCallback = callback

        // Hide web view and show custom view
        webView.visibility = View.GONE
        customViewContainer.visibility = View.VISIBLE

        // Configure Ui for full screen video
        setSystemUIFlags(true)
        setScreenOrientation(true)
        parentCallback.saveScrollPosition()
        // TODO disable auto screen off

        parentCallback.inCustomView = true
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        // Update web view margins...
        if (newProgress < 50) parentCallback.updateWebViewMargins()
        // Update web view design.
        else if (newProgress > 70) parentCallback.updateWebViewDesign()
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        if (mCustomView == null) return

        // Show web view and hide custom view
        webView.visibility = View.VISIBLE
        customViewContainer.visibility = View.GONE
        mCustomView!!.visibility = View.GONE

        // Remove the custom view from its container.
        customViewContainer.removeView(mCustomView)
        customViewCallback?.onCustomViewHidden()
        mCustomView = null

        // Restore Ui state
        setSystemUIFlags(false)
        setScreenOrientation(false)
        parentCallback.restoreScrollPosition()

        parentCallback.inCustomView = false
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Set system ui flags to manage system decor view (status bar, and navigation bar),
     * depends of toHide value.
     *
     * @param toHide true to hide system ui decor, false to show.
     */
    private fun setSystemUIFlags(toHide: Boolean) {
        val flags =
            if (toHide) (
                    View.SYSTEM_UI_FLAG_IMMERSIVE // Set the content to appear under the system bars so that the
                                                  // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
            else
                View.SYSTEM_UI_FLAG_VISIBLE

        parentCallback.setDecorUiVisibility(flags)
    }

    /**
     * Set screen orientation, to landscape or portrait, depends of toLandscape value.
     *
     * @param toLandscape true to set orientation ot landscape, false to let user choose.
     */
    private fun setScreenOrientation(toLandscape: Boolean) =
        parentCallback.setOrientation(
            if (toLandscape)
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else
                ActivityInfo.SCREEN_ORIENTATION_USER
            )

}