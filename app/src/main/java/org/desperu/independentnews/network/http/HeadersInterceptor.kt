package org.desperu.independentnews.network.http

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.desperu.independentnews.utils.CURL_HEADERS
import java.io.IOException

/**
 * Set custom headers from the response in shared prefs.
 * Fake a curl request to bypass clouflare protect.
 */
class SetHeadersInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        CURL_HEADERS.forEach {
            builder.header(it.first, it.second)
        }

        return chain.proceed(builder.build())
    }
}

/**
 * Handle received headers from the response.
 */
class HandleHeadersInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        return chain.proceed(chain.request())
    }

}

/**
 * Set headers Interceptors, set custom headers in new request
 * and handle received headers.
 *
 * @return the [OkHttpClient.Builder] with headers interceptors.
 */
fun OkHttpClient.Builder.setCustomHeaders() : OkHttpClient.Builder {
    return this
        .addInterceptor(SetHeadersInterceptor())
        .addInterceptor(HandleHeadersInterceptor())
}