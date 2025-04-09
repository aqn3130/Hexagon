package com.db

import org.flywaydb.core.Flyway

class Migration {
    fun runMigration() {
        // Run Flyway migrations
        val flyway = Flyway.configure()
            .dataSource("jdbc:postgresql://localhost:5432/hexagon", "user", "password")
            .load()
        flyway.migrate()
    }
}