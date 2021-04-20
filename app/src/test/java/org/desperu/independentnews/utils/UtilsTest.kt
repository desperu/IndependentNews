package org.desperu.independentnews.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.ParseException
import io.mockk.every
import io.mockk.mockk
import org.desperu.independentnews.utils.Utils.concatenateStringFromMutableList
import org.desperu.independentnews.utils.Utils.deConcatenateStringToMutableList
import org.desperu.independentnews.utils.Utils.getDomainFromUrl
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl
import org.desperu.independentnews.utils.Utils.intDateToString
import org.desperu.independentnews.utils.Utils.intStringToDate
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.desperu.independentnews.utils.Utils.isImageUrl
import org.desperu.independentnews.utils.Utils.isInternetAvailable
import org.desperu.independentnews.utils.Utils.isNoteRedirect
import org.desperu.independentnews.utils.Utils.isSourceArticleUrl
import org.desperu.independentnews.utils.Utils.isWifiAvailable
import org.desperu.independentnews.utils.Utils.literalDateToMillis
import org.desperu.independentnews.utils.Utils.millisToStartOfDay
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
    fun given_monthMoreThanStoreDelay_When_storeDelayMillis_Then_checkResult() {
        val cal = Calendar.getInstance()
        cal.set(2020, 3, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val expected = cal.timeInMillis

        cal.set(Calendar.MONTH, 9)
        val givenMillis = cal.timeInMillis

        val storeDelay = 6

        val output = storeDelayMillis(givenMillis, storeDelay)

        assertEquals(expected, output)
    }

    @Test
    fun given_monthLessThanStoreDelay_When_storeDelayMillis_Then_checkResult() {
        val cal = Calendar.getInstance()
        cal.set(2018, 11, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val expected = cal.timeInMillis

        cal.set(Calendar.YEAR, 2020)
        val givenMillis = cal.timeInMillis

        val storeDelay = 24

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
    fun given_url_When_getDomainFromUrl_Then_checkResult() {
        val expected ="bastamag.net"

        val url = "https://www.bastamag.net/ecologie-quartiers-populaires-front-des-meres-fatima-Ouassak-cantines-scolaires"
        val output = getDomainFromUrl(url)

        assertEquals(expected, output)
    }

    @Test
    fun given_sourceUrl_When_isHtmlData_Then_checkTrue() {
        val list = listOf(
            "data:text/html; charset=UTF-8,",
            "<html>"
        )

        list.forEach {
            val output = isHtmlData(it)

            assertTrue(output)
        }
    }

    @Test
    fun given_normalUrl_When_isHtmlData_Then_checkFalse() {
        val url = "https://www.bastamag.net/ecologie-quartiers-populaires-front-des-meres-fatima-Ouassak-cantines-scolaires"
        val output = isHtmlData(url)

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
    fun given_imageUrl_When_isImageUrl_Then_checkResult() {
        val imageUrlList = listOf(
            "https://www.bastamag.net/local/adapt-img/740/10x/IMG/png/charts-1-01.png",
            "https://reporterre.net/local/cache-gd2/20/aaaa97eaa9b5060e50383a02af2aa7.jpg?1605601443",
            "https://www.bastamag.net/local/adapt-img/740/10x/IMG/arton8033.jpeg"
        )

        imageUrlList.forEach {
            val output = isImageUrl(it)
            assertTrue(output)
        }
    }

    @Test
    fun given_wrongImageUrl_When_isImageUrl_Then_checkFalse() {
        val imageUrlList = listOf(
            "https://commons.wikimedia.org/wiki/File:Police_Municipale_Toulouse-3301.jpg",
            "https://fr.wikipedia.org/wiki/Fichier:Barricade_rue_de_la_Bonne_Montmartre_Commune_Paris_1871.jpg",
            "https://www.bastamag.net/local/adapt-img/740/10x/IMG/png/charts-1-01.pnG",
            "https://reporterre.net/local/cache-gd2/20/aaaa97eaa9b5060e50383a02af2aa7jpg?1605601443",
            "https://www.bastamag.net/local/adapt-img/740/10x/IMG/arton8033.jipeg"
        )

        imageUrlList.forEach {
            val output = isImageUrl(it)
            assertFalse(output)
        }
    }

    @Test
    fun given_url_When_isArticleSourceUrl_Then_checkTrue() {
        val urlList = listOf(
            "https://www.bastamag.net/ecologie-quartiers-populaires-front-des-meres-fatima-Ouassak-cantines-scolaires",
            "https://www.bastamag.net/Qui-sommes-nous",
            "https://reporterre.net/Tout-le-monde-craque-les-jeunes-activistes-du-climat-sonnes-par-le-Covid",
            "https://reporterre.net/quisommesnous",
            "https://multinationales.org/CAC40-le-veritable-bilan-annuel-l-edition-2020",
            "https://multinationales.org/A-propos"
        )

        urlList.forEach {
            val output = isSourceArticleUrl(it)
            assertTrue(output)
        }
    }

    @Test
    fun given_url_When_isArticleSourceUrl_Then_checkFalse() {
        val urlList = listOf(
            "https://www.delachauxetniestle.com/livre/sauvons-la-biodiversite",
            "https://www.bastamag.net/Approfondir",
            "https://www.bastamag.net/don",
            "https://www.bastamag.net/Agnes-Rousseaux",
            "https://reporterre.net/Pres-de-chez-vous",
            "https://reporterre.net/La-vie-de-Reporterre-10",
            "https://reporterre.net/Tribune-15",
            "https://reporterre.net/Soutenir",
            "https://reporterre.net/Culture-et-idees",
            "https://reporterre.net/Hors-les-murs",
            "https://reporterre.net/rubrique-de-plus",
            "https://reporterre.net/Une-minute-Une-question-21",
            "https://reporterre.net/Les-femmes-et-les-hommes-de-Reporterre-72",
            "https://multinationales.org/Enquetes",
            "https://www.okpal.com/multinationales/"
        )

        urlList.forEach {
            val output = isSourceArticleUrl(it)
            assertFalse(output)
        }
    }

    @Test
    fun given_connectedNetwork_When_isWifiAvailable_Then_checkResult() {
        every { mockNetworkInfo.isConnected } returns true
        val output = isInternetAvailable(mockContext)
        assertTrue(output)
    }

    @Test
    fun given_disconnectedNetwork_When_isWifiAvailable_Then_checkFalse() {
        every { mockNetworkInfo.isConnected } returns false
        val output = isInternetAvailable(mockContext)
        assertFalse(output)
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