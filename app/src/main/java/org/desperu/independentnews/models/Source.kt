package org.desperu.independentnews.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for a source.
 *
 * @property id                     the unique identifier of the source.
 * @property name                   the name of the source.
 * @property url                    the url of the source.
 * @property editorialUrl           the editorial url of the source.
 * @property editorial              the editorial of the source.
 * @property imageId                the unique identifier of the source image.
 * @property logoId                 the unique identifier of the source logo.
 * @property backgroundColorId      the unique identifier of the background color to set.
 * @property isEnabled              the enabled state of the source.
 *
 * @constructor Sets all properties of the source.
 *
 * @property id                     the unique identifier of the source to set.
 * @property name                   the name of the source to set.
 * @property url                    the url of the source to set.
 * @property editorialUrl           the editorial url of the source to set.
 * @property editorial              the title of the source to set.
 * @property imageId                the unique identifier of the source image to set.
 * @property logoId                 the unique identifier of the source logo to set.
 * @param backgroundColorId         the unique identifier of the background color to set.
 * @property isEnabled              the enabled state of the source to set.
 */
@Parcelize
@Entity
data class Source(@PrimaryKey(autoGenerate = true)
                  val id: Long = 0L,
                  val name: String = "",
                  val url: String = "",
                  val editorialUrl: String = "",
                  var editorial: String = "",
                  val imageId: Int = 0,
                  val logoId: Int = 0,
                  val backgroundColorId: Int = 0,
                  var isEnabled: Boolean = true
): Parcelable