package org.desperu.independentnews.ui.showArticle

import androidx.appcompat.app.AppCompatActivity
import org.desperu.independentnews.ui.showImages.ShowImagesActivity

/**
 * The image router that allows redirection of the user.
 */
interface ImageRouter {

    /**
     * Redirects the user to the Show Images Activity.
     *
     * @param imageData the image data, url or integer, to show in the Activity.
     */
    fun openShowImages(imageData: ArrayList<Any>)
}

/**
 * Implementation of the ImageRouter.
 *
 * @property activity the Activity that is used to perform redirection.
 *
 * @constructor Instantiates a new ImageRouterImpl.
 *
 * @param activity the Activity that is used to perform redirection to set.
 */
class ImageRouterImpl(private val activity: AppCompatActivity): ImageRouter {

    /**
     * Redirects the user to the Show Images Activity.
     * @param imageData the image data, url or integer, to show in the Activity.
     */
    override fun openShowImages(imageData: ArrayList<Any>) =
        ShowImagesActivity.routeFromActivity(activity, imageData, 0)
}