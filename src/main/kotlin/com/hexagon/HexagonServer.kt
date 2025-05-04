package com.hexagon

import com.hexagon.domain.application.AccountRouter
import com.hexagon.domain.application.Hexagon
import com.hexagon.domain.application.Routes
import com.hexagon.lib.common.bootstrap.ApplicationCreator
import com.hexagon.lib.common.bootstrap.Bootstrap
import com.hexagon.lib.common.bootstrap.HexagonApplication
import com.hexagon.lib.common.bootstrap.startServer
import com.hexagon.lib.monitoring.EventMonitor
import com.natpryce.krouton.http4k.ResourceRouter

object HexagonServer : ApplicationCreator {
    @JvmStatic
    fun main(args: Array<String>) {
        Bootstrap().startServer(HexagonServer)
    }

    override fun invoke(bootstrap: Bootstrap): HexagonApplication {
        return HexagonApplication(
            monitor = EventMonitor,
            server = bootstrap.createHttp4KServerWithKrouton(
                createRouter(),
                idleTimeout = null
            )
        )
    }

    private fun createRouter() : ResourceRouter = Hexagon(
            accountRouter = AccountRouter()
        ).resources

}

