package com.hexagon.adapters

import java.util.*
import com.domain.ports.UserRepository
import com.hexagon.db.DatabaseConnection
import com.hexagon.domain.models.User

class PostgresRepository(private val dbConnection: DatabaseConnection): UserRepository {

    private fun getConnection() = dbConnection.connect()

    override fun getUser(id: String): User? {
        getConnection().use { connection ->
            val statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")
            statement.setString(1, id)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                User(resultSet.getString("id"), resultSet.getString("name"))
            } else {
                null
            }
        }
    }

    private fun toExternalForm(id: Any) : String {
        return id.toString()
    }

    override fun saveUser(user: User) {
        val userId = UUID.randomUUID()
        getConnection().use { connection ->
            val statement = connection.prepareStatement("INSERT INTO users (id, name) VALUES (?, ?)")
            statement.setString(1, toExternalForm(userId))
            statement.setString(2, user.name)
            statement.executeUpdate()
        }
    }
}