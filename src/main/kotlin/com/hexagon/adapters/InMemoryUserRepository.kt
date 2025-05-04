package com.hexagon.adapters

import com.hexagon.domain.models.User
import com.domain.ports.UserRepository
import com.hexagon.ErrorCode
import com.hexagon.Result

class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<String, User>()

    override fun getUser(id: String): User? = users[id]

    override fun saveUser(user: User) {
        users[user.id] = user
    }
}