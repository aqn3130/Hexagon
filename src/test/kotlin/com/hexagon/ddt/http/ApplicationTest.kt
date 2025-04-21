package com.hexagon.ddt.http

import java.util.*
import com.hexagon.aggregate.Account
import com.hexagon.ddt.BaseTest
import com.hexagon.events.AccountEvent
import com.hexagon.events.BalanceProjection
import com.hexagon.eventstore.EventStore
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.filter.DebuggingFilters
import org.http4k.format.Jackson.auto
import org.http4k.hamkrest.hasStatus
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApplicationTest: BaseTest() {

    private val eventStore = EventStore()
    private val balanceProjection = BalanceProjection()


    data class CreateAccountRequest(val accountId: UUID, val initialBalance: Double)
    data class TransactionRequest(val accountId: String, val amount: Double)

    val createAccountLens = Body.auto<CreateAccountRequest>().toLens()
    val transactionLens = Body.auto<TransactionRequest>().toLens()


    private val app = DebuggingFilters.PrintRequestAndResponse().then(
        routes(
            "/accounts" bind Method.POST to { req ->
                val request = createAccountLens(req)
                val account = Account(request.accountId.toExternalForm())
                val event = AccountEvent.AccountCreated(request.accountId.toExternalForm(), request.initialBalance)
                account.apply(event)
                balanceProjection.updateProjection(event, request.accountId.toExternalForm())
                eventStore.save(account.getUncommittedChanges(), request.accountId.toExternalForm())
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

    @Test
    fun `test create account`() {
        val accountId = UUID.randomUUID()

        val request = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        val response = app(request)

        assertThat(response, hasStatus(Status.CREATED))
        assertEquals(response.bodyString(), "Account created")
    }

    @Test
    fun `test deposit money`() {
        val accountId = UUID.randomUUID()

        // First, create the account
        val createRequest = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        app(createRequest)

        // Then, deposit money
        val depositRequest = Request(POST, "/accounts/deposit").body("""{"accountId":"$accountId","amount":50.0}""")
        val response = app(depositRequest)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(response.bodyString(), "Deposit successful")
    }

    @Test
    fun `test withdraw money`() {
        val accountId = UUID.randomUUID()

        // First, create the account
        val createRequest = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        app(createRequest)

        // Then, withdraw money
        val withdrawRequest = Request(POST, "/accounts/withdraw").body("""{"accountId":"$accountId","amount":30.0}""")
        val response = app(withdrawRequest)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(response.bodyString(), "Withdrawal successful")
    }

    @Test
    fun `test get balance`() {
        val accountId = UUID.randomUUID()
        // First, create the account
        val createRequest = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        app(createRequest)

        // Then, deposit money
        val depositRequest = Request(POST, "/accounts/deposit").body("""{"accountId":"$accountId","amount":50.0}""")
        app(depositRequest)

        // Finally, get the balance
        val balanceRequest = Request(GET, "/accounts/$accountId/balance")
        val response = app(balanceRequest)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(response.bodyString(), "150.0")
    }
}
