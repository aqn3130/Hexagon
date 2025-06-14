package com.domain.ports

import com.hexagon.adapters.PostgresRepository
import com.hexagon.db.DatabaseConfig
import com.hexagon.models.UserViewModel
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.routing.path
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.TemplateRenderer
import org.http4k.template.ViewModel
import org.http4k.template.viewModel

class UserController(private val userService: PostgresRepository) {
    private val renderer: TemplateRenderer = HandlebarsTemplates().CachingClasspath()
    private val view = Body.viewModel(renderer, TEXT_HTML).toLens()

    fun getUser(request: Request): Response {
        val dbConfig = DatabaseConfig
        val id = request.path("id") ?: return Response(Status.BAD_REQUEST)
        val user = userService.getUser(dbConfig, id) ?: return Response(Status.NOT_FOUND)
        val viewModel = UserViewModel(user.id, user.name.getName())
        return Response(Status.OK).with(view of viewModel)
    }
}
