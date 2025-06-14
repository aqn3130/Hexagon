package com.hexagon.domain.handlers.adapter

import com.hexagon.adapters.PostgresRepository
import com.hexagon.db.SimpleTransactor
import com.hexagon.domain.models.Name
import com.hexagon.domain.models.User
import com.hexagon.handlebars.views.UserDetailViewModel
import com.hexagon.models.UserViewModel
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Status.Companion.OK
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

class UserHttpHandler(val userRepository: PostgresRepository, val transactor: SimpleTransactor) {

    fun getUserById(request: Request, userId: String) : Response {
        val renderer = HandlebarsTemplates().CachingClasspath()
        val view = Body.viewModel(renderer, TEXT_HTML).toLens()
        val user = UserDetailViewModel(userId).getUserViewModel() ?: return Response(Status.NOT_FOUND)
        val viewModel = user.let { UserViewModel(it.id, it.name.getName()) }
        return Response(OK).with(view of viewModel)
    }

    fun createUser(request: Request) : Response {
        val firstName = request.query("firstname").toString()
        val lastName = request.query("lastname").toString()
        val user = request.bodyString().let { User(it, Name(firstName, lastName)) }

        transactor.write { tx ->
            userRepository.saveUser(tx, user)
        }

        return Response(Status.CREATED)
    }
}