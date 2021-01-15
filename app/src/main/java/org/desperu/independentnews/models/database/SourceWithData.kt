package org.desperu.independentnews.models.database

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import org.desperu.independentnews.utils.EQUALS
import org.desperu.independentnews.utils.NOT_EQUALS

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
        entity = SourcePage::class,
        parentColumn = "id",
        entityColumn = "sourceId"
    )
    val sourcePages: List<SourcePage> = listOf()
): Parcelable, Comparable<SourceWithData> {

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
            url = sourcePage.url,
            title = sourcePage.title,
            article = sourcePage.body,
            cssUrl = sourcePage.cssUrl,
            source = source
        )
    }

    /**
     * Compare this source with data to another source with data. Compare fields one to one,
     * if one field is different with the other, return not equals,
     * else the two estates are equals, return equals.
     *
     * @param other the other source with data to compare with.
     *
     * @return {@code EQUALS} if equals, {@code NOT_EQUALS} otherwise.
     */
    override fun compareTo(other: SourceWithData): Int = when {
        other.source != source -> source.compareTo(other.source)
        other.sourcePages.withIndex().find { it.value.compareTo(sourcePages[it.index]) != EQUALS } != null -> NOT_EQUALS
        else -> EQUALS
    }
}