package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.helpers.DialogHelper
import org.desperu.independentnews.helpers.DialogHelperImpl
import org.desperu.independentnews.helpers.SnackBarHelper
import org.desperu.independentnews.helpers.SnackBarHelperImpl
import org.desperu.independentnews.ui.main.MainInterface
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListInterface
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleRouter
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleRouterImpl
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.ImageRouterImpl
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
     * Provides a SnackBarHelper interface from the instance of MainActivity.
     */
    single<SnackBarHelper>(override = true) { (activity: BaseActivity) ->
        SnackBarHelperImpl(
            activity
        )
    }

    /**
     * Provides a DialogHelper interface from the instance of MainActivity.
     */
    single<DialogHelper>(override = true) { (activity: BaseActivity) ->
        DialogHelperImpl(
            activity
        )
    }

    /**
     * Provides a ArticleRouter interface from the instance of MainActivity.
     */
    single<ArticleRouter> { (activity: BaseActivity) ->
        ArticleRouterImpl(
            activity
        )
    }

    // TODO useless ??? not that the error... ??? Must check
    /**
     * Provides a ArticleListInterface interface from the instance of ArticleListFragment.
     */
    factory { (fragment: BaseBindingFragment) ->
        fragment as ArticleListInterface
    }
}