package org.desperu.independentnews.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for a source.
 *
 * @param id                the unique identifier of the source.
 * @param name              the name of the source.
 * @param url               the url of the source.
 * @param editorialUrl      the editorial url of the source.
 * @param editorial         the editorial of the source.
 * @param imageUrl          the image url of the source.
 * @param isEnabled         the enabled state of the source.
 *
 * @constructor Sets all properties of the source.
 *
 * @property id             the unique identifier of the source to set.
 * @property name           the name of the source to set.
 * @property url            the url of the source to set.
 * @property editorialUrl   the editorial url of the source to set.
 * @property editorial      the title of the source to set.
 * @property imageUrl       the image url of the source to set.
 * @property isEnabled      the enabled state of the source to set.
 */
@Parcelize
@Entity
data class Source(@PrimaryKey(autoGenerate = true)
                  val id: Long = 0L,
                  var name: String = "",
                  var url: String = "",
                  var editorialUrl: String = "",
                  var editorial: String = "",
                  var imageUrl: String = "",
                  var isEnabled: Boolean = true
): Parcelable