package org.desperu.independentnews.ui.showArticle.webClient

import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.design.ArticleDesignInterface
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.views.NoScrollWebView
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * My custom Web Chrome Client, used to show full screen video in a custom view.
 *
 * @property activity               the parent activity through it's interface.
 * @property articleDesign          the article design interface access.
 * @property sysUiHelper            the interface of the system ui helper to manage it.
 * @property webView                the web view that owns this web chrome client.
 * @property customViewContainer    the container of the custom view.
 * @property mCustomView            the custom view used to display the video.
 * @property customViewCallback     the callback of the custom view, used to communicate with.
 * @property inCustomView           true if is in custom view, false otherwise.
 *
 * @constructor Instantiates a new MyWebChromeClient.
 */
class MyWebChromeClient : WebChromeClient(), KoinComponent {

    // FOR COMMUNICATION
    private val activity = get<ShowArticleInterface>().activity
    private val articleDesign: ArticleDesignInterface? = getKoin().getOrNull()
    private val sysUiHelper: SystemUiHelper = get()

    // FOR UI
    private val webView: NoScrollWebView by bindView(activity, R.id.web_view)
    private val customViewContainer: FrameLayout by bindView(activity, R.id.video_container)
    private var mCustomView: View? = null
    private var customViewCallback: CustomViewCallback? = null
    internal var inCustomView = false

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
        sysUiHelper.setWindowFlag(KEEP_SCREEN_ON) // Seems to properly work
        sysUiHelper.setDecorUiVisibility(SYS_UI_FULL_HIDE)
        sysUiHelper.setOrientation(LANDSCAPE) // Not needed for new api
        articleDesign?.saveScrollPosition()

        inCustomView = true
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        articleDesign?.updateLoadingProgress(newProgress)
        // Update layout design.
        if (newProgress >= 80) articleDesign?.handleDesign(newProgress)
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
        sysUiHelper.removeWindowFlag(KEEP_SCREEN_ON)
        sysUiHelper.setDecorUiVisibility(SYS_UI_VISIBLE)
        sysUiHelper.setOrientation(FULL_USER)
        articleDesign?.scrollTo(null)

        inCustomView = false
    }
}