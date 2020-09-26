package org.desperu.independentnews.di.module

import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleRouter
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleRouterImpl
import org.desperu.independentnews.ui.main.MainInterface
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListInterface
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
    single { (activity: BaseActivity) ->
        ArticleRouterImpl(
            activity
        ) as ArticleRouter
    }

    /**
     * Provides a ArticleListInterface interface from the instance of ArticleListFragment.
     */
    factory { (fragment: BaseBindingFragment) ->
        fragment as ArticleListInterface
    }
}