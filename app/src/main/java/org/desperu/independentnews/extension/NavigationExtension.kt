package org.desperu.independentnews.extension

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.getDefaultScope
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Shared Navigation Graphic View Model function that's support koin module injection,
 * with all koin features, qualifier, initial state and parameters.
 * Allow shared view model between UI parts.
 *
 * @param VM            the asked view model class type.
 * @param navHostId     the unique identifier of the navigation host fragment.
 * @param navGraphId    the unique identifier of the navigation graphic.
 * @param qualifier     the qualifier of the asked koin instance.
 * @param initialState  the initial state of the koin instance.
 * @param parameters    the parameters of the koin instance.
 *
 * @return the Lazy koin instance.
 */
internal inline fun <reified VM : ViewModel> AppCompatActivity.sharedGraphViewModel(
    @IdRes navHostId: Int,
    @IdRes navGraphId: Int,
    qualifier: Qualifier? = null,
    noinline initialState: BundleDefinition? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy(LazyThreadSafetyMode.NONE) {

    getKoin().getViewModel(
        qualifier = qualifier,
        state = initialState,
        owner = { ViewModelOwner.from(findNavController(navHostId).getViewModelStoreOwner(navGraphId)) },
        clazz = VM::class,
        parameters = parameters
    )
}

/**
 * Shared Navigation Graphic View Model function that's support koin module injection,
 * with all koin features, qualifier, initial state and parameters.
 * Allow shared view model between UI parts.
 *
 * @param VM            the asked view model class type.
 * @param navGraphId    the unique identifier of the navigation graphic.
 * @param qualifier     the qualifier of the asked koin instance.
 * @param initialState  the initial state of the koin instance.
 * @param parameters    the parameters of the koin instance.
 *
 * @return the Lazy koin instance.
 */
internal inline fun <reified VM : ViewModel> Fragment.sharedGraphViewModel(
    @IdRes navGraphId: Int,
    qualifier: Qualifier? = null,
    noinline initialState: BundleDefinition? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy(LazyThreadSafetyMode.NONE) {

    getDefaultScope().getViewModel(
        qualifier,
        initialState,
        { ViewModelOwner.from(findNavController().getViewModelStoreOwner(navGraphId)) },
        VM::class,
        parameters
    )
}