package org.desperu.independentnews.di.module.network

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.converter.htmlescape.HtmlEscapeStringConverter
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.desperu.independentnews.BuildConfig
import org.desperu.independentnews.utils.*
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

    /**
     * Provides the Retrofit instance.
     */
    factory<Retrofit> { (baseUrl: String, type: Int) ->
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

        retrofit.build()
    }
}