package org.desperu.independentnews

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.desperu.independentnews.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.KoinContextHandler
import org.koin.core.context.startKoin

class IndependentNews : Application() {

    /**
     * Initializes the application, by adding strict mode and starting koin.
     */
    override fun onCreate() {
        super.onCreate()
        if (KoinContextHandler.getOrNull() == null) { // For Robolectric in unit test. // TODO remove??
            startKoin {
                androidLogger()
                androidContext(this@IndependentNews)
//                modules(listOf(viewModelModule))
            }
        }

        // Support for kitkat bug with setImageDrawable() and vector drawable.
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}