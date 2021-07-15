package org.desperu.independentnews.network.http

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import org.desperu.independentnews.utils.CLOUDFLARE_BYPASS
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.qualifier
import java.io.InputStream

/**
 * App Glide Module, for whole application, that set a custom OkHttpClient for all glide request.
 */
@GlideModule
@Excludes(OkHttpLibraryGlideModule::class) // initialize OkHttp manually
class AppGlideModule : AppGlideModule(), KoinComponent {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)

        // Use custom OkHttpClient for all request.
        val httpClient: OkHttpClient = get(qualifier(CLOUDFLARE_BYPASS))

        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(httpClient)
        )
    }
}