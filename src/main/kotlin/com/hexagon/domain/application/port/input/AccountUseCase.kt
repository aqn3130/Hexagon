package com.hexagon.domain.application.port.input

import com.hexagon.events.AccountEvent

interface AccountUseCase {
    fun apply(event: AccountEvent)

    fun deposit(amount: Double)

    fun withdraw(amount: Double)

    fun getBalance() : Double

    fun getUncommittedChanges() : List<AccountEvent>

    fun replay(events: List<AccountEvent>)
}