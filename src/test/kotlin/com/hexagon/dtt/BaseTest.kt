package com.hexagon.dtt

import java.util.UUID

open class BaseTest {
    fun UUID.toExternalForm() = this.toString()
    fun getAccountId() = UUID.randomUUID().toExternalForm()
}