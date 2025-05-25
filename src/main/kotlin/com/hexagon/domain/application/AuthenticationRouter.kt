package com.hexagon.domain.application

import com.hexagon.domain.application.HexagonRoutes.authenticate
import com.hexagon.domain.handlers.AuthenticationHttpHandler
import com.natpryce.krouton.http4k.resources
import org.http4k.core.Method.POST

class AuthenticationRouter {
    val authenticationHttpHandler = AuthenticationHttpHandler()
    val routes = resources {
        authenticate methods {
            POST(authenticationHttpHandler::handleAuthentication)
        }
    }
}