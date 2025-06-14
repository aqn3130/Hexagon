package com.hexagon.domain.application.port.input

import com.hexagon.lib.common.ErrorCode
import com.hexagon.lib.common.Result

interface AccountDepositUseCase {
    fun handle(command: AccountDepositCommand) : Result<ErrorCode, Unit>
}

data class AccountDepositCommand(val accountId: String, val amount: Double)