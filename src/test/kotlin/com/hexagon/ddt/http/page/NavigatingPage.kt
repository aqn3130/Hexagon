package com.hexagon.ddt.http.page

import java.net.URI
import com.natpryce.krouton.extend
import org.http4k.webdriver.Http4kWebDriver

class NavigatingPage(
    override val webDriver: Http4kWebDriver,
    override val pageUri: URI
) : Page {

    init {
        webDriver.navigate().to(pageUri)
    }
}

fun URI.userDetailsPage(userId: String): URI = extend("/user/${userId}")