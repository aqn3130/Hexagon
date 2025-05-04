package com.hexagon.db

import com.hexagon.lib.monitoring.EventMonitor.notify
import com.hexagon.lib.monitoring.MonitoringEvent
import org.flywaydb.core.Flyway

data class MigrationEvent (val context: String, val message: String) : MonitoringEvent {
    override fun contextName(): String {
        return context
    }

    override fun message(): String {
        return message
    }
}
class Migration {
    fun runMigration() {
        // Run Flyway migrations
        val flyway = Flyway.configure()
            .dataSource("jdbc:postgresql://localhost:5432/hexagon", "user", "password")
            .load()
        flyway.migrate()

        val event: MonitoringEvent = MigrationEvent("MigrationEvent", "Migration Completed")
        notify(event)
    }
}