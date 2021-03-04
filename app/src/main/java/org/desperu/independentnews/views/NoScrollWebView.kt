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
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindDimen
import org.desperu.independentnews.extension.design.findView
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.design.ArticleDesignInterface
import org.desperu.independentnews.ui.sources.SourcesInterface
import org.desperu.independentnews.ui.sources.fragment.SourceRouter
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.desperu.independentnews.utils.Utils.isImageUrl
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.qualifier
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
    private val articleDesignInterface: ArticleDesignInterface? get() = getKoin().getOrNull()
    private val sourcesInterface: SourcesInterface? get() = getKoin().getOrNull()
    private val imageRouter: ImageRouter? get() = getKoin().getOrNull(qualifier(SOURCE_IMAGE_ROUTER))
    private val sourceRouter: SourceRouter by inject()
    private val prefs: SharedPrefService by inject()
    private var css: Css by Delegates.notNull()
    private var actualUrl: String? = null
    private var sourceName: String by Delegates.notNull()

    // --------------
    // CONFIGURATION
    // --------------

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
        // Work with updateBackground() without arguments, and getCurrentSourceColor()
        actualUrl = url
        this.sourceName = sourceName

        url?.let {
            updateTextSize(it, sourceName)
            if (articleDesignInterface?.isFirstPage != true) // To allow enter transition, from article list
                updateBackground(it, sourceName)
            updateMargins(it, sourceName)
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
                updateBackground(it, sourceName)
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

                if (isImageUrl(url))
                    imageRouter?.openShowImages(arrayListOf(url))
                else
                    sourceRouter.openShowArticle(
                        Article(
                            url = url,
                            source = Source(name = sourceName)
                        ),
                        sourcesInterface?.isExpanded ?: true
                    )
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
        val callback = { postDelayed( { articleDesignInterface?.scrollTo(null) }, 50) }

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
            if (isHtmlData(actualUrl) && sourceName == BASTAMAG
                || isHtmlData(actualUrl) && sourceName == MULTINATIONALES
                || actualUrl.contains(BASTAMAG_BASE_URL)
                || actualUrl.contains(MULTINATIONALES_BASE_URL)
            )

                textZoom += 20

            textAlignment = TEXT_ALIGNMENT_VIEW_START
        }
    }

    /**
     * Update margins of the web view, needed for Reporterre source (bottom margin),
     * and for Multinationales source (all margins).
     *
     * @param url               the actual url of the web view.
     * @param sourceName        the name of the source of the page.
     */
    private fun updateMargins(url: String, sourceName: String) {
        var margins = intArrayOf(0, 0, 0, 0)

        if (isHtmlData(url)) {
            val margin = bindDimen(R.dimen.default_margin).value.toInt()

            // Needed to correct article design.
            when (sourceName) {
                REPORTERRE -> margins = intArrayOf(0, 0, 0, margin)
                MULTINATIONALES -> margins = intArrayOf(margin, margin, margin, margin)
            }
        }

        // Apply margins to the web view.
        (layoutParams as? FrameLayout.LayoutParams)
            ?: (layoutParams as LinearLayout.LayoutParams)
                .setMargins(margins[0], margins[1], margins[2], margins[3])
    }

    /**
     * Update the background for reporterre page.
     *
     * @param url               the actual url of the web view.
     * @param sourceName        the name of the source of the page.
     */
    internal fun updateBackground(url: String, sourceName: String) {
        val color = getCurrentSourceColor(url, sourceName)

        rootView.findViewById<View>(R.id.article_root_view)?.setBackgroundColor(color)
        findView<NestedScrollView>()?.setBackgroundColor(color)
        rootView.findViewById<View>(R.id.article_metadata_container)?.setBackgroundColor(color)
    }

    /**
     * Convenience call for update background color of the parent container,
     * the linear layout that's container all content view.
     */
    internal fun updateBackground() {
        if (actualUrl != null && sourceName.isNotBlank())
            updateBackground(actualUrl!!, sourceName)
    }

    /**
     * Returns the current background color, depends of the current source name.
     *
     * @param url               the actual url of the web view.
     * @param sourceName        the name of the source of the page.
     *
     * @return the current source background color.
     */
    internal fun getCurrentSourceColor(
        url: String = actualUrl ?: "false =)",
        sourceName: String = this.sourceName
    ) =
        ResourcesCompat.getColor(
            resources,
            if (isHtmlData(url) && sourceName == REPORTERRE)
                R.color.reporterre_background
            else
                android.R.color.white,
            null
        )
}