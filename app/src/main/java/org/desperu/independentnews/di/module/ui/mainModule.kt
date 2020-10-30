package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.ui.main.MainInterface
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleRouter
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleRouterImpl
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to main activity.
 */
val mainModule = module {

    /**
     * Provides a MainCommunication interface from the instance of MainActivity.
     */
    single { (activity: BaseActivity) ->
        activity as MainInterface
    }

    /**
     * Provides a ArticleRouter interface from the instance of MainActivity.
     */
    single<ArticleRouter> { (activity: BaseActivity) ->
        ArticleRouterImpl(
            activity
        )
    }
}