package com.hexagon.domain.handlers

import java.util.*
import com.hexagon.aggregate.Account
import com.hexagon.db.DatabaseConnection
import com.hexagon.events.AccountEvent
import com.hexagon.events.BalanceProjection
import com.hexagon.eventstore.EventStore
import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto

data class CreateAccountRequest(val accountId: UUID, val initialBalance: Double)

class AccountHttpHandler {
    val eventStore = EventStore(DatabaseConnection())
    val createAccountLens = Body.auto<CreateAccountRequest>().toLens()
    val balanceProjection = BalanceProjection(DatabaseConnection())

    fun createAccount(request: Request) : Response {
        val accountLens = createAccountLens(request)
        val account = Account(accountLens.accountId.toString())
        val event = AccountEvent.AccountCreated(accountLens.accountId.toString(), accountLens.initialBalance)
        account.apply(event)
        balanceProjection.updateProjection(event, accountLens.accountId.toString())
        eventStore.save(account.getUncommittedChanges(), accountLens.accountId.toString())
        return Response(Status.CREATED).body("Account created")
    }
}