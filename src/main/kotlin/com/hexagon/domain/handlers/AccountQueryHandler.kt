package com.hexagon.domain.handlers

import com.hexagon.domain.application.AccountService
import com.hexagon.domain.application.ViewAccountUseCase
import com.hexagon.lib.common.ErrorCode
import com.hexagon.lib.common.Result
import com.hexagon.lib.common.asSuccess

class AccountQueryHandler(private val accountService: AccountService) : ViewAccountUseCase {

    override fun handle(accountId: String): Result<ErrorCode, Double> {
        return accountService.getBalance(accountId).asSuccess()
    }

}