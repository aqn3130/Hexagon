package com.hexagon.handlebars.views

import com.hexagon.adapters.PostgresRepository
import com.hexagon.db.DatabaseConfig
import com.hexagon.domain.models.User

class UserDetailViewModel(private val userId: String) {
    private val userRepository = PostgresRepository()

    fun getUserViewModel(): User? {
        val dbConfig = DatabaseConfig
        return userRepository.getUser(dbConfig, userId)
    }
}