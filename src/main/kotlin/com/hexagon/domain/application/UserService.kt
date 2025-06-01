package com.hexagon.domain.application

import com.hexagon.domain.models.User
import com.domain.ports.UserRepository
import com.hexagon.db.DatabaseConnection

//TODO: check if the code in this page is needed
//data class User(val id: String, val name: String)

class UserService(private val userRepository: UserRepository) {
    val dbConnection = DatabaseConnection()
    fun getUser(id: String): User? = userRepository.getUser(dbConnection,id)
    fun saveUser(user: User) {
        userRepository.saveUser(dbConnection, user)
    }
}