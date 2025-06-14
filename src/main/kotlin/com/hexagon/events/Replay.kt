package com.hexagon.events

import com.hexagon.db.DatabaseConfig
import com.hexagon.domain.models.Account
import com.hexagon.eventstore.EventStore

class Replay {
    fun getBalanceByReplay(accountId: String): Double {
        val events = EventStore(DatabaseConfig).getEvents(accountId)
        val account = Account(accountId)
        account.replay(events)
        return account.getBalance()
    }
}