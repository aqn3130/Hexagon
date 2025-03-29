package com.adapters

import com.domain.models.UserViewModel
import com.domain.ports.UserRepository

class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<String, UserViewModel>()

    override fun getUser(id: String): UserViewModel? = users[id]

    override fun saveUser(user: UserViewModel) {
        users[user.id] = user
    }
}