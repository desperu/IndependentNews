package org.desperu.independentnews.di.module

import org.desperu.independentnews.repositories.BastamagRepository
import org.desperu.independentnews.repositories.BastamagRepositoryImpl
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to repositories
 */
val repositoryModule = module {

    /**
     * Provides an BastamagRepository instance.
     */
    single<BastamagRepository> {
        BastamagRepositoryImpl(
                get(), get()
        )
    }
}