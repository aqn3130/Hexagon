package com.hexagon.ddt

import com.hexagon.events.AccountEvent
import com.hexagon.eventstore.EventStore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventStoreTests : BaseTest() {
    private val eventStore = EventStore()

    @Test
    fun `should save and retrieve events`() {
        val accountId = getAccountId()
        val accountCreateEvent = AccountEvent.AccountCreated(accountId, 1000.00)
        val accountWithdrawnEvent = AccountEvent.MoneyWithdrawn(100.00)

        val events = listOf(accountCreateEvent, accountWithdrawnEvent)
        eventStore.save(events, accountId)

        val retrievedEvents = eventStore.getEvents(accountId)

        assertEquals(events, retrievedEvents)
    }
}