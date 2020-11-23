package org.desperu.independentnews.di.module

import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.service.ResourceServiceImpl
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.service.SharedPrefServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to services.
 */
val serviceModule = module {

    /**
     * Provides an SharedPrefService instance.
     */
    single<SharedPrefService> {
        SharedPrefServiceImpl(
            androidContext()
        )
    }

    /**
     * Provides an ResourceService instance.
     */
    single<ResourceService> {
        ResourceServiceImpl(
            androidContext()
        )
    }
}