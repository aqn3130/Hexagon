package com.hexagon.adapters

import com.hexagon.domain.models.UserViewModel
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import com.hexagon.domain.application.UserService
import org.http4k.routing.path

fun httpHandler(userService: UserService): HttpHandler = routes(
    "/user/{id}" bind GET to { request ->
        val id = request.path("id") ?: return@to Response(Status.BAD_REQUEST)
        val user = userService.getUser(id)
        if (user != null) Response(Status.OK).body(user.toString())
        else Response(Status.NOT_FOUND)
    },
    "/user" bind POST to { request ->
        val user = request.bodyString().let { UserViewModel(it, "New User") }
        userService.saveUser(user)
        Response(Status.CREATED)
    }
)