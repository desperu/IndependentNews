package org.desperu.independentnews.models.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for a css style.
 *
 * @property id             the unique identifier of the css style.
 * @property url            the url of the css style.
 * @property style          the content of the css style.
 *
 * @constructor Sets all properties of the css style.
 *
 * @param id                the unique identifier of the css style to set.
 * @param url               the url of the css style to set.
 * @param style             the content of the css style to set.
 */
@Parcelize
@Entity
data class Css(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var url: String = "",
    var style: String = ""
): Parcelable