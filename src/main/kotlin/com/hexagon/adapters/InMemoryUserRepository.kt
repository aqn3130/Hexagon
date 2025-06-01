package com.hexagon.adapters

import com.hexagon.domain.models.User
import com.domain.ports.UserRepository
import com.hexagon.db.DatabaseConnection

class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<String, User>()

    override fun getUser(dbConnection: DatabaseConnection, id: String): User? = users[id]

    override fun saveUser(dbConnection: DatabaseConnection, user: User) {
        users[user.id] = user
    }
}