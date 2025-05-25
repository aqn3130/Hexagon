package com.hexagon.ddt.http.interactors

import java.net.URI
import com.hexagon.ddt.BaseTest
import com.hexagon.ddt.http.inmemory.interactors.UserInteractor
import com.hexagon.ddt.http.page.userDetailsPage
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldInclude
import org.http4k.webdriver.Http4kWebDriver
import org.openqa.selenium.By

class HttpUserInteractor : UserInteractor, BaseTest() {
    val http4kWebDriver = Http4kWebDriver(app)
    override fun `sees user first name`(userId: String, userName: String) {
//        val page = UserDetailsPage(
//            webDriver = http4kWebDriver,
//            baseUri = URI.create("http://localhost:8080"),
//            userId = userId
//        )
//
//        page.userFirstName shouldBe userName

        val baseUrl = URI.create("http://localhost:8080")
        http4kWebDriver.navigate().to("${baseUrl.userDetailsPage(userId)}")
        val userNameElement = http4kWebDriver.findElement(By.cssSelector("""[data-test="user-name"]"""))
        userNameElement.text shouldInclude userName

        val button = http4kWebDriver.findElement(By.cssSelector("""[data-test="button"]"""))
        button.click()

        http4kWebDriver.pageSource shouldBe "Authenticated"
    }
}