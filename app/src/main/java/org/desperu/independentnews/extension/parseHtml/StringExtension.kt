package org.desperu.independentnews.extension.parseHtml

/**
 * Convert a local url to full url for external use, if it's not already one.
 *
 * @param baseUrl the base url of the web site from came the local url.
 *
 * @return the full url.
 */
internal fun String?.toFullUrl(baseUrl: String): String =
    if (!this.isNullOrBlank() && !this.contains("http"))
        baseUrl + this.removePrefix("/").removePrefix("/")
    else
        this.mToString()