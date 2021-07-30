package org.desperu.independentnews.utils

import org.desperu.independentnews.R

// --- FOR UI ---

// Fragment identifier
// Main
const val NO_FRAG = -1
const val FRAG_TOP_STORY = 0
const val FRAG_CATEGORY = 1
const val FRAG_ALL_ARTICLES = 2
const val FRAG_USER_ARTICLE = 3
const val FRAG_TODAY_ARTICLES = 4
// View Pager Frag
// Vp Frag Categories
const val FRAG_ECOLOGY = 5
const val FRAG_SOCIAL = 6
const val FRAG_ENERGY = 7
const val FRAG_HEALTH = 8
// Vp Frag User Article
const val FRAG_FAVORITE = 9
const val FRAG_PAUSED = 10

// For Vp User Article Icon id list
internal val USER_ARTICLE_ICONS = listOf(
    R.drawable.ic_baseline_star_black_24,
    R.drawable.ic_baseline_pause_black_24
)

// Sources
const val FRAG_SOURCES_LIST = 1000
const val FRAG_SOURCES_DETAIL = 1001

// Documentation url
const val DOCUMENTATION_URL = "https://github.com/desperu/IndependentNews/blob/master/Documentation/Documentation%20Fonctionnelle%20Independent%20News.pdf"

// Who owns what image list // TODO add divided images for better view
internal val WHO_OWNS_WHAT = arrayListOf(R.drawable.who_owns_what_2019, R.drawable.who_owns_what_2016)

// SnackBar Keys
const val SOURCE_FETCH = 0
const val SOURCE_ERROR = 1
const val SEARCH = 2
const val FIND = 3
const val FETCH = 4
const val ERROR = 5
const val END_FIND = 6
const val END_NOT_FIND = 7
const val END_ERROR = 8 // Not used ...

// For retry snack bar
const val PRE_DELAY = 500L

// Alert Dialog Key
const val ABOUT = 0
const val CONNEXION = 1
const val CONNEXION_START = 10
const val FIRST_START_ERROR = 20
const val REMOVE_PAUSED = 30
const val CANT_PARSE = 40

// For Request Code
const val RC_FIRST_START = 1000     // First start activity
const val RC_SOURCE = 2000          // Source activity
const val RC_SHOW_ARTICLE = 3000    // Back from Show Article to Sources
const val RC_PERMS = 10000          // For permissions

// For Skeleton Web View Content
const val SKELETON_TITLE_LENGTH = 300
const val SKELETON_CONTENT_LENGTH = 10000

// --- FOR FABS MENU ---
// For Sub Fabs Key
const val SUB_FAB_MIN_TEXT = 0
const val SUB_FAB_UP_TEXT = 1
const val SUB_FAB_STAR = 2
const val SUB_FAB_REMOVE_STAR = 3
const val SUB_FAB_PAUSE = 4
const val SUB_FAB_REMOVE_PAUSE = 5
const val SUB_FAB_HOME = 6

// For Sub Fabs list
internal val subFabList = listOf(
    SUB_FAB_MIN_TEXT,
    SUB_FAB_UP_TEXT,
    SUB_FAB_STAR,
    SUB_FAB_PAUSE,
    SUB_FAB_HOME
)

// --- TAP TARGET ---
// For display sequence keys
const val SHOW = 1
const val RETRY = 2
const val END = 3


// --- FOR FILTERS ---

// Key for filter page
const val SOURCES = 0
const val THEMES = 1
const val SECTIONS = 2
const val DATES = 3
const val CATEGORIES = 4 // only used to apply filters, not in UI.


// --- FOR SETTINGS ACTIVITY ---

// For alert dialog
const val NOTIF_TIME_DIALOG = 0
const val TEXT_SIZE_DIALOG = 1
const val REFRESH_TIME_DIALOG = 2
const val STORE_DELAY_DIALOG = 3
const val RESET_DIALOG = 4


// --- FOR DATABASE ---

// For init DB
const val DATABASE_NAME = "article"
const val DATABASE_VERSION = 1


// --- FOR KOIN ---

// For Qualifier Name
const val STANDARD_HTTP_REQUEST = "standardHttpRequest"
const val CLOUDFLARE_BYPASS = "cloudflareBypass"
// TODO try same for ArticleListInterface !!!
const val SOURCE_IMAGE_ROUTER = "sourceImageRouter"


// --- FOR UTILS ---

// To Convert Date
internal val monthNumber = arrayOf("janvier", "février", "mars", "avril", "mai", "juin",
    "juillet", "août", "septembre", "octobre", "novembre", "décembre")

// For image type
internal val imageSuffix = listOf(".png", ".jpg", ".jpeg")

// For compare result
const val EQUALS = 0
const val NOT_EQUALS = 1


// --- FOR SERVICES ---

// For Receiver
const val UPDATE_DATA = 0
const val NOTIFICATION = 1


// --- FOR SHARED PREFERENCES ---

// File name
const val INDEPENDENT_NEWS_PREFS = "IndependentNewsPrefs"

// Shared preferences keys
// Http Request
const val COOKIES_KEY = "appCookies"
// global use
const val IS_FIRST_TIME = "isFirstTime"
const val SOURCE_OPENED_TIMES = "sourceOpenedTimes"
// settings
const val NOTIFICATION_ENABLED = "notificationEnabled"
const val NOTIFICATION_TIME = "notificationTime"
const val TEXT_SIZE = "textSize"
const val AUTO_REMOVE_PAUSE = "autoRemovePause"
const val REFRESH_ARTICLE_LIST = "refreshArticleList"
const val REFRESH_TIME = "refreshTime"
const val REFRESH_ONLY_WIFI = "refreshOnlyWifi"
const val STORE_DELAY = "storeDelay"
// user article
const val ADDED_FAVORITE = "addedFavorite"
const val ADDED_PAUSED = "addedPaused"

// Settings default value
// Http Request
val COOKIES_DEFAULT = setOf<String>()
// global use
const val FIRST_TIME_DEFAULT = true
const val SOURCE_OPENED_DEFAULT = 0
// settings
const val NOTIFICATION_DEFAULT = true
const val NOTIFICATION_TIME_DEFAULT = 12
const val TEXT_SIZE_DEFAULT = 100
const val AUTO_REMOVE_PAUSE_DEFAULT = false
const val REFRESH_ARTICLE_LIST_DEFAULT = true
const val REFRESH_TIME_DEFAULT = 11
const val REFRESH_ONLY_WIFI_DEFAULT = false
const val STORE_DELAY_DEFAULT = 6
// user article
const val ADDED_FAVORITE_DEFAULT = 0
const val ADDED_PAUSED_DEFAULT = 0