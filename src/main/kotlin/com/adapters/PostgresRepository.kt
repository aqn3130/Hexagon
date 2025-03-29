package com.adapters

import java.sql.Connection
import java.util.UUID
import com.domain.models.UserViewModel
import com.domain.ports.UserRepository
import org.postgresql.ds.PGSimpleDataSource

class PostgresRepository: UserRepository {
    private val dataSource = PGSimpleDataSource().apply {
        setURL("jdbc:postgresql://localhost:5432/hexagon")
        user = "user"
        password = "password"
    }

    private fun getConnection(): Connection = dataSource.connection

    override fun getUser(id: String): UserViewModel? {
        getConnection().use { connection ->
            val statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")
            statement.setString(1, id)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                UserViewModel(resultSet.getString("id"), resultSet.getString("name"))
            } else {
                null
            }
        }
    }

    private fun toExternalForm(id: Any) : String {
        return id.toString()
    }

    override fun saveUser(user: UserViewModel) {
        val userId = UUID.randomUUID()
        getConnection().use { connection ->
            val statement = connection.prepareStatement("INSERT INTO users (id, name) VALUES (?, ?)")
            statement.setString(1, toExternalForm(userId))
            statement.setString(2, user.name)
            statement.executeUpdate()
        }
    }
}