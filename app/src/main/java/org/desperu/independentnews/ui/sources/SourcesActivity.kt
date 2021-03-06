package org.desperu.independentnews.ui.sources

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import androidx.transition.*
import com.google.android.material.transition.MaterialFade
import icepick.State
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.app_bar.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.di.module.ui.sourcesModule
import org.desperu.independentnews.models.database.SourceWithData
import org.desperu.independentnews.ui.main.HAS_CHANGE
import org.desperu.independentnews.ui.main.MainActivity
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.sources.fragment.sourceDetail.SOURCE_POSITION
import org.desperu.independentnews.ui.sources.fragment.sourceDetail.SourceDetailFragment
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceListFragment
import org.desperu.independentnews.ui.sources.fragment.SourceRouter
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.WHO_OWNS_WHAT
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.qualifier

/**
 * Fade default time animation for fragment transition.
 */
private const val FADE_DEFAULT_TIME: Long = 300L

/**
 * The name of the intent extra to received app bar size to this Activity.
 */
const val WAS_EXPANDED: String = "wasExpanded"

/**
 * Activity to manages and present the sources medias of the application.
 *
 * @constructor Instantiates a new SourcesActivity.
 */
class SourcesActivity : BaseActivity(sourcesModule), SourcesInterface {

    // FOR DATA
    @JvmField @State var fragmentKey: Int = NO_FRAG
    private val fm = supportFragmentManager
    private var sourcePosition = -1
    private lateinit var imageRouter: ImageRouter

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_sources

    override fun configureDesign() {
        configureKoinDependency()
        configAppBar()
        configureAndShowFragment(FRAG_SOURCES_LIST, null, null, -1)
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure koin dependency for this activity.
     */
    private fun configureKoinDependency() {
        get<SourcesInterface> { parametersOf(this) }
        get<SourceRouter> { parametersOf(this) }
        imageRouter = get(qualifier(SOURCE_IMAGE_ROUTER)) { parametersOf(this) }
    }

    /**
     * Configure app bar, show icons, and set title.
     */
    private fun configAppBar() {
        appbar.showAppBarIcon(listOf(R.id.back_arrow_icon, R.id.info_icon))
        toolbar_title.text = getString(R.string.navigation_drawer_sources)
    }

    /**
     * Configure and show corresponding fragment.
     *
     * @param fragmentKey Key for fragment.
     * @param sourceWithData the source with data detail to show.
     * @param sharedElement the shared element to animate during the transition.
     * @param sourcePosition the position of the source item in the recycler view.
     */
    private fun configureAndShowFragment(fragmentKey: Int, sourceWithData: SourceWithData?,
                                         sharedElement: View?, sourcePosition: Int) {
        if (this.fragmentKey != fragmentKey) {
            this.fragmentKey = fragmentKey
            this.sourcePosition = sourcePosition

            // Get fragment instance from key.
            val fragment =  when (fragmentKey) {
                    FRAG_SOURCES_LIST -> SourceListFragment()
                    FRAG_SOURCES_DETAIL -> SourceDetailFragment.newInstance(sourceWithData!!, sourcePosition)
                    else -> Fragment()
                }

            fragmentTransaction(fragment, sharedElement)
        }
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Handle activity result on show article response.
        handleShowArticleResponse(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        setActivityTransition()
    }

    override fun onBackPressed() {
        if (fragmentKey == FRAG_SOURCES_LIST) {
            sendResult()
            while (fm.backStackEntryCount > 0) fm.popBackStackImmediate()
            super.onBackPressed()
        } else {
            sourceListFrag?.let { it.arguments?.putInt(SOURCE_POSITION, sourcePosition) }
            super.onBackPressed()
            fragmentKey = FRAG_SOURCES_LIST
        }
    }

    // --------------
    // FRAGMENT
    // --------------

    /**
     * Apply fragment transaction, add to back stack and set animation transition
     * for API > Lollipop.
     *
     * @param fragment the fragment to show in the frame layout.
     * @param sharedElement the shared element to animate.
     */
    private fun fragmentTransaction(fragment: Fragment, sharedElement: View?) {
        val fragTransaction = fm.beginTransaction()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && sharedElement != null) {
            performTransition(fragment)
            sharedElement.transitionName = getString(R.string.animation_source_list_to_detail) + sourcePosition
            fragTransaction.addSharedElement(sharedElement, sharedElement.transitionName)
        }

        if (!fm.isDestroyed) {
            fragTransaction
                .setReorderingAllowed(true)
                .replace(R.id.source_frame_container, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
                .commit()
        }
    }

    /**
     * Redirects the user to the SourcesDetailFragment to show sources detail.
     *
     * @param sourceWithData the source with data to show in the fragment.
     * @param imageView the image view to animate.
     * @param itemPosition the position of the source item in the recycler view.
     */
    override fun showSourceDetail(sourceWithData: SourceWithData, imageView: View, itemPosition: Int) =
        configureAndShowFragment(FRAG_SOURCES_DETAIL, sourceWithData, imageView, itemPosition)

    // --------------
    // ACTION
    // --------------

    /**
     * On click back arrow icon menu.
     */
    @Suppress("unused_parameter")
    fun onClickBackArrow(v: View) = onBackPressed()

    /**
     * On click info icon menu.
     */
    @Suppress("unused_parameter", "Unchecked_cast")
    fun onClickInfo(v: View) {
        imageRouter.openShowImages(WHO_OWNS_WHAT as ArrayList<Any>)
    }

    // --------------
    // ANIMATION
    // --------------

    /**
     * Perform the fragment transition animation (fade and shared element) for the given fragment.
     *
     * @param fragment the fragment for which perform transition animation.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun performTransition(fragment: Fragment) {
        if (isDestroyed) return

        // 1. Exit for Previous Fragment
        val exitFade = MaterialFade()
        exitFade.duration = FADE_DEFAULT_TIME
        currentFragment?.exitTransition = exitFade

        // 2. Shared Elements Transition
        val transitionSet = TransitionSet()
        transitionSet.addTransition(
            TransitionInflater.from(this).inflateTransition(android.R.transition.move)
        )
        transitionSet.addTransition(SourceTransition()) // ChangeImageTransform() has bad animation result
        fragment.sharedElementEnterTransition = transitionSet

        // 3. Enter Transition for New Fragment
        val enterFade = MaterialFade()
        enterFade.duration = FADE_DEFAULT_TIME
        fragment.enterTransition = enterFade // TODO anim mistake du to this line and use card view instead of image
    }

    /**
     * Set custom activity transition, only for source detail to source page transition.
     */
    private fun setActivityTransition() {
        if (fragmentKey == FRAG_SOURCES_DETAIL && sourcePosition != -1)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // --------------
    // UI
    // --------------

    /**
     * Update app bar on touch listener, used to finish app bar anim.
     */
    override fun updateAppBarOnTouch() = appbar.updateOnTouch()

    // --------------
    // UTILS
    // --------------

    /**
     * Send result for MainActivity, to synchronize sources state.
     */
    private fun sendResult() {
        setResult(
            RESULT_OK,
            Intent(baseContext, MainActivity::class.java)
                .putExtra(HAS_CHANGE, sourceListFrag?.hasChange())
        )
    }

    /**
     * Handle result when retrieve show article response, to synchronize the app bar size.
     *
     * @param requestCode   the request code of the activity result.
     * @param resultCode    the result code of the request.
     * @param data          the intent request result data.
     */
    private fun handleShowArticleResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        // If result code and request code matches with show article result.
        if (resultCode == RESULT_OK && requestCode == RC_SHOW_ARTICLE) {
            val wasExpanded = data?.getBooleanExtra(WAS_EXPANDED, true) ?: true

            // Synchronize the app bar state with the child activity app bar state.
            appbar.doOnAttach {
                appbar.syncAppBarSize(appbar, wasExpanded)
                intent.removeExtra(WAS_EXPANDED)
            }
        }
    }

    // --- GETTERS ---

    /**
     * Get the current fragment in the frame container.
     */
    private val currentFragment get() = fm.findFragmentById(R.id.source_frame_container)

    /**
     * Get the source list fragment instance from fragment manager (frame or back stack).
     */
    private val sourceListFrag get() =
        fm.findFragmentByTag(SourceListFragment().javaClass.simpleName) as SourceListFragment?

    /**
     * Returns true if the app bar is expanded, false if is collapsed.
     */
    override val isExpanded: Boolean get() = appbar.isExpanded
}