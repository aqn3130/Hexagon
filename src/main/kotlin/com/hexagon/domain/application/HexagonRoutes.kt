package com.hexagon.domain.application

import com.natpryce.krouton.getValue
import com.natpryce.krouton.plus
import com.natpryce.krouton.root
import com.natpryce.krouton.string

object HexagonRoutes {
    private val userId by string
    private val accountId by string

    val getUserById = root + "user" + userId
    val createUser = root + "user"
    val accounts = root + "accounts"
    val deposit = root + "accounts" + "deposit"
    val withdraw = accounts + "withdraw"
    val accountBalance = accounts + accountId + "balance"
}