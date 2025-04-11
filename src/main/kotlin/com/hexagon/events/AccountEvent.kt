package com.hexagon.events

sealed class AccountEvent {
    data class AccountCreated(val account_id: String, val initialBalance: Double) : AccountEvent()
    data class MoneyDeposited(val amount: Double) : AccountEvent()
    data class MoneyWithdrawn(val amount: Double) : AccountEvent()
}
