package com.hexagon.domain.handlers

import java.util.*
import com.hexagon.domain.models.Account
import com.hexagon.db.DatabaseConnection
import com.hexagon.domain.application.AccountService
import com.hexagon.domain.handlers.adapter.AccountQueryHandler
import com.hexagon.events.AccountEvent
import com.hexagon.events.BalanceProjection
import com.hexagon.eventstore.EventStore
import com.hexagon.lib.common.onFailure
import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto

data class CreateAccountRequest(val accountId: UUID, val initialBalance: Double)
data class TransactionRequest(val accountId: String, val amount: Double)

class AccountHttpHandler {
    private val eventStore = EventStore(DatabaseConnection())
    val createAccountLens = Body.auto<CreateAccountRequest>().toLens()
    private val balanceProjection = BalanceProjection(DatabaseConnection())
    val transactionLens = Body.auto<TransactionRequest>().toLens()

    fun createAccount(request: Request): Response {
        val accountLens = createAccountLens(request)
        val account = Account(accountLens.accountId.toString())
        val event = AccountEvent.AccountCreated(accountLens.accountId.toString(), accountLens.initialBalance)
        account.apply(event)
        balanceProjection.updateProjection(event, accountLens.accountId.toString())
        eventStore.save(account.getUncommittedChanges(), accountLens.accountId.toString())
        return Response(Status.CREATED).body("Account created")
    }

    fun deposit(request: Request): Response {
        val accountLens = transactionLens(request)
        val account = Account(accountLens.accountId)
        val events = eventStore.getEvents(accountLens.accountId)
        account.replay(events)
        val event = AccountEvent.MoneyDeposited(accountLens.amount)
        account.apply(event)
        balanceProjection.updateProjection(event, accountLens.accountId)
        eventStore.save(account.getUncommittedChanges(), accountLens.accountId)
        return Response(OK).body("Deposit successful")
    }

    fun withdraw(request: Request): Response {
        val accountLens = transactionLens(request)
        val account = Account(accountLens.accountId)
        val events = eventStore.getEvents(accountLens.accountId)
        account.replay(events)
        val event = AccountEvent.MoneyWithdrawn(accountLens.amount)
        account.apply(event)
        balanceProjection.updateProjection(event, accountLens.accountId)
        eventStore.save(account.getUncommittedChanges(), accountLens.accountId)
        return Response(OK).body("Withdrawal successful")
    }

    fun accountBalance(request: Request, accountId: String): Response {
        val balance = AccountQueryHandler(AccountService(connection = DatabaseConnection())).handle(accountId)
            .onFailure { return Response(NOT_FOUND)  }
        return Response(OK).body(balance.toString())
    }
}