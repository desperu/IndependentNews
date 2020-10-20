package org.desperu.independentnews.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Service for being able to access resources of the application.
 */
interface ResourceService{
    /**
     * Returns the String with the given unique identifier and format arguments from the resources
     * of the application.
     *
     * @param stringRes the unique identifier of the String resource.
     * @param formatArgs the format arguments to format the String.
     *
     * @return the String with the given unique identifier and format arguments from the resources
     *         of the application.
     */
    fun getString(@StringRes stringRes: Int, vararg formatArgs: Any): String

    /**
     * Returns the String array with the given unique identifier from the resources of the
     * application.
     *
     * @param arrayRes the unique identifier of the String array resource.
     *
     * @return the String array with the given unique identifier from the resources of the
     * application.
     */
    fun getStringArray(@ArrayRes arrayRes: Int): Array<String>

    /**
     * Returns the Drawable with the given unique identifier from the resources of the
     * application.
     *
     * @param drawableRes the unique identifier of the Drawable resource.
     *
     * @return the Drawable with the given unique identifier from the resources of the
     * application.
     */
    fun getDrawable(@DrawableRes drawableRes: Int): Drawable
}

/**
 * Implementation of the ResourceService which uses a Context instance to access the resources of
 * the application.
 *
 * @property context The Context instance used to access the resources of the application.
 *
 * @constructor Instantiates a new ResourceServiceImpl.
 *
 * @param context The Context instance used to access the resources of the application to set.
 */
class ResourceServiceImpl(private val context: Context) : ResourceService{
    /**
     * Returns the String with the given unique identifier and format arguments from the resources
     * of the application.
     *
     * @param stringRes the unique identifier of the String resource.
     * @param formatArgs the format arguments to format the String.
     *
     * @return the String with the given unique identifier and format arguments from the resources
     *         of the application.
     */
    override fun getString(stringRes: Int, vararg formatArgs: Any) = context.getString(stringRes, *formatArgs)

    /**
     * Returns the String array with the given unique identifier from the resources of the
     * application.
     *
     * @param arrayRes the unique identifier of the String array resource.
     *
     * @return the String array with the given unique identifier from the resources of the
     * application.
     */
    override fun getStringArray(arrayRes: Int): Array<String> = context.resources.getStringArray(arrayRes)

    /**
     * Returns the Drawable with the given unique identifier from the resources of the
     * application.
     *
     * @param drawableRes the unique identifier of the Drawable resource.
     *
     * @return the Drawable with the given unique identifier from the resources of the
     * application.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("deprecation")
    override fun getDrawable(@DrawableRes drawableRes: Int): Drawable = context.resources.getDrawable(drawableRes)
}