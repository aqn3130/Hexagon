package com.hexagon.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object DatabaseConfig {
    val dataSource: DataSource by lazy {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/hexagon"
            username = "user"
            password = "password"
            driverClassName = "org.postgresql.Driver"
        }
        HikariDataSource(config)
    }
}
