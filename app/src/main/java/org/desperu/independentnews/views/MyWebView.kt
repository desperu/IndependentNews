package org.desperu.independentnews.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.AttributeSet
import android.util.Base64
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindColor
import org.desperu.independentnews.extension.design.bindDimen
import org.desperu.independentnews.extension.design.findView
import org.desperu.independentnews.extension.sendMailTo
import org.desperu.independentnews.helpers.AsyncHelper.waitCondition
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.design.ArticleDesignInterface
import org.desperu.independentnews.ui.showArticle.webClient.JS_INTERFACE_NAME
import org.desperu.independentnews.ui.showArticle.webClient.JavaScriptInterface
import org.desperu.independentnews.ui.sources.SourcesInterface
import org.desperu.independentnews.ui.sources.fragment.SourceRouter
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.SourcesUtils.getSourceTextZoom
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.desperu.independentnews.utils.Utils.isImageUrl
import org.desperu.independentnews.utils.Utils.isMailTo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import kotlin.properties.Delegates

/**
 * Time out for which wait to apply css on web view content.
 */
const val APPLY_CSS_TIMEOUT = 500L

/**
 * A custom [WebView] that does not allow to vertical scroll.
 * To correct scroll jump when init the web view.
 *
 * This [MyWebView] should be placed in a [ScrollView] to allow the user to scroll
 * and show the content of this [MyWebView].
 * Set the [isScrollContainer] xml attribute to true to better performances.
 */
open class MyWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr), KoinComponent {

    // FOR DATA
    private val articleDesignInterface: ArticleDesignInterface? get() = getKoin().getOrNull()
    private val sourcesInterface: SourcesInterface? get() = getKoin().getOrNull()
    private val imageRouter: ImageRouter? get() = getKoin().getOrNull(qualifier(SOURCE_IMAGE_ROUTER))
    private val sourceRouter: SourceRouter by inject()
    private val prefs: SharedPrefService by inject()
    private val activity get() = context as AppCompatActivity
    private var css: Css by Delegates.notNull()
    private var sourceName: String by Delegates.notNull()
    internal var toApplyCss = false

    // FOR UI
    private val bgColor by bindColor(android.R.color.white)

    init {
        setupJavascript()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Setup javascript for the web view, and enable javascript interface too.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupJavascript() {
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        val jsInterface = JavaScriptInterface(activity)
        addJavascriptInterface(jsInterface, JS_INTERFACE_NAME)
    }

    /**
     * Update the design of the web view. Set the css style, text size, margin
     * and force to redirect link to another activity.
     *
     * @param sourceName        the name of the source of the page.
     * @param css               the css to apply to the web view content.
     */
    internal fun updateWebViewDesign(sourceName: String, css: Css?) {
        this.sourceName = sourceName

        css?.let {
            this.css = css
            this.webViewClient = myWebViewClient
        }
    }

    /**
     * Update the design of the web view on loading start.
     * Set the margins, text size and apply css style.
     *
     * @param url               the url of the page.
     * @param sourceName        the name of the source of the page.
     * @param css               the css of the web view content.
     */
    internal fun updateWebViewStart(url: String?, sourceName: String, css: Css) {
        url?.let {
            toApplyCss = false // Reset on new page load
            updateTextSize(it, sourceName)
            if (articleDesignInterface?.isFirstPage != true) // To allow enter transition, from article list
                updateBackground()
            updateMargins(it, sourceName)

            if (isHtmlData(url))
                waitCondition(activity.lifecycleScope, APPLY_CSS_TIMEOUT, { toApplyCss }) {
                    applyCssStyle(it, css)
                }
            else
                postDelayed({ applyCssStyle(it, css) }, 50) // To prevent design bug, mistake in source detail with 10
        }
    }

    /**
     * Web view client for the web view.
     */
    private val myWebViewClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            url?.let {
                updateBackground()
                updateWebViewStart(it, sourceName, css)
            }
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean =

            // Force to open link in a new activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val url = request?.url.toString()

                when {
                    isImageUrl(url) -> imageRouter?.openShowImages(arrayListOf(url))
                    isMailTo(url) -> activity.sendMailTo(url)
                    else ->
                        sourceRouter.openShowArticle(
                            Article(url = url),
                            sourcesInterface?.isExpanded ?: true
                        )
                }

                true

            } else
                super.shouldOverrideUrlLoading(view, request)
    }

    // --------------
    // CSS STYLE
    // --------------

    /**
     * Apply the css style to the web view content.
     *
     * @param url   the actual url of the web view.
     * @param css   the css of the web view content.
     */
    private fun applyCssStyle(url: String, css: Css) {
        // Used to delay the scroll action to be sure that the css style is applied,
        // and prevent scroll gap error.
        val callback = { postDelayed({ articleDesignInterface?.scrollTo(null) }, 50) }

        injectCssCode(resizeMedia) { }
        if (isHtmlData(url)) {
            if (css.style.isNotBlank()) injectCssCode(css.style) { callback() }
            else injectCssUrl(url, css.url) { callback() }
//            zoomOut()
        }
    }

    /**
     * Inject the css style to the content of the web view, with JavaScript.
     *
     * @param cssUrl    the css url to inject to the web view.
     * @param url       the actual url of the web view.
     * @param callback  the callback to invoke after the java script execution.
     */
    private fun injectCssUrl(url: String, cssUrl: String, callback: () -> Unit) {
        if (isHtmlData(url)) {
            val js = "var link = document.createElement('link');" +
                    " link.setAttribute('rel', 'stylesheet');" +
                    " link.setAttribute('href','$cssUrl');" +
                    " link.setAttribute('type','text/css');" +
                    " document.head.appendChild(link);"
            evaluateJavascript(js) { callback() }
        }
    }

    /**
     * Inject the css style to the content of the web view, with JavaScript.
     *
     * @param cssCode the css code to inject to the web view.
     * @param callback  the callback to invoke after the java script execution.
     */
    private fun injectCssCode(cssCode: String, callback: () -> Unit) {
        val buffer = cssCode.toByteArray()
        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)

        val js = "var parent = document.getElementsByTagName('head').item(0);" +
                " var style = document.createElement('style');" +
                " style.type = 'text/css';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                " style.innerHTML = window.atob('" + encoded + "');" +
                " parent.appendChild(style)"
        evaluateJavascript(js) { callback() }
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

            // Needed to correct Bastamag and Multinationales articles text size.
            textZoom += getSourceTextZoom(actualUrl, sourceName)

            textAlignment = TEXT_ALIGNMENT_VIEW_START
        }
    }

    /**
     * Update margins of the web view, needed for Reporterre source,
     * and for Multinationales source (all margins).
     *
     * @param url               the actual url of the web view.
     * @param sourceName        the name of the source of the page.
     */
    private fun updateMargins(url: String, sourceName: String) {
        var margins = 0

        // Needed to correct article design.
        if (isHtmlData(url) && sourceName in listOf(REPORTERRE, MULTINATIONALES))
            margins = bindDimen(R.dimen.default_margin).value.toInt()

        // Apply margins to the web view.
        ((layoutParams as? FrameLayout.LayoutParams)
            ?: (layoutParams as LinearLayout.LayoutParams))
                .setMargins(margins)
    }

    /**
     * Update the background for reporterre page.
     */
    internal fun updateBackground() {
        rootView.findViewById<View>(R.id.article_root_view)?.setBackgroundColor(bgColor)
        findView<NestedScrollView>()?.setBackgroundColor(bgColor)
        rootView.findViewById<View>(R.id.article_metadata_container)?.setBackgroundColor(bgColor)
    }
}