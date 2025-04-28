package com.hexagon.domain.application

import com.natpryce.krouton.http4k.Route
import com.natpryce.krouton.http4k.resources
import com.hexagon.domain.application.UserRoutes.usersRoute
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status

class UserRouter {
    val routes = resources {
        usersRoute methods {
            Method.GET(UserHttpHandler()::handle)
        }
    }
}