package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.ImageRouterImpl
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to show article activity.
 */
val showArticleModule = module {

    /**
     * Provides a ImageRouter from the instance of ShowArticleActivity.
     */
    single<ImageRouter> { (activity: BaseBindingActivity) ->
        ImageRouterImpl(activity)
    }
}