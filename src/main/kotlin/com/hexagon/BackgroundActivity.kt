package com.hexagon

import org.apache.sshd.client.SshClient
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit.SECONDS

interface BackgroundActivity : AutoCloseable {
    fun start()
    fun stop()

    override fun close() {
        stop()
    }
}

fun backgroundActivity(startFn: () -> Unit = {}, stopFn: () -> Unit) = object : BackgroundActivity {
    override fun start() = startFn()
    override fun stop() = stopFn()
}

fun ExecutorService.asBackgroundActivity() = backgroundActivity(
    stopFn = {
        shutdownNow()
        awaitTermination(5, SECONDS)
    },
)

fun AutoCloseable.asBackgroundActivity() = backgroundActivity(stopFn = ::close)

fun SshClient.asBackgroundActivity(): BackgroundActivity = backgroundActivity(startFn = ::start, stopFn = ::stop)
