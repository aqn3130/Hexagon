package com.hexagon.ddt.http.page

import java.net.URI
import org.http4k.webdriver.Http4kWebDriver
import org.openqa.selenium.By

class UserDetailsPage(
    override val webDriver: Http4kWebDriver,
    private val baseUri: URI,
    private val userId: String
) : Page by NavigatingPage(
    webDriver = webDriver,
    pageUri = baseUri.userDetailsPage(userId)
) {
    val userFirstName get() = findElementOrNull("""[data-test="user-name"]""")
        ?.takeIf { it.isEnabled }
}