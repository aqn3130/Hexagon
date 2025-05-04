package com.hexagon.domain.application

import com.hexagon.domain.application.HexagonRoutes.accounts
import com.hexagon.domain.handlers.AccountHttpHandler
import com.natpryce.krouton.http4k.resources
import org.http4k.core.Method.POST

class AccountRouter {
    private val accountHttpHandler = AccountHttpHandler()

    val routes = resources {
        accounts methods {
            POST(accountHttpHandler::createAccount)
        }
    }
}