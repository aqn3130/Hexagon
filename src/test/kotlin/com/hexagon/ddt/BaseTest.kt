package com.hexagon.ddt

import java.util.UUID
import com.hexagon.domain.application.AccountRouter
import com.hexagon.domain.application.Hexagon
import com.hexagon.domain.application.UserRouter

open class BaseTest {
    val app = Hexagon(accountRouter = AccountRouter(), userRouter = UserRouter()).resources
    private fun UUID.toExternalForm() = this.toString()
    fun getAccountId() = UUID.randomUUID().toExternalForm()
    fun getUserId() = UUID.randomUUID().toExternalForm()
}
