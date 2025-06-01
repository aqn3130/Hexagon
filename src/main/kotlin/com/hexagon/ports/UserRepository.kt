package com.domain.ports

import com.hexagon.db.DatabaseConnection
import com.hexagon.domain.models.User

interface UserRepository {
    fun getUser(dbConnection: DatabaseConnection, id: String): User?
    fun saveUser(dbConnection: DatabaseConnection, user: User)
}