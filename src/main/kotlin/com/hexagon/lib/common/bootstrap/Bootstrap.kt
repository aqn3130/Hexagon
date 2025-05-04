package com.hexagon.lib.common.bootstrap

import java.time.Duration
import com.hexagon.lib.common.ErrorHandler
import com.hexagon.lib.common.OpenTelemetryFilters
import com.natpryce.krouton.http4k.ResourceRouter
import com.natpryce.krouton.http4k.withFilter
import io.opentelemetry.api.OpenTelemetry
import org.http4k.core.*

class Bootstrap {
    fun createHttp4KServerWithKrouton(
        router: ResourceRouter,
        idleTimeout: Duration? = null
    ) = Http4kServer(
        applicationIdentifier = "unknown",
        port = 8080,
        idleTimeout = idleTimeout,
        handler = createHttp4KHandler(router)
    )

    fun createHttp4KHandler(
        router: ResourceRouter,
        onErrorAction: (Request, Throwable?, Status) -> Response = stacktracePrintingErrorJson,
    ) : HttpHandler {
        val filters = defaultFilters(onErrorAction)
        return AppAnatomyHttp4kHandlers.kroutons()
            .apply { otherwise(router.withFilterIncludingHandlerIfNoMatch(filters)) }
            .toHandler()
    }

    private val openTelemetry = configureOpenTelemetry("unknown")
    private fun defaultFilters(onErrorAction: (Request, Throwable?, Status) -> Response) =
        OpenTelemetryFilters.server(openTelemetry)

    val stacktracePrintingErrorJson = { _: Request, throwable: Throwable?, status: Status ->
        ErrorHandler.stacktracePrintingErrorJson(
            throwable,
            status
        )
    }

    private fun configureOpenTelemetry(serviceInstanceId: String): OpenTelemetry {

        System.setProperty("otel.service.name", serviceInstanceId)
        System.setProperty("otel.traces.exporter", "otlp")
        System.setProperty("otel.metrics.exporter", "none")
        System.setProperty("otel.logs.exporter", "none")

        return OpenTelemetry.noop()
    }

        fun ResourceRouter.withFilterIncludingHandlerIfNoMatch(newFilter: Filter): ResourceRouter = withFilter(newFilter).run {
        copy(
            router = router.copy(
                handlerIfNoMatch = { request, parsed ->
                    newFilter { filteredRequest -> router.handlerIfNoMatch(filteredRequest, parsed) }.invoke(request)
                },
            ),
        )
    }
}

