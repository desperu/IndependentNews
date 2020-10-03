package org.desperu.independentnews.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import org.desperu.independentnews.R

/**
 * Custom TextView which allow to set custom font.
 */
class FontTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        applyStyle(context, attrs)
    }

    private fun applyStyle(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.FontTextView)
        val cf = a.getInteger(R.styleable.FontTextView_fontName, 0)
        val fontName: Int
        when (cf) {
            1 -> fontName = R.string.AnnieUseYourTelescope_Regular
            else -> fontName = R.string.AnnieUseYourTelescope_Regular
        }
        val customFont = resources.getString(fontName)
        val tf = Typeface.createFromAsset(context.assets, "fonts/$customFont.ttf")
        typeface = Typeface.create(tf, Typeface.BOLD)
        a.recycle()
    }
}
