package com.hexagon.domain.application

import com.hexagon.domain.models.UserViewModel
import com.domain.ports.UserRepository

data class User(val id: String, val name: String)

class UserService(private val userRepository: UserRepository) {
    fun getUser(id: String): UserViewModel? = userRepository.getUser(id)
    fun saveUser(user: UserViewModel) {
        userRepository.saveUser(user)
        println("User id: ${user.id}")
    }
}