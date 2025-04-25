package com.hexagon.events

import java.sql.PreparedStatement
import java.sql.ResultSet
import com.db.DatabaseConnection

class BalanceProjection (private val dbConnection: DatabaseConnection) {

    private val connection = dbConnection.connect()
    fun updateProjection(event: AccountEvent, account_id: String) {
        when (event) {
            is AccountEvent.AccountCreated -> {
                val sql = "INSERT INTO account_balance (account_id, balance) VALUES (?, ?)"
                val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
                preparedStatement.setString(1, account_id)
                preparedStatement.setDouble(2, event.initialBalance)
                preparedStatement.executeUpdate()
            }
            is AccountEvent.MoneyDeposited -> {
                val sql = "UPDATE account_balance SET balance = balance + ? WHERE account_id = ?"
                val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
                preparedStatement.setDouble(1, event.amount)
                preparedStatement.setString(2, account_id)
                preparedStatement.executeUpdate()
            }
            is AccountEvent.MoneyWithdrawn -> {
                val sql = "UPDATE account_balance SET balance = balance - ? WHERE account_id = ?"
                val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
                preparedStatement.setDouble(1, event.amount)
                preparedStatement.setString(2, account_id)
                preparedStatement.executeUpdate()
            }
        }
    }

    fun getBalance(account_id: String): Double {
        val sql = "SELECT balance FROM account_balance WHERE account_id = ?"
        val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, account_id)
        val resultSet: ResultSet = preparedStatement.executeQuery()

        return if (resultSet.next()) {
            resultSet.getDouble("balance")
        } else {
            0.0
        }
    }
}
