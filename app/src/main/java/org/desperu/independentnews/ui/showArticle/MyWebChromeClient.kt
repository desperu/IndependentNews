package org.desperu.independentnews.ui.showArticle

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import org.desperu.independentnews.R
import org.desperu.independentnews.anim.SystemUiHelper.hideFullSystemUi
import org.desperu.independentnews.anim.SystemUiHelper.setOrientationLandscape
import org.desperu.independentnews.anim.SystemUiHelper.setOrientationUser
import org.desperu.independentnews.anim.SystemUiHelper.showFullSystemUi
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
    private val activity = webView.context as Activity

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
        hideFullSystemUi(activity)
        setOrientationLandscape(activity)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)// TODO disable auto screen off, need to check, and if good serialize
        parentCallback.saveScrollPosition()


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
        showFullSystemUi(activity)
        setOrientationUser(activity)
        parentCallback.restoreScrollPosition()

        parentCallback.inCustomView = false
    }
}