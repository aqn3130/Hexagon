package com.hexagon.db

import java.sql.Connection
import java.sql.DriverManager
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

//class DatabaseConnection {
//    fun connect(): Connection {
//        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/hexagon", "user", "password")
//    }
//}