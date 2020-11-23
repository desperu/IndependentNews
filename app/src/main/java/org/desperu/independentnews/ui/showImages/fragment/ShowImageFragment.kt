package org.desperu.independentnews.ui.showImages.fragment

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_image.*
import org.desperu.independentnews.R
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.databinding.FragmentImageBinding
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.design.setScale
import org.desperu.independentnews.ui.showImages.ShowImagesInterface
import org.desperu.independentnews.utils.LOW_NAV_AND_STATUS_BAR
import org.desperu.independentnews.utils.SYS_UI_HIDE
import org.desperu.independentnews.views.GestureImageView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.max
import kotlin.math.min

/**
 * The name of the argument to received the image url in this fragment.
 */
const val IMAGE_URL: String = "showImage"

/**
 * Image scale and system ui show delay values.
 */
private const val MIN_SCALE = 1.0f
private const val MIDDLE_SCALE = 2.0f
private const val MAX_SCALE = 10.0f
private const val SHOW_DELAY = 4000L

/**
 * Fragment to show image.
 *
 * @constructor Instantiates a new ShowImageFragment.
 */
class ShowImageFragment: BaseBindingFragment() {

    // FOR DATA
    private lateinit var binding: FragmentImageBinding
    private val viewModel: ShowImageViewModel by viewModel { parametersOf(imageUrl) }
    private val imageUrl: String get() = arguments?.getString(IMAGE_URL) ?: ""

    // FOR IMAGE GESTURE
    // Detectors instances
    private lateinit var gestureDetector: GestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    // Root layout, for size
    private val root: View by bindView(R.id.show_image_root)
    private val screenWidth get() = root.width
    private val screenHeight get() = root.height
    // GestureImageView instance and Image drawable real rect position value.
    private val image: GestureImageView by bindView(R.id.show_image_view)
    private val hitRect get() = image.run { Rect().apply(::getHitRect) }
    // Scale values, for zoom
    private val minScale get() = show_image_view?.scaleFactor ?: MIN_SCALE
    private val middleScale get() = minScale * MIDDLE_SCALE
    private val maxScale get() = minScale * MAX_SCALE
    private var scaleFactor: Float = minScale
    private val isZoomed: Boolean get() = image.scaleX > minScale
    // Activity interface and boolean to dispatch motion event.
    private val showImagesInterface: ShowImagesInterface by inject()
    private var isVpEvent = false
    // System ui
    private val systemUiHelper: SystemUiHelper by inject()
    private val backArrow get() =  activity?.back_arrow_icon
    private val sysUiShow get() = activity?.appbar?.isVisible
    private val handler = Handler()

    /**
     * Companion object, used to create new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set the image url bundle.
         * @param imageUrl the image url to load.
         * @return the new instance of ShowImageFragment.
         */
        fun newInstance(imageUrl: String): ShowImageFragment {
            val showImageFragment = ShowImageFragment()
            showImageFragment.arguments = Bundle()
            showImageFragment.arguments?.putString(IMAGE_URL, imageUrl)
            return showImageFragment
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        configureGestureDetector()
    }

    override fun updateDesign() {}

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure data binding with view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Configure global and scale gesture listeners.
     */
    private fun configureGestureDetector() {
        gestureDetector = GestureDetector(context, gestureListener)
        scaleGestureDetector = ScaleGestureDetector(context, scaleListener)
    }

    internal fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (!isVpEvent) {
            gestureDetector.onTouchEvent(ev)
            scaleGestureDetector.onTouchEvent(ev) // Always return true why ???
        }

        if (isVpEvent || ev?.action == MotionEvent.ACTION_DOWN || !isZoomed) {
            showImagesInterface.viewPagerOnTouchEvent(ev) // Always return true ...
            if (ev?.action == MotionEvent.ACTION_UP) isVpEvent = false
            if (ev?.action == MotionEvent.ACTION_CANCEL) isVpEvent = false
        }

        return true
    }

    // -----------------
    // LISTENERS
    // -----------------

    /**
     * Gesture listener, to handle user action on fragment, single tap, double tap and scroll.
     */
    private val gestureListener = object : SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            val backArrowRect = backArrow?.run { Rect().apply(::getDrawingRect) }
            val isArrowClick = e?.let { backArrowRect?.contains(it.x.toInt(), it.y.toInt()) }
            if (isArrowClick == true) showImagesInterface.onClickBackArrow(backArrow!!)

            // show or hide nav, status and action bars
            showSystemUi(sysUiShow == false)

            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            scaleFactor = if (!isZoomed) middleScale else minScale
            image.setScale(scaleFactor)
            image.postOnAnimation {
                if (!isZoomed) {
                    image.translationX = 0.0f
                    image.translationY = 0.0f
                }
            }

            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {

            var translate = 0

            if (isZoomed) {
                image.apply {

                    // Get image drawable size
                    val hitRect = hitRect

                    if (canScrollHorizontally(hitRect)) {
                        translate = max(
                            min(0, hitRect.left),
                            min(distanceX.toInt(), hitRect.right - screenWidth)
                        )
                        translationX -= translate
                    }

                    if (canScrollVertically(hitRect)) {
                        translate = max(
                            min(0, hitRect.top),
                            min(distanceY.toInt(), hitRect.bottom - screenHeight)
                        )
                        translationY -= translate
                    }

                    // need to handle all gesture in this listener vp here, for perfect dispatch
                    // PageTransformer interface ... position -> distanceX / 2 / screenWidth

                    val isDistPos = distanceX > 0
                    val canVpToLeft = hitRect.left == 0 && !isDistPos
                    val canVpToRight = hitRect.right == screenWidth && isDistPos

                    if (translate == 0 && (canVpToLeft || canVpToRight)) {
                        val ev = MotionEvent.obtain(e2)
                        ev.action = MotionEvent.ACTION_DOWN
                        showImagesInterface.viewPagerOnTouchEvent(ev)
                        isVpEvent = true
                    }
                }
            }

            return translate != 0
        }
    }

    /**
     * Scale gesture listener, to handle scale user action.
     */
    private val scaleListener = object : SimpleOnScaleGestureListener() {

        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = max(minScale, min(scaleFactor, maxScale))
            image.setScale(scaleFactor)

            correctImagePosition()

            return true
        }
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Show or hide all system ui, status, navigation and action bar. Depends of toShow value.
     *
     * @param toShow true to show, false to hide.
     */
    private fun showSystemUi(toShow: Boolean) {
        activity?.let {
            if (toShow) {
                systemUiHelper.removeDecorUiFlag(LOW_NAV_AND_STATUS_BAR)
                showImagesInterface.showAppBar(true)
                handler.postDelayed({ showSystemUi(false) }, SHOW_DELAY)
            } else {
                systemUiHelper.setDecorUiVisibility(SYS_UI_HIDE)
                showImagesInterface.showAppBar(false)
                handler.removeCallbacksAndMessages(null)
            }
        }
    }

    /**
     * Returns true if the image drawable can scroll horizontally, false otherwise.
     *
     * @return true if the image drawable can scroll horizontally, false otherwise.
     */
    private fun canScrollHorizontally(hitRect: Rect): Boolean = hitRect.width() > screenWidth

    /**
     * Returns true if the image drawable can scroll vertically, false otherwise.
     *
     * @return true if the image drawable can scroll vertically, false otherwise.
     */
    private fun canScrollVertically(hitRect: Rect): Boolean = hitRect.height() > screenHeight

    /**
     * Correct image drawable position, needed for scaling and prevent image leave screen side.
     */
    private fun correctImagePosition() {
        // Get image drawable rect
        val hitRect = hitRect

        if (canScrollHorizontally(hitRect)) {
            if (hitRect.left > 0) image.translationX -= hitRect.left // Left into screen
            if (hitRect.right < screenWidth) image.translationX += screenWidth - hitRect.right // Right into screen
        }

        if (canScrollVertically(hitRect)) {
            if (hitRect.top > 0) image.translationY -= hitRect.top // Top into screen
            if (hitRect.bottom < screenHeight) image.translationY += screenHeight - hitRect.bottom // Bottom into screen
        }
    }
}