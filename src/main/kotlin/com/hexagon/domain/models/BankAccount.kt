package com.hexagon.domain.models

import com.hexagon.events.AccountEvent

// Define events
sealed class BankEvent {
    abstract val accountId: String
}

data class AccountCreated(
    override val accountId: String,
    val initialBalance: Double
) : BankEvent()

data class Deposited(
    override val accountId: String,
    val amount: Double
) : BankEvent()

data class Withdrawn(
    override val accountId: String,
    val amount: Double
) : BankEvent()

// Define state
data class BankAccount(
    val accountId: String,
    val balance: Double
)

// Apply events to state
fun applyEvent(state: BankAccount?, event: BankEvent): BankAccount? = when (event) {
    is AccountCreated -> BankAccount(accountId = event.accountId, balance = event.initialBalance)
    is Deposited -> state?.takeIf { it.accountId == event.accountId }
        ?.copy(balance = state.balance + event.amount)
    is Withdrawn -> state?.takeIf { it.accountId == event.accountId }
        ?.copy(balance = state.balance - event.amount)
}

// Example usage
fun main() {
    val accountId = "acc-123"

    val events = listOf(
        AccountCreated(accountId, initialBalance = 100.00),
        Deposited(accountId, amount = 50.00),
        Withdrawn(accountId, amount = 30.00)
    )

    val currentState = events.fold(null as BankAccount?, ::applyEvent)

    println("Current state: $currentState")
}
