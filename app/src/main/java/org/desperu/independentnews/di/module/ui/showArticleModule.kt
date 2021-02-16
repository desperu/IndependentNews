package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.helpers.SystemUiHelperImpl
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.ImageRouterImpl
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.design.ArticleDesign
import org.desperu.independentnews.ui.showArticle.design.ArticleDesignInterface
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClient
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClientInterface
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to show article activity.
 */
val showArticleModule = module {

    /**
     * Provides a ShowArticleInterface from the instance of ShowArticleActivity.
     */
    single { (activity: BaseBindingActivity) ->
        activity as ShowArticleInterface
    }

    /**
     * Provides a ImageRouter from the instance of ShowArticleActivity.
     */
    single<ImageRouter>(override = true) { (activity: BaseBindingActivity) ->
        ImageRouterImpl(activity)
    }

    /**
     * Provides a SystemUiHelper from the instance of the given Activity.
     */
    single<SystemUiHelper>(override = true) { (activity: BaseBindingActivity) ->
        SystemUiHelperImpl(activity)
    }

    /**
     * Provides a ArticleDesignInterface from the instance of ArticleDesign.
     */
    single<ArticleDesignInterface> { (articleDesign: ArticleDesign) ->
        articleDesign
    }

    /**
     * Provides a MyWebViewClientInterface from the instance of MyWebViewClient.
     */
    single<MyWebViewClientInterface> { (mWebViewClient: MyWebViewClient) ->
        mWebViewClient
    }
}