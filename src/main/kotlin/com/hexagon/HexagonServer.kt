package com.hexagon

import com.natpryce.krouton.http4k.ResourceRouter
import com.natpryce.krouton.http4k.resources
import com.natpryce.krouton.plus
import com.natpryce.krouton.root
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Status

interface MonitoringEvent :

    Flattenable {
    val actionForDevOnSupport: String? get() = null

    fun contextName(): String = "context"

    fun message(): String = "test"
}

interface Monitor<in T : MonitoringEvent> {
    fun notify(event: T)
}

object HexagonServer : ApplicationCreator {
    @JvmStatic
    fun main(args: Array<String>) {
        Bootstrap().startServer(HexagonServer)
    }

    override fun invoke(bootstrap: Bootstrap): HexagonApplication =
        HexagonApplication(tempMonitor, bootstrap.createHttp4KServerWithKrouton(app()))

    private val example = root + "example"

    private fun app(): ResourceRouter = resources {

        example methods {
            GET { Response(Status.OK).body("Welcome to anura-tester! Your IP address is ${it.source?.address}.\n${it.headers.joinToString("\n", transform = { (name, value) -> "$name = $value" })}") }
        }
        root methods {
            GET { Response(Status.OK) }
        }
    }

    object tempMonitor : Monitor<MonitoringEvent> {
        override fun notify(event: MonitoringEvent) {
            println("${event.contextName()} - ${event.message()}")
        }
    }
}

