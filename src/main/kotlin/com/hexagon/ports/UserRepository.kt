package com.domain.ports

import com.hexagon.domain.models.UserViewModel

interface UserRepository {
    fun getUser(id: String): UserViewModel?
    fun saveUser(user: UserViewModel)
}