package org.desperu.independentnews.ui.showArticle

import android.webkit.WebView
import android.webkit.WebViewClient
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_show_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.BaseActivity
import org.desperu.independentnews.models.Article


const val ARTICLE = "article"

class ShowArticleActivity: BaseActivity() {

    // FOR DATA
    private val article: Article? get() = intent.getParcelableExtra(ARTICLE)

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_show_article

    override fun configureDesign() {
        bindData()
        scrollToTop()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     *
     */
    private fun bindData() {
        show_article_title.text = article?.title
        web_view.loadData(article?.article, "text/html; charset=UTF-8", null)
//        val base64 = Base64.encodeToString(
//            data.getBytes("UTF-8"),
//            Base64.DEFAULT
//        )
//        test_article.loadData(base64, "text/html; charset=utf-8", "base64")

//        web_view.settings.defaultTextEncodingName = "utf-8"
//        web_view.loadDataWithBaseURL(null, article?.article, "text/html", "utf-8", null)

        web_view.settings.javaScriptEnabled = true
        web_view.settings.javaScriptCanOpenWindowsAutomatically = true
//        web_view.loadUrl(article?.url)

        web_view.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                val cssLink = article?.css
                val js = "var link = document.createElement('link');" +
                        " link.setAttribute('rel', 'stylesheet');" +
                        " link.setAttribute('href','$cssLink');" +
                        " link.setAttribute('type','text/css');" +
                        " document.head.appendChild(link);"
                view.evaluateJavascript(js, null)
                super.onPageFinished(view, url)
            }
        }

        Glide.with(this).load(article?.imageUrl).into(show_article_image)
    }

//    /**
//     * Configure and show Web View with Progress Bar.
//     */
//    private fun configureAndShowWebViewWithProgressBar() {
//
//        // Set progress bar with page loading.
//        webView.setWebChromeClient(object : WebChromeClient() {
//            override fun onProgressChanged(view: WebView, newProgress: Int) {
//                super.onProgressChanged(view, newProgress)
//                progressBar.setVisibility(View.VISIBLE)
//                progressBar.setMax(100)
//                progressBar.setProgress(newProgress)
//            }
//        })
//
//        // Force links and redirects to open in the WebView.
//        webView.setWebViewClient(object : WebViewClient() {
//            override fun onPageStarted(
//                view: WebView,
//                url: String,
//                favicon: Bitmap
//            ) {
//                super.onPageStarted(view, url, favicon)
//                articleUrl = url
//                progressBar.setProgress(0)
//            }
//
//            override fun onPageFinished(view: WebView, url: String) {
//                super.onPageFinished(view, url)
//                progressBar.setVisibility(View.GONE)
//                swipeRefreshLayout.setRefreshing(false)
//            }
//        })
//        webView.loadUrl(articleUrl)
//    }

    // --------------
    // UI
    // --------------

    /**
     * Scroll to top the scroll view.
     */
    private fun scrollToTop() { show_article_scroll_view.scrollX = 0 }
}