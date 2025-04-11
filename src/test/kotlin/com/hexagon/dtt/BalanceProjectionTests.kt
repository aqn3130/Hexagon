package com.hexagon.dtt

import com.hexagon.events.AccountEvent
import com.hexagon.events.BalanceProjection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BalanceProjectionTests : BaseTest() {
    val balanceProjection = BalanceProjection()

    @Test
    fun `should update projection for AccountCreated event`() {

        val accountId = getAccountId()
        val event = AccountEvent.AccountCreated(accountId, 90.00)
        balanceProjection.updateProjection(event, accountId)

        assertEquals(90.00, balanceProjection.getBalance(accountId))
    }
}