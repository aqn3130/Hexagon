package com.hexagon.domain.application

import com.hexagon.lib.common.ErrorCode
import com.hexagon.lib.common.Result

interface ViewAccountUseCase {
    fun handle(accountId: String) : Result<ErrorCode, Double>
}