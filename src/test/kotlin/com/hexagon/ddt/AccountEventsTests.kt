package com.hexagon.ddt

import com.hexagon.aggregate.Account
import com.hexagon.events.AccountEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AccountEventsTests: BaseTest(){
    @Test
    fun `should apply AccountCreated event`() {
        val accountId = getAccountId()
        val account = Account(accountId)

        val event = AccountEvent.AccountCreated(accountId, 100.00)
        account.apply(event)

        val expectedAccountBalance = 100.00
        assertEquals(expectedAccountBalance, account.getBalance())
    }

    @Test
    fun `should apply MoneyDeposited event`() {
        val accountId = getAccountId()
        val account = Account(accountId)

        account.apply(AccountEvent.AccountCreated(accountId, 50.00))
        val event = AccountEvent.MoneyDeposited(200.00)
        account.apply(event)

        val expectedAccountBalance = 250.00
        assertEquals(expectedAccountBalance, account.getBalance())
    }

    @Test
    fun `should apply MoneyWithdrawn event`() {
        val accountId = getAccountId()
        val account = Account(accountId)

        val accountCreateEvent = AccountEvent.AccountCreated(accountId, 20.00)
        account.apply(accountCreateEvent)

        val moneyWithdrawnEvent = AccountEvent.MoneyWithdrawn(10.00)
        account.apply(moneyWithdrawnEvent)

        val expectedAccountBalance = 10.00
        assertEquals(expectedAccountBalance, account.getBalance())
    }

    @Test
    fun `should replay events to rebuild state`() {
        val accountId = getAccountId()
        val account = Account(accountId)
        val accountCreateEvent = AccountEvent.AccountCreated(accountId, 200.00)
        val accountDepositEvent = AccountEvent.MoneyDeposited(100.00)
        val accountWithdrawnEvent = AccountEvent.MoneyWithdrawn(50.00)

        val expectedAccountBalance = (200.00 + 100.00) - 50.00

        val listOfEvents = listOf(accountCreateEvent, accountDepositEvent, accountWithdrawnEvent)
        account.replay(listOfEvents)

        assertEquals(expectedAccountBalance, account.getBalance())
    }
}