package org.desperu.independentnews.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for a source with data.
 *
 * @property source         the source.
 * @property sourcePages    the source page associated to the source.
 *
 * @constructor Sets all properties of the source with data.
 *
 * @param source            the source to set.
 * @param sourcePages       the source page associated to the source to set.
 */
@Parcelize
data class SourceWithData(
    @Embedded val source: Source,
    @Relation(
        parentColumn = "id",
        entityColumn = "sourceId"
    )
    val sourcePages: List<SourcePage>
): Parcelable