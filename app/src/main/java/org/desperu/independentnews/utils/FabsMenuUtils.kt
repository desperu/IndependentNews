package org.desperu.independentnews.utils

import org.desperu.independentnews.R
import org.desperu.independentnews.service.ResourceService
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * FabsMenuUtils object witch provide utils functions for Fabs Menu.
 */
object FabsMenuUtils : KoinComponent {

    // FOR DATA
    private val resources: ResourceService get() = get()

    /**
     * Returns the unique identifier for the given sub fab key.
     *
     * @param subFabKey the key of the sub fab.
     *
     * @return the unique identifier for the given sub fab key.
     *
     * @throws IllegalArgumentException if the sub fab key was not found.
     */
    internal fun getSubFabId(subFabKey: Int): Int = when (subFabKey) {
        SUB_FAB_MIN_TEXT -> R.id.fab_minus_text
        SUB_FAB_UP_TEXT -> R.id.fab_up_text
        SUB_FAB_STAR -> R.id.fab_star
        SUB_FAB_PAUSE -> R.id.fab_pause
        SUB_FAB_HOME -> R.id.fab_home
        else -> throw IllegalArgumentException("Sub Fab key not found : $subFabKey")
    }

    /**
     * Returns the unique identifier of the Icon for the given sub fab key.
     *
     * @param subFabKey the key of the sub fab.
     *
     * @return the unique identifier of the Icon for the given sub fab key.
     *
     * @throws IllegalArgumentException if the sub fab key was not found.
     */
    internal fun getSubFabIcon(subFabKey: Int): Int = when (subFabKey) {
        SUB_FAB_MIN_TEXT -> R.drawable.ic_baseline_minus_text_black_18
        SUB_FAB_UP_TEXT -> R.drawable.ic_baseline_up_text_black_18
        SUB_FAB_STAR -> R.drawable.ic_baseline_star_black_18
        SUB_FAB_PAUSE -> R.drawable.ic_baseline_pause_black_18
        SUB_FAB_HOME -> R.drawable.ic_baseline_home_black_18
        else -> throw IllegalArgumentException("Sub Fab key not found : $subFabKey")
    }

    /**
     * Returns sub fab label for the given sub fab key.
     *
     * @param subFabKey the key of the sub fab.
     *
     * @return the sub fab label for the sub fab key.
     *
     * @throws IllegalArgumentException if the sub fab key was not found.
     */
    internal fun getSubFabLabel(subFabKey: Int): String = when (subFabKey) {
        SUB_FAB_MIN_TEXT -> resources.getString(R.string.sub_fab_label_min_text)
        SUB_FAB_UP_TEXT -> resources.getString(R.string.sub_fab_label_up_text)
        SUB_FAB_STAR -> resources.getString(R.string.sub_fab_label_star)
        SUB_FAB_REMOVE_STAR -> resources.getString(R.string.sub_fab_label_remove_star)
        SUB_FAB_PAUSE -> resources.getString(R.string.sub_fab_label_pause)
        SUB_FAB_REMOVE_PAUSE -> resources.getString(R.string.sub_fab_label_remove_pause)
        SUB_FAB_HOME -> resources.getString(R.string.sub_fab_label_home)
        else -> throw IllegalArgumentException("Sub Fab key not found : $subFabKey")
    }
}