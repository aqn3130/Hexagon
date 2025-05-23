package com.hexagon.ddt.http.page

import org.http4k.webdriver.Http4kWebDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

interface PageFragment {
    val webDriver: Http4kWebDriver

    fun findElementOrNull(cssSelector: String): WebElement? = webDriver.findElementOrNull(cssSelector)
}

fun WebDriver.findElementOrNull(cssSelector: String): WebElement? = findElements(By.cssSelector(cssSelector)).let {
    if (it.isNotEmpty()) {
        it.single()
    } else {
        null
    }
}