package com.db

import java.sql.Connection
import java.sql.DriverManager

class DatabaseConnection {
    fun connect(): Connection {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/hexagon", "user", System.getenv("DB_PASSWORD"))
    }
}