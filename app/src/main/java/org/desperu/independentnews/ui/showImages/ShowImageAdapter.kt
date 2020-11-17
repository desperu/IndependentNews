package org.desperu.independentnews.ui.showImages

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.desperu.independentnews.ui.showImages.fragment.ShowImageFragment

/**
 * Recycler view adapter for Show Images.
 *
 * @constructor Instantiate a new SourceImageAdapter.
 *
 * @param fm            the fragment manager of the parent activity to set.
 * @param behavior      the behavior flag for the fragment manager to set.
 */
class ShowImageAdapter(fm: FragmentManager,
                       behavior: Int) : FragmentPagerAdapter(fm, behavior) {

    private lateinit var imageList: List<String>

    override fun getCount(): Int = if (::imageList.isInitialized) imageList.size else 0

    override fun getItem(position: Int): Fragment = ShowImageFragment.newInstance(imageList[position])

    /**
     * Update all item list.
     * @param newImageList the new image list to set.
     */
    internal fun updateImageList(newImageList: List<String>) { imageList = newImageList }
}