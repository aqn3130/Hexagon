package com.hexagon.lib.monitoring

object EventMonitor : Monitor<MonitoringEvent> {
    override fun notify(event: MonitoringEvent) {
        println("${event.contextName()} - ${event.message()}")
    }
}