package com.hexagon

import java.io.PrintWriter
import java.io.StringWriter
import java.net.URI
import java.nio.file.Path
import java.util.ArrayDeque
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible

interface SensitiveData

interface HasExternalForm<out T : Any> {
    val raw: T

    fun toExternalForm() = raw
}

val sensitiveHeaders = listOf("Authorization")

interface ErrorCode : Flattenable {

    override fun properties(): Map<String, String> =
        super.properties() + ("_type" to this::class.java.simpleName)
}

interface Flattenable {
    fun properties() =
        propertiesByReflection()
}

interface Collapsible : Flattenable

private fun Any.propertiesByReflection(): Map<String, String> {
    val thisClass = this::class

    @Suppress("UNCHECKED_CAST") // Kotlin reflection API has wrong type constraints!
    val memberProperties = thisClass.memberProperties as Collection<KProperty1<Any, *>>

    return memberProperties
        .flatMap { p -> toPairs(thisClass, p, this) }
        .toMap()
}

private fun toPairs(ownerClass: KClass<*>, property: KProperty1<Any, *>, owner: Any): List<Pair<String, String>> {
    property.isAccessible = true // Don't set isAccessible to false to avoid possible race condition

    val definitions = inheritedPropertyDefinitions(ownerClass, property).toList()

    val reportedValue = property.get(owner)

    return when {
        shouldReport(definitions) -> reportedNames(property, definitions).flatMap { name -> toPairs(name, reportedValue) }
        else -> emptyList()
    }
}

private fun inheritedPropertyDefinitions(ownerClass: KClass<*>, property: KProperty1<*, *>) =
    ownerClass.resolutionOrder()
        .mapNotNull { c -> c.declaredMemberProperties.find { p -> p.name == property.name } }

private fun shouldReport(inheritedPropertyDefinitions: List<KProperty1<out Any, Any?>>) =
    inheritedPropertyDefinitions.none { it.findAnnotation<NotReported>() != null }

private fun reportedNames(property: KProperty1<*, *>, inheritedPropertyDefinitions: List<KProperty1<out Any, Any?>>) =
    inheritedPropertyDefinitions.asSequence()
        .mapNotNull { it.findAnnotation<ReportedName>() }
        .map { it.names }
        .firstOrNull()
        ?.takeIf { it.isNotEmpty() }
        ?.asList() ?: listOf(property.name)

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

private fun toPairs(name: String, value: Any?): List<Pair<String, String>> = when (value) {
    null ->
        emptyList()

    is Flattenable -> {
        val prefix = if (value is Collapsible) "" else "${name}_"
        value.properties().map { (pname, pvalue) ->
            "$prefix$pname" to pvalue
        }
    }


    is Path ->
        listOf(name to value.toString())

    is Iterable<*> ->
        listOf(name to value.filterNotNull().joinToString(separator = "\n", transform = ::stringValue))

    else ->
        listOf(name to stringValue(value))
}

private fun stringValue(value: Any): String = when (value) {
    is SensitiveData -> "*** REDACTED ***"
    is HasExternalForm<*> -> stringValue(value.raw)
    is Throwable -> stringValueOfThrowable(value)
    is KFunction<*> -> value.name
    // This makes it easy to report HTTP header fields.
    is Pair<*, *> -> "${stringValue(value.first ?: "null")}: ${stringValue(value.second ?: "null")}"
    else -> value.toString()
}

fun Map<String, List<String>>.redact(sensitiveKeys: List<String>): Map<String, List<String>> =
    map { (key, value) -> key to (value.takeUnless { key in sensitiveKeys } ?: listOf("***redacted***")) }.toMap()

private fun stringValueOfThrowable(value: Throwable): String {
    val throwable = when {
        value is ErrorCodeException &&
                value.errorCode is UnexpectedResponse ->
            value.copy(errorCode = value.errorCode.copy(headers = value.errorCode.headers.redact(sensitiveHeaders)))

        else ->
            value
    }
    return StringWriter()
        .also { throwable.printStackTrace(PrintWriter(it)) }
        .toString()
}

data class UnexpectedResponse(
    val status: Int,
    override val method: String,
    override val uri: URI,
    val headers: Map<String, List<String>>,
    val cause: ErrorCode?,
    val payload: String,
) : HttpCommunicationErrorCode()

sealed class HttpCommunicationErrorCode : ErrorCode {
    abstract val method: String
    abstract val uri: URI
}