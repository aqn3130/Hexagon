package com.hexagon.domain.application

import com.hexagon.db.DatabaseConfig
import com.hexagon.domain.models.Account
import com.hexagon.eventstore.EventStore

class AccountService (dbConfig: DatabaseConfig) {

    private val eventStore = EventStore(dbConfig)

    fun getBalance(accountId: String) : Double {
        val events = eventStore.getEvents(accountId)
        val account = Account(accountId)
        account.replay(events)
        return account.getBalance()
    }
}