package org.desperu.independentnews.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for a source page.
 *
 * @property id                     the unique identifier of the source page.
 * @property sourceId               the unique identifier of the source.
 * @property url                    the url of the source page.
 * @property title                  the title of the source page.
 * @property body                   the body of the source page.
 * @property imageUrl               the image url of the source page.
 * @property cssUrl                 the css url of the source page.
 * @property position               the position of the source page.
 * @property isPrimary              true if it's the primary source page.
 *
 * @constructor Sets all properties of the source page.
 *
 * @param id                        the unique identifier of the source page to set.
 * @param sourceId                  the unique identifier of the source to set.
 * @param url                       the url of the source page to set.
 * @param title                     the title of the source page to set.
 * @param body                      the body of the source page to set.
 * @param imageUrl                  the image url of the source page to set.
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
                      val title: String = "",
                      val body: String = "",
                      val imageUrl: String = "",
                      val cssUrl: String = "",
                      val position: Int = -1,
                      val isPrimary: Boolean = false
): Parcelable