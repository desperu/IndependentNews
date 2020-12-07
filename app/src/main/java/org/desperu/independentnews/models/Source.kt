package org.desperu.independentnews.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.desperu.independentnews.utils.EQUALS
import org.desperu.independentnews.utils.NOT_EQUALS

/**
 * Class witch provides a model for a source.
 *
 * @property id                     the unique identifier of the source.
 * @property name                   the name of the source.
 * @property url                    the url of the source.
 * @property isEnabled              the enabled state of the source.
 *
 * @constructor Sets all properties of the source.
 *
 * @param id                        the unique identifier of the source to set.
 * @param name                      the name of the source to set.
 * @param url                       the url of the source to set.
 * @param isEnabled                 the enabled state of the source to set.
 */
@Parcelize
@Entity
data class Source(@PrimaryKey(autoGenerate = true)
                  val id: Long = 0L,
                  val name: String = "",
                  val url: String = "",
                  var isEnabled: Boolean = true
): Parcelable, Comparable<Source> {

    /**
     * Compare this source to another source. Compare fields one to one,
     * if one field is different with the other, return not equals,
     * else the two estates are equals, return equals.
     *
     * @param other the other source to compare with.
     *
     * @return {@code EQUALS} if equals, {@code NOT_EQUALS} otherwise.
     */
    override fun compareTo(other: Source): Int = when {
        other.id != id -> NOT_EQUALS
        other.name != name -> NOT_EQUALS
        other.url != url -> NOT_EQUALS
        other.isEnabled != isEnabled -> NOT_EQUALS
        else -> EQUALS
    }
}