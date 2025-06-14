package com.hexagon.db

import java.sql.Connection
import javax.sql.DataSource

interface Transactor {
    fun <T> write(block: (Transaction) -> T): T
}

//typealias DataSource = DatabaseConnection

class SimpleTransactor(private val dataSource: DataSource) : Transactor {
    override fun <T> write(block: (Transaction) -> T): T {
        val connection = dataSource.connection
        return try {
            connection.autoCommit = false
            val tx = Transaction(connection)
            val result = block(tx)
            connection.commit()
            result
        } catch (e: Exception) {
            connection.rollback()
            println("Transaction rolled back due to: ${e.message}")
            throw e
        } finally {
            connection.close()
        }
    }
}

class Transaction(val connection: Connection)

