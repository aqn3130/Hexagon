package com.hexagon.domain.handlers.adapter

import com.hexagon.db.DatabaseConfig
import com.hexagon.domain.application.port.input.AccountDepositCommand
import com.hexagon.domain.application.port.input.AccountDepositUseCase
import com.hexagon.domain.models.Account
import com.hexagon.events.AccountEvent
import com.hexagon.events.BalanceProjection
import com.hexagon.eventstore.EventStore
import com.hexagon.lib.common.ErrorCode
import com.hexagon.lib.common.Result
import com.hexagon.lib.common.asSuccess

class AccountDepositHandler : AccountDepositUseCase {
    private val eventStore = EventStore(DatabaseConfig)
    private val balanceProjection = BalanceProjection(DatabaseConfig)

    override fun handle(command: AccountDepositCommand): Result<ErrorCode, Unit> {
        val account = Account(command.accountId)
        val events = eventStore.getEvents(command.accountId)

        account.replay(events)

        val event = AccountEvent.MoneyDeposited(command.amount)

        account.apply(event)

        balanceProjection.updateProjection(event, command.accountId)
        eventStore.save(account.getUncommittedChanges(), command.accountId)

        return Unit.asSuccess()
    }
}