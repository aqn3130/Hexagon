package com.hexagon

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.google.common.net.MediaType
import com.google.common.net.MediaType.create
import io.opentelemetry.api.OpenTelemetry
import org.apache.hc.core5.http.ContentType
import org.http4k.core.*
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.filter.ClientFilters
import org.http4k.filter.OpenTelemetryTracing
import org.http4k.filter.ServerFilters
import org.http4k.filter.defaultSpanNamer

val APPLICATION_JSON: MediaType = create("application", "json")

private val json = JsonNodeFactory.instance

val userNotFoundError: Error = error("User not found")

object Json {
    fun mapperPrettyPrint(): ObjectMapper =
        ObjectMapper()
            .enable(INDENT_OUTPUT)
            .setDefaultPrettyPrinter(
                DefaultPrettyPrinter()
                    .withObjectIndenter(
                        DefaultIndenter().withLinefeed("\n"),
                    ),
            )
}

fun JsonNode.prettyPrint(): String =
    Json.mapperPrettyPrint().writeValueAsString(this)

fun obj(vararg props: Pair<String, JsonNode?>?): ObjectNode =
    obj(listOf(*props))

fun obj(props: Iterable<Pair<String, JsonNode?>?>): ObjectNode =
    json.objectNode().apply {
        props.filterNotNull().forEach { set<ObjectNode>(it.first, it.second) }
    }

private fun inheritedPropertyDefinitions(ownerClass: KClass<*>, property: KProperty1<*, *>) =
    ownerClass.resolutionOrder()
        .mapNotNull { c -> c.declaredMemberProperties.find { p -> p.name == property.name } }

annotation class ReportedName(vararg val names: String)
annotation class NotReported
private fun reportedNames(property: KProperty1<*, *>, inheritedPropertyDefinitions: List<KProperty1<out Any, Any?>>) =
    inheritedPropertyDefinitions.asSequence()
        .mapNotNull { it.findAnnotation<ReportedName>() }
        .map { it.names }
        .firstOrNull()
        ?.takeIf { it.isNotEmpty() }
        ?.asList() ?: listOf(property.name)

private fun shouldReport(inheritedPropertyDefinitions: List<KProperty1<out Any, Any?>>) =
    inheritedPropertyDefinitions.none { it.findAnnotation<NotReported>() != null }

private fun KClass<*>.resolutionOrder(): List<KClass<*>> {
    val next = ArrayDeque<KClass<*>>()
    val hierarchy = linkedSetOf<KClass<*>>()

    next.addLast(this)

    while (next.isNotEmpty()) {
        val c = next.removeFirst()
        hierarchy.add(c)
        next.addAll(c.superclasses.filterNot { it in hierarchy })
    }

    return hierarchy.toList()
}

interface HasCause {
    val cause: Throwable
}

data class ErrorCodeException(val errorCode: ErrorCode) : Exception(errorCode.toString(), (errorCode as? HasCause)?.cause)

internal object OpenTelemetryFilters {

    fun server(openTelemetry: OpenTelemetry) = ServerFilters.OpenTelemetryTracing(
        openTelemetry,
    )

    private fun addTraceIdToMdc() = Filter { next ->
        { request ->
            next(request)
        }
    }

    fun client(openTelemetry: OpenTelemetry) = ClientFilters.OpenTelemetryTracing(
        openTelemetry,
        spanCompletionMutator = { span, _, response ->
            response.header("x-cache")?.let { xCacheValue ->
                span.setAttribute("http.x-cache", xCacheValue)
            }
        },
        spanNamer = { request ->
            if (true) {
                defaultSpanNamer(request)
            } else {
                "${request.method.name} ${request.uri.host}"
            }
        },
    )

}

class ErrorHandler {
    companion object {
        fun stacktracePrintingErrorJson(throwable: Throwable?, status: Status = INTERNAL_SERVER_ERROR): Response =
            toErrorJson(throwable, status) {
                throwable.toJsonWithStacktrace(it.code)
            }
        fun toErrorJson(
            throwable: Throwable?,
            status: Status = INTERNAL_SERVER_ERROR,
            toJson: Throwable?.(Status) -> ObjectNode,
        ): Response {
            val jsonDescription = when (throwable) {
                is ErrorCodeException -> throwable.errorCode.toJsonWithStacktrace()
                else -> throwable.toJson(status)
            }

            return Response(status)
                .header("content-type", ContentType.APPLICATION_JSON.toString())
        }

        private fun Throwable?.toJsonWithStacktrace(responseCode: Int): ObjectNode = obj(

        )

        private fun ErrorCode.toJsonWithStacktrace(): ObjectNode {
            val throwableReducingMapper = ObjectMapper()
                .registerModule(
                    SimpleModule().addSerializer(
                        Throwable::class.java,
                        object : StdSerializer<Throwable>(Throwable::class.java) {
                            override fun serialize(value: Throwable, gen: JsonGenerator, provider: SerializerProvider) {
                                gen.writeString("${value.javaClass.canonicalName}: ${value.message}")
                            }
                        },
                    ),
                )
            return obj(

            )
        }
    }
}