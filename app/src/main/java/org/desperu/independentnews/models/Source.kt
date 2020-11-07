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
 * @property imageId                the unique identifier of the source image.
 * @property logoId                 the unique identifier of the source logo.
 * @property backgroundColorId      the unique identifier of the background color.
 * @property isEnabled              the enabled state of the source.
 *
 * @constructor Sets all properties of the source.
 *
 * @param id                        the unique identifier of the source to set.
 * @param name                      the name of the source to set.
 * @param url                       the url of the source to set.
 * @param imageId                   the unique identifier of the source image to set.
 * @param logoId                    the unique identifier of the source logo to set.
 * @param backgroundColorId         the unique identifier of the background color to set.
 * @param isEnabled                 the enabled state of the source to set.
 */
@Parcelize
@Entity
data class Source(@PrimaryKey(autoGenerate = true)
                  val id: Long = 0L,
                  val name: String = "",
                  val url: String = "",
                  val imageId: Int = 0,
                  val logoId: Int = 0,
                  val backgroundColorId: Int = 0,
                  var isEnabled: Boolean = true
): Parcelable