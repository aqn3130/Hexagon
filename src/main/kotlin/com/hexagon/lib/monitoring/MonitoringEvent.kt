package com.hexagon.lib.monitoring

import com.hexagon.lib.common.Flattenable

interface MonitoringEvent :
    Flattenable {
    val actionForDevOnSupport: String? get() = null

    fun contextName(): String = "context"

    fun message(): String = "test"
}
