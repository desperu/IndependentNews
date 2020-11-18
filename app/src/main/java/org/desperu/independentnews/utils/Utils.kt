package org.desperu.independentnews.utils

import android.content.Context
import android.net.ConnectivityManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utils object witch provide utils functions for this application.
 */
internal object Utils {

    // -----------------
    // CONVERT DATE
    // -----------------

    /**
     * Concatenate date from int to string.
     * @param day Selected day.
     * @param month Selected month.
     * @param year Selected year.
     * @return String date.
     */
    internal fun intDateToString(day: Int, month: Int, year: Int): String {
        var month1 = month
        month1 += 1
        val stringDay: String = if (day < 10) "0$day" else day.toString()
        val stringMonth: String = if (month1 < 10) "0$month1" else month1.toString()
        return "$stringDay/$stringMonth/$year"
    }

    /**
     * Convert string date format from "yyyy-MM-dd'T'HH:mm:ssZ" to Date, return null if an error happened.
     * @param givenDate The given date.
     * @return Date object, null if an error happened.
     */
    internal fun stringToDate(givenDate: String): Date? {
        val givenDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.FRANCE)
        var date: Date? = null
        try {
            date = givenDateFormat.parse(givenDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    /**
     * Convert int string date format from "dd/MM/yyyy" to Date, return null if an error happened.
     * @param givenDate The given date.
     * @return Date object, null if an error happened.
     */
    internal fun intStringToDate(givenDate: String): Date? {
        val givenDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        var date: Date? = null
        try {
            date = givenDateFormat.parse(givenDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    /**
     * Convert a time in millis to another time in millis for the start of the day, 00h:00M:00s:000S.
     * @param millis the time in millis to convert to the start of the day.
     * @return the time in millie for the start of the day.
     */
    internal fun millisToStartOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return cal.timeInMillis
    }

    /**
     * Convert date object to string format "yyyy-MM-dd'T'HH:mm:ssZ".
     * @param givenDate Given date object.
     * @return String date with good format.
     */
    internal fun dateToString(givenDate: Date): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE)
        return simpleDateFormat.format(givenDate)
    }

    /**
     * Convert time in millis to string date with pattern "dd/MM/yyyy".
     * @param millis the time in millis to convert.
     * @return the time converted to string format.
     */
    internal fun millisToString(millis: Long): String {
        val cal = Calendar.getInstance()
        cal.time = Date(millis)
        return "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
    }

    /**
     * Convert literal date to millis.
     * @param literalDate the literal date in string.
     * @return the literalDate in millis.
     */
    internal fun literalDateToMillis(literalDate: String): Long? {
        val tabDate = literalDate.split(" ")
        if (tabDate.size < 3) return null
        val month = monthNumber.indexOf(tabDate[1])
        val dayOfMonth = tabDate[0].replace("([a-z])".toRegex(), "").toInt()
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, tabDate[2].toInt())
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return cal.timeInMillis
    }

    /**
     * Returns the store delay day in millis, from today.
     * @param nowMillis the millis value of now.
     * @param storeDelay the store delay value in month.
     * @return the store delay day in millis.
     */
    internal fun storeDelayMillis(nowMillis: Long, storeDelay: Int): Long {
        val monthMillis = 2592000000 // 30 * 24 * 60 * 60 * 1000
        val storeDelayMillis = storeDelay * monthMillis
        return nowMillis - storeDelayMillis
    }

    // -----------------
    // CONVERT STRING
    // -----------------

    /**
     * Concatenate mutable list of string to simple string.
     * @param mutableList the given mutable list of string.
     * @return the simple string concatenated.
     */
    internal fun concatenateStringFromMutableList(mutableList: MutableList<String>): String {
        val stringBuilder = StringBuilder()
        mutableList.forEachIndexed { index, s ->
            stringBuilder.append(s)
            if (index + 1 < mutableList.size) stringBuilder.append(", ")
        }
        return stringBuilder.toString()
    }

    /**
     * DeConcatenate string to mutable list of string.
     * @param string the concatenated string.
     * @return the mutable list of string.
     */
    internal fun deConcatenateStringToMutableList(string: String): MutableList<String> {
        val list = string.split(", ").toMutableList()
        list.forEach { it.replace(", ", "") }
        return list
    }

    // -----------------
    // URL
    // -----------------

    /**
     * Return the page name from it's complete url.
     * @param url the given url to parse.
     * @return the page name from it's complete url.
     */
    internal fun getPageNameFromUrl(url: String): String {
        val list = url.split("/")
        return list[list.size - 1]
    }

    /**
     * Returns true if the given url is a source article, false otherwise.
     * @param url the given url to compare with source urls.
     * @return true if the given url is a source article, false otherwise.
     */
    internal fun isSourceUrl(url: String) =
        url.contains("data:text/html; charset=UTF-8,")

    /**
     * Returns true if the given url is a note redirection, false otherwise.
     * @param url the given url to compare with pattern.
     * @return true if the given url is a note redirection, false otherwise.
     */
    internal fun isNoteRedirect(url: String): Boolean =
        url.matches("""(#|%23)n([bh])(\d){1,2}(-\d{1,2})?""".toRegex())

    /**
     * Returns true if the given url is an image url, false otherwise.
     * @param url the given url to compare with images suffix.
     * @return true if the given url is an image url, false otherwise.
     */
    internal fun isImageUrl(url: String): Boolean {
        // Specific here, it's html page...
        if (url.contains("wikimedia.org")) return false
        val pageName = getPageNameFromUrl(url)

        imageSuffix.forEach {
            if (pageName.contains(it)) return true
        }

        return false
    }

    // -----------------
    // WEB CONNECTION
    // -----------------

    /**
     * Returns if the wifi connexion is available.
     * @param context the context from this function is called.
     * @return true if the wifi is connected.
     */
    @Suppress("DEPRECATION")
    internal fun isWifiAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected && netInfo.type == ConnectivityManager.TYPE_WIFI
    }
}