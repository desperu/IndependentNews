package org.desperu.independentnews.ui.showImages.fragment

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.design.getScreenRect
import org.desperu.independentnews.extension.design.setScale
import org.desperu.independentnews.ui.showImages.ShowImagesInterface
import org.desperu.independentnews.utils.LOW_NAV_AND_STATUS_BAR
import org.desperu.independentnews.utils.SYS_UI_HIDE
import org.desperu.independentnews.views.GestureImageView
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * The name of the argument to received the image url or id in this fragment.
 */
const val IMAGE_URL: String = "showImage"
const val IMAGE_ID: String = "imageId"

/**
 * Image scale and system ui show delay values.
 */
private const val MIN_SCALE = 1.0f
private const val MIDDLE_SCALE = 2.0f
private const val MAX_SCALE = 10.0f
private const val SHOW_DELAY = 4000L

/**
 * Fragment image used to display the image with all gestures enabled,
 * zoom, move into the image, click, double click, wrap into a view pager.
 *
 * @constructor Instantiates a new ImageFragment.
 */
class ImageFragment: BaseBindingFragment() {

    // FOR DATA
    private val binding get() = viewBinding!!
    private val viewModel: ImageViewModel by viewModel { parametersOf(imageData) }
    private val imageData: Any
        get() = arguments?.getString(IMAGE_URL) ?: arguments?.getInt(IMAGE_ID) ?: ""

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

    // View Pager values to dispatch motion event
    private var isVpEvent = false
    private val vpPosition get() = showImagesInterface.getPosition()
    private val inVpTransition get() = (vpPosition - vpPosition.roundToInt()) != 0.0f

    // Motion Event values
    private var ev: MotionEvent? = null
    private val lastIndex get() = ev?.historySize?.minus(1) ?: 0
    private var dirEvStartIndex = 0
    private var minX = 0.0f
    private var maxX = 0.0f

    // Activity interface
    private val showImagesInterface: ShowImagesInterface get() = get()

    // System ui
    private val systemUiHelper: SystemUiHelper get() = get()
    private val backArrow get() =  activity?.back_arrow_icon
    private val sysUiShow get() = activity?.appbar?.isVisible
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Companion object, used to create new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set the image url bundle.
         * @param imageData the image data, url or id to load.
         * @return the new instance of ImageFragment.
         */
        fun newInstance(imageData: Any): ImageFragment {
            val imageFragment = ImageFragment()
            imageFragment.arguments = Bundle()

            when(imageData) {
                is String -> imageFragment.arguments?.putString(IMAGE_URL, imageData)
                is Int -> imageFragment.arguments?.putInt(IMAGE_ID, imageData)
            }

            return imageFragment
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
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_image, container, false)
        binding.setVariable(org.desperu.independentnews.BR.viewModel, viewModel)
        return binding.root
    }

    /**
     * Configure global and scale gesture listeners.
     */
    private fun configureGestureDetector() {
        gestureDetector = GestureDetector(context, gestureListener)
        scaleGestureDetector = ScaleGestureDetector(context, scaleListener)
    }

    // -----------------
    // MANAGE EVENTS
    // -----------------

    /**
     * Dispatch the Motion Event between the three listeners :
     *
     * - Gesture listener (tap, double tap and translation)
     * - Scale listener (for zoom)
     * - View Pager listener (to handle pages)
     *
     * To handle the events, we take care about zoom state, image position,
     * event direction and direction changes, pages position.
     *
     * This function doesn't consume the events, it only dispatch them,
     * and handle listeners and global gesture coordination.
     *
     * @author Desperu
     *
     * @param ev the motion event to dispatch between listeners.
     *
     * @return true if the event was consumed, false otherwise.
     */
    internal fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        this.ev = ev // Save event for later use
        val hitRect = hitRect // Store hitRect for best perf, get only once

        when (ev?.action) {

            MotionEvent.ACTION_DOWN -> {
                handleMinMaxX(ev, false)
            }

            MotionEvent.ACTION_MOVE -> {
                handleMinMaxX(ev, true)

                val directionEvFirstX = getEventDirectionFirstX(ev)

                val canTransToLeft = canTranslateToLeft(hitRect)
                val eventToRight = ev.x > directionEvFirstX

                val canTransToRight = canTranslateToRight(hitRect)
                val eventToLeft = ev.x < directionEvFirstX

                    // If image can not translate to the left and event go to the right
                if (!canTransToLeft && eventToRight
                    // If image can not translate to the right and event go to the left
                    || !canTransToRight && eventToLeft) {

                    // It's a view pager event, so
                    // If it's not already one, start as it
                    if (!isVpEvent) startVpEvent(ev)
//                    isVpEvent = true

                    // If event direction has changed, image is zoomed and it's a view pager event
                } else if (hasDirectionChanged(ev) && isZoomed && isVpEvent) { // Could be removed to be equal as built in android app

                    // Close it.
                    closeVpEvent(ev)
                }
            }
            // Handle after in onTouchEvent, because we need to send the end event
            // to the listener and close the view pager event after only.
//            MotionEvent.ACTION_UP -> {}
//            MotionEvent.ACTION_CANCEL -> {}
        }

        onTouchEvent(ev)

        return false
    }

    /**
     * Provides the motion event to the listeners.
     *
     * @param ev the motion event to provide.
     */
    private fun onTouchEvent(ev: MotionEvent?): Boolean {

        gestureDetector.onTouchEvent(ev)
        scaleGestureDetector.onTouchEvent(ev) // Always return true why ???

        if (isVpEvent) {
            showImagesInterface.viewPagerOnTouchEvent(ev) // Always return true ...
            if (ev?.action == MotionEvent.ACTION_UP) isVpEvent = false
            if (ev?.action == MotionEvent.ACTION_CANCEL) isVpEvent = false
        }

        return true
    }

    /**
     * Start the motion event as view pager event, provide future event to vp listener.
     *
     * @param ev the motion event to provide.
     */
    private fun startVpEvent(ev: MotionEvent?) {
        val startEv = MotionEvent.obtain(ev)
        startEv?.action = MotionEvent.ACTION_DOWN
        showImagesInterface.viewPagerOnTouchEvent(startEv)

        isVpEvent = true
    }

    /**
     * Close the motion event as view pager event, stop provide future event to vp listener.
     *
     * @param ev the motion event to provide.
     */
    internal fun closeVpEvent(ev: MotionEvent?) {
        val closeEv = MotionEvent.obtain(ev ?: this.ev)
        closeEv.action = MotionEvent.ACTION_UP
        showImagesInterface.viewPagerOnTouchEvent(closeEv)

        isVpEvent = false
    }

    // -----------------
    // LISTENERS
    // -----------------

    /**
     * Gesture listener, to handle user action on fragment, single tap, double tap and scroll.
     */
    private val gestureListener = object : SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (inVpTransition) return false

            val backArrowRect = backArrow?.getScreenRect()
            val isArrowClick = e?.let { backArrowRect?.contains(it.rawX.toInt(), it.rawY.toInt()) }
            if (isArrowClick == true) showImagesInterface.onClickBackArrow(backArrow!!)

            // show or hide nav, status and action bars
            showSystemUi(sysUiShow == false)

            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            if (inVpTransition) return false

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

                    // Check that we are not in a view pager transition
                    if (canScrollHorizontally(hitRect) && !inVpTransition) {
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
            closeVpEvent(ev)

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

    /**
     * Handle min and max X values, save or reset, depends of toSave value.
     *
     * @param toSave true to save, false to reset values.
     */
    private fun handleMinMaxX(ev: MotionEvent, toSave: Boolean) {
        if (toSave) {
            // Store min and max X values
            if (ev.x < minX) minX = ev.x
            if (ev.x > maxX) maxX = ev.x
        } else {
            // Reset min and max X values
            minX = screenWidth.toFloat()
            maxX = 0.0f
        }
    }

    /**
     * Find the index of the given X value into the historical list of the motion event.
     *
     * @param ev the motion event for which search the index.
     *
     * @return the index of the given X value, 0 if not find.
     */
    private fun findXIndex(ev: MotionEvent?, x: Float): Int {
        if (x == ev?.x) return lastIndex// - 1 // Better always always true

        for (i in 0..lastIndex) {
            val oldX = ev?.getHistoricalX(i)
            if (oldX == x) return i
        }

        return 0
    }

    /**
     * Returns true if the motion event has change of direction false otherwise.
     *
     * @param ev the motion event for which determine if has change of direction.
     *
     * @return true if the motion event has change of direction, false otherwise.
     */
    private fun hasDirectionChanged(ev: MotionEvent?): Boolean {
        val minXIndex = findXIndex(ev, minX)
        val maxXIndex = findXIndex(ev, maxX)

        setEventDirectionStartIndex(minXIndex, maxXIndex)

        return minXIndex != lastIndex || maxXIndex != lastIndex
    }

    /**
     * Set the event direction start index value. This is used when the event direction change,
     * to be able to retrieved the X start value at the direction change.
     *
     * @param minXIndex the minimum X index.
     * @param maxXIndex the maximum X index.
     */
    private fun setEventDirectionStartIndex(minXIndex: Int, maxXIndex: Int) {
        dirEvStartIndex = when {
            minXIndex != lastIndex -> minXIndex
            maxXIndex != lastIndex -> maxXIndex
            else -> 0
        }
    }

    /**
     * Returns the start X value for the motion event with the a constant direction,
     * to right or to left fo example.
     *
     * @param ev the motion event for which return the first X direction event.
     *
     * @return X value of the start of the constant direction event.
     */
    private fun getEventDirectionFirstX(ev: MotionEvent): Float =
        if (ev.historySize > 1)
            ev.getHistoricalAxisValue(MotionEvent.AXIS_X, dirEvStartIndex)
        else
            ev.x

    // --- CAN GETTERS ---

    /**
     * Return true if the image drawable can scroll horizontally, false otherwise.
     */
    private fun canScrollHorizontally(hitRect: Rect): Boolean = hitRect.width() > screenWidth

    /**
     * Return true if the image drawable can scroll vertically, false otherwise.
     */
    private fun canScrollVertically(hitRect: Rect): Boolean = hitRect.height() > screenHeight

    /**
     * Return true if the image drawable can translate to the left, false otherwise.
     */
    private fun canTranslateToLeft(hitRect: Rect): Boolean = hitRect.left < 0

    /**
     * Return true if the image drawable can translate to the right, false otherwise.
     */
    private fun canTranslateToRight(hitRect: Rect): Boolean = hitRect.right > screenWidth
}