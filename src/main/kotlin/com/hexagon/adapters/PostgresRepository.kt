package com.hexagon.adapters

import java.util.*
import com.domain.ports.UserRepository
import com.hexagon.db.DatabaseConnection
import com.hexagon.domain.models.Name
import com.hexagon.domain.models.User

class PostgresRepository(): UserRepository {

    override fun getUser(dbConnection: DatabaseConnection, id: String): User? {
        val connection = dbConnection.connect()
        connection.use {
            val statement = it.prepareStatement("SELECT * FROM users WHERE id = ?")
            statement.setString(1, id)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                User(resultSet.getString("id"), Name(
                    resultSet.getString("name"),
                    resultSet.getString("name")
                )
                )
            } else {
                null
            }
        }
    }

    private fun toExternalForm(id: Any) : String {
        return id.toString()
    }

    override fun saveUser(dbConnection: DatabaseConnection, user: User) {
        val userId = UUID.randomUUID()
        val connection = dbConnection.connect()
        connection.use {
            val statement = it.prepareStatement("INSERT INTO users (id, name) VALUES (?, ?)")
            statement.setString(1, toExternalForm(userId))
            statement.setString(2, user.name.getName())
            statement.executeUpdate()
        }
    }
}