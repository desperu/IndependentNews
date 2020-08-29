package org.desperu.independentnews.ui

import kotlinx.android.synthetic.main.activity_show_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.BaseActivity


const val TITLE = "title"
const val ARTICLE = "article"

class ShowArticleActivity: BaseActivity() {

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_show_article

    override fun configureDesign() {
        bindData()
    }

    private fun bindData() {
        test_title.text = intent.extras?.getString(TITLE)
//        test_article.loadData(intent.extras?.getString(ARTICLE), "text/html; charset=UTF-8", null)
        val data = intent.extras?.getString(ARTICLE)
//        val base64 = Base64.encodeToString(
//            data.getBytes("UTF-8"),
//            Base64.DEFAULT
//        )
//        test_article.loadData(base64, "text/html; charset=utf-8", "base64")

//        test_article.settings.defaultTextEncodingName = "utf-8"
//        test_article.loadDataWithBaseURL(null, data, "text/html", "utf-8", null)

        test_article.settings.javaScriptEnabled = true
        test_article.settings.javaScriptCanOpenWindowsAutomatically = true
        test_article.loadUrl("https://www.bastamag.net/reformes-police-Defund-police-cameras-pietons-desarmement")
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
}