package org.desperu.independentnews.models.database

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.desperu.independentnews.utils.CSS_STYLE
import org.desperu.independentnews.utils.CSS_ID
import org.desperu.independentnews.utils.CSS_URL

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
): Parcelable {

    /**
     * Get css data from content values.
     *
     * @param values the content value to get data from.
     *
     * @return the Css object created from content values.
     */
    fun fromContentValues(values: ContentValues?): Css {
        val css = Css()

        if (values != null) {
            if (values.containsKey(CSS_ID)) css.id = values.getAsLong(CSS_ID)
            if (values.containsKey(CSS_URL)) css.url = values.getAsString(CSS_URL)
            if (values.containsKey(CSS_STYLE)) css.style = values.getAsString(CSS_STYLE)
        }

        return css
    }

    /**
     * Convert css object to content values.
     *
     * @param css the css object to convert.
     *
     * @return the content values of the css object.
     */
    fun toContentValues(css: Css?): ContentValues {
        val contentValues = ContentValues()

        if (css != null) {
            contentValues.put(CSS_ID, css.id)
            contentValues.put(CSS_URL, css.url)
            contentValues.put(CSS_STYLE, css.style)
        }

        return contentValues
    }
}