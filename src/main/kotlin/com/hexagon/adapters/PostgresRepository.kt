package com.hexagon.adapters

import java.sql.Connection
import java.util.*
import com.domain.ports.UserRepository
import com.hexagon.db.DatabaseConfig
import com.hexagon.db.Transaction
import com.hexagon.domain.models.Name
import com.hexagon.domain.models.User

class PostgresRepository() : UserRepository {

    override fun getUser(dbConfig: DatabaseConfig, id: String): User? {
        val connection = dbConfig.dataSource.connection
            val statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")
            statement.setString(1, id)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                User(
                    resultSet.getString("id"), Name(
                        resultSet.getString("name"),
                        resultSet.getString("name")
                    )
                )
            } else {
                null
            }
    }

    private fun toExternalForm(id: Any): String {
        return id.toString()
    }

    override fun saveUser(tx: Transaction, user: User) {
        val userId = UUID.randomUUID()
        val statement = tx.connection.prepareStatement("INSERT INTO users (id, name) VALUES (?, ?)")
        statement.setString(1, toExternalForm(userId))
        statement.setString(2, user.name.getName())
        statement.executeUpdate()
    }
}