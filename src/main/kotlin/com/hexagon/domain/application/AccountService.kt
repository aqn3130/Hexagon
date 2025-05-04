package com.hexagon.domain.application

import com.hexagon.db.DatabaseConnection
import com.hexagon.events.BalanceProjection

class AccountService (connection: DatabaseConnection) {

    private val balanceProjection = BalanceProjection(connection)

    fun getBalance(accountId: String) = balanceProjection.getBalance(accountId)
}