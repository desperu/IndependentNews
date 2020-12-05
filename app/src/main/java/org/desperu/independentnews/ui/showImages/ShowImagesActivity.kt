package org.desperu.independentnews.ui.showImages

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_show_images.*
import kotlinx.android.synthetic.main.app_bar.*
import org.desperu.independentnews.R
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.di.module.ui.showImagesModule
import org.desperu.independentnews.extension.design.bindColor
import org.desperu.independentnews.extension.design.bindDimen
import org.desperu.independentnews.ui.showImages.fragment.ImageFragment
import org.desperu.independentnews.utils.SYS_UI_HIDE
import org.desperu.independentnews.views.pageTransformer.DepthPageTransformer
import org.desperu.independentnews.views.pageTransformer.PageTransformerInterface
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

/**
 * The name of the argument to received image list for this Activity.
 */
const val IMAGE_LIST: String = "imageList"

/**
 * The name of the argument to received the position of the clicked image in the list.
 */
const val POSITION: String = "position"

/**
 * Activity to show estate's images.
 *
 * @constructor Instantiates a new ShowImagesActivity.
 */
class ShowImagesActivity: BaseActivity(showImagesModule), ShowImagesInterface {

    // FROM BUNDLE
    private val imageList: List<Any>? get() = intent.getParcelableArrayListExtra(IMAGE_LIST)
    private val position: Int? get() = intent.getIntExtra(POSITION, 0)

    // FOR DATA
    private lateinit var viewPager: ViewPager
    private lateinit var mAdapter: ShowImageAdapter
    private lateinit var pageTransformer: DepthPageTransformer
    private lateinit var sysUiHelper: SystemUiHelper

    /**
     * Companion object, used to redirect to this Activity.
     */
    companion object {

        /**
         * Redirects from an Activity to this Activity.
         *
         * @param activity the activity use to perform redirection.
         * @param imageList the images list to show in this activity.
         * @param position the position of the clicked image in the list.
         */
        fun routeFromActivity(activity: AppCompatActivity, imageList: ArrayList<Any>, position: Int) {
            val intent = Intent(activity, ShowImagesActivity::class.java)

            if (imageList.isNotEmpty())
                when (imageList[0]) {

                    is Int -> intent.putIntegerArrayListExtra(IMAGE_LIST,
                        imageList.map { it as Int } as ArrayList<Int>)

                    is String -> intent.putStringArrayListExtra(IMAGE_LIST,
                        imageList.map { it.toString() } as java.util.ArrayList<String>?)
                }

            intent.putExtra(POSITION, position)
            activity.startActivity(intent)
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_show_images

    override fun configureDesign() {
        configureKoinDependency()
        configureSystemDesign()
        configureAppBarDesign()
        showAppBarIcon(listOf(R.id.back_arrow_icon))
        configureViewPager()
        updateViewPager()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure koin dependency for show images activity.
     */
    private fun configureKoinDependency() {
        get<ShowImagesInterface> { parametersOf(this) }
        sysUiHelper = get { parametersOf(this) }
    }

    /**
     * Configure the system ui design, hide status bar and low nav bar.
     */
    private fun configureSystemDesign() = sysUiHelper.setDecorUiVisibility(SYS_UI_HIDE)

    /**
     * Configure App Bar design.
     */
    @Suppress("Deprecation")
    private fun configureAppBarDesign() {
        val color by bindColor(R.color.colorTransWhite)
        val actionBarSize by bindDimen(R.dimen.action_bar_height)
        val statusBarSize by bindDimen(R.dimen.status_bar_height)

        toolbar_title.setTextColor(color)
        appbar.apply {
            updateLayoutParams { height = (actionBarSize + statusBarSize).toInt() }
            setPadding(0, statusBarSize.toInt(), 0, 0)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            back_arrow_icon.imageTintList = ColorStateList.valueOf(color)
        else
            back_arrow_icon.setColorFilter(color)
    }

    /**
     * Configure View pager with Depth Page Transformer.
     */
    private fun configureViewPager() {
        viewPager = show_images_view_pager

        mAdapter = ShowImageAdapter(supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.adapter = mAdapter

        pageTransformer = DepthPageTransformer()
        viewPager.setPageTransformer(true, pageTransformer)
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean = getCurrentPage().dispatchTouchEvent(ev)

    // --------------
    // ACTION
    // --------------

    /**
     * On click back arrow icon menu.
     */
    @Suppress("unused_parameter")
    override fun onClickBackArrow(v: View) = onClickBackArrow()

    /**
     * Dispatch the motion event to the view pager to consume it.
     *
     * @param ev the motion event action to dispatch.
     *
     * @return true if the event was consumed, false if not.
     */
    override fun viewPagerOnTouchEvent(ev: MotionEvent?) = viewPager.dispatchTouchEvent(ev)

    // -----------------
    // UI
    // -----------------

    /**
     * Update view pager data, and set current item.
     */
    private fun updateViewPager() {
        imageList?.let { mAdapter.updateImageList(it) }
        mAdapter.notifyDataSetChanged()
        position?.let { viewPager.currentItem = it }
    }

    /**
     * Show app bar when click on fragment.
     *
     * @param toShow true to show, false to hide.
     */
    override fun showAppBar(toShow: Boolean) {
        appbar.visibility = if (toShow) View.VISIBLE else View.INVISIBLE
    }

    // --- GETTERS ---

    /**
     * Returns the current page instance of the view pager.
     * @return the current page instance of the view pager.
     */
    private fun getCurrentPage(): ImageFragment =
        mAdapter.instantiateItem(show_images_view_pager, viewPager.currentItem) as ImageFragment

    /**
     * Returns the current value of the page position.
     */
    override fun getPosition(): Float = (pageTransformer as PageTransformerInterface).getPosition()
}