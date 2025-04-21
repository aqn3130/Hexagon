package com.hexagon.ddt

import com.hexagon.events.AccountEvent
import com.hexagon.events.BalanceProjection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BalanceProjectionTests : BaseTest() {
    private val balanceProjection = BalanceProjection()

    @Test
    fun `should update projection for AccountCreated event`() {

        val accountId = getAccountId()
        val event = AccountEvent.AccountCreated(accountId, 90.00)
        balanceProjection.updateProjection(event, accountId)

        assertEquals(90.00, balanceProjection.getBalance(accountId))
    }

    @Test
    fun `should update projection for MoneyDeposited event`() {
        val accountId = getAccountId()
        val event = AccountEvent.AccountCreated(accountId, 90.00)
        balanceProjection.updateProjection(event, accountId)

        val moneyDepositEvent = AccountEvent.MoneyDeposited(2000.00)
        balanceProjection.updateProjection(moneyDepositEvent, accountId)

        assertEquals(2090.00, balanceProjection.getBalance(accountId))
    }

    @Test
    fun `should update projection for MoneyWithdrawn event`() {
        val accountId = getAccountId()
        val event = AccountEvent.AccountCreated(accountId, 90.00)
        balanceProjection.updateProjection(event, accountId)

        val moneyWithdrawnEvent = AccountEvent.MoneyWithdrawn(100.00)
        balanceProjection.updateProjection(moneyWithdrawnEvent, accountId)

        assertEquals(-10.00, balanceProjection.getBalance(accountId))
    }
}