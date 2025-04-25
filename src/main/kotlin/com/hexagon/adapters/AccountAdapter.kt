package com.hexagon.adapters

import java.time.Duration
import java.util.*
import com.hexagon.db.DatabaseConnection
import com.hexagon.aggregate.Account
import com.hexagon.events.AccountEvent
import com.hexagon.events.BalanceProjection
import com.hexagon.eventstore.EventStore
import com.hexagon.formats.JacksonMessage
import com.hexagon.formats.jacksonMessageLens
import com.hexagon.models.HandlebarsViewModel
import com.hexagon.models.UserViewModel
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowSynchronizationStrategy.SYNCHRONIZED
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

class AccountAdapter {

    val eventStore = EventStore(DatabaseConnection())
    val balanceProjection = BalanceProjection(DatabaseConnection())

    data class CreateAccountRequest(val accountId: UUID, val initialBalance: Double)
    data class TransactionRequest(val accountId: String, val amount: Double)

    val createAccountLens = Body.auto<CreateAccountRequest>().toLens()
    val transactionLens = Body.auto<TransactionRequest>().toLens()

    val userRepository = PostgresRepository(DatabaseConnection())

    val app: HttpHandler = routes(

        "/user/{id}" bind GET to { request ->
            val renderer = HandlebarsTemplates().CachingClasspath()
            val view = Body.viewModel(renderer, TEXT_HTML).toLens()
            val id = request.path("id") ?: Response(Status.BAD_REQUEST)
            val user: com.hexagon.domain.models.UserViewModel? = userRepository.getUser(id.toString())

            run {
                val viewModel = user?.let { UserViewModel(it.id, it.name) }
                Response(OK).with(view of viewModel!!)
            }
        },
        "/user" bind POST to { request ->
            val name = request.query("name")
            val user = request.bodyString().let { com.hexagon.domain.models.UserViewModel(it, name.toString()) }
            userRepository.saveUser(user)
            Response(Status.CREATED)
        },

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
}