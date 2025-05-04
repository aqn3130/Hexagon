package com.hexagon.lib.common

import com.hexagon.lib.common.Result.Companion.failure
import com.hexagon.lib.common.Result.Companion.success

@Suppress("unused")
sealed class Result<out ERR : ErrorCode, out T> {

    data class Failure<out ERR : ErrorCode>(val value: ERR) : Result<ERR, Nothing>()
    data class Success<out T>(val value: T) : Result<Nothing, T>() {
        companion object {
            operator fun invoke(): Result<Nothing, Unit> = Success(Unit)
        }
    }

    companion object {
        fun <ERR : ErrorCode> failure(err: ERR): Result<ERR, Nothing> = Failure(err)

        fun <T> success(value: T): Result<Nothing, T> = Success(value)

        fun <T> of(value: T): Result<Nothing, T> = Success(value)

        fun <ERR : ErrorCode, T> ERR?.onSuccess(value: T): Result<ERR, T> = this?.asFailure() ?: success(value)

        fun <ERR : ErrorCode, T> fromNullable(opt: T?, orElse: ERR): Result<ERR, T> = opt?.asSuccess()
            ?: orElse.asFailure()

        fun <ERR : ErrorCode, T> tryCatch(tryable: () -> T, catchable: (Throwable) -> ERR): Result<ERR, T> = tryCatchResult(tryable, catchable)
    }
}

fun <ERR : ErrorCode> ERR.asFailure(): Result<ERR, Nothing> = failure(this)
fun <T> T.asSuccess(): Result<Nothing, T> = success(this)
fun <ERR : ErrorCode, T> tryCatchResult(f: () -> T, onError: (Throwable) -> ERR): Result<ERR, T> =
    try {
        success(f())
    } catch (t: Throwable) {
        failure(onError(t))
    }