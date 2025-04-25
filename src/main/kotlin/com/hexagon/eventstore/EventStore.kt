package com.hexagon.eventstore

import java.sql.PreparedStatement
import java.sql.ResultSet
import com.db.DatabaseConnection
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hexagon.events.AccountEvent

class EventStore(dbConnection: DatabaseConnection) {
    private val objectMapper = jacksonObjectMapper()
    private val connection = dbConnection.connect()
    fun save(events: List<AccountEvent>, account_id: String) {
        val sql = "INSERT INTO events (account_id, event_type, event_data) VALUES (?, ?, ?)"
        val preparedStatement: PreparedStatement = connection.prepareStatement(sql)

        events.forEach { event ->
            preparedStatement.setString(1, account_id)
            preparedStatement.setString(2, event::class.simpleName!!)
            preparedStatement.setString(3, objectMapper.writeValueAsString(event)) // Simplified for example purposes
            preparedStatement.addBatch()
        }
        preparedStatement.executeBatch()
    }

    fun getEvents(account_id: String): MutableList<AccountEvent> {
        val sql = "SELECT event_type, event_data FROM events WHERE account_id = ?"
        val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, account_id)
        val resultSet: ResultSet = preparedStatement.executeQuery()

        val events = mutableListOf<AccountEvent>()
        while (resultSet.next()) {
            val eventType = resultSet.getString("event_type")
            val eventData = resultSet.getString("event_data")
            val event = when (eventType) {
                "AccountCreated" -> objectMapper.readValue<AccountEvent.AccountCreated>(eventData)
                "MoneyDeposited" -> objectMapper.readValue<AccountEvent.MoneyDeposited>(eventData)
                "MoneyWithdrawn" -> objectMapper.readValue<AccountEvent.MoneyWithdrawn>(eventData)
                else -> throw IllegalArgumentException("Unknown event type")
            }
            events.add(event)
        }
        return events
    }
}