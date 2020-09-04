package org.desperu.independentnews.utils

import android.net.ParseException
import org.desperu.independentnews.utils.Utils.dateToString
import org.desperu.independentnews.utils.Utils.intDateToString
import org.desperu.independentnews.utils.Utils.stringToDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.*

/**
 * Utils class test, to check that all utils functions work as needed.
 */
class UtilsTest {

//    private var mockContext = mockk<Context>()

    private lateinit var output: String

    @Test
    fun given_intDateMonthSeptember_When_intDateToString_Then_checkStringDate() {
        val day = 1
        val month = 8
        val year = 2019
        output = intDateToString(day, month, year)
        val expected = "01/09/2019"

        assertEquals(expected, output)
    }

    @Test
    fun given_intDateMonthNovember_When_intDateToString_Then_checkStringDate() {
        val day = 21
        val month = 10
        val year = 2019
        output = intDateToString(day, month, year)
        val expected = "21/11/2019"

        assertEquals(expected, output)
    }

    @Test
    @Throws(ParseException::class)
    fun given_stringDate_When_askStringToDate_Then_checkNewDateFormat() {
        val givenDate = "2019-09-05T19:25:35+0200"
        val cal = Calendar.getInstance()
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.SECOND, 35)
        cal.set(Calendar.MINUTE, 25)
        cal.set(Calendar.HOUR_OF_DAY, 19)
        cal.set(Calendar.DAY_OF_MONTH, 5)
        cal.set(Calendar.MONTH, 8)
        cal.set(Calendar.YEAR, 2019)
        val expected = Date()
        expected.time = cal.timeInMillis
        val output: Date? = stringToDate(givenDate)

        assertEquals(expected, output)
    }

    @Test
    @Throws(ParseException::class)
    fun given_wrongStringDate_When_askStringToDate_Then_checkNull() {
        val givenDate = "592019"
        val output = stringToDate(givenDate)

        assertNull(output)
    }

    @Test
    fun given_date_When_dateToString_Then_checkResult() {
        val givenDate = Date()
        val cal = Calendar.getInstance()
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.SECOND, 56)
        cal.set(Calendar.MINUTE, 22)
        cal.set(Calendar.HOUR_OF_DAY, 19)
        cal.set(Calendar.DAY_OF_MONTH, 4)
        cal.set(Calendar.MONTH, 8)
        cal.set(Calendar.YEAR, 2020)
        givenDate.time = cal.timeInMillis
        output = dateToString(givenDate)

        val expected = "2020-09-04T19:22:56+0200"

        assertEquals(expected, output)
    }

    @Test
    fun given_mutableList_When_concatenateStringFromMutableList_Then_checkString() {
        val expected = "School, Shop, Park"

        val interestPlaces = mutableListOf<String>()
        interestPlaces.add("School")
        interestPlaces.add("Shop")
        interestPlaces.add("Park")
        output = Utils.concatenateStringFromMutableList(interestPlaces)

        assertEquals(expected, output)
    }

    @Test
    fun given_emptyMutableList_When_concatenateStringFromMutableList_Then_checkEmptyString() {
        val expected = ""

        val interestPlaces = mutableListOf<String>()
        output = Utils.concatenateStringFromMutableList(interestPlaces)

        assertEquals(expected, output)
    }

    @Test
    fun given_stringPlaces_When_deConcatenateStringToMutableList_Then_checkMutableList() {
        val expected = mutableListOf<String>()
        expected.add("School")
        expected.add("Shop")
        expected.add("Park")

        val interestPlaces = "School, Shop, Park"
        val output: List<String> = Utils.deConcatenateStringToMutableList(interestPlaces)

        assertEquals(expected, output)
    }
}