package org.desperu.independentnews.di.module.network

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.converter.htmlescape.HtmlEscapeStringConverter
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.desperu.independentnews.BuildConfig
import org.desperu.independentnews.network.http.setCookieStore
import org.desperu.independentnews.network.http.setCustomHeaders
import org.desperu.independentnews.utils.CLOUDFLARE_BYPASS
import org.desperu.independentnews.utils.REPORTERRE_BASE_URL
import org.desperu.independentnews.utils.STANDARD_HTTP_REQUEST
import org.desperu.independentnews.utils.XML
import org.koin.core.qualifier.qualifier
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
     * Provides the standard OkHttpClient,
     * with logging interceptor in Debug build config.
     */
    single(qualifier = qualifier(STANDARD_HTTP_REQUEST)) {
        val builder = OkHttpClient.Builder()
        if(BuildConfig.DEBUG) {
            builder.addInterceptor(get() as Interceptor)
        }
        builder.build()
    }

    /**
     * Provides the cloudflare bypass OkHttpClient, that :
     *
     * - use custom headers to fake a curl request,
     * - handle cookies, store and re-send cookies,
     * - follow all redirects, ssl or not,
     * - enable logging interceptor in Debug build config.
     */
    single(qualifier = qualifier(CLOUDFLARE_BYPASS)) {
        val builder = OkHttpClient.Builder()

        // Set custom headers and handle cookies.
        builder.setCustomHeaders()
        builder.setCookieStore()

        // Enable following all redirects.
        builder.followRedirects(true)
        builder.followSslRedirects(true)

        if(BuildConfig.DEBUG) {
            builder.addInterceptor(get() as Interceptor)
        }
        builder.build()
    }

    /**
     * Provides the Retrofit instance, handle specific request for Reporterre source,
     * with cloudflare bypass, and HTTP or XML, for RSS and Web response.
     */
    factory<Retrofit> { (baseUrl: String, type: Int) ->
        val qualifier =
            if (baseUrl == REPORTERRE_BASE_URL) CLOUDFLARE_BYPASS
            else STANDARD_HTTP_REQUEST

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(get(qualifier(qualifier)) as OkHttpClient)

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