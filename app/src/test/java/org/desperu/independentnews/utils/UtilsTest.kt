package org.desperu.independentnews.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.ParseException
import io.mockk.every
import io.mockk.mockk
import org.desperu.independentnews.utils.Utils.concatenateStringFromMutableList
import org.desperu.independentnews.utils.Utils.millisToStartOfDay
import org.desperu.independentnews.utils.Utils.dateToString
import org.desperu.independentnews.utils.Utils.deConcatenateStringToMutableList
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl
import org.desperu.independentnews.utils.Utils.intDateToString
import org.desperu.independentnews.utils.Utils.intStringToDate
import org.desperu.independentnews.utils.Utils.isNoteRedirect
import org.desperu.independentnews.utils.Utils.isSourceUrl
import org.desperu.independentnews.utils.Utils.isWifiAvailable
import org.desperu.independentnews.utils.Utils.literalDateToMillis
import org.desperu.independentnews.utils.Utils.millisToString
import org.desperu.independentnews.utils.Utils.storeDelayMillis
import org.desperu.independentnews.utils.Utils.stringToDate
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * Utils class test, to check that all utils functions work as needed.
 */
@Suppress("Deprecation")
class UtilsTest {

    // FOR DATA
    private var mockContext = mockk<Context>()
    private val mockConnectivityManager = mockk<ConnectivityManager>()
    private val mockNetworkInfo = mockk<NetworkInfo>()

    private lateinit var output: String


    @Before
    fun before() {
        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns mockConnectivityManager
        every { mockConnectivityManager.activeNetworkInfo } returns mockNetworkInfo
    }

    @Test
    fun given_intDateMonthSeptember_When_intDateToString_Then_checkStringDate() {
        val expected = "01/09/2019"

        val day = 1
        val month = 8
        val year = 2019
        output = intDateToString(day, month, year)

        assertEquals(expected, output)
    }

    @Test
    fun given_intDateMonthNovember_When_intDateToString_Then_checkStringDate() {
        val expected = "21/11/2019"

        val day = 21
        val month = 10
        val year = 2019
        output = intDateToString(day, month, year)

        assertEquals(expected, output)
    }

    @Test
    @Throws(ParseException::class)
    fun given_stringDate_When_stringToDate_Then_checkNewDateFormat() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.SECOND, 35)
        cal.set(Calendar.MINUTE, 25)
        cal.set(Calendar.HOUR_OF_DAY, 19)
        cal.set(Calendar.DAY_OF_MONTH, 5)
        cal.set(Calendar.MONTH, 8)
        cal.set(Calendar.YEAR, 2019)
        val expected = cal.time

        val givenDate = "2019-09-05T19:25:35Z"
        val output: Date? = stringToDate(givenDate)

        assertEquals(expected, output)
    }

    @Test
    @Throws(ParseException::class)
    fun given_wrongStringDate_When_stringToDate_Then_checkNull() {
        val givenDate = "592019"
        val output = stringToDate(givenDate)

        assertNull(output)
    }

    @Test
    @Throws(ParseException::class)
    fun given_intStringDate_When_intStringToDate_Then_checkNewDateFormat() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.DAY_OF_MONTH, 15)
        cal.set(Calendar.MONTH, 9)
        cal.set(Calendar.YEAR, 2020)
        val expected = cal.time

        val givenDate = "15/10/2020"
        val output: Date? = intStringToDate(givenDate)

        assertEquals(expected, output)
    }

    @Test
    @Throws(ParseException::class)
    fun given_wrongIntStringDate_When_intStringToDate_Then_checkNull() {
        val givenDate = "15-10-2020"
        val output = intStringToDate(givenDate)

        assertNull(output)
    }

    @Test
    fun given_millis_When_millisToStartOfDay_Then_checkResult() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val expected = cal.timeInMillis

        val output = millisToStartOfDay(Calendar.getInstance().timeInMillis)

        assertEquals(expected, output)
    }

    @Test
    fun given_millis_When_millisToStartOfDay_Then_checkNotEquals() {
        val cal = Calendar.getInstance()
        val expected = cal.time // Not set to first millis of the day

        val output = millisToStartOfDay(Calendar.getInstance().timeInMillis)

        assertNotEquals(expected, output)
    }

    @Test
    fun given_date_When_dateToString_Then_checkResult() {
        val expected = "2020-09-04T19:22:56+0200"

        val cal = Calendar.getInstance()
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.SECOND, 56)
        cal.set(Calendar.MINUTE, 22)
        cal.set(Calendar.HOUR_OF_DAY, 19)
        cal.set(Calendar.DAY_OF_MONTH, 4)
        cal.set(Calendar.MONTH, 8)
        cal.set(Calendar.YEAR, 2020)
        output = dateToString(cal.time)

        assertEquals(expected, output)
    }

    @Test
    fun given_millis_When_millisToString_Then_checkResult() {
        val expected = "5/9/2020"

        val millis = 1599330621163
        output = millisToString(millis)

        assertEquals(expected, output)
    }

    @Test
    fun given_literalDate_When_literalDateToMillis_Then_checkResult() {
        val cal = Calendar.getInstance()
        cal.set(2020, 8, 25, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val expected = cal.timeInMillis

        val literalDate = "25 septembre 2020"

        val output = literalDateToMillis(literalDate)

        assertEquals(expected, output)
    }

    @Test
    fun given_wrongLiteralDate_When_literalDateToMillis_Then_checkResult() {
        val literalDate = "25 Septembre2020"

        val output = literalDateToMillis(literalDate)

        assertNull(output)
    }

    @Test
    fun given_specificLiteralDate_When_literalDateToMillis_Then_checkResult() {
        val cal = Calendar.getInstance()
        cal.set(2020, 9, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val expected = cal.timeInMillis

        val literalDate = "1er octobre 2020"

        val output = literalDateToMillis(literalDate)

        assertEquals(expected, output)
    }

    @Test
    fun given_storeDelay_When_storeDelayMillis_Then_checkResult() {
        val expected = 1585951200000

        val cal = Calendar.getInstance()
        cal.set(2020, 9, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val givenMillis = cal.timeInMillis

        val storeDelay = 6

        val output = storeDelayMillis(givenMillis, storeDelay)

        assertEquals(expected, output)
    }

    @Test
    fun given_mutableList_When_concatenateStringFromMutableList_Then_checkString() {
        val expected = "School, Shop, Park"

        val interestPlaces = mutableListOf("School", "Shop", "Park")
        output = concatenateStringFromMutableList(interestPlaces)

        assertEquals(expected, output)
    }

    @Test
    fun given_emptyMutableList_When_concatenateStringFromMutableList_Then_checkEmptyString() {
        val expected = ""

        val interestPlaces = mutableListOf<String>()
        output = concatenateStringFromMutableList(interestPlaces)

        assertEquals(expected, output)
    }

    @Test
    fun given_stringPlaces_When_deConcatenateStringToMutableList_Then_checkMutableList() {
        val expected = mutableListOf("School", "Shop", "Park")

        val interestPlaces = "School, Shop, Park"
        val output: List<String> = deConcatenateStringToMutableList(interestPlaces)

        assertEquals(expected, output)
    }

    @Test
    fun given_url_When_getPageNameFromUrl_Then_checkResult() {
        val expected ="ecologie-quartiers-populaires-front-des-meres-fatima-Ouassak-cantines-scolaires"

        val url = "https://www.bastamag.net/ecologie-quartiers-populaires-front-des-meres-fatima-Ouassak-cantines-scolaires"
        output = getPageNameFromUrl(url)

        assertEquals(expected, output)
    }

    @Test
    fun given_sourceUrl_When_isSourceUrl_Then_checkTrue() {
        val output = isSourceUrl("data:text/html; charset=UTF-8,")

        assertTrue(output)
    }

    @Test
    fun given_normalUrl_When_isSourceUrl_Then_checkFalse() {
        val url = "https://www.bastamag.net/ecologie-quartiers-populaires-front-des-meres-fatima-Ouassak-cantines-scolaires"
        val output = isSourceUrl(url)

        assertFalse(output)
    }

    @Test
    fun given_noteRedirect_When_isNoteRedirect_Then_checkTrue() {
        val noteRedirectList = listOf(
            "#nb1", "%23nb99", "#nh1", "#nh99",
            "%23nb1-1", "#nb1-99", "#nh1-1", "#nh1-99"
        )

        noteRedirectList.forEach {
            val output = isNoteRedirect(it)
            assertTrue(output)
        }
    }

    @Test
    fun given_wrongNoteRedirect_When_isNoteRedirect_Then_checkFalse() {
        val noteRedirectList = listOf(
            "#nb", "%23nb9-99-9", "#nt1", "#nh99-",
            "#nb1-100", "#nb100", "#nh-", "#nh1--99",
            "%nb2", "%24nh2-8"
        )

        noteRedirectList.forEach {
            val output = isNoteRedirect(it)
            assertFalse(output)
        }
    }

    @Test
    fun given_connectedNetworkAndWifi_When_isWifiAvailable_Then_checkResult() {
        every { mockNetworkInfo.isConnected } returns true
        every { mockNetworkInfo.type } returns ConnectivityManager.TYPE_WIFI
        val output = isWifiAvailable(mockContext)
        assertTrue(output)
    }

    @Test
    fun given_connectedNetworkAndDisabledWifi_When_isWifiAvailable_Then_checkResult() {
        every { mockNetworkInfo.isConnected } returns true
        every { mockNetworkInfo.type } returns ConnectivityManager.TYPE_MOBILE
        val output = isWifiAvailable(mockContext)
        assertFalse(output)
    }

    @Test
    fun given_connectedNetworkAndEnabledWifi_When_isWifiAvailable_Then_checkResult() {
        every { mockNetworkInfo.isConnected } returns false
        every { mockNetworkInfo.type } returns ConnectivityManager.TYPE_WIFI
        val disabledOutput = isWifiAvailable(mockContext)
        assertFalse(disabledOutput)
    }

    @Test
    fun given_disabledNetworkAndWifi_When_isWifiAvailable_Then_checkResult() {
        every { mockNetworkInfo.isConnected } returns false
        every { mockNetworkInfo.type } returns ConnectivityManager.TYPE_MOBILE
        val disabledOutput = isWifiAvailable(mockContext)
        assertFalse(disabledOutput)
    }
}