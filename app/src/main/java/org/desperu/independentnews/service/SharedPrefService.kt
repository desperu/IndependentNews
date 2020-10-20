package org.desperu.independentnews.service

import android.content.Context
import android.content.SharedPreferences
import org.desperu.independentnews.utils.INDEPENDENT_NEWS_PREFS

/**
 * Service for being able to access shared preferences of the application.
 */
interface SharedPrefService {

    /**
     * Returns the shared preferences object of the application.
     *
     * @return the shared preferences object of the application.
     */
    fun getPrefs(): SharedPreferences
}

/**
 * Implementation of the SharedPrefService which uses a Context instance to access the
 * shared preferences of the application.
 *
 * @property context The Context instance used to access the shared preferences of the application.
 *
 * @constructor Instantiates a new SharedPrefServiceImpl.
 *
 * @param context The Context instance used to access the shared preferences of the application to set.
 */
class SharedPrefServiceImpl(private val context: Context) : SharedPrefService {

    /**
     * Returns the shared preferences object of the application.
     *
     * @return the shared preferences object of the application.
     */
    override fun getPrefs(): SharedPreferences =
        context.getSharedPreferences(INDEPENDENT_NEWS_PREFS, Context.MODE_PRIVATE)
}