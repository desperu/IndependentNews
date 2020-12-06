package org.desperu.independentnews.ui.showArticle

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import org.desperu.independentnews.R
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.utils.FULL_USER
import org.desperu.independentnews.utils.LANDSCAPE
import org.desperu.independentnews.utils.SYS_UI_FULL_HIDE
import org.desperu.independentnews.utils.SYS_UI_VISIBLE
import org.desperu.independentnews.views.NoScrollWebView
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * My custom Web Chrome Client, used to show full screen video in a custom view.
 *
 * @property webView                the web view witch is used this custom Web Chrome Client.
 * @property mCustomView            the custom view used to display the video.
 * @property customViewContainer    the container of the custom view.
 * @property customViewCallback     the callback of the custom view, used to communicate with.
 * @property showArticleInterface   the interface of the parent activity.
 * @property sysUiHelper            the interface of the system ui helper to manage it.
 *
 * @constructor Instantiates a new MyWebChromeClient.
 *
 * @param webView the web view witch used this custom Web Chrome Client to set.
 */
class MyWebChromeClient(private val webView: NoScrollWebView) : WebChromeClient(), KoinComponent {

    // FOR DATA
    private var mCustomView: View? = null
    private val customViewContainer = webView.rootView.findViewById<FrameLayout>(R.id.video_container)
    private var customViewCallback: CustomViewCallback? = null
    private val showArticleInterface: ShowArticleInterface? = getKoin().getOrNull()
    private val activity = webView.context as Activity
    private val sysUiHelper: SystemUiHelper = get()

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
        sysUiHelper.setDecorUiVisibility(SYS_UI_FULL_HIDE)
        sysUiHelper.setOrientation(LANDSCAPE) // Not needed for new api
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)// TODO disable auto screen off, need to check, and if good serialize
        showArticleInterface?.saveScrollPosition()                              //  not good ... find another way !!

        showArticleInterface?.inCustomView = true
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        // Update web view margins...
        if (newProgress < 50) showArticleInterface?.updateWebViewMargins()
        // Update web view design.
        else if (newProgress > 80) showArticleInterface?.updateWebViewDesign()
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
        sysUiHelper.setDecorUiVisibility(SYS_UI_VISIBLE)
        sysUiHelper.setOrientation(FULL_USER)
        showArticleInterface?.restoreScrollPosition()

        showArticleInterface?.inCustomView = false
    }
}