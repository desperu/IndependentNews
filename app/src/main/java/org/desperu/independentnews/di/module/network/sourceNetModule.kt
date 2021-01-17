package org.desperu.independentnews.di.module.network

import org.desperu.independentnews.network.bastamag.BastamagRssService
import org.desperu.independentnews.network.bastamag.BastamagWebService
import org.desperu.independentnews.network.multinationales.MultinationalesRssService
import org.desperu.independentnews.network.multinationales.MultinationalesWebService
import org.desperu.independentnews.network.reporterre.ReporterreRssService
import org.desperu.independentnews.network.reporterre.ReporterreWebService
import org.desperu.independentnews.utils.*
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Koin module which provides dependencies related to source network.
 */
val sourceNetModule = module {

    // --- Bastamag ---

    /**
     * Provides the RSS service for Bastamag.
     */
    single<BastamagRssService> {
        (get<Retrofit> { parametersOf(BASTAMAG_BASE_URL, XML) }).create(BastamagRssService::class.java)
    }

    /**
     * Provides the Web service for Bastamag.
     */
    single<BastamagWebService> {
        (get<Retrofit> { parametersOf(BASTAMAG_BASE_URL, HTML) }).create(BastamagWebService::class.java)
    }

    // --- Reporterre ---

    /**
     * Provides the RSS service for Reporterre.
     */
    single<ReporterreRssService> {
        (get<Retrofit> { parametersOf(REPORTERRE_BASE_URL, XML) }).create(ReporterreRssService::class.java)
    }

    /**
     * Provides the Web service for Reporterre.
     */
    single<ReporterreWebService> {
        (get<Retrofit> { parametersOf(REPORTERRE_BASE_URL, HTML) }).create(ReporterreWebService::class.java)
    }

    // --- Multinationales ---

    /**
     * Provides the RSS service for Multinationales.
     */
    single<MultinationalesRssService> {
        (get<Retrofit> { parametersOf(MULTINATIONALES_BASE_URL, XML) }).create(MultinationalesRssService::class.java)
    }

    /**
     * Provides the Web service for Multinationales.
     */
    single<MultinationalesWebService> {
        (get<Retrofit> { parametersOf(MULTINATIONALES_BASE_URL, HTML) }).create(MultinationalesWebService::class.java)
    }
}