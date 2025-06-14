package com.domain.ports

import com.hexagon.db.DatabaseConfig
import com.hexagon.db.Transaction
import com.hexagon.domain.models.User

interface UserRepository {
    fun getUser(dbConfig: DatabaseConfig, id: String): User?
    fun saveUser(tx: Transaction, user: User)
}