package com.domain.ports

import com.hexagon.aggregate.User

interface UserRepository {
    fun getUser(id: String): User?
    fun saveUser(user: User)
}