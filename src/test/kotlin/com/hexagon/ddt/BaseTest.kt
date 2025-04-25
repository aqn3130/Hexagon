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
    val app = startApp()
}

fun startApp(): RoutingHttpHandler {
    val eventStore = EventStore(DatabaseConnection())
    val balanceProjection = BalanceProjection(DatabaseConnection())

    data class CreateAccountRequest(val accountId: UUID, val initialBalance: Double)
    data class TransactionRequest(val accountId: String, val amount: Double)
    val createAccountLens = Body.auto<CreateAccountRequest>().toLens()
    val transactionLens = Body.auto<TransactionRequest>().toLens()


    return DebuggingFilters.PrintRequestAndResponse().then(
        routes(
            "/accounts" bind Method.POST to { req ->
                val request = createAccountLens(req)
                val account = Account(request.accountId.toString())
                val event = AccountEvent.AccountCreated(request.accountId.toString(), request.initialBalance)
                account.apply(event)
                balanceProjection.updateProjection(event, request.accountId.toString())
                eventStore.save(account.getUncommittedChanges(), request.accountId.toString())
                Response(Status.CREATED).body("Account created")
            },
            "/accounts/deposit" bind Method.POST to { req ->
                val request = transactionLens(req)
                val account = Account(request.accountId)
                val events = eventStore.getEvents(request.accountId)
                account.replay(events)
                val event = AccountEvent.MoneyDeposited(request.amount)
                account.apply(event)
                balanceProjection.updateProjection(event, request.accountId)
                eventStore.save(account.getUncommittedChanges(), request.accountId)
                Response(Status.OK).body("Deposit successful")
            },
            "/accounts/withdraw" bind Method.POST to { req ->
                val request = transactionLens(req)
                val account = Account(request.accountId)
                val events = eventStore.getEvents(request.accountId)
                account.replay(events)
                val event = AccountEvent.MoneyWithdrawn(request.amount)
                account.apply(event)
                balanceProjection.updateProjection(event, request.accountId)
                eventStore.save(account.getUncommittedChanges(), request.accountId)
                Response(Status.OK).body("Withdrawal successful")
            },
            "/accounts/{accountId}/balance" bind Method.GET to { req ->
                val accountId = req.path("accountId")!!
                val balance = balanceProjection.getBalance(accountId)
                Response(Status.OK).body(balance.toString())
            }
        )
    )
}