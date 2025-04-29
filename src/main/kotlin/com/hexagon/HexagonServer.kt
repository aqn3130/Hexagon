package com.hexagon

import com.hexagon.domain.application.Routes
import com.hexagon.lib.monitoring.EventMonitor
import com.natpryce.krouton.http4k.ResourceRouter
import com.natpryce.krouton.http4k.resources
import com.natpryce.krouton.plus
import com.natpryce.krouton.root
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status

object HexagonServer : ApplicationCreator {
    @JvmStatic
    fun main(args: Array<String>) {
        Bootstrap().startServer(HexagonServer)
    }


    override fun invoke(bootstrap: Bootstrap): HexagonApplication =
        HexagonApplication(EventMonitor, bootstrap.createHttp4KServerWithKrouton(Routes().resources))
}

