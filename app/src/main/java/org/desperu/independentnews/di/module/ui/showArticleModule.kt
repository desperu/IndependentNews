package org.desperu.independentnews.di.module.ui

import androidx.fragment.app.Fragment
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.helpers.DialogHelper
import org.desperu.independentnews.helpers.DialogHelperImpl
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.helpers.SystemUiHelperImpl
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.ImageRouterImpl
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.design.ArticleDesign
import org.desperu.independentnews.ui.showArticle.design.ArticleDesignInterface
import org.desperu.independentnews.ui.showArticle.fragment.FragmentInterface
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
    single { (activity: BaseActivity) ->
        activity as ShowArticleInterface
    }

    /**
     * Provides a FragmentInterface from the instance of the given Fragment.
     */
    factory { (fragment: Fragment) ->
        fragment as FragmentInterface
    }

    /**
     * Provides an ImageRouter from the instance of ShowArticleActivity.
     */
    single<ImageRouter>(override = true) { (activity: BaseActivity) ->
        ImageRouterImpl(activity)
    }

    /**
     * Provides a SystemUiHelper from the instance of the given Activity.
     */
    single<SystemUiHelper>(override = true) { (activity: BaseActivity) ->
        SystemUiHelperImpl(activity)
    }

    /**
     * Provides a DialogHelper interface from the instance of MainActivity.
     */
    single<DialogHelper>(override = true) { (activity: BaseActivity) ->
        DialogHelperImpl(activity)
    }

    /**
     * Provides an ArticleDesignInterface from the instance of ArticleDesign.
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