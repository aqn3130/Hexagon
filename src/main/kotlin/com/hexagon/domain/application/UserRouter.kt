package com.hexagon.domain.application

import com.hexagon.adapters.PostgresRepository
import com.hexagon.db.DatabaseConfig
import com.hexagon.db.SimpleTransactor
import com.hexagon.domain.application.HexagonRoutes.createUser
import com.hexagon.domain.application.HexagonRoutes.getUserById
import com.hexagon.domain.handlers.adapter.UserHttpHandler
import com.natpryce.krouton.http4k.resources
import org.http4k.core.Method.POST
import org.http4k.core.Method.GET

class UserRouter {
    val userHttpHandler = UserHttpHandler(
        userRepository = PostgresRepository(),
        transactor = SimpleTransactor(dataSource = DatabaseConfig.dataSource),
    )

    val routes = resources {
         getUserById methods {
            GET(userHttpHandler::getUserById)
        }
        createUser methods {
            POST(userHttpHandler::createUser)
        }
    }
}