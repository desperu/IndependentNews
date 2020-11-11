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
    @Embedded val source: Source = Source(),
    @Relation(
        parentColumn = "id",
        entityColumn = "sourceId"
    )
    val sourcePages: List<SourcePage> = listOf()
): Parcelable {

    /**
     * Convert SourceWithData (with all source pages)
     * to SourceWithData (with the single source page we want to show).
     *
     * @param position the position of source page to show in the list.
     *
     * @return the SourceWithData with the single source page to show.
     */
    internal fun toSimplePage(position: Int) =
        SourceWithData(
            source,
            if (sourcePages.isNotEmpty())
                listOf(sourcePages[position])
            else
                listOf()
        )

    /**
     * Convert SourceWithData to Article, to show the single source page into the web view.
     */
    internal fun toArticle(): Article {
        val sourcePage =
            if (sourcePages.isNotEmpty()) sourcePages[0]
            else SourcePage()

        return Article(
            sourceName = source.name,
            url = sourcePage.url,
            title = sourcePage.title,
            article = sourcePage.body,
            cssUrl = sourcePage.cssUrl,
            source = source

        )
    }
}