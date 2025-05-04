package com.hexagon.domain.application

import java.util.*
import com.hexagon.adapters.PostgresRepository
import com.hexagon.aggregate.Account
import com.hexagon.db.DatabaseConnection
import com.hexagon.domain.models.User
import com.hexagon.events.AccountEvent
import com.hexagon.events.BalanceProjection
import com.hexagon.eventstore.EventStore
import com.hexagon.models.UserViewModel
import com.natpryce.krouton.*
import com.natpryce.krouton.http4k.ResourceRouter
import com.natpryce.krouton.http4k.resources
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

class Routes {

    val eventStore = EventStore(DatabaseConnection())
    val balanceProjection = BalanceProjection(DatabaseConnection())

    data class CreateAccountRequest(val accountId: UUID, val initialBalance: Double)
    data class TransactionRequest(val accountId: String, val amount: Double)

    val createAccountLens = Body.auto<CreateAccountRequest>().toLens()
    val transactionLens = Body.auto<TransactionRequest>().toLens()

    val userRepository = PostgresRepository(DatabaseConnection())

    private val userId by string
    private val accountId by string
    private val getUserById = root + "user" + userId
    private val createUser = root + "user"
    private val accounts = root + "accounts"
    private val deposit = root + "accounts" + "deposit"
    private val withdraw = accounts + "withdraw"
    private val accountBalance = accounts + accountId + "balance"

    val resources: ResourceRouter = resources {

        getUserById methods {
            GET { _, userId ->
                val renderer = HandlebarsTemplates().CachingClasspath()
                val view = Body.viewModel(renderer, TEXT_HTML).toLens()
                val user = userRepository.getUser(userId) ?: return@GET Response(Status.NOT_FOUND)
                val viewModel = user?.let { UserViewModel(it.id, it.name) }
                Response(OK).with(view of viewModel!!)
            }
        }

        createUser methods {
            POST { request ->
                val name = request.query("name")
                val user = request.bodyString().let { User(it, name.toString()) }
                userRepository.saveUser(user)
                Response(Status.CREATED)
            }
        }

        accounts methods {
            POST { req ->
                val request = createAccountLens(req)
                val account = Account(request.accountId.toString())
                val event = AccountEvent.AccountCreated(request.accountId.toString(), request.initialBalance)
                account.apply(event)
                balanceProjection.updateProjection(event, request.accountId.toString())
                eventStore.save(account.getUncommittedChanges(), request.accountId.toString())
                Response(Status.CREATED).body("Account created")
            }
        }

        deposit methods {
            POST { req ->
                val request = transactionLens(req)
                val account = Account(request.accountId)
                val events = eventStore.getEvents(request.accountId)
                account.replay(events)
                val event = AccountEvent.MoneyDeposited(request.amount)
                account.apply(event)
                balanceProjection.updateProjection(event, request.accountId)
                eventStore.save(account.getUncommittedChanges(), request.accountId)
                Response(OK).body("Deposit successful")
            }
        }

        withdraw methods {
            POST { req ->
                val request = transactionLens(req)
                val account = Account(request.accountId)
                val events = eventStore.getEvents(request.accountId)
                account.replay(events)
                val event = AccountEvent.MoneyWithdrawn(request.amount)
                account.apply(event)
                balanceProjection.updateProjection(event, request.accountId)
                eventStore.save(account.getUncommittedChanges(), request.accountId)
                Response(OK).body("Withdrawal successful")
            }
        }
        accountBalance methods {
            GET { req, accountId ->
                val balance = balanceProjection.getBalance(accountId)
                Response(OK).body(balance.toString())
            }
        }
    }
}