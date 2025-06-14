package com.hexagon.adapters

import com.hexagon.domain.models.User
import com.domain.ports.UserRepository
import com.hexagon.db.DatabaseConfig
import com.hexagon.db.Transaction

class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<String, User>()

    override fun getUser(dbConfig: DatabaseConfig, id: String): User? = users[id]

    override fun saveUser(tx: Transaction, user: User) {
        users[user.id] = user
    }
}