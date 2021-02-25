package org.desperu.independentnews.service

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat

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
     *
     * @throws Exception Drawable not found.
     */
    fun getDrawable(@DrawableRes drawableRes: Int): Drawable?

    /**
     * Returns the Color with the given unique identifier from the resources of the
     * application.
     *
     * @param colorRes the unique identifier of the Color resource.
     *
     * @return the Color with the given unique identifier from the resources of the
     * application.
     */
    fun getColor(@ColorRes colorRes: Int): Int
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
class ResourceServiceImpl(private val context: Context) : ResourceService {
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
     *
     * @throws Exception Drawable not found.
     */
    override fun getDrawable(@DrawableRes drawableRes: Int): Drawable =
        ResourcesCompat.getDrawable(context.resources, drawableRes, null)
            ?: throw Exception("Drawable not found")
    /**
     * Returns the Color with the given unique identifier from the resources of the
     * application.
     *
     * @param colorRes the unique identifier of the Color resource.
     *
     * @return the Color with the given unique identifier from the resources of the
     * application.
     */
    override fun getColor(@ColorRes colorRes: Int): Int =
        ResourcesCompat.getColor(context.resources, colorRes, null)
}