package org.desperu.independentnews.di.module

import org.desperu.independentnews.service.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to services.
 */
val serviceModule = module {

    /**
     * Provides a SharedPrefService instance.
     */
    single<SharedPrefService> {
        SharedPrefServiceImpl(
            androidContext()
        )
    }

    /**
     * Provides a ResourceService instance.
     */
    single<ResourceService> {
        ResourceServiceImpl(
            androidContext()
        )
    }
}