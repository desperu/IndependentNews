package org.desperu.independentnews.ui.sources

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import icepick.State
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.di.module.ui.sourcesModule
import org.desperu.independentnews.models.SourceWithData
import org.desperu.independentnews.ui.sources.fragment.sourceDetail.SOURCE_POSITION
import org.desperu.independentnews.ui.sources.fragment.sourceDetail.SourceDetailFragment
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceListFragment
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceRouter
import org.desperu.independentnews.utils.*
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

private const val FADE_DEFAULT_TIME: Long = 300L

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

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_sources

    override fun configureDesign() {
        configureKoinDependency()
        configureAppBar()
        showAppBarIcon(listOf(R.id.back_arrow_icon))
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

            // Try to restore fragment instance from back stack.
            val fragment = fm.findFragmentByTag(getFragFromKey(fragmentKey).javaClass.simpleName)

                // If null, instantiate a new fragment.
                ?: when (fragmentKey) {
                    FRAG_SOURCES_LIST -> SourceListFragment()
                    FRAG_SOURCES_DETAIL -> SourceDetailFragment.newInstance(sourceWithData!!, sourcePosition)
                    else -> Fragment()
                }

            fragmentTransaction(fragment, sharedElement)
        }
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onBackPressed() {
        if (fragmentKey == FRAG_SOURCES_LIST) {
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

        fragTransaction
            .setReorderingAllowed(true)
            .replace(R.id.source_frame_container, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
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

    /**
     * Get the associated fragment with the given fragment key.
     * @param fragmentKey the given fragment key from witch get the key.
     * @return the corresponding fragment instance.
     */
    private fun getFragFromKey(fragmentKey: Int): Fragment = when(fragmentKey) {
        FRAG_SOURCES_LIST -> SourceListFragment()
        FRAG_SOURCES_DETAIL -> SourceDetailFragment()
        else -> throw IllegalArgumentException("Fragment key not found : $fragmentKey")
    }

    // --------------
    // ACTION
    // --------------

    /**
     * On click back arrow icon menu.
     */
    @Suppress("unused_parameter")
    fun onClickBackArrow(v: View) = onClickBackArrow()

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
        val exitFade = Fade()
        exitFade.duration = FADE_DEFAULT_TIME
        currentFragment?.exitTransition = exitFade

        // 2. Shared Elements Transition
        val transitionSet = TransitionSet()
        transitionSet.addTransition(
            TransitionInflater.from(this).inflateTransition(android.R.transition.move)
        )
        fragment.sharedElementEnterTransition = transitionSet

        // 3. Enter Transition for New Fragment
        val enterFade = Fade()
        enterFade.duration = FADE_DEFAULT_TIME
        fragment.enterTransition = enterFade
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
}