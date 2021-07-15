package org.desperu.independentnews.network.http

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.utils.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.IOException

/**
 * Send saved cookies in the request header.
 */
@Suppress("Deprecation")
class SendCookiesInterceptor : Interceptor, KoinComponent {

    // FOR DATA
    private val prefs: SharedPrefService = get()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val cookies = prefs.getPrefs().getStringSet(COOKIES_KEY, COOKIES_DEFAULT)

        cookies?.forEach {
            builder.addHeader(COOKIE_HEADER, it)
        }

        return chain.proceed(builder.build())
    }
}

/**
 * Save received cookies from the response in shared prefs.
 */
@Suppress("Deprecation")
class SaveCookiesInterceptor : Interceptor, KoinComponent {

    // FOR DATA
    private val prefs: SharedPrefService = get()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val setCookies = originalResponse.headers(SET_COOKIE_HEADER)

        if (setCookies.isNotEmpty()) {
            // Retrieve previous saved cookies
            val cookies = prefs.getPrefs().getStringSet(COOKIES_KEY, setOf())

            // Add new cookies from response
            setCookies.forEach {
                cookies?.add(it)
            }

            // Store all cookies
            prefs.getPrefs().edit().putStringSet(COOKIES_KEY, cookies).apply()
        }

        return originalResponse
    }

}

/**
 * Set cookies Interceptors, send saved cookies with new request
 * and store received cookies.
 *
 * @return the [OkHttpClient.Builder] with cookies interceptors.
 */
fun OkHttpClient.Builder.setCookieStore() : OkHttpClient.Builder {
    return this
        .addInterceptor(SendCookiesInterceptor())
        .addInterceptor(SaveCookiesInterceptor())
}