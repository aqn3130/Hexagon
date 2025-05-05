package com.domain.ports

import com.hexagon.domain.models.User

interface UserRepository {
    fun getUser(id: String): User?
    fun saveUser(user: User)
}