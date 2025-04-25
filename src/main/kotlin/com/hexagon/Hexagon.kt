package com.hexagon

import com.adapters.AccountAdapter
import com.db.Migration
import com.hexagon.events.AccountEvents
import com.hexagon.events.Replay
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.server.Undertow
import org.http4k.server.asServer

val app = AccountAdapter().app

fun main() {
    Migration().runMigration()
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(Undertow(9000)).start()

    println("Server started on " + server.port())

    applyAccountEvents()
}

private fun applyAccountEvents() {
    val sampleAccountId = AccountEvents().applyAccountEvents()
    val balance = Replay().getBalanceByReplay(sampleAccountId.toString()) ?: null
    println(balance)
}
