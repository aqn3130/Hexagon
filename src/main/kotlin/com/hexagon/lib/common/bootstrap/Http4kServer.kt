package com.hexagon.lib.common.bootstrap

import io.undertow.Undertow
import io.undertow.UndertowOptions.IDLE_TIMEOUT
import io.undertow.server.handlers.BlockingHandler
import io.undertow.server.handlers.GracefulShutdownHandler
import org.http4k.core.HttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.Http4kUndertowHttpHandler
import org.http4k.server.ServerConfig
import org.http4k.server.ServerConfig.StopMode.Graceful
import java.net.InetSocketAddress
import java.net.URI
import java.time.Duration
import java.util.concurrent.TimeoutException

interface HttpServer : BackgroundActivity {
    fun rootUri(): URI
}

class Http4kServer(
    val applicationIdentifier: String,
    val port: Int,
    gracefulShutdownPeriod: Duration = Duration.ofSeconds(15),
    idleTimeout: Duration? = null,
    handler: HttpHandler
) : HttpServer {
    private val http4k = object : ServerConfig {
        override val stopMode = Graceful(timeout = gracefulShutdownPeriod)

        override fun toServer(http: HttpHandler): Http4kServer = object : Http4kServer {
            private val httpHandler = http
                .let(::Http4kUndertowHttpHandler)
                .let(::BlockingHandler)
                .let(::GracefulShutdownHandler)

            private val server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setIdleTimeoutServerOption(idleTimeout)
                .setHandler(httpHandler).build()

            private fun Undertow.Builder.setIdleTimeoutServerOption(idleTimeout: Duration?): Undertow.Builder = if (idleTimeout == null) {
                this
            } else {
                setServerOption(IDLE_TIMEOUT, idleTimeout.toMillis().toInt())
            }

            override fun start() = apply {
                server.start()
            }

            override fun stop() = apply {
                try {
                    httpHandler.shutdown()

                    if (httpHandler.shutdownFails()) throw TimeoutException()
                } finally {
                    server.stop()
                }
            }

            private fun GracefulShutdownHandler.shutdownFails(): Boolean = !awaitShutdown(stopMode.timeout.toMillis())

            override fun port(): Int = when {
                port > 0 -> port
                else -> (server.listenerInfo[0].address as InetSocketAddress).port
            }
        }
    }.toServer(handler)

    override fun start() {
        http4k.start()
        println("started http4k server $applicationIdentifier on ${rootUri()}")
    }

    override fun stop() {
        http4k.stop()
    }

    override fun rootUri(): URI = URI("http", null, "localhost", http4k.port(), "/", null, null)
}