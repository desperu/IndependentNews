package org.desperu.independentnews.ui.showArticle.fragment

import org.desperu.independentnews.ui.showArticle.webClient.MyWebChromeClient
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClient

interface FragmentInterface {

    /**
     * The web view client of the fragment.
     */
    val mWebViewClient: MyWebViewClient?

    /**
     * The web chrome client of the fragment
     */
    val mWebChromeClient: MyWebChromeClient?
}