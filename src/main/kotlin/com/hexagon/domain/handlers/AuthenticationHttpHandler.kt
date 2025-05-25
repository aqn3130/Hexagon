package com.hexagon.domain.handlers

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class AuthenticationHttpHandler {
    fun handleAuthentication(request: Request) : Response {
        return Response(Status.OK).body("Authenticated")
    }
}