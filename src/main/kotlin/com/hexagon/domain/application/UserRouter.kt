package com.hexagon.domain.application

import com.hexagon.domain.application.HexagonRoutes.createUser
import com.hexagon.domain.application.HexagonRoutes.getUserById
import com.hexagon.domain.handlers.UserHttpHandler
import com.natpryce.krouton.http4k.resources
import org.http4k.core.Method.POST
import org.http4k.core.Method.GET

class UserRouter {
    val userHttpHandler = UserHttpHandler()

    val routes = resources {
         getUserById methods {
            GET(userHttpHandler::getUserById)
        }
        createUser methods {
            POST(userHttpHandler::createUser)
        }
    }
}