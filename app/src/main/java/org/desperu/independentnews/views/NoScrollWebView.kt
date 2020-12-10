package org.desperu.independentnews.views

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.AttributeSet
import android.util.Base64
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.view.setMargins
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindDimen
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.sources.fragment.SourceRouter
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.isSourceUrl
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.properties.Delegates

/**
 * A custom [WebView] that does not allow to vertical scroll.
 * To correct scroll jump when init the web view.
 *
 * This [NoScrollWebView] should be placed in a [ScrollView] to allow the user to scroll
 * and show the content of this [NoScrollWebView].
 * Set the [isScrollContainer] xml attribute to true to better performances.
 */
class NoScrollWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr), KoinComponent {

    // FOR DATA
    private val router: SourceRouter by inject()
    private val prefs: SharedPrefService by inject()
    private var cssUrl: String by Delegates.notNull()
    private var sourceName: String by Delegates.notNull()
    private var margins = 0

    init {
        setWebContentsDebuggingEnabled(true) // TODO needed ??
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun overScrollBy(
        deltaX: Int,
        deltaY: Int,
        scrollX: Int,
        scrollY: Int,
        scrollRangeX: Int,
        scrollRangeY: Int,
        maxOverScrollX: Int,
        maxOverScrollY: Int,
        isTouchEvent: Boolean
    ): Boolean {

        // Consume only horizontal event (X axe).
        return super.overScrollBy(
            deltaX,
            0,
            scrollX,
            0,
            scrollRangeX,
            0,
            maxOverScrollX,
            0,
            isTouchEvent
        )
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Update the design of the web view. Set the css style, text size, margin
     * and force to redirect link to another activity.
     *
     * @param sourceName        the name of the source of the page.
     * @param cssUrl            the css url to apply to the web view.
     */
    internal fun updateWebViewDesign(sourceName: String, cssUrl: String?) {
        this.sourceName = sourceName

        cssUrl?.let {
            this.cssUrl = cssUrl
            this.webViewClient = myWebViewClient
        }
    }

    /**
     * Update the design of the web view on loading start.
     * Set the text margin, size and background.
     *
     * @param sourceName        the name of the source of the page.
     * @param url               the url of the page.
     */
    internal fun updateWebViewStart(sourceName: String, url: String?) {
        url?.let {
            updateMargins(it, sourceName)
            updateTextSize(it, sourceName)
            updateBackground(it, sourceName)
        }
    }

    /**
     * Update the design of the web view on loading finish. Resize media and set the css style.
     *
     * @param url               the url of the page.
     * @param cssUrl            the css url to apply to the web view.
     */
    internal fun updateWebViewFinish(url: String?, cssUrl: String?) {
        url?.let {
            injectCssCode(resizeMedia)
            cssUrl?.let { cssUrl -> injectCssUrl(it, cssUrl) }
        }
    }

    /**
     * Web view client for the web view.
     */
    private val myWebViewClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            url?.let {
                // Update web view margins, needed for Reporterre pages
                updateMargins(it, sourceName)

                // Update the text size, needed for Bastamag pages
                updateTextSize(it, sourceName)

                // Update background color, needed for Reporterre pages
                updateBackground(it, sourceName)
            }
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            url?.let {
                // Force to resize medias
                injectCssCode(resizeMedia)

                // Apply css style with JavaScript support
                injectCssUrl(it, cssUrl)
            }
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean =

            // Force to open link in a new activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                router.openShowArticle(
                    Article(
                        url = request?.url.toString(),
                        sourceName = sourceName
                    )
                )
                true
            } else
                super.shouldOverrideUrlLoading(view, request)
    }

    // --------------
    // CSS STYLE
    // --------------

    /**
     * Inject the css style to the content of the web view, with JavaScript.
     *
     * @param cssUrl    the css url to inject to the web view.
     * @param url       the actual url of the web view.
     */
    private fun injectCssUrl(url: String, cssUrl: String) {
        if (isSourceUrl(url)) {
            val js = "var link = document.createElement('link');" +
                    " link.setAttribute('rel', 'stylesheet');" +
                    " link.setAttribute('href','$cssUrl');" +
                    " link.setAttribute('type','text/css');" +
                    " document.head.appendChild(link);"
            evaluateJavascript(js, null)// { zoomOut() }
        }
    }

    /**
     * Inject the css style to the content of the web view, with JavaScript.
     *
     * @param cssCode the css code to inject to the web view.
     */
    private fun injectCssCode(cssCode: String) {
        val buffer = cssCode.toByteArray()
        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)

        val js = "var parent = document.getElementsByTagName('head').item(0);" +
                " var style = document.createElement('style');" +
                " style.type = 'text/css';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                " style.innerHTML = window.atob('" + encoded + "');" +
                " parent.appendChild(style)"
        evaluateJavascript(js, null)
    }

    /**
     * Css code to resize media content of the web view.
     */
    private val resizeMedia =
        ("iframe {" +
            "display: block;" +
            "max-width:100%;" +
            "margin-top:10px;" +
            "margin-bottom:10px;" +
            "}")

    // --------------
    // DESIGN
    // --------------

    /**
     * Update web view text size with the selected value in settings.
     * Special text size correction for Bastamag.
     */
    private fun updateTextSize(actualUrl: String, sourceName: String) {
        settings.apply {
            textZoom = prefs.getPrefs().getInt(TEXT_SIZE, TEXT_SIZE_DEFAULT)
            // Needed to correct Bastamag article text size.
            if (isSourceUrl(actualUrl) && sourceName == BASTAMAG ||
                actualUrl.contains(BASTAMAG_BASE_URL))
                textZoom += 20
        }
    }

    /**
     * Update margins of the web view, needed for Reporterre source.
     *
     * @param url               the actual url of the web view.
     * @param sourceName        the name of the source of the page.
     */
    internal fun updateMargins(url: String, sourceName: String) {
        margins = 0

        if (isSourceUrl(url)) {

            // Needed to correct Reporterre article design.
            if (sourceName == REPORTERRE)
                margins = bindDimen(R.dimen.default_margin).value.toInt()
        }

        // Apply margins to the web view.
        (this.layoutParams as LinearLayout.LayoutParams).setMargins(margins)
    }

    /**
     * Update the background for reporterre page.
     *
     * @param url               the actual url of the web view.
     * @param sourceName        the name of the source of the page.
     */
    @Suppress("Deprecation")
    private fun updateBackground(url: String, sourceName: String) { // TODO use alpha with value animator
        (parent as View).setBackgroundColor(
            resources.getColor(
                if (isSourceUrl(url) && sourceName == REPORTERRE)
                    R.color.reporterre_background
                else
                    android.R.color.white
            )
        )
    }
}