package com.hexagon.lib.common

sealed interface AccountErrorCode : ErrorCode {
    data object NotFound : AccountErrorCode
}