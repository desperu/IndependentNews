package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.helpers.DialogHelper
import org.desperu.independentnews.helpers.DialogHelperImpl
import org.desperu.independentnews.helpers.SnackBarHelper
import org.desperu.independentnews.helpers.SnackBarHelperImpl
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to first start activity.
 */
val firstStartModule = module {

    /**
     * Provides a SnackBarHelper interface from the instance of FirstStartActivity.
     */
    single<SnackBarHelper>(override = true) { (activity: BaseActivity) ->
        SnackBarHelperImpl(
            activity
        )
    }

    /**
     * Provides a DialogHelper interface from the instance of FirstStartActivity.
     */
    single<DialogHelper>(override = true) { (activity: BaseActivity) ->
        DialogHelperImpl(
            activity
        )
    }
}