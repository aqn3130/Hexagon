package com.hexagon.domain.models

import com.hexagon.domain.application.port.input.AccountUseCase
import com.hexagon.events.AccountEvent

data class Account(private val account_id: String) : AccountUseCase {
    private var balance: Double = 0.0
    private val changes = mutableListOf<AccountEvent>()

    override fun apply(event: AccountEvent) {
        when (event) {
            is AccountEvent.AccountCreated -> balance = event.initialBalance
            is AccountEvent.MoneyDeposited -> balance += event.amount
            is AccountEvent.MoneyWithdrawn -> balance -= event.amount
        }
        changes.add(event)
    }

    override fun deposit(amount: Double) {
        apply(AccountEvent.MoneyDeposited(amount))
    }

    override fun withdraw(amount: Double) {
        apply(AccountEvent.MoneyWithdrawn(amount))
    }

    override fun getBalance(): Double = balance
    override fun getUncommittedChanges(): List<AccountEvent> = changes

    override fun replay(events: List<AccountEvent>) {
        events.forEach { apply(it) }
    }
}
