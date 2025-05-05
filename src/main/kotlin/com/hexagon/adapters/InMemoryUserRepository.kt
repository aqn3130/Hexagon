package com.hexagon.adapters

import com.hexagon.aggregate.User
import com.domain.ports.UserRepository

class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<String, User>()

    override fun getUser(id: String): User? = users[id]

    override fun saveUser(user: User) {
        users[user.id] = user
    }
}