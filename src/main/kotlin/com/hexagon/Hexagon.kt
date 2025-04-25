package com.hexagon

import com.hexagon.domain.application.Routes
import com.hexagon.db.Migration
import com.hexagon.events.ExampleAccountEvents
import com.hexagon.events.Replay
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.server.Undertow
import org.http4k.server.asServer

val app = Routes().app

fun main() {
    Migration().runMigration()
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(Undertow(9000)).start()

    println("Server started on " + server.port())

    applyAccountEvents()
}

private fun applyAccountEvents() {
    val sampleAccountId = ExampleAccountEvents().applyAccountEvents()
    val balance = Replay().getBalanceByReplay(sampleAccountId.toString()) ?: null
    println(balance)
}
