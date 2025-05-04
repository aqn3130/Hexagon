package com.hexagon.lib.common.bootstrap

import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.natpryce.krouton.http4k.ResourceRoutesBuilder
import com.natpryce.krouton.plus
import com.natpryce.krouton.root
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

object AppAnatomyHttp4kHandlers {
    private val mapper = ObjectMapper()

    private val getConfig: HttpHandler = {
        jsonResponse(config())
    }

    private val getRobotsTxt: HttpHandler = {
        Response(Status.OK).body("User-agent: *\nDisallow: /\n")
    }

    fun create(): RoutingHttpHandler =
        routes(
            "/robots.txt" bind GET to getRobotsTxt,
            "/internal" bind routes(
                "/config" bind GET to getConfig,
            ),
        )

    fun kroutons(): ResourceRoutesBuilder {
        val internal = root + "internal"

        return ResourceRoutesBuilder().apply {
            root + "robots.txt" methods { GET(getRobotsTxt) }
            internal + "config" methods { GET(getConfig) }
        }
    }

    private fun jsonResponse(json: ByteArray): Response = Response(Status.OK)
        .header("Content-Type", ContentType.APPLICATION_JSON.toHeaderValue())
        .body(json.toString(UTF_8))

    private fun config(): ByteArray = mapper.writeValueAsBytes(
        linkedMapOf(
            "systemProperties" to System.getProperties().toSafeSortedMap(),
            "environmentVariables" to System.getenv().toSafeSortedMap(),
        ),
    )
}
fun Map<*, *>.toSafeSortedMap(): SortedMap<String, String> = map { entry -> safePair(entry.key.toString(), entry.value.toString()) }
    .toMap()
    .toSortedMap()

private fun safePair(key: String, value: String) =
    key to (if (key.isSecret()) "********" else value)

private fun String.isSecret(): Boolean {
    val uppercaseKey = uppercase()
    return uppercaseKey.endsWith("PASSWORD") ||
            uppercaseKey.endsWith("_SECURITY_TOKEN") ||
            uppercaseKey.startsWith("ACCESS_CONTROL_")
}