package org.desperu.independentnews.views.webview.skeleton

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.transition.Fade
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.eudycontreras.boneslibrary.bindings.requireSkeletonDrawable
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.helpers.AsyncHelper.waitCondition
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.design.ScrollHandlerInterface
import org.desperu.independentnews.views.webview.MyWebView
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * States of the skeleton web view.
 */
private const val LOADING_SKELETON = 1
private const val HIDE_SKELETON = 2

/**
 * Skeleton Web View that fake web view content loading with text view and extend from [MyWebView].
 * Require Api > Marshmallow, for Skeleton support.
 * Need to be wrapped under a ViewGroup to properly works, if not throw an exception.
 *
 * @constructor instantiates a new SkeletonWebView.
 */
@RequiresApi(Build.VERSION_CODES.M)
open class SkeletonWebView @JvmOverloads constructor (
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MyWebView(context, attrs, defStyleAttr), KoinComponent {

    // FOR COMMUNICATION
    private val scrollHandler: ScrollHandlerInterface get() = get()

    // FOR VIEWS
    private lateinit var skeletonBindingView: ViewDataBinding
    private lateinit var viewGroup: ViewGroup
    private val skeletonWebViewContainer: ViewGroup by bindView(R.id.skeleton_web_view_container)

    // FOR DATA
    private lateinit var originalWebViewClient: WebViewClient
    private val viewModel get() = get<ShowArticleInterface>().viewModel
    private var state = HIDE_SKELETON
    private var index = 0

    // --------------
    // CONFIGURATION
    // --------------

    init {
        doOnPreDraw { setViewGroup() }
    }

    /**
     * Set the view group of this SkeletonWebView
     *
     * @throws IllegalStateException if it's not a ViewGroup.
     */
    private fun setViewGroup() {
        viewGroup = when (val parent = parent) {
            is LinearLayout -> {
                index = viewGroup.indexOfChild(this)
                parent
            }
            is ViewGroup -> parent
            else -> throw IllegalStateException("The parent of the SkeletonWebView" +
                    " is not a ViewGroup, it is : ${parent.javaClass.simpleName}")
        }

//        if (parent is ViewGroup)
//            viewGroup = parent as ViewGroup
//        else
//            throw IllegalStateException("The parent of the SkeletonWebView" +
//                    " is not a ViewGroup, it is : ${parent.javaClass.simpleName}")
    }

    /**
     * Set the Skeleton Binding View with the skeleton view model.
     */
    private fun setSkeletonBindingView() {
        skeletonBindingView = DataBindingUtil.inflate(
            (context as Activity).layoutInflater,
            R.layout.skeleton_webview,
            viewGroup,
            false
        )

        skeletonBindingView.setVariable(
            org.desperu.independentnews.BR.viewModel,
            viewModel as SkeletonWebViewBinding
        )
    }

    // --------------
    // SHOW / HIDE
    // --------------

    /**
     * Show the skeleton web view only if not already shown,
     * add it at the same place of the web view in its parent view group.
     */
    private fun showSkeleton() {
        if (state != LOADING_SKELETON) {
            viewModel.isLoading.set(true)
            if (!::skeletonBindingView.isInitialized) setSkeletonBindingView()
            visibility = INVISIBLE
            viewGroup.addView(skeletonBindingView.root, index)
            state = LOADING_SKELETON
        }
    }

    /**
     * Hide the skeleton web view, remove from its parent view group, after has scroll.
     */
    private fun hideSkeleton() {
        if (state != HIDE_SKELETON && ::skeletonBindingView.isInitialized) {
            waitCondition(findViewTreeLifecycleOwner()?.lifecycleScope ?: return,
                2000L,
                { scrollHandler.hasScroll } // TODO use toApplyCss or create onCssApplied
            ) {
                // Add fade transition
                val fade = Fade().apply { duration = viewModel.duration }
                TransitionManager.beginDelayedTransition(viewGroup, fade)

                viewModel.isLoading.set(false)
                viewGroup.removeView(skeletonBindingView.root)

                TransitionManager.beginDelayedTransition(viewGroup, fade)
                visibility = VISIBLE

//                fade.doOnEnd { && doOnCancel
                state = HIDE_SKELETON
                skeletonWebViewContainer.requireSkeletonDrawable().dispose()
                requireSkeletonDrawable().dispose()
                // TODO kill after each playing ot prevent memory leak...
                // skeletonBindingView = null
//                }
            }
        }
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun setWebViewClient(client: WebViewClient) {
        // TODO Else use content loading bar ????

        originalWebViewClient = client
        skeletonWebViewClient
        super.setWebViewClient(skeletonWebViewClient)
    }

    override fun destroy() {
        // TO prevent leak from SkeletonManager.owner on View.mContext
        requireSkeletonDrawable().dispose()
        skeletonWebViewContainer.requireSkeletonDrawable().dispose()
        // skeletonBindingView = null
        super.destroy()
    }

    // --------------
    // WEB CLIENT
    // --------------

    /**
     * Skeleton web view client, to support skeleton features and not override other configuration.
     */
    private val skeletonWebViewClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            originalWebViewClient.onPageStarted(view, url, favicon)
            showSkeleton()
        }

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            originalWebViewClient.onPageCommitVisible(view, url)
            hideSkeleton()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            originalWebViewClient.onPageFinished(view, url)
            hideSkeleton()
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return originalWebViewClient.shouldOverrideUrlLoading(view, request)
        }
    }
}