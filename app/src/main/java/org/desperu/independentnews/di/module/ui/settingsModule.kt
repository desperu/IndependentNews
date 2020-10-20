package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.ui.settings.SettingsInterface
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to settings activity.
 */
val settingsModule = module {

    /**
     * Provides a SettingsInterface from the instance of SettingsActivity.
     */
    single { (activity: BaseBindingActivity) ->
        activity as SettingsInterface
    }
}