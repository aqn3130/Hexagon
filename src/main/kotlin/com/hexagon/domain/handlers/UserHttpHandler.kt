package com.hexagon.domain.handlers

import com.hexagon.adapters.PostgresRepository
import com.hexagon.db.DatabaseConnection
import com.hexagon.domain.models.User
import com.hexagon.models.UserViewModel
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Status.Companion.OK
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

class UserHttpHandler {
    private val userRepository = PostgresRepository(DatabaseConnection())

    fun getUserById(request: Request, userId: String) : Response {
        val renderer = HandlebarsTemplates().CachingClasspath()
        val view = Body.viewModel(renderer, TEXT_HTML).toLens()
        val user = userRepository.getUser(userId) ?: return Response(Status.NOT_FOUND)
        val viewModel = user.let { UserViewModel(it.id, it.name) }
        return Response(OK).with(view of viewModel)
    }

    fun createUser(request: Request) : Response {
        val name = request.query("name")
        val user = request.bodyString().let { User(it, name.toString()) }
        userRepository.saveUser(user)
        return Response(Status.CREATED)
    }
}