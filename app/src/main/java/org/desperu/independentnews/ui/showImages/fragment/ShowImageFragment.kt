package org.desperu.independentnews.ui.showImages.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.databinding.FragmentImageBinding
import org.koin.core.parameter.parametersOf
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * The name of the argument to received the image url in this fragment.
 */
const val IMAGE_URL: String = "showImage"

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

    override fun configureDesign() {}

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
}