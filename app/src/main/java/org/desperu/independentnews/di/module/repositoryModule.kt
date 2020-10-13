package org.desperu.independentnews.di.module

import org.desperu.independentnews.repositories.*
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to repositories.
 */
val repositoryModule = module {

    /**
     * Provides an IndependentNewsRepository instance.
     */
    single<IndependentNewsRepository> {
        IndependentNewsRepositoryImpl(
            get(), get(), get(), get()
        )
    }

    /**
     * Provides an SourceRepository instance.
     */
    single<SourceRepository> {
        SourceRepositoryImpl(
            get()
        )
    }

    /**
     * Provides an BastamagRepository instance.
     */
    single<BastamagRepository> {
        BastamagRepositoryImpl(
                get(), get()
        )
    }

    /**
     * Provides an ReporterreRepository instance.
     */
    single<ReporterreRepository> {
        ReporterreRepositoryImpl(
            get(), get()
        )
    }
}