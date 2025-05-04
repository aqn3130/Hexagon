package com.hexagon.db

import org.jooq.DSLContext
import org.jooq.SQLDialect.POSTGRES
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import java.sql.Connection
import javax.sql.DataSource

private val DefaultSettings = Settings().withExecuteLogging(false)

fun Connection.dsl(settings: Settings = DefaultSettings): DSLContext = DSL.using(this, POSTGRES, settings)

fun DataSource.dsl(settings: Settings = DefaultSettings): DSLContext = DSL.using(this, POSTGRES, settings)
