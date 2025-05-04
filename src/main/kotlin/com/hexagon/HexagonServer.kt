package com.hexagon

import com.hexagon.domain.application.Routes
import com.hexagon.lib.common.bootstrap.ApplicationCreator
import com.hexagon.lib.common.bootstrap.Bootstrap
import com.hexagon.lib.common.bootstrap.HexagonApplication
import com.hexagon.lib.common.bootstrap.startServer
import com.hexagon.lib.monitoring.EventMonitor

object HexagonServer : ApplicationCreator {
    @JvmStatic
    fun main(args: Array<String>) {
        Bootstrap().startServer(HexagonServer)
    }


    override fun invoke(bootstrap: Bootstrap): HexagonApplication =
        HexagonApplication(EventMonitor, bootstrap.createHttp4KServerWithKrouton(Routes().resources))
}

