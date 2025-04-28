package com.hexagon

class HexagonApplication(
    private val monitor: Monitor<BackgroundActivityShutdownFailed>,
    private val server: HttpServer,
    vararg otherActivity: BackgroundActivity
) : BackgroundActivity {

    private val activities: List<BackgroundActivity> = listOf(*otherActivity, server)

    override fun start() {

        activities.forEach(BackgroundActivity::start)

        Runtime.getRuntime().addShutdownHook(
            object : Thread() {
                override fun run() {
                    this@HexagonApplication.stop()
                }
            },
        )
    }
    override fun stop() = activities.asReversed().forEach {
        println()
    }

    fun rootUri() = server.rootUri()


}

typealias ApplicationCreator = (bootstrap: Bootstrap) -> HexagonApplication

fun Bootstrap.startServer(creator: ApplicationCreator) = creator(this).start()

object stderrMonitor : Monitor<MonitoringEvent> {
    override fun notify(event: MonitoringEvent) {
        System.err.println("${event.contextName()} - ${event.message()}")
    }
}

fun custom(fn: (Bootstrap) -> HttpServer) = object : ApplicationCreator {
    override fun invoke(bootstrap: Bootstrap) = HexagonApplication(stderrMonitor,fn(bootstrap))
}

data class BackgroundActivityShutdownFailed(val cause: Throwable) :
    MonitoringEvent