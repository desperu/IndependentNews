package org.desperu.independentnews.utils

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
     * Convert date object to string format "yyyy-MM-dd'T'HH:mm:ssZ".
     * @param givenDate Given date object.
     * @return String date with good format.
     */
    internal fun dateToString(givenDate: Date): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE)
        return simpleDateFormat.format(givenDate)
    }

    /**
     * Convert time in millis to string date with pattern "yyyy/mm/dd".
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
    // CONVERT URL
    // -----------------

    /**
     * Return the page name from it's complete url.
     * @param url the given url to parse.
     * @return the page name from it's complete url.
     */
    internal fun getPageNameFromUrl(url: String): String {
        val list = url.split("/").toTypedArray()
        return list[list.size - 1]
    }
}