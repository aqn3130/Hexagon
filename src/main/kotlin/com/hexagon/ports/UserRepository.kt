package com.domain.ports

import com.domain.models.UserViewModel

interface UserRepository {
    fun getUser(id: String): UserViewModel?
    fun saveUser(user: UserViewModel)
}