package com.hexagon.domain.application

import com.domain.ports.UserRepository
import com.hexagon.db.SimpleTransactor
import com.hexagon.domain.models.User

//TODO: check if the code in this page is needed
//data class User(val id: String, val name: String)

class UserService(private val userRepository: UserRepository) {
//    val dataSource = DatabaseConnection().getDataSource()
//    val transactor = SimpleTransactor(dataSource)
//    fun getUser(id: String): User? = userRepository.getUser(dataSource,id)
//    fun saveUser(user: User) {
//        transactor.write { tx ->
//            userRepository.saveUser(tx, user)
//        }
//    }
}