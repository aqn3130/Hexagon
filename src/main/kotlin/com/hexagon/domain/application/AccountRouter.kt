package com.hexagon.domain.application

import kotlin.reflect.KProperty
import com.hexagon.domain.application.HexagonRoutes.accounts
import com.hexagon.domain.application.HexagonRoutes.deposit
import com.hexagon.domain.application.HexagonRoutes.withdraw
import com.hexagon.domain.application.HexagonRoutes.accountBalance
import com.hexagon.domain.handlers.AccountHttpHandler
import com.natpryce.krouton.http4k.resources
import org.http4k.core.Method.POST
import org.http4k.core.Method.GET

class AccountRouter {
    private val accountHttpHandler = AccountHttpHandler()

    val routes = resources {
        accounts methods {
            POST(accountHttpHandler::createAccount)
        }
        deposit methods {
            POST(accountHttpHandler::deposit)
        }
        withdraw methods {
            POST(accountHttpHandler::withdraw)
        }
        accountBalance methods {
            GET(accountHttpHandler::accountBalance)
        }
    }
}