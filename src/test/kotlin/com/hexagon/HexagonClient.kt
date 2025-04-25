package com.hexagon

import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintResponse
import org.http4k.filter.ResilienceFilters.CircuitBreak
import com.hexagon.adapters.AccountAdapter

fun main() {
    val client: HttpHandler = JavaHttpClient()
    val circuitBreaker = AccountAdapter().circuitBreaker

    val printingClient: HttpHandler = PrintResponse()
            .then(CircuitBreak(circuitBreaker, isError = { r: Response -> !r.status.successful } )).then(client)

    val response: Response = printingClient(Request(GET, "http://localhost:9000/ping"))

    println(response.bodyString())
}
