package com.hexagon.ddt

import java.util.UUID
import com.hexagon.db.DatabaseConnection
import com.hexagon.aggregate.Account
import com.hexagon.events.AccountEvent
import com.hexagon.events.BalanceProjection
import com.hexagon.eventstore.EventStore
import org.http4k.core.*
import org.http4k.filter.DebuggingFilters
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

open class BaseTest {
    private fun UUID.toExternalForm() = this.toString()
    fun getAccountId() = UUID.randomUUID().toExternalForm()
}
