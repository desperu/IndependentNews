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
     * @param imageUrl the image url to show in the Activity.
     */
    fun openShowImages(imageUrl: String)
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
     * @param imageUrl the image url to show in the Activity.
     */
    override fun openShowImages(imageUrl: String) =
        ShowImagesActivity.routeFromActivity(activity, arrayListOf(imageUrl), 0)
}