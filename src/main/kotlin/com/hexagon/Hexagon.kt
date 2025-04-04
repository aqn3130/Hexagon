package com.hexagon

import com.hexagon.formats.JacksonMessage
import com.hexagon.formats.jacksonMessageLens
import com.hexagon.models.HandlebarsViewModel
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowSynchronizationStrategy.SYNCHRONIZED
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED
import java.time.Duration
import java.util.ArrayDeque
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

val circuitBreaker = CircuitBreaker.of(
    "circuit",
    CircuitBreakerConfig.custom()
        .slidingWindow(2, 2, COUNT_BASED, SYNCHRONIZED)
        .permittedNumberOfCallsInHalfOpenState(2)
        .waitDurationInOpenState(Duration.ofSeconds(1))
        .build()
)

val circuitBreakerEndpointResponses = ArrayDeque<Response>().apply {
    add(Response(OK))
    add(Response(OK))
    add(Response(INTERNAL_SERVER_ERROR))
}
val app: HttpHandler = routes(
    "/ping" bind GET to {
        Response(OK).body("pong")
    },

    "/formats/json/jackson" bind GET to {
        Response(OK).with(jacksonMessageLens of JacksonMessage("Barry", "Hello there!"))
    },

    "/templates/handlebars" bind GET to {
        val renderer = HandlebarsTemplates().CachingClasspath()
        val view = Body.viewModel(renderer, TEXT_HTML).toLens()
        val viewModel = HandlebarsViewModel("Hello there!")
        Response(OK).with(view of viewModel)
    },

    "/testing/hamkrest" bind GET to {request ->
        Response(OK).body("Echo '${request.bodyString()}'")
    },

    "/resilience" bind GET to {
        circuitBreakerEndpointResponses.pop()
    }
)

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(Undertow(9000)).start()

    println("Server started on " + server.port())
}
