package com.hexagon.domain.application

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class UserHttpHandler {
    fun handle(request: Request): Response {
        return Response(Status.OK).body("Hello World!")
    }
}