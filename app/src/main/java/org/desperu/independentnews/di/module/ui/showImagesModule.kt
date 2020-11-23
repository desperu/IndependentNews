package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.helpers.SystemUiHelperImpl
import org.desperu.independentnews.ui.showImages.ShowImagesInterface
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to show images activity.
 */
val showImagesModule = module {

    /**
     * Provides a ShowImagesInterface from the instance of ShowImagesActivity.
     */
    single { (activity: BaseActivity) ->
        activity as ShowImagesInterface
    }

    /**
     * Provides a SystemUiHelper from the instance of the given Activity.
     */
    single<SystemUiHelper>(override = true) { (activity: BaseActivity) ->
        SystemUiHelperImpl(activity)
    }
}