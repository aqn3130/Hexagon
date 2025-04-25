package com.hexagon.domain.application

import com.hexagon.domain.models.User
import com.domain.ports.UserRepository

data class User(val id: String, val name: String)

class UserService(private val userRepository: UserRepository) {
    fun getUser(id: String): User? = userRepository.getUser(id)
    fun saveUser(user: User) {
        userRepository.saveUser(user)
    }
}