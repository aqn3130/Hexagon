package com.hexagon.events

import java.util.UUID
import com.hexagon.db.DatabaseConfig
import com.hexagon.domain.models.Account
import com.hexagon.eventstore.EventStore

class ExampleAccountEvents {
    fun applyAccountEvents() : UUID {
        val eventStore = EventStore(DatabaseConfig)
        val balanceProjection = BalanceProjection(DatabaseConfig)
        var accountId = UUID.randomUUID()
        val account = Account(accountId.toExternalForm())

        // Create account with initial balance
        val createEvent = AccountEvent.AccountCreated(accountId.toExternalForm(), 100.0)
        account.apply(createEvent)
        balanceProjection.updateProjection(createEvent, accountId.toExternalForm())

        // Perform some operations
        val depositEvent = AccountEvent.MoneyDeposited(50.0)
        account.apply(depositEvent)
        balanceProjection.updateProjection(depositEvent, accountId.toExternalForm())

        val withdrawEvent = AccountEvent.MoneyWithdrawn(30.0)
        account.apply(withdrawEvent)
        balanceProjection.updateProjection(withdrawEvent, accountId.toExternalForm())

        // Save events to the store
        eventStore.save(account.getUncommittedChanges(), accountId.toExternalForm())

        // Print the current balance from the projection
        println("Current Balance: ${balanceProjection.getBalance(accountId.toExternalForm())}")

        // Retrieve and print all events
        val events = eventStore.getEvents(accountId.toExternalForm())
        println("Events: $events")

        return accountId
    }

    fun UUID.toExternalForm(): String {
        return this.toString()
    }
}