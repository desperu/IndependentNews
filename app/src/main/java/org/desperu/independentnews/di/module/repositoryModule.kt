package org.desperu.independentnews.di.module

import org.desperu.independentnews.repositories.*
import org.desperu.independentnews.repositories.database.*
import org.desperu.independentnews.repositories.network.BastamagRepository
import org.desperu.independentnews.repositories.network.BastamagRepositoryImpl
import org.desperu.independentnews.repositories.network.ReporterreRepository
import org.desperu.independentnews.repositories.network.ReporterreRepositoryImpl
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
            get(), get(), get(), get(), get()
        )
    }

    /**
     * Provides an ArticleRepository instance.
     */
    single<ArticleRepository> {
        ArticleRepositoryImpl(
            get(), get(), get(), get()
        )
    }

    /**
     * Provides a CssRepository instance.
     */
    single<CssRepository> {
        CssRepositoryImpl(get())
    }

    /**
     * Provides an SourceRepository instance.
     */
    single<SourceRepository> {
        SourceRepositoryImpl(
            get(), get(), get()
        )
    }

    /**
     * Provides an BastamagRepository instance.
     */
    single<BastamagRepository> {
        BastamagRepositoryImpl(
                get(), get(), get()
        )
    }

    /**
     * Provides an ReporterreRepository instance.
     */
    single<ReporterreRepository> {
        ReporterreRepositoryImpl(
            get(), get(), get()
        )
    }
}