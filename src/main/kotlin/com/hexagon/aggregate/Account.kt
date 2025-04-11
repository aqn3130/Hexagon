package com.hexagon.aggregate

import com.hexagon.events.AccountEvent

class Account(private val account_id: String) {
    private var balance: Double = 0.0
    private val changes = mutableListOf<AccountEvent>()

    fun apply(event: AccountEvent) {
        when (event) {
            is AccountEvent.AccountCreated -> balance = event.initialBalance
            is AccountEvent.MoneyDeposited -> balance += event.amount
            is AccountEvent.MoneyWithdrawn -> balance -= event.amount
        }
        changes.add(event)
    }

    fun deposit(amount: Double) {
        apply(AccountEvent.MoneyDeposited(amount))
    }

    fun withdraw(amount: Double) {
        apply(AccountEvent.MoneyWithdrawn(amount))
    }

    fun getBalance(): Double = balance
    fun getUncommittedChanges(): List<AccountEvent> = changes

    fun replay(events: List<AccountEvent>) {
        events.forEach { apply(it) }
    }
}
