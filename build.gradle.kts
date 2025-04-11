import org.gradle.api.JavaVersion.VERSION_21
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.1.20"
    application
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass = "com.hexagon.HexagonKt"
}

repositories {
    mavenCentral()
}

tasks {
    withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            allWarningsAsErrors = false
            jvmTarget.set(JVM_21)
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    java {
        sourceCompatibility = VERSION_21
        targetCompatibility = VERSION_21
    }
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:6.4.1.0"))
    implementation("org.http4k:http4k-server-jetty:4.9.9.0")
    implementation("org.http4k.pro:http4k-tools-hotreload")
    implementation("org.http4k:http4k-config")
    implementation("org.http4k:http4k-connect-storage-jdbc")
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-format-jackson")
    implementation("org.http4k:http4k-ops-resilience4j")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-template-handlebars")
    implementation("org.postgresql:postgresql:42.2.23")
    implementation("org.flywaydb:flyway-core:8.5.13")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    testImplementation("org.http4k:http4k-testing-approval")
    testImplementation("org.http4k:http4k-testing-hamkrest")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.12.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.12.1")
}

