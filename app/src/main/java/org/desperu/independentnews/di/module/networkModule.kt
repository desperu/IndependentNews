package org.desperu.independentnews.di.module

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.converter.htmlescape.HtmlEscapeStringConverter
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.desperu.independentnews.BuildConfig
import org.desperu.independentnews.network.bastamag.BastamagRssService
import org.desperu.independentnews.network.bastamag.BastamagWebService
import org.desperu.independentnews.utils.BASTAMAG_BASE_URL
import org.desperu.independentnews.utils.HTML
import org.desperu.independentnews.utils.XML
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Koin module which provides dependencies related to network.
 */
val networkModule = module {

    /**
     * Provides the logging interceptor.
     */
    single<Interceptor> {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        logging
    }

    /**
     * Provides the OkHttpClient.
     */
    single {
        val builder = OkHttpClient.Builder()
        if(BuildConfig.DEBUG){
            builder.addInterceptor(get() as Interceptor)
        }
        builder.build()
    }

//    /**
//     * Provides the Retrofit instance.
//     */
//    single<Retrofit> {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(get() as OkHttpClient)
//            .addConverterFactory(TikXmlConverterFactory.create(
//                TikXml.Builder()
//                    .exceptionOnUnreadXml(false)
//                    .addTypeConverter(String::class.java, HtmlEscapeStringConverter())
//                    .build()
//            ))
//            .build()
//    }

    /**
     * Provides the Retrofit instance.
     */
    factory<Retrofit> { (baseUrl: String, type: Int) -> // TODO test HTML with converter, should rocks
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(get() as OkHttpClient)

        if (type == XML)
            retrofit.addConverterFactory(
                TikXmlConverterFactory.create(
                    TikXml.Builder()
                        .exceptionOnUnreadXml(false)
                        .addTypeConverter(String::class.java, HtmlEscapeStringConverter())
                        .build()
                )
            )

        retrofit.build() // TODO java.net.SocketTimeoutException: SSL handshake timed out from OkkHttp ??
    }

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
}