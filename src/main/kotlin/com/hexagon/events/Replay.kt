package com.hexagon.events

import com.hexagon.db.DatabaseConnection
import com.hexagon.aggregate.Account
import com.hexagon.eventstore.EventStore

class Replay {
    fun getBalanceByReplay(accountId: String): Double {
        val events = EventStore(DatabaseConnection()).getEvents(accountId)
        val account = Account(accountId)
        account.replay(events)
        return account.getBalance()
    }
}