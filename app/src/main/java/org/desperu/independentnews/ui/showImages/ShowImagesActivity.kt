package org.desperu.independentnews.ui.showImages

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_show_images.*
import kotlinx.android.synthetic.main.app_bar.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.extension.design.bindColor
import org.desperu.independentnews.extension.design.bindDimen
import org.desperu.independentnews.views.DepthPageTransformer

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
class ShowImagesActivity: BaseActivity() {

    // FROM BUNDLE
    private val imageList: List<String>? get() = intent.getStringArrayListExtra(IMAGE_LIST)
    private val position: Int? get() = intent.getIntExtra(POSITION, 0)

    // FOR DATA
    private lateinit var viewPager: ViewPager
    private lateinit var mAdapter: ShowImageAdapter

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
        fun routeFromActivity(activity: AppCompatActivity, imageList: ArrayList<String>, position: Int) {
            activity.startActivity(
                Intent(activity, ShowImagesActivity::class.java)
                    .putStringArrayListExtra(IMAGE_LIST, imageList)
                    .putExtra(POSITION, position)
            )
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_show_images

    override fun configureDesign() {
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
     * Configure the system ui design.
     */
    private fun configureSystemDesign() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Layout draw under status bar
//                        or View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Re hide status and/or navigation bar
//                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hide navigation bar
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Layout draw under navigation bar
                        or View.SYSTEM_UI_FLAG_LOW_PROFILE // Alpha 0.5 the navigation bar
                )
    }

    /**
     * Configure App Bar design.
     */
    @Suppress("Deprecation")
    private fun configureAppBarDesign() {
        val color by bindColor(R.color.colorTransWhite)
        val actionBarSize by bindDimen(R.dimen.action_bar_height)
        val statusBarSize by bindDimen(R.dimen.status_bar_height)

        toolbar_title.setTextColor(color)
        appbar_container.updateLayoutParams { height = (actionBarSize + statusBarSize).toInt() }
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
        viewPager.setPageTransformer(true, DepthPageTransformer())


    }

    // --------------
    // ACTION
    // --------------

    /**
     * On click back arrow icon menu.
     */
    @Suppress("unused_parameter")
    fun onClickBackArrow(v: View) = onClickBackArrow()

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
     * Show toolbar when click on fragment.
     * @param v the clicked view.
     */
    @Suppress("unused_parameter")
    fun showAppBar(v: View) {
        appbar.visibility = View.VISIBLE
        Handler().postDelayed( { appbar.visibility = View.INVISIBLE }, 3000)
    }
}