package com.hexagon.ddt

import java.util.UUID

open class BaseTest {
    private fun UUID.toExternalForm() = this.toString()
    fun getAccountId() = UUID.randomUUID().toExternalForm()
}
