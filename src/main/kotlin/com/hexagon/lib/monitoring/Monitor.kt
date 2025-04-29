package com.hexagon.lib.monitoring

interface Monitor<in T : MonitoringEvent> {
    fun notify(event: T)
}