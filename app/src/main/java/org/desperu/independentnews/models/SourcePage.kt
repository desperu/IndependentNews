package org.desperu.independentnews.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.desperu.independentnews.utils.EQUALS
import org.desperu.independentnews.utils.NOT_EQUALS

/**
 * Class witch provides a model for a source page.
 *
 * @property id                     the unique identifier of the source page.
 * @property sourceId               the unique identifier of the source.
 * @property url                    the url of the source page.
 * @property buttonName             the button name of the source page.
 * @property title                  the title of the source page.
 * @property body                   the body of the source page.
 * @property cssUrl                 the css url of the source page.
 * @property position               the position of the source page.
 * @property isPrimary              true if it's the primary source page.
 *
 * @constructor Sets all properties of the source page.
 *
 * @param id                        the unique identifier of the source page to set.
 * @param sourceId                  the unique identifier of the source to set.
 * @param url                       the url of the source page to set.
 * @param buttonName                the button name of the source page to set.
 * @param title                     the title of the source page to set.
 * @param body                      the body of the source page to set.
 * @param cssUrl                    the css url of the source page to set.
 * @param position                  the position of the source page to set.
 * @param isPrimary                 true if it's the primary source page to set.
 */
@Parcelize
@Entity(foreignKeys = [ForeignKey(entity = Source::class,
    parentColumns = ["id"],
    childColumns = ["sourceId"])],
    indices = [Index(name = "sourcePage_sourceId_index", value = ["sourceId"])])
data class SourcePage(@PrimaryKey(autoGenerate = true)
                      val id: Long = 0L,
                      var sourceId: Long = 0L,
                      val url: String = "",
                      val buttonName: String = "",
                      val title: String = "",
                      var body: String = "",
                      val cssUrl: String = "",
                      val position: Int = -1,
                      val isPrimary: Boolean = false
): Parcelable, Comparable<SourcePage> {

    /**
     * Compare this source page to another source page. Compare fields one to one,
     * if one field is different with the other, return not equals,
     * else the two estates are equals, return equals.
     *
     * @param other the other source page to compare with.
     *
     * @return {@code EQUALS} if equals, {@code NOT_EQUALS} otherwise.
     */
    override fun compareTo(other: SourcePage): Int = when {
        other.id != id -> NOT_EQUALS
        other.sourceId != sourceId -> NOT_EQUALS
        other.url != url -> NOT_EQUALS
        other.buttonName != buttonName -> NOT_EQUALS
        other.title != title -> NOT_EQUALS
        other.body != body -> NOT_EQUALS
        other.cssUrl != cssUrl -> NOT_EQUALS
        other.position != position -> NOT_EQUALS
        other.isPrimary != isPrimary -> NOT_EQUALS
        else -> EQUALS
    }
}