package com.hexagon.handlebars.views

import com.hexagon.adapters.PostgresRepository
import com.hexagon.db.DatabaseConnection
import com.hexagon.domain.models.User

class UserDetailViewModel(private val userId: String) {
    private val userRepository = PostgresRepository(DatabaseConnection())

    fun getUserViewModel(): User? {
        return userRepository.getUser(userId)
    }
}