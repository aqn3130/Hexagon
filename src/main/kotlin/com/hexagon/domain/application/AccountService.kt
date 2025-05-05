package com.hexagon.domain.application

import com.hexagon.db.DatabaseConnection
import com.hexagon.domain.models.Account
import com.hexagon.eventstore.EventStore

class AccountService (connection: DatabaseConnection) {

    private val eventStore = EventStore(connection)

    fun getBalance(accountId: String) : Double {
        val events = eventStore.getEvents(accountId)
        val account = Account(accountId)
        account.replay(events)
        return account.getBalance()
    }
}